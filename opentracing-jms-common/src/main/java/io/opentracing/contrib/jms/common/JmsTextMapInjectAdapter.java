package io.opentracing.contrib.jms.common;

import io.opentracing.propagation.TextMap;

import javax.jms.JMSException;
import javax.jms.Message;
import java.util.Iterator;
import java.util.Map;

/**
 * Helper class to inject span context into JMS message properties
 */
public class JmsTextMapInjectAdapter implements TextMap {
    private final Message message;

    public JmsTextMapInjectAdapter(Message message) {
        this.message = message;
    }

    @Override
    public Iterator<Map.Entry<String, String>> iterator() {
        throw new UnsupportedOperationException("iterator should never be used with Tracer.inject()");
    }

    @Override
    public void put(String key, String value) {
        try {
            message.setStringProperty(key, value);
        } catch (JMSException e) {
            throw new RuntimeException(e);
        }

    }
}
