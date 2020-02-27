/*
 * Copyright 2017-2020 The OpenTracing Authors
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

import javax.jms.JMSException;
import javax.jms.Message;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@ActiveProfiles("extended")
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
