package io.opentracing.contrib.jms2;

import java.io.Serializable;

import javax.jms.BytesMessage;
import javax.jms.ConnectionMetaData;
import javax.jms.Destination;
import javax.jms.ExceptionListener;
import javax.jms.JMSConsumer;
import javax.jms.JMSContext;
import javax.jms.JMSProducer;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.QueueBrowser;
import javax.jms.StreamMessage;
import javax.jms.TemporaryQueue;
import javax.jms.TemporaryTopic;
import javax.jms.TextMessage;
import javax.jms.Topic;

public class TracingJMSListener implements JMSContext {

	@Override
	public void acknowledge() {
		// TODO Auto-generated method stub

	}

	@Override
	public void close() {
		// TODO Auto-generated method stub

	}

	@Override
	public void commit() {
		// TODO Auto-generated method stub

	}

	@Override
	public QueueBrowser createBrowser(Queue arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public QueueBrowser createBrowser(Queue arg0, String arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BytesMessage createBytesMessage() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public JMSConsumer createConsumer(Destination arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public JMSConsumer createConsumer(Destination arg0, String arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public JMSConsumer createConsumer(Destination arg0, String arg1, boolean arg2) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public JMSContext createContext(int arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public JMSConsumer createDurableConsumer(Topic arg0, String arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public JMSConsumer createDurableConsumer(Topic arg0, String arg1, String arg2, boolean arg3) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public MapMessage createMapMessage() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Message createMessage() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ObjectMessage createObjectMessage() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ObjectMessage createObjectMessage(Serializable arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public JMSProducer createProducer() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Queue createQueue(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public JMSConsumer createSharedConsumer(Topic arg0, String arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public JMSConsumer createSharedConsumer(Topic arg0, String arg1, String arg2) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public JMSConsumer createSharedDurableConsumer(Topic arg0, String arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public JMSConsumer createSharedDurableConsumer(Topic arg0, String arg1, String arg2) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public StreamMessage createStreamMessage() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TemporaryQueue createTemporaryQueue() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TemporaryTopic createTemporaryTopic() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TextMessage createTextMessage() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TextMessage createTextMessage(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Topic createTopic(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean getAutoStart() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getClientID() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ExceptionListener getExceptionListener() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ConnectionMetaData getMetaData() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getSessionMode() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean getTransacted() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void recover() {
		// TODO Auto-generated method stub

	}

	@Override
	public void rollback() {
		// TODO Auto-generated method stub

	}

	@Override
	public void setAutoStart(boolean arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setClientID(String arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setExceptionListener(ExceptionListener arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void start() {
		// TODO Auto-generated method stub

	}

	@Override
	public void stop() {
		// TODO Auto-generated method stub

	}

	@Override
	public void unsubscribe(String arg0) {
		// TODO Auto-generated method stub

	}

}
