/*
 * Copyright 2017-2018 The OpenTracing Authors
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

import io.opentracing.Tracer;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;

/**
 * Tracing decorator for JMS MessageConsumer
 */
public class TracingMessageConsumer implements MessageConsumer {

  private final MessageConsumer messageConsumer;
  private final Tracer tracer;

  public TracingMessageConsumer(MessageConsumer messageConsumer, Tracer tracer) {
    this.messageConsumer = messageConsumer;
    this.tracer = tracer;
  }

  @Override
  public String getMessageSelector() throws JMSException {
    return messageConsumer.getMessageSelector();
  }

  @Override
  public MessageListener getMessageListener() throws JMSException {
    return messageConsumer.getMessageListener();
  }

  @Override
  public void setMessageListener(MessageListener listener) throws JMSException {
    messageConsumer.setMessageListener(listener);
  }

  @Override
  public Message receive() throws JMSException {
    Message message = messageConsumer.receive();
    finishSpan(message);
    return message;
  }

  @Override
  public Message receive(long timeout) throws JMSException {
    Message message = messageConsumer.receive(timeout);
    finishSpan(message);
    return message;
  }

  @Override
  public Message receiveNoWait() throws JMSException {
    Message message = messageConsumer.receiveNoWait();
    finishSpan(message);
    return message;
  }

  @Override
  public void close() throws JMSException {
    messageConsumer.close();
  }

  private void finishSpan(Message message) throws JMSException {
    TracingMessageUtils.buildAndFinishChildSpan(message, tracer);
  }
}


