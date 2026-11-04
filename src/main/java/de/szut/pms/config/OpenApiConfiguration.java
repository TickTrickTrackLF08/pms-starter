package de.szut.pms.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.*;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import jakarta.servlet.ServletContext;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfiguration {

    private final ServletContext context;

    public OpenApiConfiguration(ServletContext context) {
        this.context = context;
    }

    @Bean
    public OpenAPI projectManagementOpenAPI() {
        final String securitySchemeName = "bearerAuth";

        return new OpenAPI()
                .addServersItem(new Server().url(context.getContextPath()))
                .info(new Info()
                        .title("Project-Management-Service")
                        .description("""
                                ## Authentifizierung

                                This service verifies JWTs against the Authentication Server running locally via `docker compose`.
                                You can obtain a bearer token as follows:

                                1. Run `docker compose up`.
                                2. Execute the request in `GetBearerToken.http` (the app password for `john`
                                is already hard-coded there, pre-provisioned via the blueprint).
                                3. Copy the `access_token` from the response.

                                Details and a working example can be found in `GetBearerToken.http` in the
                                project root directory.
                                """)
                        .version("0.1"))
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                .components(
                        new Components()
                                .addSecuritySchemes(securitySchemeName,
                                        new SecurityScheme()
                                                .name(securitySchemeName)
                                                .type(SecurityScheme.Type.HTTP)
                                                .scheme("bearer")
                                                .bearerFormat("JWT")
                                )
                );
    }

    //Extend Swagger with Token Authentication
    @Bean
    public OpenApiCustomizer unauthorizedResponseCustomizer() {
        return openApi -> {
            if (openApi.getPaths() == null) {
                return;
            }

            Components components = openApi.getComponents();
            if (components == null) {
                components = new Components();
                openApi.setComponents(components);
            }

            //The problem schema
            Schema<?> problemDetail = new ObjectSchema()
                    .addProperty("type", new StringSchema().format("uri"))
                    .addProperty("title", new StringSchema())
                    .addProperty("status", new IntegerSchema().format("int32"))
                    .addProperty("detail", new StringSchema())
                    .addProperty("instance", new StringSchema().format("uri"));
            components.addSchemas("ProblemDetail", problemDetail);

            // Error blueprint
            ApiResponse unauthorized = new ApiResponse()
                    .description("No, invalid, or expired token")
                    .content(new Content().addMediaType("application/problem+json",
                            new MediaType().schema(new Schema<>().$ref("#/components/schemas/ProblemDetail"))));
            components.addResponses("Unauthorized", unauthorized);

            ApiResponse unauthorizedRef = new ApiResponse().$ref("#/components/responses/Unauthorized");

            // Append 401 to every path and every operation
            openApi.getPaths().values().forEach(pathItem -> {
                for (Operation operation : pathItem.readOperations()) {
                    if (operation.getResponses() == null) {
                        operation.setResponses(new ApiResponses());
                    }
                    operation.getResponses().putIfAbsent("401", unauthorizedRef);
                }
            });
        };
    }
}
