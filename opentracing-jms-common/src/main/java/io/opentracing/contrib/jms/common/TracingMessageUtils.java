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
package io.opentracing.contrib.jms.common;

import io.opentracing.References;
import io.opentracing.Span;
import io.opentracing.SpanContext;
import io.opentracing.Tracer;
import io.opentracing.propagation.Format;
import io.opentracing.tag.Tags;

import javax.jms.Destination;
import javax.jms.Message;

public class TracingMessageUtils {

  public static final String COMPONENT_NAME = "java-jms";
  public static final String OPERATION_NAME_SEND = "jms-send";
  public static final String OPERATION_NAME_RECEIVE = "jms-receive";
  public static final String OPERATION_NAME_ON_MESSAGE = "jms-on-message";

  /**
   * Start message consumer {@code span} and finish it.
   *
   * @param message the JMS message
   * @param tracer  the tracer
   * @return the span context
   */
  public static SpanContext startAndFinishConsumerSpan(Message message, Tracer tracer) {
    if (message == null) {
      return null;
    }
    Span span = startConsumerSpan(message, tracer, OPERATION_NAME_RECEIVE);
    span.finish();
    return span.context();
  }

  /**
   * Start message listener {@code span}.
   *
   * @param message the JMS message
   * @param tracer  the tracer
   * @return the span
   */
  public static Span startListenerSpan(Message message, Tracer tracer) {
    return startConsumerSpan(message, tracer, OPERATION_NAME_ON_MESSAGE);
  }

  /**
   * Extract {@code spanContext} from the {@code message} or an active {@code span}.
   *
   * @param message the JMS message
   * @param tracer  the tracer
   * @return an extracted span context
   */
  public static SpanContext extract(Message message, Tracer tracer) {
    SpanContext context = tracer.extract(Format.Builtin.TEXT_MAP, new JmsTextMapExtractAdapter(message));
    if (context != null && context.toTraceId() != null && context.toSpanId() != null) {
      return context;
    }
    Span span = tracer.activeSpan();
    return span != null ? span.context() : null;
  }

  /**
   * Start message producer {@code span} and inject {@code spanContext} into the {@code message}.
   *
   * @param destination the destination
   * @param message     the JMS message
   * @param tracer      the tracer
   * @return the span
   */
  public static Span startAndInjectSpan(Destination destination, Message message, Tracer tracer) {
    SpanContext context = extract(message, tracer);
    Span span = tracer.buildSpan(TracingMessageUtils.OPERATION_NAME_SEND)
            .ignoreActiveSpan()
            .withTag(Tags.SPAN_KIND.getKey(), Tags.SPAN_KIND_PRODUCER)
            .asChildOf(context)
            .start();
    SpanJmsDecorator.onRequest(destination, span);
    inject(span, message, tracer);
    return span;
  }

  /**
   * Inject {@code spanContext} into the {@code message}.
   *
   * @param span    the span
   * @param message the JMS message
   * @param tracer  the tracer
   */
  public static void inject(Span span, Message message, Tracer tracer) {
    tracer.inject(span.context(), Format.Builtin.TEXT_MAP, new JmsTextMapInjectAdapter(message));
  }

  /**
   * Start message consumer {@code span} with {@code FollowsFrom} reference type.
   *
   * @param message       the JMS message
   * @param tracer        the tracer
   * @param operationName the operation name
   * @return the span
   */
  private static Span startConsumerSpan(Message message, Tracer tracer, String operationName) {
    SpanContext context = extract(message, tracer);
    Span span = tracer.buildSpan(operationName)
            .ignoreActiveSpan()
            .withTag(Tags.SPAN_KIND.getKey(), Tags.SPAN_KIND_CONSUMER)
            .addReference(References.FOLLOWS_FROM, context)
            .start();
    SpanJmsDecorator.onResponse(message, span);
    return span;
  }
}
