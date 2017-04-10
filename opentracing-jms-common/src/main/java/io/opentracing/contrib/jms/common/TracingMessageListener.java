package io.opentracing.contrib.jms.common;


import io.opentracing.Span;
import io.opentracing.contrib.spanmanager.DefaultSpanManager;
import io.opentracing.contrib.spanmanager.SpanManager;

import javax.jms.Message;
import javax.jms.MessageListener;

/**
 * Tracing decorator for JMS MessageListener
 */
public class TracingMessageListener implements MessageListener {
    private final MessageListener messageListener;

    public TracingMessageListener(MessageListener messageListener) {
        this.messageListener = messageListener;
    }

    @Override
    public void onMessage(Message message) {
        Span span = TracingMessageUtils.buildFollowingSpan(message);
        SpanManager.ManagedSpan managedSpan = null;
        if (span != null) {
            managedSpan = DefaultSpanManager.getInstance().activate(span);
        }

        try {
            if (messageListener != null) {
                messageListener.onMessage(message);
            }
        } finally {
            if (span != null) {
                span.finish();
                managedSpan.deactivate();
            }
        }

    }
}
