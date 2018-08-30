package io.opentracing.contrib.jms.spring;

import io.opentracing.Tracer;
import io.opentracing.contrib.jms.common.TracingMessageListener;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.Session;

public class ExtendedTracingMessagingMessageListenerAdapter extends TracingMessagingMessageListenerAdapter {

    protected ExtendedTracingMessagingMessageListenerAdapter(Tracer tracer) {
        super(tracer);
    }

    @Override
    public void onMessage(final Message jmsMessage, final Session session) {
        TracingMessageListener listener = new TracingMessageListener(
            new MessageListener() {
                @Override
                public void onMessage(Message message) {
                    try {
                        jmsMessage.acknowledge();
                    } catch (JMSException e) {

                    }
                }
            }, tracer);
        listener.onMessage(jmsMessage);
    }

    @Override
    protected TracingMessagingMessageListenerAdapter newInstance() {
        return new ExtendedTracingMessagingMessageListenerAdapter(tracer);
    }
}
