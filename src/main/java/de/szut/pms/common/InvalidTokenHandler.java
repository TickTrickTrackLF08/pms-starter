package de.szut.pms.common;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.http.ProblemDetail;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.server.resource.InvalidBearerTokenException;
import org.springframework.security.web.AuthenticationEntryPoint;


import java.io.IOException;

@Component
@Slf4j
public class InvalidTokenHandler implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    public InvalidTokenHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void commence(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull AuthenticationException authException) throws IOException, ServletException {

        //Creates the ProblemDetail
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.UNAUTHORIZED, "The request was unauthorized, please check your credentials");

        //Distinguishes between “token is missing” and “token is invalid”
        if(authException instanceof InvalidBearerTokenException) {
            problem.setTitle("Bearer Token Invalid");

        }else{
            problem.setTitle("No token is provided");
        }
        //Sets the error status
        response.setStatus(401);
        response.setContentType("application/problem+json");

        //Logs the error message
        log.info(authException.getClass().getSimpleName());
        log.info(problem.toString());

        //To make it work with umlauts
        response.setCharacterEncoding("UTF-8");
        //Writes the error to the client
        response.getWriter().print(objectMapper.writeValueAsString(problem));

    }
}
