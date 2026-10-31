package de.szut.pms.common;

import jakarta.security.auth.message.callback.PrivateKeyCallback;
import org.springframework.boot.actuate.web.exchanges.HttpExchange;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.server.resource.InvalidBearerTokenException;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Order(Ordered.HIGHEST_PRECEDENCE)
public class TokenInvalidException {
    
    public ProblemDetail handleInvalidToken(PrivateKeyCallback.Request request, HttpExchange.Response response, AuthenticationException authenticationException){

        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.UNAUTHORIZED, authenticationException.getMessage());

        if(authenticationException instanceof InvalidBearerTokenException) {
            problem.setTitle("Bearer Token Invalid");
            problem.setProperty("errors", authenticationException.getMessage());
        }else if(authenticationException instanceof AuthenticationException) {
            problem.setTitle("Fehler beim Erstellen");
            problem.setProperty("errors", authenticationException.getMessage());
        }else{
            problem.setTitle("Kein Token vorhanden");
            problem.setProperty("errors", authenticationException.getMessage());
        }
        return problem;
    }
}
