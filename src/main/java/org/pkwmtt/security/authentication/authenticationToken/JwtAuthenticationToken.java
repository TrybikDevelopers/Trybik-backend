package org.pkwmtt.security.authentication.authenticationToken;

import lombok.Getter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.UUID;

public class JwtAuthenticationToken extends AbstractAuthenticationToken {

    private UUID principal;
    private String jwtToken;
    @Getter
    private String superiorGroup;


    /**
     * This constructor can be safely used by any code that wishes to create a JwtAuthenticationToken,
     * as the isAuthenticated() will return false
     *
     * @param jwtToken
     */
    public JwtAuthenticationToken(String jwtToken) {
        super(null);
        this.jwtToken = jwtToken;
        setAuthenticated(false);
    }

    /**
     * This constructor should only be used by AuthenticationManager or AuthenticationProvider
     * implementations that are satisfied with producing a trusted (i.e. isAuthenticated() = true)
     * authentication token. It refers to users with authorities for specific group only
     *
     * @param principal
     * @param authorities
     * @param superiorGroup
     */
    public JwtAuthenticationToken(UUID principal, Collection<? extends GrantedAuthority> authorities, String superiorGroup) {
        super(authorities);
        this.principal = principal;
        this.jwtToken = null;
        this.superiorGroup = superiorGroup;
        super.setAuthenticated(true);
    }

    /**
     * This constructor should only be used by AuthenticationManager or AuthenticationProvider
     * implementations that are satisfied with producing a trusted (i.e. isAuthenticated() = true)
     * authentication token. It refers to users without authorities for specific groups
     *
     * @param principal
     * @param authorities
     */
    public JwtAuthenticationToken(UUID principal, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.principal = principal;
        this.jwtToken = null;
        this.superiorGroup = null;
        super.setAuthenticated(true);
    }

    @Override
    public String getCredentials() {
        return this.jwtToken;
    }

    @Override
    public Object getPrincipal() {
        return this.principal;
    }

    @Override
    public void eraseCredentials() {
        super.eraseCredentials();
        this.jwtToken = null;
    }
}