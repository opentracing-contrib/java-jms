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
            // TODO: decide what is the best approach for this mismatch: JMS doesn't allow dashes in the key name
            message.setStringProperty(cleanForJms(key), value);
        } catch (JMSException e) {
            throw new RuntimeException(e);
        }
    }

    private String cleanForJms(String key) {
        if (key == null || key.isEmpty()) {
            return key;
        }
        return key.replaceAll("-", "");
    }
}
