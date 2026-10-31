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

        //Erstellt das ProblemDetail
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.UNAUTHORIZED, "The request was unauthorized, please check your credentials");

        //Differenziert zwischen Token ist nicht vorhanden und token is invalid
        if(authException instanceof InvalidBearerTokenException) {
            problem.setTitle("Bearer Token Invalid");

        }else{
            problem.setTitle("No token is provided");
        }
        //Setzt den Fehlerstatus
        response.setStatus(401);

        //
        response.setContentType("application/problem+json");

        //Loged die Fehlermeldung
        log.info(authException.getClass().getSimpleName());
        log.info(problem.toString());

        //
        response.setCharacterEncoding("UTF-8"); //Damit es mit umlauten funktioniert
        response.getWriter().print(objectMapper.writeValueAsString(problem)); //SChreibt das Fehler an den Client


    }
}
