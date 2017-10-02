/*
 * Copyright 2017 The OpenTracing Authors
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


import io.opentracing.Tracer;
import io.opentracing.contrib.jms.common.TracingMessageUtils;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import org.springframework.jms.support.converter.MessageConversionException;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.jms.support.converter.SimpleMessageConverter;

/**
 * Decorator for Spring MessageConverter
 */
public class TracingMessageConverter implements MessageConverter {

  private final MessageConverter messageConverter;
  private final SimpleMessageConverter simpleMessageConverter = new SimpleMessageConverter();
  private final Tracer tracer;

  public TracingMessageConverter(MessageConverter messageConverter, Tracer tracer) {
    this.messageConverter = messageConverter;
    this.tracer = tracer;
  }

  @Override
  public Message toMessage(Object object, Session session)
      throws JMSException, MessageConversionException {
    if (messageConverter != null) {
      return messageConverter.toMessage(object, session);
    }
    return simpleMessageConverter.toMessage(object, session);
  }

  @Override
  public Object fromMessage(Message message) throws JMSException, MessageConversionException {
    TracingMessageUtils.buildAndFinishChildSpan(message, tracer);
    if (messageConverter != null) {
      return messageConverter.fromMessage(message);
    }
    return simpleMessageConverter.fromMessage(message);
  }
}

