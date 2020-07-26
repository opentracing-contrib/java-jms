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

import javax.jms.BytesMessage;
import javax.jms.ConnectionMetaData;
import javax.jms.Destination;
import javax.jms.ExceptionListener;
import javax.jms.JMSConsumer;
import javax.jms.JMSContext;
import javax.jms.JMSProducer;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.QueueBrowser;
import javax.jms.StreamMessage;
import javax.jms.TemporaryQueue;
import javax.jms.TemporaryTopic;
import javax.jms.TextMessage;
import javax.jms.Topic;
import java.io.Serializable;

/**
 * Tracing decorator for JMS {@code javax.jms.JMSContext}.
 */
public class TracingJMSContext implements JMSContext {

  private final JMSContext jmsContext;
  private final Tracer tracer;

  public TracingJMSContext(JMSContext jmsContext, Tracer tracer) {
    this.jmsContext = jmsContext;
    this.tracer = tracer;
  }

  @Override
  public JMSContext createContext(int sessionMode) {
    return new TracingJMSContext(jmsContext.createContext(sessionMode), tracer);
  }

  @Override
  public JMSProducer createProducer() {
    return new TracingJMSProducer(jmsContext.createProducer(), jmsContext, tracer);
  }

  @Override
  public String getClientID() {
    return jmsContext.getClientID();
  }

  @Override
  public void setClientID(String clientID) {
    jmsContext.setClientID(clientID);
  }

  @Override
  public ConnectionMetaData getMetaData() {
    return jmsContext.getMetaData();
  }

  @Override
  public ExceptionListener getExceptionListener() {
    return jmsContext.getExceptionListener();
  }

  @Override
  public void setExceptionListener(ExceptionListener listener) {
    jmsContext.setExceptionListener(listener);
  }

  @Override
  public void start() {
    jmsContext.start();
  }

  @Override
  public void stop() {
    jmsContext.stop();
  }

  @Override
  public boolean getAutoStart() {
    return jmsContext.getAutoStart();
  }

  @Override
  public void setAutoStart(boolean autoStart) {
    jmsContext.setAutoStart(autoStart);
  }

  @Override
  public void close() {
    jmsContext.close();
  }

  @Override
  public BytesMessage createBytesMessage() {
    return jmsContext.createBytesMessage();
  }

  @Override
  public MapMessage createMapMessage() {
    return jmsContext.createMapMessage();
  }

  @Override
  public Message createMessage() {
    return jmsContext.createMessage();
  }

  @Override
  public ObjectMessage createObjectMessage() {
    return jmsContext.createObjectMessage();
  }

  @Override
  public ObjectMessage createObjectMessage(Serializable object) {
    return jmsContext.createObjectMessage(object);
  }

  @Override
  public StreamMessage createStreamMessage() {
    return jmsContext.createStreamMessage();
  }

  @Override
  public TextMessage createTextMessage() {
    return jmsContext.createTextMessage();
  }

  @Override
  public TextMessage createTextMessage(String text) {
    return jmsContext.createTextMessage(text);
  }

  @Override
  public boolean getTransacted() {
    return jmsContext.getTransacted();
  }

  @Override
  public int getSessionMode() {
    return jmsContext.getSessionMode();
  }

  @Override
  public void commit() {
    jmsContext.commit();
  }

  @Override
  public void rollback() {
    jmsContext.rollback();
  }

  @Override
  public void recover() {
    jmsContext.recover();
  }

  @Override
  public JMSConsumer createConsumer(Destination destination) {
    return new TracingJMSConsumer(jmsContext.createConsumer(destination), tracer);
  }

  @Override
  public JMSConsumer createConsumer(Destination destination, String messageSelector) {
    return new TracingJMSConsumer(jmsContext.createConsumer(destination, messageSelector), tracer);
  }

  @Override
  public JMSConsumer createConsumer(Destination destination, String messageSelector, boolean noLocal) {
    return new TracingJMSConsumer(jmsContext.createConsumer(destination, messageSelector, noLocal), tracer);
  }

  @Override
  public Queue createQueue(String queueName) {
    return jmsContext.createQueue(queueName);
  }

  @Override
  public Topic createTopic(String topicName) {
    return jmsContext.createTopic(topicName);
  }

  @Override
  public JMSConsumer createDurableConsumer(Topic topic, String name) {
    return new TracingJMSConsumer(jmsContext.createDurableConsumer(topic, name), tracer);
  }

  @Override
  public JMSConsumer createDurableConsumer(Topic topic, String name, String messageSelector, boolean noLocal) {
    return new TracingJMSConsumer(jmsContext.createDurableConsumer(topic, name, messageSelector, noLocal), tracer);
  }

  @Override
  public JMSConsumer createSharedDurableConsumer(Topic topic, String name) {
    return new TracingJMSConsumer(jmsContext.createSharedDurableConsumer(topic, name), tracer);
  }

  @Override
  public JMSConsumer createSharedDurableConsumer(Topic topic, String name, String messageSelector) {
    return new TracingJMSConsumer(jmsContext.createSharedDurableConsumer(topic, name, messageSelector), tracer);
  }

  @Override
  public JMSConsumer createSharedConsumer(Topic topic, String sharedSubscriptionName) {
    return new TracingJMSConsumer(jmsContext.createSharedConsumer(topic, sharedSubscriptionName), tracer);
  }

  @Override
  public JMSConsumer createSharedConsumer(Topic topic, String sharedSubscriptionName, String messageSelector) {
    return new TracingJMSConsumer(jmsContext.createSharedConsumer(topic, sharedSubscriptionName, messageSelector),
            tracer);
  }

  @Override
  public QueueBrowser createBrowser(Queue queue) {
    return jmsContext.createBrowser(queue);
  }

  @Override
  public QueueBrowser createBrowser(Queue queue, String messageSelector) {
    return jmsContext.createBrowser(queue, messageSelector);
  }

  @Override
  public TemporaryQueue createTemporaryQueue() {
    return jmsContext.createTemporaryQueue();
  }

  @Override
  public TemporaryTopic createTemporaryTopic() {
    return jmsContext.createTemporaryTopic();
  }

  @Override
  public void unsubscribe(String name) {
    jmsContext.unsubscribe(name);
  }

  @Override
  public void acknowledge() {
    jmsContext.acknowledge();
  }
}
