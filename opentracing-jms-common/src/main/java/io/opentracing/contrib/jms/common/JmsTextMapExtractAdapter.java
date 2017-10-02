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
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.jms.JMSException;
import javax.jms.Message;

/**
 * Helper class to extract span context from JMS message properties
 */
public class JmsTextMapExtractAdapter implements TextMap {

  private final Map<String, String> map = new HashMap<>();

  public JmsTextMapExtractAdapter(Message message) {
    try {
      Enumeration enumeration = message.getPropertyNames();
      if (enumeration != null) {
        while (enumeration.hasMoreElements()) {
          String key = (String) enumeration.nextElement();
          Object value = message.getObjectProperty(key);
          if (value instanceof String) {
            map.put(decodeDash(key), (String) value);
          }
        }
      }
    } catch (JMSException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public Iterator<Map.Entry<String, String>> iterator() {
    return map.entrySet().iterator();
  }

  @Override
  public void put(String key, String value) {
    throw new UnsupportedOperationException(
        "JmsTextMapExtractAdapter should only be used with Tracer.extract()");
  }

  /**
   * Decode dashes (encoded in {@link JmsTextMapInjectAdapter}
   */
  private String decodeDash(String key) {
    return key.replace(JmsTextMapInjectAdapter.DASH, "-");
  }
}
