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
package io.opentracing.contrib.jms.common;

import static io.opentracing.contrib.jms.common.JmsTextMapInjectAdapter.DASH;
import static org.junit.Assert.assertEquals;
import javax.jms.JMSException;
import org.apache.activemq.command.ActiveMQTextMessage;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


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
    assertEquals("value1", message.getStringProperty("key" + DASH + "1"));

    adapter.put("-key-1-2-", "value2");
    assertEquals("value2",
        message.getStringProperty(DASH + "key" + DASH + "1" + DASH + "2" + DASH));
  }
}
