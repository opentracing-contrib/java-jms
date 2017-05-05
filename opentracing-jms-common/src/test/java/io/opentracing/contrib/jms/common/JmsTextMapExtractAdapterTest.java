package io.opentracing.contrib.jms.common;

import org.apache.activemq.command.ActiveMQTextMessage;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import javax.jms.JMSException;
import java.util.Iterator;
import java.util.Map;

import static io.opentracing.contrib.jms.common.JmsTextMapInjectAdapter.DASH;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;


public class JmsTextMapExtractAdapterTest {
    private ActiveMQTextMessage message;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Before
    public void before() {
        message = new ActiveMQTextMessage();
    }

    @Test
    public void cannotPut() {
        JmsTextMapExtractAdapter adapter = new JmsTextMapExtractAdapter(message);
        thrown.expect(UnsupportedOperationException.class);
        adapter.put("one", "two");
    }

    @Test
    public void noProperties() {
        JmsTextMapExtractAdapter adapter = new JmsTextMapExtractAdapter(message);
        Iterator<Map.Entry<String, String>> iterator = adapter.iterator();
        assertFalse(iterator.hasNext());
    }

    @Test
    public void oneProperty() throws JMSException {
        message.setStringProperty("key", "value");
        JmsTextMapExtractAdapter adapter = new JmsTextMapExtractAdapter(message);
        Iterator<Map.Entry<String, String>> iterator = adapter.iterator();
        Map.Entry<String, String> entry = iterator.next();
        assertEquals("key", entry.getKey());
        assertEquals("value", entry.getValue());
    }

    @Test
    public void propertyWithDash() throws JMSException {
        message.setStringProperty(DASH + "key" + DASH + "1" + DASH, "value1");
        JmsTextMapExtractAdapter adapter = new JmsTextMapExtractAdapter(message);
        Iterator<Map.Entry<String, String>> iterator = adapter.iterator();
        Map.Entry<String, String> entry = iterator.next();
        assertEquals("-key-1-", entry.getKey());
        assertEquals("value1", entry.getValue());
    }

}