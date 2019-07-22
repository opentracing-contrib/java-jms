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

import io.opentracing.Span;
import io.opentracing.Tracer;
import io.opentracing.contrib.jms.common.TracingMessageListener;
import io.opentracing.contrib.jms.common.TracingMessageUtils;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.Session;
import org.springframework.jms.listener.adapter.MessagingMessageListenerAdapter;

public class TracingMessagingMessageListenerAdapter extends MessagingMessageListenerAdapter {

  protected Tracer tracer;

  protected boolean traceInLog;

  protected TracingMessagingMessageListenerAdapter(Tracer tracer, boolean traceInLog) {
    this.tracer = tracer;
    this.traceInLog = traceInLog;
  }

  @Override
  public void onMessage(final Message jmsMessage, final Session session) throws JMSException {
    TracingMessageListener listener = new TracingMessageListener(new MessageListener() {
      @Override
      public void onMessage(Message message) {
        onMessageInternal(message, session);
      }
    }, tracer, traceInLog);
    listener.onMessage(jmsMessage);
  }

  private void onMessageInternal(Message jmsMessage, Session session) {
    try {
      super.onMessage(jmsMessage, session);
    } catch (JMSException e) {
      throw new IllegalStateException(e);
    }
  }

  @Override
  protected void sendResponse(Session session, Destination destination, Message response)
      throws JMSException {
    Span span = TracingMessageUtils.buildAndInjectSpan(destination, response, tracer);
    try {
      super.sendResponse(session, destination, response);
    } finally {
      span.finish();
    }
  }

  protected TracingMessagingMessageListenerAdapter newInstance() {
    return new TracingMessagingMessageListenerAdapter(tracer, traceInLog);
  }
}
