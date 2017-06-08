package io.opentracing.contrib.jms.spring;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import io.opentracing.contrib.jms.common.TracingMessageUtils;
import io.opentracing.mock.MockSpan;
import io.opentracing.mock.MockTracer;
import io.opentracing.tag.Tags;
import io.opentracing.util.ThreadLocalActiveSpanSource;
import java.io.IOException;
import java.util.List;
import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.jms.TextMessage;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;


public class TracingJmsTemplateTest {

  private final MockTracer mockTracer = new MockTracer(new ThreadLocalActiveSpanSource(),
      MockTracer.Propagator.TEXT_MAP);
  private Session session;
  private Connection connection;
  private ActiveMQConnectionFactory connectionFactory;

  @Before
  public void before() throws IOException, JMSException {
    mockTracer.reset();

    connectionFactory = new ActiveMQConnectionFactory("vm://localhost");
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

    JmsTemplate jmsTemplate = new TracingJmsTemplate(connectionFactory, mockTracer);

    jmsTemplate.send(destination, new MessageCreator() {
      @Override
      public Message createMessage(Session session) throws JMSException {
        return session.createTextMessage("Hello world");
      }
    });

    TextMessage received = (TextMessage) jmsTemplate.receive(destination);
    assertEquals("Hello world", received.getText());

    jmsTemplate.convertAndSend(destination, "Hello world");
    assertEquals("Hello world", jmsTemplate.receiveAndConvert(destination));

    List<MockSpan> mockSpans = mockTracer.finishedSpans();
    assertEquals(4, mockSpans.size());

    checkSpans(mockSpans);
    assertNull(mockTracer.activeSpan());
  }


  private void checkSpans(List<MockSpan> mockSpans) {
    for (MockSpan mockSpan : mockSpans) {
      assertTrue(mockSpan.tags().get(Tags.SPAN_KIND.getKey()).equals(Tags.SPAN_KIND_CONSUMER)
          || mockSpan.tags().get(Tags.SPAN_KIND.getKey()).equals(Tags.SPAN_KIND_PRODUCER));
      assertEquals(TracingMessageUtils.COMPONENT_NAME,
          mockSpan.tags().get(Tags.COMPONENT.getKey()));
      assertEquals(0, mockSpan.generatedErrors().size());
      String operationName = mockSpan.operationName();
      assertTrue(operationName.equals(TracingMessageUtils.OPERATION_NAME_SEND)
          || operationName.equals(TracingMessageUtils.OPERATION_NAME_RECEIVE));
    }
  }
}