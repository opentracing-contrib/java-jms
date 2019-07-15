/*
 * Copyright 2017-2019 The OpenTracing Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package io.opentracing.contrib.jms.spring;

import io.opentracing.Tracer;
import io.opentracing.contrib.jms.TracingMessageProducer;
import io.opentracing.contrib.jms.common.TracingMessageConsumer;
import io.opentracing.util.GlobalTracer;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TemporaryQueue;

import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.jms.support.JmsUtils;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

// Decorator for Spring JmsTemplate
public class TracingJmsTemplate extends JmsTemplate {

	private final Tracer tracer;

	/**
	 * GlobalTracer is used to get tracer
	 */
	public TracingJmsTemplate() {
		this(GlobalTracer.get());
	}

	public TracingJmsTemplate(Tracer tracer) {
		this.tracer = tracer;
	}

	/**
	 * GlobalTracer is used to get tracer
	 */
	public TracingJmsTemplate(ConnectionFactory connectionFactory) {
		this(connectionFactory, GlobalTracer.get());
	}

	public TracingJmsTemplate(ConnectionFactory connectionFactory, Tracer tracer) {
		super(connectionFactory);
		this.tracer = tracer;
	}

	@Override
	protected MessageProducer createProducer(Session session, Destination destination) throws JMSException {

		return new TracingMessageProducer(super.createProducer(session, destination), tracer);
	}

	@Override
	protected MessageConsumer createConsumer(Session session, Destination destination, String messageSelector)
			throws JMSException {
		return new TracingMessageConsumer(super.createConsumer(session, destination, messageSelector), tracer);
	}

	@Nullable
	protected Message doSendAndReceive(Session session, Destination destination, MessageCreator messageCreator)
			throws JMSException {

		Assert.notNull(messageCreator, "MessageCreator must not be null");
		TemporaryQueue responseQueue = null;
		MessageProducer producer = null;
		MessageConsumer consumer = null;
		try {
			Message requestMessage = messageCreator.createMessage(session);
			responseQueue = session.createTemporaryQueue();
			producer = new TracingMessageProducer(session.createProducer(destination));
			consumer = new TracingMessageConsumer(session.createConsumer(responseQueue));
			requestMessage.setJMSReplyTo(responseQueue);
			if (logger.isDebugEnabled()) {
				logger.debug("Sending created message: " + requestMessage);
			}
			doSend(producer, requestMessage);
			return receiveFromConsumer(consumer, getReceiveTimeout());
		} finally {
			JmsUtils.closeMessageConsumer(consumer);
			JmsUtils.closeMessageProducer(producer);
			if (responseQueue != null) {
				responseQueue.delete();
			}
		}
	}
}
