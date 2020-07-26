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

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSContext;
import javax.jms.JMSException;

/**
 * Tracing decorator for JMS {@code javax.jms.ConnectionFactory}.
 */
public class TracingConnectionFactory implements ConnectionFactory {

  private final ConnectionFactory connectionFactory;
  private final Tracer tracer;

  public TracingConnectionFactory(ConnectionFactory connectionFactory, Tracer tracer) {
    this.connectionFactory = connectionFactory;
    this.tracer = tracer;
  }

  @Override
  public Connection createConnection() throws JMSException {
    return new TracingConnection(connectionFactory.createConnection(), tracer);
  }

  @Override
  public Connection createConnection(String userName, String password) throws JMSException {
    return new TracingConnection(connectionFactory.createConnection(userName, password), tracer);
  }

  @Override
  public JMSContext createContext() {
    return new TracingJMSContext(connectionFactory.createContext(), tracer);
  }

  @Override
  public JMSContext createContext(String userName, String password) {
    return new TracingJMSContext(connectionFactory.createContext(userName, password), tracer);
  }

  @Override
  public JMSContext createContext(String userName, String password, int sessionMode) {
    return new TracingJMSContext(connectionFactory.createContext(userName, password, sessionMode), tracer);
  }

  @Override
  public JMSContext createContext(int sessionMode) {
    return new TracingJMSContext(connectionFactory.createContext(sessionMode), tracer);
  }
}
