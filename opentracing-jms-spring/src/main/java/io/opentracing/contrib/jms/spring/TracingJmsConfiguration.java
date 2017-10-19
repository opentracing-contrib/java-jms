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
package io.opentracing.contrib.jms.spring;

import io.opentracing.Tracer;
import javax.jms.ConnectionFactory;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.aop.target.AbstractLazyCreationTargetSource;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.JmsListenerConfigurer;
import org.springframework.jms.core.JmsTemplate;

@Configuration
public class TracingJmsConfiguration {

  @Bean
  public TracingJmsListenerEndpointRegistry createTracingJmsListenerEndpointRegistry(
      Tracer tracer) {
    return new TracingJmsListenerEndpointRegistry(tracer);
  }

  @Bean
  public JmsListenerConfigurer createTracingJmsListenerConfigurer(
      TracingJmsListenerEndpointRegistry registry) {
    return new TracingJmsListenerConfigurer(registry);
  }

  @Bean
  public JmsTemplate jmsTemplate(BeanFactory beanFactory, Tracer tracer) {
    // we create lazy proxy, to avoid dependency and config order
    // if JMS is used, and ConnectionFactory bean is not present,
    // it will throw an error on first use, so imo, we should be all good
    ConnectionFactory connectionFactory = createProxy(beanFactory);
    return new TracingJmsTemplate(connectionFactory, tracer);
  }

  private ConnectionFactory createProxy(final BeanFactory beanFactory) {
    return (ConnectionFactory) ProxyFactory.getProxy(new AbstractLazyCreationTargetSource() {
      @Override
      public synchronized Class<?> getTargetClass() {
        return ConnectionFactory.class;
      }

      @Override
      protected Object createObject() throws Exception {
        return beanFactory.getBean(ConnectionFactory.class);
      }
    });
  }
}
