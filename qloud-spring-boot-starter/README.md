# Integration for Spring Boot / Spring Security

Spring Boot auto-configuration that automatically verifies and parses the Qloud JSON Web Token if the
`qloud.secret` application property is configured.

## Getting Started

Add the `qloud-spring-boot-starter` as a dependency to your pom.xml. Versions >= `0.2.x` are compatible with Spring Boot
3 while version `0.1.0` is compatible with Spring Boot 2.

```xml

<dependency>
    <groupId>network.qloud</groupId>
    <artifactId>qloud-spring-boot-starter</artifactId>
    <version>0.2.0</version>
</dependency>
```

Or your gradle.build:

```kotlin
implementation("network.qloud:qloud-spring-boot-starter:0.2.0")
```

Add the secret of your Qloud application to the application properties:

```properties
qloud.secret=<secret>
```

Your application will then receive an authenticated request.
See [Spring Security Integrations](https://docs.spring.io/spring-security/site/docs/current/reference/html5/#integrations)
.

If you are using Spring Web MVC, you can inject a `QloudUser` into your handler methods to get access to the user data
from the JSON Web Token.

```kotlin
@GetMapping("/user")
fun getUser(user: QloudUser): QloudUser {
    return user
}
```

## Logout Controller

There is an optional logout controller that will delete the JSON Web Token cookie and redirect the user agent to the
Qloud login page. It can be enabled by configuring the URL path for this controller:

```properties
qloud.logoutPath=/logout
```

The controller also supports a `return_path` parameter. If present, the endpoint will redirect to the given path instead
of redirecting to the Qloud login page.

## Access Management API

The starter also makes it easy to access Qloud's Managemnet API from your application. Just specify your Qloud domain
(or your custom domain if you have configured one) in your application properties:

```properties
qloud.domain=your-subdomain.qloud.space
```

The autoconfiguration will then add
a [QloudApi](https://github.com/SemanticlabsGmbH/qloud-integrations/blob/main/qloud-spring-boot-starter/src/main/kotlin/network/qloud/integrations/boot/api/QloudApi.kt)
bean for you to inject exposing the API's operations .
