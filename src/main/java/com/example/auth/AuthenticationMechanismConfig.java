package com.example.auth;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.security.enterprise.authentication.mechanism.http.BasicAuthenticationMechanismDefinition;

/**
 * Basic認証を用いて認証します。
 */
@BasicAuthenticationMechanismDefinition
@ApplicationScoped
public class AuthenticationMechanismConfig {
}
