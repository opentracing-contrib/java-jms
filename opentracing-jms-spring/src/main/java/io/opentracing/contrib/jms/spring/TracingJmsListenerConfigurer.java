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

import org.springframework.jms.annotation.JmsListenerConfigurer;
import org.springframework.jms.config.JmsListenerEndpointRegistrar;

public class TracingJmsListenerConfigurer implements JmsListenerConfigurer {

  private final TracingJmsListenerEndpointRegistry registry;

  public TracingJmsListenerConfigurer(TracingJmsListenerEndpointRegistry registry) {
    this.registry = registry;
  }

  @Override
  public void configureJmsListeners(JmsListenerEndpointRegistrar registrar) {
    registry.setRegistrar(registrar);
    registrar.setEndpointRegistry(registry);
  }

}
