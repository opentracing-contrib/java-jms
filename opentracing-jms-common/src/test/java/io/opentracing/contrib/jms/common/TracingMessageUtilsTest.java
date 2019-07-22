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
package io.opentracing.contrib.jms.common;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import io.opentracing.Span;
import io.opentracing.SpanContext;
import io.opentracing.mock.MockSpan;
import io.opentracing.mock.MockTracer;
import io.opentracing.tag.Tags;
import java.io.IOException;
import javax.jms.Destination;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.activemq.command.ActiveMQTextMessage;
import org.junit.Before;
import org.junit.Test;


public class TracingMessageUtilsTest {

  private final MockTracer mockTracer = new MockTracer();


  @Before
  public void before() {
    mockTracer.reset();
  }

  @Test
  public void noSpanToExtract() {
    SpanContext context = TracingMessageUtils.extract(new ActiveMQTextMessage(), mockTracer);
    assertNull(context);
  }

  @Test
  public void extractContextFromManager() {
    MockSpan span = mockTracer.buildSpan("test").start();
    mockTracer.scopeManager().activate(span);
    MockSpan.MockContext context =
        (MockSpan.MockContext) TracingMessageUtils.extract(new ActiveMQTextMessage(), mockTracer);
    assertNotNull(context);
    assertEquals(span.context().spanId(), context.spanId());
  }

  @Test
  public void extractContextFromProperties() {
    MockSpan span = mockTracer.buildSpan("test").start();
    ActiveMQTextMessage message = new ActiveMQTextMessage();
    TracingMessageUtils.inject(span, message, mockTracer);
    MockSpan.MockContext context =
        (MockSpan.MockContext) TracingMessageUtils.extract(message, mockTracer);
    assertNotNull(context);
    assertEquals(span.context().spanId(), context.spanId());
  }

  @Test
  public void buildAndFinishChildSpan() {
    MockSpan span = mockTracer.buildSpan("test").start();
    mockTracer.scopeManager().activate(span);
    SpanContext childContext =
        TracingMessageUtils.buildAndFinishChildSpan(new ActiveMQTextMessage(), mockTracer);
    assertNotNull(childContext);

    assertNotNull(mockTracer.activeSpan());

    assertEquals(1, mockTracer.finishedSpans().size());

    MockSpan finished = mockTracer.finishedSpans().get(0);

    assertEquals(TracingMessageUtils.OPERATION_NAME_RECEIVE, finished.operationName());
    assertEquals(Tags.SPAN_KIND_CONSUMER, finished.tags().get(Tags.SPAN_KIND.getKey()));
    assertEquals(TracingMessageUtils.COMPONENT_NAME, finished.tags().get(Tags.COMPONENT.getKey()));
    assertEquals(span.context().spanId(), finished.parentId());
    assertEquals(span.context().traceId(), finished.context().traceId());
  }

  @Test
  public void inject() throws IOException {
    ActiveMQTextMessage message = new ActiveMQTextMessage();
    assertTrue(message.getProperties().isEmpty());

    MockSpan span = mockTracer.buildSpan("test").start();
    TracingMessageUtils.inject(span, message, mockTracer);
    assertFalse(message.getProperties().isEmpty());
  }

  @Test
  public void buildAndInjectSpan() throws Exception {
    Destination destination = new ActiveMQQueue("queue");

    ActiveMQTextMessage message = new ActiveMQTextMessage();
    MockSpan span = mockTracer.buildSpan("test").start();
    mockTracer.scopeManager().activate(span);

    MockSpan injected =
        (MockSpan) TracingMessageUtils.buildAndInjectSpan(destination, message, mockTracer);

    assertFalse(message.getProperties().isEmpty());
    assertEquals(span.context().spanId(), injected.parentId());
  }

  @Test
  public void buildFollowingSpanNoParent() {
    ActiveMQTextMessage message = new ActiveMQTextMessage();
    Span span = TracingMessageUtils.buildFollowingSpan(message, mockTracer);
    assertTrue(span instanceof MockSpan);
  }

  @Test
  public void buildFollowingSpan() {
    ActiveMQTextMessage message = new ActiveMQTextMessage();
    Destination destination = new ActiveMQQueue("queue");
    MockSpan injected =
        (MockSpan) TracingMessageUtils.buildAndInjectSpan(destination, message, mockTracer);
    MockSpan span = (MockSpan) TracingMessageUtils.buildFollowingSpan(message, mockTracer);
    assertEquals(span.parentId(), injected.context().spanId());
  }

  @Test
  public void buildFollowingSpanWithActiveSpan() {
    ActiveMQTextMessage message = new ActiveMQTextMessage();
    MockSpan parent = mockTracer.buildSpan("test").start();
    mockTracer.scopeManager().activate(parent);
    MockSpan span = (MockSpan) TracingMessageUtils.buildFollowingSpan(message, mockTracer);
    assertEquals(span.parentId(), parent.context().spanId());
  }

}
