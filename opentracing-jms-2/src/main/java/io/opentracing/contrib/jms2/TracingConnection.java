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
package io.opentracing.contrib.jms2;

import io.opentracing.Tracer;
import javax.jms.Connection;
import javax.jms.ConnectionConsumer;
import javax.jms.ConnectionMetaData;
import javax.jms.Destination;
import javax.jms.ExceptionListener;
import javax.jms.JMSException;
import javax.jms.ServerSessionPool;
import javax.jms.Session;
import javax.jms.Topic;

public class TracingConnection implements Connection {
  private final Connection connection;
  private final Tracer tracer;
  private final boolean traceInLog;

  public TracingConnection(Connection connection, Tracer tracer, boolean traceInLog) {
    this.connection = connection;
    this.tracer = tracer;
    this.traceInLog = traceInLog;
  }

  @Override
  public Session createSession(boolean transacted, int acknowledgeMode) throws JMSException {
    return new TracingSession(connection.createSession(transacted, acknowledgeMode), tracer,traceInLog);
  }

  @Override
  public Session createSession(int sessionMode) throws JMSException {
    return new TracingSession(connection.createSession(sessionMode), tracer,traceInLog);
  }

  @Override
  public Session createSession() throws JMSException {
    return new TracingSession(connection.createSession(), tracer,traceInLog);
  }

  @Override
  public String getClientID() throws JMSException {
    return connection.getClientID();
  }

  @Override
  public void setClientID(String clientID) throws JMSException {
    connection.setClientID(clientID);
  }

  @Override
  public ConnectionMetaData getMetaData() throws JMSException {
    return connection.getMetaData();
  }

  @Override
  public ExceptionListener getExceptionListener() throws JMSException {
    return connection.getExceptionListener();
  }

  @Override
  public void setExceptionListener(ExceptionListener listener) throws JMSException {
    connection.setExceptionListener(listener);
  }

  @Override
  public void start() throws JMSException {
    connection.start();
  }

  @Override
  public void stop() throws JMSException {
    connection.stop();
  }

  @Override
  public void close() throws JMSException {
    connection.close();
  }

  @Override
  public ConnectionConsumer createConnectionConsumer(Destination destination, String messageSelector,
      ServerSessionPool sessionPool, int maxMessages) throws JMSException {
    return connection.createConnectionConsumer(destination, messageSelector, sessionPool, maxMessages);
  }

  @Override
  public ConnectionConsumer createSharedConnectionConsumer(Topic topic, String subscriptionName,
      String messageSelector, ServerSessionPool sessionPool, int maxMessages) throws JMSException {
    return connection.createSharedConnectionConsumer(topic, subscriptionName, messageSelector, sessionPool,
        maxMessages);
  }

  @Override
  public ConnectionConsumer createDurableConnectionConsumer(Topic topic, String subscriptionName,
      String messageSelector, ServerSessionPool sessionPool, int maxMessages) throws JMSException {
    return connection.createDurableConnectionConsumer(topic, subscriptionName, messageSelector, sessionPool,
        maxMessages);
  }

  @Override
  public ConnectionConsumer createSharedDurableConnectionConsumer(Topic topic, String subscriptionName,
      String messageSelector, ServerSessionPool sessionPool, int maxMessages) throws JMSException {
    return connection.createSharedDurableConnectionConsumer(topic, subscriptionName, messageSelector, sessionPool,
        maxMessages);
  }

}
