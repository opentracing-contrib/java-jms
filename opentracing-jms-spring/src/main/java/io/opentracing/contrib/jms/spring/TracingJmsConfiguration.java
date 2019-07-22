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
package io.opentracing.contrib.jms.spring;

import io.opentracing.Tracer;
import javax.jms.ConnectionFactory;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.aop.target.AbstractLazyCreationTargetSource;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.JmsListenerConfigurer;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.support.converter.MessageConverter;

@Configuration
public class TracingJmsConfiguration {

  private final ObjectProvider<MessageConverter> messageConverter;
  
  @Value("${io.opentracing.contrib.jms.common.tracingMessageListener.traceInLog:false}")
  private boolean traceInLog;

  public TracingJmsConfiguration(ObjectProvider<MessageConverter> messageConverter) {
    this.messageConverter = messageConverter;
  }

  @Bean
  public TracingMessagingMessageListenerAdapter createTracingMessagingMessageListenerAdapter(
      Tracer tracer) {
    return new TracingMessagingMessageListenerAdapter(tracer,traceInLog);
  }

  @Bean
  public TracingJmsListenerEndpointRegistry createTracingJmsListenerEndpointRegistry(
      TracingMessagingMessageListenerAdapter listenerAdapter) {
    return new TracingJmsListenerEndpointRegistry(listenerAdapter);
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
    JmsTemplate ret = new TracingJmsTemplate(connectionFactory, tracer,traceInLog);
    MessageConverter mc = messageConverter.getIfAvailable();
    if (mc != null) {
      ret.setMessageConverter(mc);
    }
    return ret;
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
