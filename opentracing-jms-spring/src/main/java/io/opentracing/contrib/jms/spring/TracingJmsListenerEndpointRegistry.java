/*
 * Copyright 2017-2018 The OpenTracing Authors
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

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.jms.config.JmsListenerContainerFactory;
import org.springframework.jms.config.JmsListenerEndpoint;
import org.springframework.jms.config.JmsListenerEndpointRegistrar;
import org.springframework.jms.config.JmsListenerEndpointRegistry;
import org.springframework.jms.config.MethodJmsListenerEndpoint;
import org.springframework.jms.listener.adapter.MessagingMessageListenerAdapter;
import org.springframework.messaging.handler.annotation.support.DefaultMessageHandlerMethodFactory;
import org.springframework.messaging.handler.annotation.support.MessageHandlerMethodFactory;

public class TracingJmsListenerEndpointRegistry extends JmsListenerEndpointRegistry implements
    BeanFactoryAware {

  private TracingMessagingMessageListenerAdapter listenerAdapter;
  private BeanFactory beanFactory;
  private JmsListenerEndpointRegistrar registrar;

  public TracingJmsListenerEndpointRegistry(TracingMessagingMessageListenerAdapter listenerAdapter) {
    this.listenerAdapter = listenerAdapter;
  }

  @Override
  public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
    this.beanFactory = beanFactory;
  }

  void setRegistrar(JmsListenerEndpointRegistrar registrar) {
    this.registrar = registrar;
  }

  @Override
  public void registerListenerContainer(JmsListenerEndpoint endpoint,
      JmsListenerContainerFactory<?> factory, boolean startImmediately) {
    if (endpoint instanceof MethodJmsListenerEndpoint) {
      endpoint = replaceMethodJmsListenerEndpoint((MethodJmsListenerEndpoint) endpoint);
    }
    super.registerListenerContainer(endpoint, factory, startImmediately);
  }

  private JmsListenerEndpoint replaceMethodJmsListenerEndpoint(MethodJmsListenerEndpoint original) {
    MethodJmsListenerEndpoint replacement = new TracingMethodJmsListenerEndpoint();

    replacement.setBean(original.getBean());
    replacement.setMethod(original.getMethod());
    replacement.setMostSpecificMethod(original.getMostSpecificMethod());
    MessageHandlerMethodFactory messageHandlerMethodFactory = registrar
        .getMessageHandlerMethodFactory();
    if (messageHandlerMethodFactory == null) {
      messageHandlerMethodFactory = createDefaultJmsHandlerMethodFactory();
    }
    replacement.setMessageHandlerMethodFactory(messageHandlerMethodFactory);
    replacement.setBeanFactory(beanFactory);
    replacement.setId(original.getId());
    replacement.setDestination(original.getDestination());
    replacement.setSelector(original.getSelector());
    replacement.setSubscription(original.getSubscription());
    replacement.setConcurrency(original.getConcurrency());

    return replacement;
  }

  private class TracingMethodJmsListenerEndpoint extends MethodJmsListenerEndpoint {

    @Override
    protected MessagingMessageListenerAdapter createMessageListenerInstance() {
      return listenerAdapter.newInstance();
    }
  }

  private MessageHandlerMethodFactory createDefaultJmsHandlerMethodFactory() {
    DefaultMessageHandlerMethodFactory defaultFactory = new DefaultMessageHandlerMethodFactory();
    defaultFactory.setBeanFactory(beanFactory);
    defaultFactory.afterPropertiesSet();
    return defaultFactory;
  }
}
