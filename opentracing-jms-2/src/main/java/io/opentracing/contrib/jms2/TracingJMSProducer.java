package io.opentracing.contrib.jms2;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;

import javax.jms.CompletionListener;
import javax.jms.Destination;
import javax.jms.JMSProducer;
import javax.jms.Message;

public class TracingJMSProducer implements JMSProducer {

	@Override
	public JMSProducer clearProperties() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CompletionListener getAsync() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean getBooleanProperty(String arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public byte getByteProperty(String arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long getDeliveryDelay() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getDeliveryMode() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean getDisableMessageID() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean getDisableMessageTimestamp() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public double getDoubleProperty(String arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public float getFloatProperty(String arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getIntProperty(String arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getJMSCorrelationID() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public byte[] getJMSCorrelationIDAsBytes() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Destination getJMSReplyTo() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getJMSType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getLongProperty(String arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Object getObjectProperty(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getPriority() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Set<String> getPropertyNames() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public short getShortProperty(String arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getStringProperty(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getTimeToLive() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean propertyExists(String arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public JMSProducer send(Destination arg0, Message arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public JMSProducer send(Destination arg0, String arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public JMSProducer send(Destination arg0, Map<String, Object> arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public JMSProducer send(Destination arg0, byte[] arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public JMSProducer send(Destination arg0, Serializable arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public JMSProducer setAsync(CompletionListener arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public JMSProducer setDeliveryDelay(long arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public JMSProducer setDeliveryMode(int arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public JMSProducer setDisableMessageID(boolean arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public JMSProducer setDisableMessageTimestamp(boolean arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public JMSProducer setJMSCorrelationID(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public JMSProducer setJMSCorrelationIDAsBytes(byte[] arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public JMSProducer setJMSReplyTo(Destination arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public JMSProducer setJMSType(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public JMSProducer setPriority(int arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public JMSProducer setProperty(String arg0, boolean arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public JMSProducer setProperty(String arg0, byte arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public JMSProducer setProperty(String arg0, short arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public JMSProducer setProperty(String arg0, int arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public JMSProducer setProperty(String arg0, long arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public JMSProducer setProperty(String arg0, float arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public JMSProducer setProperty(String arg0, double arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public JMSProducer setProperty(String arg0, String arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public JMSProducer setProperty(String arg0, Object arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public JMSProducer setTimeToLive(long arg0) {
		// TODO Auto-generated method stub
		return null;
	}

}
