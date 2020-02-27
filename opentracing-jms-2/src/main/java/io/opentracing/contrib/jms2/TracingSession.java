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
import io.opentracing.contrib.jms.common.TracingMessageConsumer;
import java.io.Serializable;
import javax.jms.BytesMessage;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.QueueBrowser;
import javax.jms.Session;
import javax.jms.StreamMessage;
import javax.jms.TemporaryQueue;
import javax.jms.TemporaryTopic;
import javax.jms.TextMessage;
import javax.jms.Topic;
import javax.jms.TopicSubscriber;

public class TracingSession implements Session {
  private final Session session;
  private final Tracer tracer;
  private final boolean traceInLog;

  public TracingSession(Session session, Tracer tracer) {
    this(session, tracer, false);
  }

  public TracingSession(Session session, Tracer tracer, boolean traceInLog) {
    this.session = session;
    this.tracer = tracer;
    this.traceInLog = traceInLog;
  }

  @Override
  public BytesMessage createBytesMessage() throws JMSException {
    return session.createBytesMessage();
  }

  @Override
  public MapMessage createMapMessage() throws JMSException {
    return session.createMapMessage();
  }

  @Override
  public Message createMessage() throws JMSException {
    return session.createMessage();
  }

  @Override
  public ObjectMessage createObjectMessage() throws JMSException {
    return session.createObjectMessage();
  }

  @Override
  public ObjectMessage createObjectMessage(Serializable object) throws JMSException {
    return session.createObjectMessage(object);
  }

  @Override
  public StreamMessage createStreamMessage() throws JMSException {
    return session.createStreamMessage();
  }

  @Override
  public TextMessage createTextMessage() throws JMSException {
    return session.createTextMessage();
  }

  @Override
  public TextMessage createTextMessage(String text) throws JMSException {
    return session.createTextMessage(text);
  }

  @Override
  public boolean getTransacted() throws JMSException {
    return session.getTransacted();
  }

  @Override
  public int getAcknowledgeMode() throws JMSException {
    return session.getAcknowledgeMode();
  }

  @Override
  public void commit() throws JMSException {
    session.commit();
  }

  @Override
  public void rollback() throws JMSException {
    session.rollback();
  }

  @Override
  public void close() throws JMSException {
    session.close();
  }

  @Override
  public void recover() throws JMSException {
    session.recover();
  }

  @Override
  public MessageListener getMessageListener() throws JMSException {
    return session.getMessageListener();
  }

  @Override
  public void setMessageListener(MessageListener listener) throws JMSException {
    session.setMessageListener(listener);
  }

  @Override
  public void run() {
    session.run();
  }

  @Override
  public MessageProducer createProducer(Destination destination) throws JMSException {
    return new TracingMessageProducer(session.createProducer(destination), tracer);
  }

  @Override
  public MessageConsumer createConsumer(Destination destination) throws JMSException {
    return new TracingMessageConsumer(session.createConsumer(destination), tracer, false,
        traceInLog);
  }

  @Override
  public MessageConsumer createConsumer(Destination destination, String messageSelector)
      throws JMSException {
    return new TracingMessageConsumer(session.createConsumer(destination, messageSelector), tracer,
        false, traceInLog);
  }

  @Override
  public MessageConsumer createConsumer(Destination destination, String messageSelector,
      boolean noLocal) throws JMSException {
    return new TracingMessageConsumer(session.createConsumer(destination, messageSelector, noLocal),
        tracer, false, traceInLog);
  }

  @Override
  public MessageConsumer createSharedConsumer(Topic topic, String sharedSubscriptionName)
      throws JMSException {
    return new TracingMessageConsumer(session.createSharedConsumer(topic, sharedSubscriptionName),
        tracer, false, traceInLog);
  }

  @Override
  public MessageConsumer createSharedConsumer(Topic topic, String sharedSubscriptionName,
      String messageSelector) throws JMSException {
    return new TracingMessageConsumer(
        session.createSharedConsumer(topic, sharedSubscriptionName, messageSelector), tracer,
        false, traceInLog);
  }

  @Override
  public Queue createQueue(String queueName) throws JMSException {
    return session.createQueue(queueName);
  }

  @Override
  public Topic createTopic(String topicName) throws JMSException {
    return session.createTopic(topicName);
  }

  @Override
  public TopicSubscriber createDurableSubscriber(Topic topic, String name) throws JMSException {
    return session.createDurableSubscriber(topic, name);
  }

  @Override
  public TopicSubscriber createDurableSubscriber(Topic topic, String name, String messageSelector,
      boolean noLocal) throws JMSException {
    return session.createDurableSubscriber(topic, name, messageSelector, noLocal);
  }

  @Override
  public MessageConsumer createDurableConsumer(Topic topic, String name) throws JMSException {
    return session.createDurableConsumer(topic, name);
  }

  @Override
  public MessageConsumer createDurableConsumer(Topic topic, String name, String messageSelector,
      boolean noLocal) throws JMSException {
    return new TracingMessageConsumer(
        session.createDurableConsumer(topic, name, messageSelector, noLocal), tracer, false,
        traceInLog);
  }

  @Override
  public MessageConsumer createSharedDurableConsumer(Topic topic, String name) throws JMSException {
    return new TracingMessageConsumer(session.createSharedDurableConsumer(topic, name), tracer,
        false, traceInLog);
  }

  @Override
  public MessageConsumer createSharedDurableConsumer(Topic topic, String name,
      String messageSelector) throws JMSException {
    return new TracingMessageConsumer(
        session.createSharedDurableConsumer(topic, name, messageSelector), tracer, false,
        traceInLog);
  }

  @Override
  public QueueBrowser createBrowser(Queue queue) throws JMSException {
    return session.createBrowser(queue);
  }

  @Override
  public QueueBrowser createBrowser(Queue queue, String messageSelector) throws JMSException {
    return session.createBrowser(queue, messageSelector);
  }

  @Override
  public TemporaryQueue createTemporaryQueue() throws JMSException {
    return session.createTemporaryQueue();
  }

  @Override
  public TemporaryTopic createTemporaryTopic() throws JMSException {
    return session.createTemporaryTopic();
  }

  @Override
  public void unsubscribe(String name) throws JMSException {
    session.unsubscribe(name);
  }
}
