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
    <version>0.0.3</version>
</dependency>
```

### JMS 2
pom.xml
```xml
<dependency>
    <groupId>io.opentracing.contrib</groupId>
    <artifactId>opentracing-jms-2</artifactId>
    <version>0.0.3</version>
</dependency>
```

### Spring JMS
pom.xml
```xml
<dependency>
    <groupId>io.opentracing.contrib</groupId>
    <artifactId>opentracing-jms-spring</artifactId>
    <version>0.0.3</version>
</dependency>
```
You most likely need to exclude spring-jms dependency and add own (to avoid jar hell):
```xml
<dependency>
    <groupId>io.opentracing.contrib</groupId>
    <artifactId>opentracing-jms-spring</artifactId>
    <version>0.0.3</version>
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

```java
// Instantiate tracer
Tracer tracer = ...

```

### JMS API
```java
// decorate JMS MessageProducer with TracingMessageProducer
TracingMessageProducer producer = new TracingMessageProducer(messageProducer, tracer);

// decorate JMS MessageConsumer with TracingMessageConsumer
TracingMessageConsumer consumer = new TracingMessageConsumer(messageConsumer, tracer);

// decorate JMS MessageListener if used with TracingMessageListener
TracingMessageListener listener = new TracingMessageListener(messageListener, tracer);
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
JmsTemplate jmsTemplate = new TracingJmsTemplate(connectionFactory, tracer); 

// send and receive messages as usual
jmsTemplate.send(...)
jmsTemplate.convertAndSend(...);
jmsTemplate.receive(...)
jmsTemplate.receiveAndConvert(...);
...
```

If `@JmsListener` is used then it is required to import TracingJmsConfiguration e.g.
 ```java
@Configuration 
@Import(TracingJmsConfiguration.class)
@EnableJms
public class JmsConfiguration {
  ...
}
```

[ci-img]: https://travis-ci.org/opentracing-contrib/java-jms.svg?branch=master
[ci]: https://travis-ci.org/opentracing-contrib/java-jms
[maven-img]: https://img.shields.io/maven-central/v/io.opentracing.contrib/opentracing-jms-1.svg
[maven]: http://search.maven.org/#search%7Cga%7C1%7Copentracing-jms-1
