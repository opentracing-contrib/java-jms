package io.opentracing.contrib.jms;


import io.opentracing.Span;
import io.opentracing.contrib.jms.common.TracingMessageConsumer;
import io.opentracing.contrib.jms.common.TracingMessageListener;
import io.opentracing.contrib.jms.common.TracingMessageUtils;
import io.opentracing.contrib.spanmanager.DefaultSpanManager;
import io.opentracing.mock.MockSpan;
import io.opentracing.mock.MockTracer;
import io.opentracing.tag.Tags;
import io.opentracing.util.GlobalTracer;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.hamcrest.core.IsEqual;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.jms.Connection;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static com.jayway.awaitility.Awaitility.await;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class TracingActiveMQTest {
    private static final MockTracer mockTracer = new MockTracer(MockTracer.Propagator.TEXT_MAP);
    private Session session;
    private Connection connection;

    @BeforeClass
    public static void init() {
        GlobalTracer.register(mockTracer);
    }

    @Before
    public void before() throws IOException, JMSException {
        mockTracer.reset();
        DefaultSpanManager.getInstance().clear();

        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory("vm://localhost");
        connection = connectionFactory.createConnection();
        connection.start();
        session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
    }

    @After
    public void after() throws JMSException {
        session.close();
        connection.close();
    }

    @Test
    public void sendAndReceive() throws Exception {
        Destination destination = session.createQueue("TEST.FOO");

        MessageProducer messageProducer = session.createProducer(destination);
        messageProducer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);

        // Instrument MessageProducer with TracingMessageProducer
        TracingMessageProducer producer =
                new TracingMessageProducer(messageProducer);

        MessageConsumer messageConsumer = session.createConsumer(destination);

        // Instrument MessageConsumer with TracingMessageConsumer
        TracingMessageConsumer consumer = new TracingMessageConsumer(messageConsumer);

        TextMessage message = session.createTextMessage("Hello world");

        producer.send(message);

        TextMessage received = (TextMessage) consumer.receive(5000);
        assertEquals("Hello world", received.getText());


        List<MockSpan> mockSpans = mockTracer.finishedSpans();
        assertEquals(2, mockSpans.size());

        checkSpans(mockSpans);
        Span span = DefaultSpanManager.getInstance().current().getSpan();
        assertNull(span);
    }

    @Test
    public void sendAndReceiveInListener() throws Exception {
        Destination destination = session.createQueue("TEST.FOO");

        MessageProducer messageProducer = session.createProducer(destination);
        messageProducer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);

        // Instrument MessageProducer with TracingMessageProducer
        TracingMessageProducer producer =
                new TracingMessageProducer(messageProducer);

        MessageConsumer messageConsumer = session.createConsumer(destination);

        final CountDownLatch countDownLatch = new CountDownLatch(1);
        // Instrument MessgaeListener with TraceMessageListener
        MessageListener messageListener = new TracingMessageListener(
                new MessageListener() {
                    @Override
                    public void onMessage(Message message) {
                        countDownLatch.countDown();
                    }
                });

        messageConsumer.setMessageListener(messageListener);

        TextMessage message = session.createTextMessage("Hello world");

        producer.send(message);
        countDownLatch.await(15, TimeUnit.SECONDS);

        await().atMost(15, TimeUnit.SECONDS).until(reportedSpansSize(), IsEqual.equalTo(2));

        List<MockSpan> mockSpans = mockTracer.finishedSpans();
        assertEquals(2, mockSpans.size());

        checkSpans(mockSpans);

        Span span = DefaultSpanManager.getInstance().current().getSpan();
        assertNull(span);
    }

    private void checkSpans(List<MockSpan> mockSpans) {
        for (MockSpan mockSpan : mockSpans) {
            assertEquals(Tags.SPAN_KIND_CLIENT, mockSpan.tags().get(Tags.SPAN_KIND.getKey()));
            assertEquals(TracingMessageUtils.COMPONENT_NAME, mockSpan.tags().get(Tags.COMPONENT.getKey()));
            assertEquals(0, mockSpan.generatedErrors().size());
            String operationName = mockSpan.operationName();
            assertTrue(operationName.equals(TracingMessageUtils.OPERATION_NAME_SEND)
                    || operationName.equals(TracingMessageUtils.OPERATION_NAME_RECEIVE));
        }
    }

    private Callable<Integer> reportedSpansSize() {
        return new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                return mockTracer.finishedSpans().size();
            }
        };
    }
}
