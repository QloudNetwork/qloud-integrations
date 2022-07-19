# Integration for Spring Boot / Spring Security

Spring Boot auto-configuration that automatically verifies and parses the Qloud JWT token if the
`qloud.secret` application property is configured.

## Getting Started

Add the `qloud-spring-boot-starter` as a dependency:

```xml
<dependency>
  <groupId>network.qloud</groupId>
  <artifactId>qloud-spring-boot-starter</artifactId>
  <version>{version}</version>
</dependency>
```

Add the secret of your Qloud application to the application properties:

```properties
qloud.secret=<secret>
```

Your application will then receive an authenticated request. See [Spring Security Integrations](https://docs.spring.io/spring-security/site/docs/current/reference/html5/#integrations).

## Logout Controller

There is an optional logout controller that will delete the JWT token cookie and redirect the user agent to the
Qloud login page. It can be enabled by configuring the URL path for this controller:

```properties
qloud.logoutPath=/logout
```

The controller also supports a `return_path` parameter. If present, the endpoint will redirect to the given path instead
of redirecting to the Qloud login page.
