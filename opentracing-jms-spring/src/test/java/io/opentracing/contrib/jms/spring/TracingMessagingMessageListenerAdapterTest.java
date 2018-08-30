package io.opentracing.contrib.jms.spring;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import javax.jms.JMSException;
import javax.jms.Message;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {TestConfiguration.class})
public class TracingMessagingMessageListenerAdapterTest {

    @Autowired
    private TracingMessagingMessageListenerAdapter adapter;

    @Mock
    private Message jmsMessage;

    @Before
    public void before() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testExtendedAdapterCreatesCorrectInstance() {
        TracingMessagingMessageListenerAdapter instance = adapter.newInstance();
        Assert.assertTrue(instance instanceof ExtendedTracingMessagingMessageListenerAdapter);
    }

    @Test
    public void testExtendedAdapterHandlesMessage() throws JMSException {
        adapter.onMessage(jmsMessage, null);
        Mockito.verify(jmsMessage, Mockito.times(1)).acknowledge();
    }
}
