[![Build Status][ci-img]][ci] [![Released Version][maven-img]][maven]

# OpenTracing JMS Instrumentation
OpenTracing instrumentation for JMS.

## Installation

### JMS 1
pom.xml
```xml
<dependency>
    <groupId>io.opentracing.contrib</groupId>
    <artifactId>opentracing-jms-1</artifactId>
    <version>0.0.1</version>
</dependency>
```

### JMS 2
pom.xml
```xml
<dependency>
    <groupId>io.opentracing.contrib</groupId>
    <artifactId>opentracing-jms-2</artifactId>
    <version>0.0.1</version>
</dependency>
```

### Spring JMS
pom.xml
```xml
<dependency>
    <groupId>io.opentracing.contrib</groupId>
    <artifactId>opentracing-jms-spring</artifactId>
    <version>0.0.1</version>
</dependency>
```
You most likely need to exclude spring-jms dependency and add own (to avoid jar hell):
```xml
<dependency>
    <groupId>io.opentracing.contrib</groupId>
    <artifactId>opentracing-jms-spring</artifactId>
    <version>0.0.1</version>
    <exclusions>
        <exclusion>
            <groupId>org.springframework</groupId>
            <artifactId>spring-jms</artifactId>
        </exclusion>
    </exclusions>
</dependency>

<dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-jms</artifactId>
    <version>required version</version>
</dependency>

```

## Usage

`DefaultSpanManager` is used to get active span

```java
// Instantiate tracer
Tracer tracer = ...

// Register tracer with GlobalTracer
GlobalTracer.register(tracer);

```

### JMS API
```java
// decorate JMS MessageProducer with TracingMessageProducer
TracingMessageProducer producer = new TracingMessageProducer(messageProducer);

// decorate JMS MessageConsumer with TracingMessageConsumer
TracingMessageConsumer consumer = new TracingMessageConsumer(messageConsumer);

// decorate JMS MessageListener if used with TracingMessageListener
TracingMessageListener listener = new TracingMessageListener(messageListener);
consumer.setMessageListener(listener);

// send message
Message message = ...
producer.send(message);

// receive message
Message message = consumer.receive();

```

### Spring JMS
```java
// create TracingJmsTemplate which extends Spring JmsTemplate
JmsTemplate jmsTemplate = new TracingJmsTemplate(connectionFactory); 

// send and receive messages as usual
jmsTemplate.send(...)
jmsTemplate.convertAndSend(...);
jmsTemplate.receive(...)
jmsTemplate.receiveAndConvert(...);
...
```

If `@JmsListener` is used then it is required to decorate MessageConverter with TracingMessageConverter e.g.
 ```java
@Bean
public MessageConverter tracingJmsMessageConverter() {
    return new TracingMessageConverter(jacksonJmsMessageConverter());
}

private MessageConverter jacksonJmsMessageConverter() {
    MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
    converter.setTargetType(MessageType.TEXT);
    converter.setTypeIdPropertyName("_type");
    return converter;
}

@Bean
public JmsTemplate jmsTemplate(ConnectionFactory connectionFactory) {
    JmsTemplate jmsTemplate = new TracingJmsTemplate(connectionFactory);
    jmsTemplate.setMessageConverter(jacksonJmsMessageConverter());
    return jmsTemplate;
}

@Bean
public JmsListenerContainerFactory<?> myFactory(ConnectionFactory connectionFactory,
                                                DefaultJmsListenerContainerFactoryConfigurer configurer) {
    DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
    configurer.configure(factory, connectionFactory);
    return factory;
}
```

[ci-img]: https://travis-ci.org/opentracing-contrib/java-jms.svg?branch=master
[ci]: https://travis-ci.org/opentracing-contrib/java-jms
[maven-img]: https://img.shields.io/maven-central/v/io.opentracing.contrib/opentracing-jms-1.svg?maxAge=2592000
[maven]: http://search.maven.org/#search%7Cga%7C1%7Copentracing-jms-1
