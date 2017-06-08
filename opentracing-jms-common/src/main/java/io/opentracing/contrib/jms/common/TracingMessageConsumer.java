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


