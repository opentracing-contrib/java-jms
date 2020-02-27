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

import io.opentracing.Scope;
import io.opentracing.mock.MockSpan;
import io.opentracing.mock.MockTracer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Component;

@Component
public class Receiver {

  @Autowired
  private MockTracer mockTracer;

  @JmsListener(destination = "TEST.FIRST")
  @SendTo("TEST.SECOND")
  public String onMessageFirst(Message message) {
    return message.getPayload().toString();
  }

  @JmsListener(destination = "TEST.SECOND")
  public void onMessageSecond(String message) {
    System.out.println(message);

    final MockSpan span = mockTracer.buildSpan("on message").start();
    try (Scope ignored = mockTracer.activateSpan(span)) {
      span.setTag("test", "test");
    } finally {
      span.finish();
    }
  }

  @JmsListener(destination = "TEST.THIRD")
  public String onMessageThird(Message message) {
    return message.getPayload().toString().toUpperCase();
  }
}
