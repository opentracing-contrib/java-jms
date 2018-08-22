package io.opentracing.contrib.jms2;

import io.opentracing.Span;
import io.opentracing.Tracer;
import io.opentracing.contrib.jms.common.SpanJmsDecorator;
import io.opentracing.contrib.jms.common.TracingMessageUtils;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;

import javax.jms.CompletionListener;
import javax.jms.Destination;
import javax.jms.JMSProducer;
import javax.jms.Message;

import java.lang.UnsupportedOperationException;

public class TracingJMSProducer implements JMSProducer {


    private final JMSProducer jmsProducer;
    private final Tracer tracer;

    public TracingJMSProducer(JMSProducer jmsProducer, Tracer tracer) {
        this.jmsProducer = jmsProducer;
        this.tracer = tracer;
    }

    @Override
    public JMSProducer clearProperties() {
        jmsProducer.clearProperties();
        return this;
    }

    @Override
    public CompletionListener getAsync() {
        return jmsProducer.getAsync();
    }

    @Override
    public boolean getBooleanProperty(String arg0) {
        return jmsProducer.getBooleanProperty(arg0);
    }

    @Override
    public byte getByteProperty(String arg0) {
        return jmsProducer.getByteProperty(arg0);
    }

    @Override
    public long getDeliveryDelay() {
        return jmsProducer.getDeliveryDelay();
    }

    @Override
    public int getDeliveryMode() {
        return jmsProducer.getDeliveryMode();
    }

    @Override
    public boolean getDisableMessageID() {
        return jmsProducer.getDisableMessageID();
    }

    @Override
    public boolean getDisableMessageTimestamp() {
        return jmsProducer.getDisableMessageTimestamp();
    }

    @Override
    public double getDoubleProperty(String arg0) {
        return jmsProducer.getDoubleProperty(arg0);
    }

    @Override
    public float getFloatProperty(String arg0) {
        return jmsProducer.getFloatProperty(arg0);
    }

    @Override
    public int getIntProperty(String arg0) {
        return jmsProducer.getIntProperty(arg0);
    }

    @Override
    public String getJMSCorrelationID() {
        return jmsProducer.getJMSCorrelationID();
    }

    @Override
    public byte[] getJMSCorrelationIDAsBytes() {
        return jmsProducer.getJMSCorrelationIDAsBytes();
    }

    @Override
    public Destination getJMSReplyTo() {
        return jmsProducer.getJMSReplyTo();
    }

    @Override
    public String getJMSType() {
        return jmsProducer.getJMSType();
    }

    @Override
    public long getLongProperty(String arg0) {
        return jmsProducer.getLongProperty(arg0);
    }

    @Override
    public Object getObjectProperty(String arg0) {
        return jmsProducer.getObjectProperty(arg0);
    }

    @Override
    public int getPriority() {
        return jmsProducer.getPriority();
    }

    @Override
    public Set<String> getPropertyNames() {
        return jmsProducer.getPropertyNames();
    }

    @Override
    public short getShortProperty(String arg0) {
        return jmsProducer.getShortProperty(arg0);
    }

    @Override
    public String getStringProperty(String arg0) {
        return jmsProducer.getStringProperty(arg0);
    }

    @Override
    public long getTimeToLive() {
        return jmsProducer.getTimeToLive();
    }

    @Override
    public boolean propertyExists(String arg0) {
        return jmsProducer.propertyExists(arg0);
    }

    @Override
    public JMSProducer send(Destination destination, Message message) {
        Span span = TracingMessageUtils.buildAndInjectSpan(destination, message, tracer);
        try {
            jmsProducer.send(destination, message);
        } catch (Throwable e) {
            SpanJmsDecorator.onError(e, span);
            throw e;
        } finally {
            span.finish();
        }
        return this;
    }

    @Override
    public JMSProducer send(Destination destination, String message) {
        throw new UnsupportedOperationException(
                "This send is not implemented yet");
    }

    @Override
    public JMSProducer send(Destination destination, Map<String, Object> arg1) {
        throw new UnsupportedOperationException(
                "This send is not implemented yet");
    }

    @Override
    public JMSProducer send(Destination destination, byte[] arg1) {
        throw new UnsupportedOperationException(
                "This send is not implemented yet");
    }

    @Override
    public JMSProducer send(Destination destination, Serializable arg1) {
        throw new UnsupportedOperationException(
                "This send is not implemented yet");
    }

    @Override
    public JMSProducer setAsync(CompletionListener arg0) {
        jmsProducer.setAsync(arg0);
        return this;
    }

    @Override
    public JMSProducer setDeliveryDelay(long arg0) {
        jmsProducer.setDeliveryDelay(arg0);
        return this;
    }

    @Override
    public JMSProducer setDeliveryMode(int arg0) {
        jmsProducer.setDeliveryMode(arg0);
        return this;
    }

    @Override
    public JMSProducer setDisableMessageID(boolean arg0) {
        jmsProducer.setDisableMessageID(arg0);
        return this;
    }

    @Override
    public JMSProducer setDisableMessageTimestamp(boolean arg0) {
        jmsProducer.setDisableMessageTimestamp(arg0);
        return this;
    }

    @Override
    public JMSProducer setJMSCorrelationID(String arg0) {
        jmsProducer.setJMSCorrelationID(arg0);
        return this;
    }

    @Override
    public JMSProducer setJMSCorrelationIDAsBytes(byte[] arg0) {
        jmsProducer.setJMSCorrelationIDAsBytes(arg0);
        return this;
    }

    @Override
    public JMSProducer setJMSReplyTo(Destination destination) {
        jmsProducer.setJMSReplyTo(destination);
        return this;
    }

    @Override
    public JMSProducer setJMSType(String arg0) {
        jmsProducer.setJMSType(arg0);
        return this;
    }

    @Override
    public JMSProducer setPriority(int arg0) {
        jmsProducer.setPriority(arg0);
        return this;
    }

    @Override
    public JMSProducer setProperty(String arg0, boolean arg1) {
        jmsProducer.setProperty(arg0, arg1);
        return this;
    }

    @Override
    public JMSProducer setProperty(String arg0, byte arg1) {
        jmsProducer.setProperty(arg0, arg1);
        return this;
    }

    @Override
    public JMSProducer setProperty(String arg0, short arg1) {
        jmsProducer.setProperty(arg0, arg1);
        return this;
    }

    @Override
    public JMSProducer setProperty(String arg0, int arg1) {
        jmsProducer.setProperty(arg0, arg1);
        return this;
    }

    @Override
    public JMSProducer setProperty(String arg0, long arg1) {
        jmsProducer.setProperty(arg0, arg1);
        return this;
    }

    @Override
    public JMSProducer setProperty(String arg0, float arg1) {
        jmsProducer.setProperty(arg0, arg1);
        return this;
    }

    @Override
    public JMSProducer setProperty(String arg0, double arg1) {
        jmsProducer.setProperty(arg0, arg1);
        return this;
    }

    @Override
    public JMSProducer setProperty(String arg0, String arg1) {
        jmsProducer.setProperty(arg0, arg1);
        return this;
    }

    @Override
    public JMSProducer setProperty(String arg0, Object arg1) {
        jmsProducer.setProperty(arg0, arg1);
        return this;
    }

    @Override
    public JMSProducer setTimeToLive(long arg0) {
        jmsProducer.setTimeToLive(arg0);
        return this;
    }

}
