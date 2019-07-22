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

import io.opentracing.Scope;
import io.opentracing.Span;
import io.opentracing.Tracer;
import javax.jms.Message;
import javax.jms.MessageListener;
import org.slf4j.MDC;

/**
 * Tracing decorator for JMS MessageListener
 */
public class TracingMessageListener implements MessageListener {

  private final MessageListener messageListener;
  private final Tracer tracer;
  private final boolean traceInLog;

  public TracingMessageListener(MessageListener messageListener, Tracer tracer,
      boolean traceInLog) {
    this.messageListener = messageListener;
    this.tracer = tracer;
    this.traceInLog = traceInLog;
  }

  @Override
  public void onMessage(Message message) {
    Span span = TracingMessageUtils.buildFollowingSpan(message, tracer);
    if (traceInLog) {
      if (span != null) {
        MDC.put("spanId", span.context().toSpanId());
        MDC.put("traceId", span.context().toTraceId());
      }
    }
    try (Scope ignored = tracer.activateSpan(span)) {
      if (messageListener != null) {
        messageListener.onMessage(message);
      }
    } finally {
      span.finish();
      if (traceInLog) {
        MDC.remove("spanId");
        MDC.remove("traceId");
      }
    }

  }

}
