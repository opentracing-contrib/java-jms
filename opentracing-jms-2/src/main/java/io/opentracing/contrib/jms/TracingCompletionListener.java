package io.opentracing.contrib.jms;


import io.opentracing.Span;
import io.opentracing.contrib.jms.common.SpanJmsDecorator;
import javax.jms.CompletionListener;
import javax.jms.Message;

/**
 * Listener for sending messages.
 * <p>
 * If sending of the message is complete then method {@code onCompletion(Message)} is called.
 * <br/>
 * If sending of the message fails then method {@code onException(Exception)} is called.
 */
public class TracingCompletionListener implements CompletionListener {

  private final Span span;
  private final CompletionListener completionListener;

  public TracingCompletionListener(Span span, CompletionListener completionListener) {
    this.span = span;
    this.completionListener = completionListener;
  }


  @Override
  public void onCompletion(Message message) {
    try {
      completionListener.onCompletion(message);
    } finally {
      span.finish();
    }
  }

  @Override
  public void onException(Message message, Exception exception) {
    try {
      completionListener.onException(message, exception);
    } finally {
      SpanJmsDecorator.onError(exception, span);
      span.finish();
    }
  }
}
