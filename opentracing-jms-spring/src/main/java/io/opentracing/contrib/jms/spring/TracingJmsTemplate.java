package io.opentracing.contrib.jms.spring;


import io.opentracing.contrib.jms.TracingMessageProducer;
import io.opentracing.contrib.jms.common.TracingMessageConsumer;
import org.springframework.jms.core.JmsTemplate;

import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;

// Decorator for Spring JmsTemplate
public class TracingJmsTemplate extends JmsTemplate {

    public TracingJmsTemplate() {
    }

    public TracingJmsTemplate(ConnectionFactory connectionFactory) {
        super(connectionFactory);
    }

    @Override
    protected MessageProducer createProducer(Session session, Destination destination) throws JMSException {
        return new TracingMessageProducer(super.createProducer(session, destination));
    }

    @Override
    protected MessageConsumer createConsumer(Session session, Destination destination, String messageSelector)
            throws JMSException {
        return new TracingMessageConsumer(super.createConsumer(session, destination, messageSelector));
    }
}
