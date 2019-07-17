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


import io.opentracing.Tracer;
import io.opentracing.contrib.jms2.TracingConnection;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
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
  protected Connection createConnection() throws JMSException {
    return new TracingConnection(super.createConnection(), tracer);
  }
}
