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
import io.opentracing.contrib.jms.TracingMessageProducer;
import io.opentracing.contrib.jms.common.TracingMessageConsumer;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;
import org.springframework.jms.core.JmsTemplate;

// Decorator for Spring JmsTemplate
public class TracingJmsTemplate extends JmsTemplate {

  private final Tracer tracer;

  public TracingJmsTemplate(Tracer tracer) {
    this.tracer = tracer;
  }

  public TracingJmsTemplate(ConnectionFactory connectionFactory, Tracer tracer) {
    super(connectionFactory);
    this.tracer = tracer;
  }

  @Override
  protected MessageProducer createProducer(Session session, Destination destination)
      throws JMSException {
    return new TracingMessageProducer(super.createProducer(session, destination), tracer);
  }

  @Override
  protected MessageConsumer createConsumer(Session session, Destination destination,
      String messageSelector)
      throws JMSException {
    return new TracingMessageConsumer(super.createConsumer(session, destination, messageSelector),
        tracer);
  }
}
