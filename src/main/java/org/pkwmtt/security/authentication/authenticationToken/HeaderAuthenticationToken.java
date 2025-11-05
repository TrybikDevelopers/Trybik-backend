package org.pkwmtt.security.authentication.authenticationToken;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collections;

public class HeaderAuthenticationToken extends AbstractAuthenticationToken {

    public HeaderAuthenticationToken(GrantedAuthority role) {
        super(Collections.singletonList(role));
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return null;
    }
}
