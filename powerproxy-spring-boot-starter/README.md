# Integration for Spring Boot / Spring Security

Spring Boot auto-configuration that automatically verifies and parses the PowerProxy JWT token if the
`powerproxy.secret` application property is configured.

## Getting Started

Add the `powerproxy-spring-boot-starter` as a dependency:

```xml
<dependency>
  <groupId>io.powerproxy</groupId>
  <artifactId>powerproxy-spring-boot-starter</artifactId>
  <version>{version}</version>
</dependency>
```

Add the secret of your PowerProxy application to the application properties:

```properties
powerproxy.secret=<secret>
```

Your application will then receive an authenticated request. See [Spring Security Integrations](https://docs.spring.io/spring-security/site/docs/current/reference/html5/#integrations).
