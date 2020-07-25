/*
 * Copyright 2017-2020 The OpenTracing Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package io.opentracing.contrib.jms2;

import io.opentracing.contrib.jms.common.TracingMessageUtils;
import io.opentracing.mock.MockSpan;
import io.opentracing.mock.MockTracer;
import io.opentracing.tag.Tags;
import org.apache.activemq.artemis.api.core.TransportConfiguration;
import org.apache.activemq.artemis.core.config.Configuration;
import org.apache.activemq.artemis.core.config.impl.ConfigurationImpl;
import org.apache.activemq.artemis.core.remoting.impl.invm.InVMAcceptorFactory;
import org.apache.activemq.artemis.core.server.ActiveMQServer;
import org.apache.activemq.artemis.core.server.impl.ActiveMQServerImpl;
import org.apache.activemq.artemis.jms.client.ActiveMQJMSConnectionFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSContext;
import javax.jms.JMSProducer;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;
import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class TracingArtemisViaConnectionFactoryTest {

  private final MockTracer mockTracer = new MockTracer();

  private ActiveMQServer server;
  private Connection connection;
  private Session session;
  private JMSContext jmsContext;

  @Before
  public void before() throws Exception {
    mockTracer.reset();

    Configuration configuration = new ConfigurationImpl();

    HashSet<TransportConfiguration> transports = new HashSet<>();
    transports.add(new TransportConfiguration(InVMAcceptorFactory.class.getName()));
    configuration.setAcceptorConfigurations(transports);
    configuration.setSecurityEnabled(false);

    File targetDir = new File(System.getProperty("user.dir") + "/target");
    configuration.setBrokerInstance(targetDir);

    server = new ActiveMQServerImpl(configuration);
    server.start();

    ConnectionFactory activeMQJMSConnectionFactory = new ActiveMQJMSConnectionFactory("vm://0");
    ConnectionFactory tracingConnectionFactory = new TracingConnectionFactory(activeMQJMSConnectionFactory, mockTracer);
    connection = tracingConnectionFactory.createConnection();
    connection.start();

    session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

    jmsContext = tracingConnectionFactory.createContext();
  }

  @After
  public void after() throws Exception {
    jmsContext.close();
    session.close();
    connection.close();
    server.stop();
  }

  @Test
  public void sendAndReceive() throws Exception {
    Queue queue = session.createQueue("TEST.FOO");
    MessageProducer producer = session.createProducer(queue);
    MessageConsumer consumer = session.createConsumer(queue);
    TextMessage message = session.createTextMessage("Hello world");

    producer.send(message);

    TextMessage received = (TextMessage) consumer.receive(5000);
    assertEquals("Hello world", received.getText());

    List<MockSpan> mockSpans = mockTracer.finishedSpans();
    assertEquals(2, mockSpans.size());

    checkSpans(mockSpans);
    assertNull(mockTracer.activeSpan());
  }

  @Test
  public void sendAndReceiveJMSProducer() throws Exception {
    Destination destination = session.createQueue("TEST.FOO");
    JMSProducer producer = jmsContext.createProducer();
    MessageConsumer consumer = session.createConsumer(destination);
    TextMessage message = session.createTextMessage("Hello world");

    producer.send(destination, message);

    TextMessage received = (TextMessage) consumer.receive(5000);
    assertEquals("Hello world", received.getText());

    List<MockSpan> mockSpans = mockTracer.finishedSpans();
    assertEquals(2, mockSpans.size());

    checkSpans(mockSpans);
    assertNull(mockTracer.activeSpan());
  }

  @Test
  public void sendAndReceiveInListener() throws Exception {
    Destination destination = session.createQueue("TEST.FOO");
    MessageProducer producer = session.createProducer(destination);
    MessageConsumer consumer = session.createConsumer(destination);

    final CountDownLatch countDownLatch = new CountDownLatch(1);
    MessageListener listener = new MessageListener() {
      @Override
      public void onMessage(Message message) {
        countDownLatch.countDown();
      }
    };
    consumer.setMessageListener(listener);

    TextMessage message = session.createTextMessage("Hello world");

    producer.send(message);
    countDownLatch.await(15, TimeUnit.SECONDS);

    await().atMost(15, TimeUnit.SECONDS).until(reportedSpansSize(), equalTo(2));

    List<MockSpan> mockSpans = mockTracer.finishedSpans();
    assertEquals(2, mockSpans.size());

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

  private Callable<Integer> reportedSpansSize() {
    return new Callable<Integer>() {
      @Override
      public Integer call() {
        return mockTracer.finishedSpans().size();
      }
    };
  }
}
