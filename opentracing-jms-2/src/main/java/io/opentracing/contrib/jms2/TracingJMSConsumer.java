package io.opentracing.contrib.jms2;

import javax.jms.JMSConsumer;
import javax.jms.JMSRuntimeException;
import javax.jms.Message;
import javax.jms.MessageListener;

public class TracingJMSConsumer implements JMSConsumer {

	@Override
	public void close() {
		// TODO Auto-generated method stub

	}

	@Override
	public MessageListener getMessageListener() throws JMSRuntimeException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getMessageSelector() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Message receive() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Message receive(long arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> T receiveBody(Class<T> arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> T receiveBody(Class<T> arg0, long arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> T receiveBodyNoWait(Class<T> arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Message receiveNoWait() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setMessageListener(MessageListener arg0) throws JMSRuntimeException {
		// TODO Auto-generated method stub

	}

}
