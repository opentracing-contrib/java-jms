package io.opentracing.contrib.jms.common;

import org.apache.activemq.command.ActiveMQTextMessage;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import javax.jms.JMSException;

import static org.junit.Assert.assertEquals;


public class JmsTextMapInjectAdapterTest {
    private ActiveMQTextMessage message;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Before
    public void before() {
        message = new ActiveMQTextMessage();
    }

    @Test
    public void cannotGetIterator() {
        JmsTextMapInjectAdapter adapter = new JmsTextMapInjectAdapter(message);
        thrown.expect(UnsupportedOperationException.class);
        adapter.iterator();
    }

    @Test
    public void putProperties() throws JMSException {
        JmsTextMapInjectAdapter adapter = new JmsTextMapInjectAdapter(message);
        adapter.put("key1", "value1");
        adapter.put("key2", "value2");
        adapter.put("key1", "value3");
        assertEquals("value3", message.getStringProperty("key1"));
        assertEquals("value2", message.getStringProperty("key2"));
    }

    @Test
    public void propertyWithDash() throws JMSException {
        JmsTextMapInjectAdapter adapter = new JmsTextMapInjectAdapter(message);
        adapter.put("key-1", "value1");
        assertEquals("value1", message.getStringProperty("key1"));
    }

}