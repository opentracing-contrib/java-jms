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
import static org.junit.Assert.assertFalse;
import java.util.Iterator;
import java.util.Map;
import javax.jms.JMSException;
import org.apache.activemq.command.ActiveMQTextMessage;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


public class JmsTextMapExtractAdapterTest {

  private ActiveMQTextMessage message;

  @Rule
  public ExpectedException thrown = ExpectedException.none();

  @Before
  public void before() {
    message = new ActiveMQTextMessage();
  }

  @Test
  public void cannotPut() {
    JmsTextMapExtractAdapter adapter = new JmsTextMapExtractAdapter(message);
    thrown.expect(UnsupportedOperationException.class);
    adapter.put("one", "two");
  }

  @Test
  public void noProperties() {
    JmsTextMapExtractAdapter adapter = new JmsTextMapExtractAdapter(message);
    Iterator<Map.Entry<String, String>> iterator = adapter.iterator();
    assertFalse(iterator.hasNext());
  }

  @Test
  public void oneProperty() throws JMSException {
    message.setStringProperty("key", "value");
    JmsTextMapExtractAdapter adapter = new JmsTextMapExtractAdapter(message);
    Iterator<Map.Entry<String, String>> iterator = adapter.iterator();
    Map.Entry<String, String> entry = iterator.next();
    assertEquals("key", entry.getKey());
    assertEquals("value", entry.getValue());
  }

  @Test
  public void propertyWithDash() throws JMSException {
    message.setStringProperty(DASH + "key" + DASH + "1" + DASH, "value1");
    JmsTextMapExtractAdapter adapter = new JmsTextMapExtractAdapter(message);
    Iterator<Map.Entry<String, String>> iterator = adapter.iterator();
    Map.Entry<String, String> entry = iterator.next();
    assertEquals("-key-1-", entry.getKey());
    assertEquals("value1", entry.getValue());
  }

}
