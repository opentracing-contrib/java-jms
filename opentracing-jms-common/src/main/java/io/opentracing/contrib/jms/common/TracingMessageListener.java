package io.opentracing.contrib.jms.common;


import io.opentracing.ActiveSpan;
import io.opentracing.Tracer;
import javax.jms.Message;
import javax.jms.MessageListener;

/**
 * Tracing decorator for JMS MessageListener
 */
public class TracingMessageListener implements MessageListener {

  private final MessageListener messageListener;
  private final Tracer tracer;

  public TracingMessageListener(MessageListener messageListener, Tracer tracer) {
    this.messageListener = messageListener;
    this.tracer = tracer;
  }

  @Override
  public void onMessage(Message message) {
    ActiveSpan span = TracingMessageUtils.buildFollowingSpan(message, tracer);

    try {
      if (messageListener != null) {
        messageListener.onMessage(message);
      }
    } finally {
      if (span != null) {
        span.close();
      }
    }

  }
}
