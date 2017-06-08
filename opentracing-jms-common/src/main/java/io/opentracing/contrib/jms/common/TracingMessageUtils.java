package io.opentracing.contrib.jms.common;


import io.opentracing.ActiveSpan;
import io.opentracing.References;
import io.opentracing.Span;
import io.opentracing.SpanContext;
import io.opentracing.Tracer;
import io.opentracing.propagation.Format;
import io.opentracing.tag.Tags;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;

public class TracingMessageUtils {

  public static final String OPERATION_NAME_SEND = "jms-send";
  public static final String OPERATION_NAME_RECEIVE = "jms-receive";
  public static final String COMPONENT_NAME = "java-jms";

  /**
   * Build following span and finish it. Should be used by consumers/listeners
   *
   * @param message JMS message
   * @param tracer Tracer
   * @return child span
   */
  public static ActiveSpan buildAndFinishChildSpan(Message message, Tracer tracer) {

    ActiveSpan child = buildFollowingSpan(message, tracer);
    if (child != null) {
      child.close();
    }
    return child;
  }

  /**
   * It is used by consumers only
   */
  static ActiveSpan buildFollowingSpan(Message message, Tracer tracer) {
    SpanContext context = extract(message, tracer);

    if (context != null) {

      Tracer.SpanBuilder spanBuilder = tracer.buildSpan(OPERATION_NAME_RECEIVE)
          .ignoreActiveSpan()
          .withTag(Tags.SPAN_KIND.getKey(), Tags.SPAN_KIND_CONSUMER);

      spanBuilder.addReference(References.FOLLOWS_FROM, context);

      ActiveSpan span = spanBuilder.startActive();

      SpanJmsDecorator.onResponse(message, span);

      return span;
    }

    return null;
  }

  /**
   * Extract span context from JMS message properties or active span
   *
   * @param message JMS message
   * @param tracer Tracer
   * @return extracted span context
   */
  public static SpanContext extract(Message message, Tracer tracer) {
    SpanContext spanContext = tracer
        .extract(Format.Builtin.TEXT_MAP, new JmsTextMapExtractAdapter(message));
    if (spanContext != null) {
      return spanContext;
    }

    ActiveSpan span = tracer.activeSpan();
    if (span != null) {
      return span.context();
    }
    return null;
  }

  /**
   * Inject span context to JMS message properties
   *
   * @param span span
   * @param message JMS message
   */
  public static void inject(Span span, Message message, Tracer tracer) {
    tracer.inject(span.context(), Format.Builtin.TEXT_MAP, new JmsTextMapInjectAdapter(message));
  }

  /**
   * Build span and inject. Should be used by producers.
   *
   * @param message JMS message
   * @return span
   */
  public static Span buildAndInjectSpan(Destination destination, final Message message,
      Tracer tracer)
      throws JMSException {
    Tracer.SpanBuilder spanBuilder = tracer.buildSpan(TracingMessageUtils.OPERATION_NAME_SEND)
        .ignoreActiveSpan()
        .withTag(Tags.SPAN_KIND.getKey(), Tags.SPAN_KIND_PRODUCER);

    SpanContext parent = TracingMessageUtils.extract(message, tracer);

    if (parent != null) {
      spanBuilder.asChildOf(parent);
    }

    Span span = spanBuilder.startManual();

    SpanJmsDecorator.onRequest(destination, span);

    TracingMessageUtils.inject(span, message, tracer);
    return span;
  }
}
