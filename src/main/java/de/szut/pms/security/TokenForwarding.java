package de.szut.pms.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

// Provides the bearer token of the current request so it can be forwarded to EmployeeService
@Component
public class TokenForwarding {

    //Returns the raw JWT of the currently authenticated user.
    public String getToken() {
        SecurityContext context = SecurityContextHolder.getContext();
        Authentication authentication = context.getAuthentication();
        if(authentication == null) {throw new IllegalStateException("Authentication is null");}

        Object principal = authentication.getPrincipal();

        // With a JWT resource server, the principal is the decoded Jwt
        if(principal instanceof Jwt){
            return ((Jwt) principal).getTokenValue(); //Cast on JWT
        }
        throw new IllegalStateException("Principal is not Jwt");
    }

}
