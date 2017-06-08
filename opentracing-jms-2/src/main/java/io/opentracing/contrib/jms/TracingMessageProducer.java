package io.opentracing.contrib.jms;


import io.opentracing.Span;
import io.opentracing.Tracer;
import io.opentracing.contrib.jms.common.SpanJmsDecorator;
import io.opentracing.contrib.jms.common.TracingMessageUtils;
import javax.jms.CompletionListener;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageProducer;

/**
 * Tracing decorator for JMS MessageProducer
 */
public class TracingMessageProducer implements MessageProducer {

  private final MessageProducer messageProducer;
  private final Tracer tracer;

  public TracingMessageProducer(MessageProducer messageProducer, Tracer tracer) {
    this.messageProducer = messageProducer;
    this.tracer = tracer;
  }

  @Override
  public boolean getDisableMessageID() throws JMSException {
    return messageProducer.getDisableMessageID();
  }

  @Override
  public void setDisableMessageID(boolean value) throws JMSException {
    messageProducer.setDisableMessageID(value);
  }

  @Override
  public boolean getDisableMessageTimestamp() throws JMSException {
    return messageProducer.getDisableMessageTimestamp();
  }

  @Override
  public void setDisableMessageTimestamp(boolean value) throws JMSException {
    messageProducer.setDisableMessageTimestamp(value);
  }

  @Override
  public int getDeliveryMode() throws JMSException {
    return messageProducer.getDeliveryMode();
  }

  @Override
  public void setDeliveryMode(int deliveryMode) throws JMSException {
    messageProducer.setDeliveryMode(deliveryMode);
  }

  @Override
  public int getPriority() throws JMSException {
    return messageProducer.getPriority();
  }

  @Override
  public void setPriority(int defaultPriority) throws JMSException {
    messageProducer.setPriority(defaultPriority);
  }

  @Override
  public long getTimeToLive() throws JMSException {
    return messageProducer.getTimeToLive();
  }

  @Override
  public void setTimeToLive(long timeToLive) throws JMSException {
    messageProducer.setTimeToLive(timeToLive);
  }

  @Override
  public long getDeliveryDelay() throws JMSException {
    return messageProducer.getDeliveryDelay();
  }

  @Override
  public void setDeliveryDelay(long deliveryDelay) throws JMSException {
    messageProducer.setDeliveryDelay(deliveryDelay);
  }

  @Override
  public Destination getDestination() throws JMSException {
    return messageProducer.getDestination();
  }

  @Override
  public void close() throws JMSException {
    messageProducer.close();
  }

  @Override
  public void send(Message message) throws JMSException {
    Span span = TracingMessageUtils.buildAndInjectSpan(getDestination(), message, tracer);
    try {
      messageProducer.send(message);
    } catch (Throwable e) {
      SpanJmsDecorator.onError(e, span);
      throw e;
    } finally {
      span.finish();
    }

  }

  @Override
  public void send(Message message, int deliveryMode, int priority, long timeToLive)
      throws JMSException {
    Span span = TracingMessageUtils.buildAndInjectSpan(getDestination(), message, tracer);
    try {
      messageProducer.send(message, deliveryMode, priority, timeToLive);
    } catch (Throwable e) {
      SpanJmsDecorator.onError(e, span);
      throw e;
    } finally {
      span.finish();
    }
  }

  @Override
  public void send(Destination destination, Message message) throws JMSException {
    Span span = TracingMessageUtils.buildAndInjectSpan(destination, message, tracer);
    try {
      messageProducer.send(destination, message);
    } catch (Throwable e) {
      SpanJmsDecorator.onError(e, span);
      throw e;
    } finally {
      span.finish();
    }
  }

  @Override
  public void send(Destination destination, Message message, int deliveryMode, int priority,
      long timeToLive) throws JMSException {
    Span span = TracingMessageUtils.buildAndInjectSpan(destination, message, tracer);
    try {
      messageProducer.send(destination, message, deliveryMode, priority, timeToLive);
    } catch (Throwable e) {
      SpanJmsDecorator.onError(e, span);
      throw e;
    } finally {
      span.finish();
    }
  }

  @Override
  public void send(Message message, CompletionListener completionListener) throws JMSException {
    Span span = TracingMessageUtils.buildAndInjectSpan(getDestination(), message, tracer);
    messageProducer.send(message, new TracingCompletionListener(span, completionListener));
  }

  @Override
  public void send(Message message, int deliveryMode, int priority, long timeToLive,
      CompletionListener completionListener) throws JMSException {
    Span span = TracingMessageUtils.buildAndInjectSpan(getDestination(), message, tracer);
    messageProducer.send(message, deliveryMode, priority, timeToLive,
        new TracingCompletionListener(span, completionListener));
  }

  @Override
  public void send(Destination destination, Message message, CompletionListener completionListener)
      throws JMSException {
    Span span = TracingMessageUtils.buildAndInjectSpan(destination, message, tracer);
    messageProducer
        .send(destination, message, new TracingCompletionListener(span, completionListener));
  }

  @Override
  public void send(Destination destination, Message message, int deliveryMode, int priority,
      long timeToLive, CompletionListener completionListener) throws JMSException {
    Span span = TracingMessageUtils.buildAndInjectSpan(destination, message, tracer);
    messageProducer.send(destination, message, deliveryMode, priority, timeToLive,
        new TracingCompletionListener(span, completionListener));
  }
}
