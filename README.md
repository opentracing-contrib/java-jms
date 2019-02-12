[![Build Status][ci-img]][ci] [![Coverage Status][cov-img]][cov] [![Released Version][maven-img]][maven] [![Apache-2.0 license](https://img.shields.io/badge/license-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)

# OpenTracing JMS Instrumentation
OpenTracing instrumentation for JMS.

## Installation

### JMS 1
pom.xml
```xml
<dependency>
    <groupId>io.opentracing.contrib</groupId>
    <artifactId>opentracing-jms-1</artifactId>
    <version>VERSION</version>
</dependency>
```

### JMS 2
pom.xml
```xml
<dependency>
    <groupId>io.opentracing.contrib</groupId>
    <artifactId>opentracing-jms-2</artifactId>
    <version>VERSION</version>
</dependency>
```

### Spring JMS
pom.xml
```xml
<dependency>
    <groupId>io.opentracing.contrib</groupId>
    <artifactId>opentracing-jms-spring</artifactId>
    <version>VERSION</version>
</dependency>
```
You most likely need to exclude spring-jms and spring-context dependencies and add own (to avoid jar hell):
```xml
<dependency>
    <groupId>io.opentracing.contrib</groupId>
    <artifactId>opentracing-jms-spring</artifactId>
    <version>VERSION</version>
    <exclusions>
        <exclusion>
            <groupId>org.springframework</groupId>
            <artifactId>spring-jms</artifactId>
        </exclusion>
        <exclusion>
             <groupId>org.springframework</groupId>
             <artifactId>spring-context</artifactId>
        </exclusion>
    </exclusions>
</dependency>

<dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-jms</artifactId>
    <version>required version</version>
</dependency>

<dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-context</artifactId>
    <version>required version</version>
</dependency>
```

## Usage

```java
// Instantiate tracer
Tracer tracer = ...

// Optionally register tracer with GlobalTracer:
GlobalTracer.register(tracer);
```

### JMS API
```java
// decorate JMS MessageProducer with TracingMessageProducer
TracingMessageProducer producer = new TracingMessageProducer(messageProducer, tracer);

// decorate JMS JMSProducer with TracingJMSProducer need Session
Session session = ...
TracingJMSProducer producer = new TracingJMSProducer(jmsProducer, session, tracer);
// or with JMSContext
JMSContext jmsContext = ...
TracingJMSProducer producer = new TracingJMSProducer(jmsProducer, jmsContext, tracer);

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

### Java 9+

Modules _opentracing-jms-1_ and _opentracing-jms-2_ have next _Automatic-Module-Name_ accordingly:
- io.opentracing.contrib.jms1
- io.opentracing.contrib.jms2

## OpenTracing Conventions

### Message properties

When a message exchange between a producer and consumer is traced using an OpenTracing compliant tracer,
the trace context and any defined baggage items will be carried in the JMS message properties.

OpenTracing does not place any restrictions on the names used for the trace context and baggage item
properties. However the JMS API does not permit the hyphen/dash `-` character to be used. Therefore, it
is necessary to encode the trace context and baggage item names.

The steps used to encode the key names are:

- replace any `-` character with `_$dash$_`

When the message is consumed, the steps are reversed to decode the original key names.

Any libraries that instrument the JMS API should conform to this convention to enable tracing interoperability.

## License

[Apache 2.0 License](./LICENSE).

[ci-img]: https://travis-ci.org/opentracing-contrib/java-jms.svg?branch=master
[ci]: https://travis-ci.org/opentracing-contrib/java-jms
[cov-img]: https://coveralls.io/repos/github/opentracing-contrib/java-jms/badge.svg?branch=master
[cov]: https://coveralls.io/github/opentracing-contrib/java-jms?branch=master
[maven-img]: https://img.shields.io/maven-central/v/io.opentracing.contrib/opentracing-jms-1.svg
[maven]: http://search.maven.org/#search%7Cga%7C1%7Copentracing-jms-1
