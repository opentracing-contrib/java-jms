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
    static final String DASH = "_$dash$_";
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
            message.setStringProperty(encodeDash(key), value);
        } catch (JMSException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Encode all dashes because JMS specification doesn't allow them in property name
     */
    private String encodeDash(String key) {
        if (key == null || key.isEmpty()) {
            return key;
        }

        return key.replace("-", DASH);
    }
}
