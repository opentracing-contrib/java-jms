/*
 * Copyright 2017 The OpenTracing Authors
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

import io.opentracing.propagation.TextMap;
import java.util.Iterator;
import java.util.Map;
import javax.jms.JMSException;
import javax.jms.Message;

/**
 * Helper class to inject span context into JMS message properties
 */
public class JmsTextMapInjectAdapter implements TextMap {

  static final String DASH = "_$dash$_";
  private final Message message;

  public JmsTextMapInjectAdapter(Message message) {
    this.message = message;
  }

  @Override
  public Iterator<Map.Entry<String, String>> iterator() {
    throw new UnsupportedOperationException("iterator should never be used with Tracer.inject()");
  }

  @Override
  public void put(String key, String value) {
    try {
      message.setStringProperty(encodeDash(key), value);
    } catch (JMSException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Encode all dashes because JMS specification doesn't allow them in property name
   */
  private String encodeDash(String key) {
    if (key == null || key.isEmpty()) {
      return key;
    }

    return key.replace("-", DASH);
  }
}
