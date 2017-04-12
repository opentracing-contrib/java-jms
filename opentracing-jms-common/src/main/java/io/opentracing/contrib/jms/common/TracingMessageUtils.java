package io.opentracing.contrib.jms.common;


import io.opentracing.References;
import io.opentracing.Span;
import io.opentracing.SpanContext;
import io.opentracing.Tracer;
import io.opentracing.contrib.spanmanager.DefaultSpanManager;
import io.opentracing.propagation.Format;
import io.opentracing.tag.Tags;
import io.opentracing.util.GlobalTracer;

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
     * @return child span
     */
    public static Span buildAndFinishChildSpan(Message message) {

        Span child = buildFollowingSpan(message);
        if (child != null) {
            child.finish();
        }
        return child;
    }

    /**
     * It is used by consumers only
     */
    static Span buildFollowingSpan(Message message) {
        SpanContext context = extract(message);

        if (context != null) {

            Tracer.SpanBuilder spanBuilder = GlobalTracer.get().buildSpan(OPERATION_NAME_RECEIVE)
                    .withTag(Tags.SPAN_KIND.getKey(), Tags.SPAN_KIND_CONSUMER);

            spanBuilder.addReference(References.FOLLOWS_FROM, context);

            Span span = spanBuilder.start();

            SpanJmsDecorator.onResponse(message, span);

            return span;
        }

        return null;
    }

    /**
     * Extract span context from JMS message properties or Span Manager
     *
     * @param message JMS message
     * @return extracted span context
     */
    public static SpanContext extract(Message message) {
        SpanContext spanContext = GlobalTracer.get().extract(Format.Builtin.TEXT_MAP, new JmsTextMapExtractAdapter(message));
        if (spanContext != null) {
            return spanContext;
        }

        Span span = DefaultSpanManager.getInstance().current().getSpan();
        if (span != null) {
            return span.context();
        }
        return null;
    }

    /**
     * Inject span context to JMS message properties
     *
     * @param span    span
     * @param message JMS message
     */
    public static void inject(Span span, Message message) {
        GlobalTracer.get().inject(span.context(), Format.Builtin.TEXT_MAP, new JmsTextMapInjectAdapter(message));
    }

    /**
     * Build span and inject. Should be used by producers.
     *
     * @param message JMS message
     * @return span
     * @throws JMSException
     */
    public static Span buildAndInjectSpan(Destination destination, final Message message) throws JMSException {
        Tracer tracer = GlobalTracer.get();

        Tracer.SpanBuilder spanBuilder = tracer.buildSpan(TracingMessageUtils.OPERATION_NAME_SEND)
                .withTag(Tags.SPAN_KIND.getKey(), Tags.SPAN_KIND_PRODUCER);

        SpanContext parent = TracingMessageUtils.extract(message);

        if (parent != null) {
            spanBuilder.asChildOf(parent);
        }

        Span span = spanBuilder.start();

        SpanJmsDecorator.onRequest(destination, span);

        TracingMessageUtils.inject(span, message);
        return span;
    }
}
