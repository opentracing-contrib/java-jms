/*
 * Copyright 2017-2019 The OpenTracing Authors
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
package io.opentracing.contrib.jms.spring;

import static org.awaitility.Awaitility.await;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import io.opentracing.contrib.jms.common.TracingMessageUtils;
import io.opentracing.mock.MockSpan;
import io.opentracing.mock.MockTracer;
import io.opentracing.tag.Tags;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.jms.TextMessage;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.jms.support.converter.MappingJackson2MessageConverter;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {TracingJmsConfiguration.class, TestConfiguration.class})
public class TracingJmsTemplateTest {

  @Autowired
  private JmsTemplate jmsTemplate;

  @Autowired
  private MockTracer mockTracer;

  @Before
  public void before() {
    mockTracer.reset();
  }

  @Test
  public void messageConverterConfigured() {
    assertTrue(jmsTemplate.getMessageConverter() instanceof MappingJackson2MessageConverter);
  }

  @Test
  public void oneListener() {
    jmsTemplate.convertAndSend("TEST.SECOND", "test");

    await().atMost(15, TimeUnit.SECONDS).until(reportedSpansSize(), equalTo(3));

    List<MockSpan> spans = mockTracer.finishedSpans();
    assertEquals(3, spans.size());

    for (MockSpan span : spans) {
      assertEquals(spans.get(0).context().traceId(), span.context().traceId());
    }
  }

  @Test
  public void twoListeners() {
    jmsTemplate.convertAndSend("TEST.FIRST", "test");

    await().atMost(15, TimeUnit.SECONDS).until(reportedSpansSize(), equalTo(5));

    List<MockSpan> spans = mockTracer.finishedSpans();
    assertEquals(5, spans.size());

    for (MockSpan span : spans) {
      assertEquals(spans.get(0).context().traceId(), span.context().traceId());
    }
  }

  @Test
  public void sendAndReceive() throws Exception {
    String destination = "TEST.THIRD";
    final Message message = jmsTemplate.sendAndReceive(destination, new MessageCreator() {
      @Override
      public Message createMessage(Session session) throws JMSException {
        return session.createTextMessage("message");
      }
    });
    assertNotNull(message);
    assertEquals("MESSAGE", ((TextMessage) message).getText());

    List<MockSpan> mockSpans = mockTracer.finishedSpans();
    assertEquals(4, mockSpans.size());

    checkSpans(mockSpans);
    assertNull(mockTracer.activeSpan());
  }

  @Test
  public void sendAndThenReceive() throws Exception {
    String destination = "TEST.DEST";

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

  private Callable<Integer> reportedSpansSize() {
    return new Callable<Integer>() {
      @Override
      public Integer call() {
        return mockTracer.finishedSpans().size();
      }
    };
  }
}
