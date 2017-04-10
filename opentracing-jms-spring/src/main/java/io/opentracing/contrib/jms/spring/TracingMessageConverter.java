package io.opentracing.contrib.jms.spring;


import io.opentracing.contrib.jms.common.TracingMessageUtils;
import org.springframework.jms.support.converter.MessageConversionException;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.jms.support.converter.SimpleMessageConverter;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

/**
 * Decorator for Spring MessageConverter
 */
public class TracingMessageConverter implements MessageConverter {
    private final MessageConverter messageConverter;
    private final SimpleMessageConverter simpleMessageConverter = new SimpleMessageConverter();

    public TracingMessageConverter(MessageConverter messageConverter) {
        this.messageConverter = messageConverter;
    }

    @Override
    public Message toMessage(Object object, Session session) throws JMSException, MessageConversionException {
        if (messageConverter != null) {
            return messageConverter.toMessage(object, session);
        }
        return simpleMessageConverter.toMessage(object, session);
    }

    @Override
    public Object fromMessage(Message message) throws JMSException, MessageConversionException {
        TracingMessageUtils.buildAndFinishChildSpan(message);
        if (messageConverter != null) {
            return messageConverter.fromMessage(message);
        }
        return simpleMessageConverter.fromMessage(message);
    }
}

