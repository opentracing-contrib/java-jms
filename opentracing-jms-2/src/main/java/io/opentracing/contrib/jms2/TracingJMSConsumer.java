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

import io.opentracing.Tracer;
import io.opentracing.contrib.jms.common.TracingMessageListener;

import javax.jms.JMSConsumer;
import javax.jms.JMSRuntimeException;
import javax.jms.Message;
import javax.jms.MessageListener;

/**
 * Tracing decorator for JMS {@code javax.jms.JMSConsumer}.
 */
public class TracingJMSConsumer implements JMSConsumer {

  private final JMSConsumer jmsConsumer;
  private final Tracer tracer;

  public TracingJMSConsumer(JMSConsumer jmsConsumer, Tracer tracer) {
    this.jmsConsumer = jmsConsumer;
    this.tracer = tracer;
  }

  @Override
  public String getMessageSelector() {
    return jmsConsumer.getMessageSelector();
  }

  @Override
  public MessageListener getMessageListener() throws JMSRuntimeException {
    return jmsConsumer.getMessageListener();
  }

  @Override
  public void setMessageListener(MessageListener listener) throws JMSRuntimeException {
    jmsConsumer.setMessageListener(new TracingMessageListener(listener, tracer));
  }

  @Override
  public Message receive() {
    return jmsConsumer.receive();
  }

  @Override
  public Message receive(long timeout) {
    return jmsConsumer.receive(timeout);
  }

  @Override
  public Message receiveNoWait() {
    return jmsConsumer.receiveNoWait();
  }

  @Override
  public void close() {
    jmsConsumer.close();
  }

  @Override
  public <T> T receiveBody(Class<T> c) {
    return jmsConsumer.receiveBody(c);
  }

  @Override
  public <T> T receiveBody(Class<T> c, long timeout) {
    return jmsConsumer.receiveBody(c, timeout);
  }

  @Override
  public <T> T receiveBodyNoWait(Class<T> c) {
    return jmsConsumer.receiveBodyNoWait(c);
  }
}
