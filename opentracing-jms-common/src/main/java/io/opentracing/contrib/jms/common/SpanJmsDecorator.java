package io.opentracing.contrib.jms.common;

import io.opentracing.Span;
import io.opentracing.tag.Tags;

import javax.jms.Destination;
import javax.jms.Message;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * Span decorator to add tags, logs and operation name.
 */
public class SpanJmsDecorator {


    /**
     * Decorate span before a request is made.
     *
     * @param destination destination
     * @param span        span
     */
    public static void onRequest(Destination destination, Span span) {
        Tags.COMPONENT.set(span, TracingMessageUtils.COMPONENT_NAME);
        Tags.MESSAGE_BUS_DESTINATION.set(span, destination.toString());
    }


    /**
     * Decorate span after request is made.
     *
     * @param message message
     * @param span    span
     */
    public static void onResponse(Message message, Span span) {
        Tags.COMPONENT.set(span, TracingMessageUtils.COMPONENT_NAME);
    }

    /**
     * Decorate span on an error e.g. {@link java.net.UnknownHostException} or any exception in interceptor.
     *
     * @param throwable exception
     * @param span      span
     */
    public static void onError(Throwable throwable, Span span) {
        Tags.ERROR.set(span, Boolean.TRUE);
        span.log(errorLogs(throwable));
    }

    private static Map<String, Object> errorLogs(Throwable throwable) {
        Map<String, Object> errorLogs = new HashMap<>(4);
        errorLogs.put("event", Tags.ERROR.getKey());
        errorLogs.put("error.kind", throwable.getClass().getName());
        errorLogs.put("message", throwable.getMessage());
        errorLogs.put("error.object", throwable);

        StringWriter sw = new StringWriter();
        throwable.printStackTrace(new PrintWriter(sw));
        errorLogs.put("stack", sw.toString());

        return errorLogs;
    }
}
