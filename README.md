# Spring Showcase

Simple application written in java showcasing some of Spring's capabilities:

* [RabbitMQ](https://github.com/miguel-oliveira/spring-demo-java/tree/main/src/main/java/miguel/oliveira/demo/rabbitmq)
    - Simple message [route](https://github.com/miguel-oliveira/spring-demo-java/blob/main/src/main/java/miguel/oliveira/demo/rabbitmq/EventRouteBuilder.java) and [consumer](https://github.com/miguel-oliveira/spring-demo-java/blob/main/src/main/java/miguel/oliveira/demo/rabbitmq/Consumer.java)
      using [Apache Camel Spring RabbitMQ](https://camel.apache.org/components/3.16.x/spring-rabbitmq-component.html).
    - [Consistent Hash Exchange](https://github.com/rabbitmq/rabbitmq-consistent-hash-exchange/blob/master/README.md) example:
      - [Producer and Consumer](https://github.com/miguel-oliveira/spring-demo-java/blob/main/src/main/java/miguel/oliveira/demo/rabbitmq/consistenthash/ConsistentHashExchange.java) route declarations
      - [ConsistentHashConsumer](https://github.com/miguel-oliveira/spring-demo-java/blob/main/src/main/java/miguel/oliveira/demo/rabbitmq/consistenthash/ConsistentHashConsumer.java)
      - [ConsistentHashProducer](https://github.com/miguel-oliveira/spring-demo-java/blob/main/src/main/java/miguel/oliveira/demo/rabbitmq/consistenthash/ConsistentHashProducer.java)


* [Cache](https://github.com/miguel-oliveira/spring-demo-java/tree/main/src/main/java/miguel/oliveira/demo/cache)
    - Rest Controller with caching endpoint by name and key, and a cache evicting endpoint by the same name and key.


* [Custom Spring Bean Thread Scope](https://github.com/miguel-oliveira/spring-demo-java/tree/main/src/main/java/miguel/oliveira/demo/scope)
    - Supports destruction callback registration and clean up;
    - This thread scope is also inheritable, which means the scope will be propagated to spawned
      threads. [Here](https://github.com/miguel-oliveira/spring-demo-java/blob/main/src/main/java/miguel/oliveira/demo/configuration/AsyncConfiguration.java)
      is an example of a spring simple async task executor configuration which makes use of this capability. Note that
      since the scope is only propagated when spawning a new thread, the task executor must not
      reuse threads.


* [Custom Message Formatter](https://github.com/miguel-oliveira/spring-demo-java/tree/main/src/main/java/miguel/oliveira/demo/formatter)
    - Supports both empty and ordered placeholder replacement with the support of [Sl4j Message Formatter](https://www.javadoc.io/doc/org.slf4j/slf4j-api/1.7.30/org/slf4j/helpers/MessageFormatter.html) and [Apache StringSubstitutor](https://commons.apache.org/proper/commons-text/apidocs/org/apache/commons/text/StringSubstitutor.html)
    - Junit5 Parameterized test also available [here](https://github.com/miguel-oliveira/spring-demo-java/blob/main/src/test/java/miguel/oliveira/demo/formatter/MyMessageFormatterTest.java)


* [Recording](https://github.com/miguel-oliveira/spring-demo-java/tree/main/src/main/java/miguel/oliveira/demo/record)
  and [replaying](https://github.com/miguel-oliveira/spring-demo-java/tree/main/src/main/java/miguel/oliveira/demo/replay)
  of method invocations using [Spring AOP](https://docs.spring.io/spring-framework/docs/2.5.x/reference/aop.html) and reflection


* [MongoDB](https://github.com/miguel-oliveira/spring-demo-java/tree/main/src/main/java/miguel/oliveira/demo/mongodb)
  - [Person Entity](https://github.com/miguel-oliveira/spring-demo-java/blob/main/src/main/java/miguel/oliveira/demo/mongodb/Person.java)
  - [Person Controller](https://github.com/miguel-oliveira/spring-demo-java/blob/main/src/main/java/miguel/oliveira/demo/mongodb/PersonController.java)
    - Exposes a REST API with some CRUD operation on the Person resource
    - Also exposes an export endpoint which exports every persisted person objects to a csv file