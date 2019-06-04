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

import io.opentracing.SpanContext;
import io.opentracing.Tracer;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
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
  private final boolean proxyMessage;

  public TracingMessageConsumer(MessageConsumer messageConsumer, Tracer tracer) {
    this(messageConsumer, tracer, false);
  }

  public TracingMessageConsumer(MessageConsumer messageConsumer, Tracer tracer,
      boolean proxyMessage) {
    this.messageConsumer = messageConsumer;
    this.tracer = tracer;
    this.proxyMessage = proxyMessage;
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
    if (listener instanceof TracingMessageConsumer) {
      messageConsumer.setMessageListener(listener);
    } else {
      messageConsumer.setMessageListener(new TracingMessageListener(listener, tracer));
    }
  }

  @Override
  public Message receive() throws JMSException {
    Message message = messageConsumer.receive();
    if (proxyMessage) {
      return proxy(message, finishSpan(message));
    }
    finishSpan(message);
    return message;
  }

  @Override
  public Message receive(long timeout) throws JMSException {
    Message message = messageConsumer.receive(timeout);
    if (proxyMessage) {
      return proxy(message, finishSpan(message));
    }
    finishSpan(message);
    return message;
  }

  @Override
  public Message receiveNoWait() throws JMSException {
    Message message = messageConsumer.receiveNoWait();
    if (proxyMessage) {
      return proxy(message, finishSpan(message));
    }
    finishSpan(message);
    return message;
  }

  @Override
  public void close() throws JMSException {
    messageConsumer.close();
  }

  private SpanContext finishSpan(Message message) {
    return TracingMessageUtils.buildAndFinishChildSpan(message, tracer);
  }

  private Message proxy(final Message message, final SpanContext spanContext) {
    if (message == null) {
      return null;
    }
    final Class<?>[] interfaces = message.getClass().getInterfaces();
    Class<?>[] allInterfaces = new Class<?>[interfaces.length + 1];
    System.arraycopy(interfaces, 0, allInterfaces, 0, interfaces.length);
    allInterfaces[interfaces.length] = SpanContextContainer.class;

    return (Message) Proxy.newProxyInstance(message.getClass().getClassLoader(),
        allInterfaces,
        new InvocationHandler() {
          @Override
          public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            if (method.getName().equals("getSpanContext")) {
              return spanContext;
            }
            return method.invoke(message, args);
          }
        });
  }
}
