package com.bucket.thingstodobeforedie.config;

import com.bucket.thingstodobeforedie.dto.ApiResponse;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.media.MapSchema;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
public class OpenApiConfig {

    private static final String SECURITY_SCHEME_NAME = "Bearer Authentication";

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Things To Do Before Die API")
                        .description("RESTful API for managing bucket lists and blog posts")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("API Support")
                                .url("https://www.thingstodobeforedie.com")
                                .email("support@thingstodobeforedie.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0.html")))
                .addSecurityItem(new SecurityRequirement().addList(SECURITY_SCHEME_NAME))
                .components(new Components()
                        .addSecuritySchemes(SECURITY_SCHEME_NAME, new SecurityScheme()
                                .name(SECURITY_SCHEME_NAME)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT"))
                        // Add schema for Map<String, String>
                        .addSchemas("StringStringMap", new MapSchema()
                                .additionalProperties(new StringSchema()))
                        // Add schema for ApiResponse with Map<String, String>
                        .addSchemas("ApiResponseMapStringString", createApiResponseSchema("StringStringMap")));
    }
    
    private Schema createApiResponseSchema(String dataSchemaRef) {
        Schema apiErrorSchema = new Schema<ApiResponse.ApiError>()
                .type("object")
                .addProperty("status", new Schema<Integer>().type("integer").format("int32"))
                .addProperty("message", new Schema<String>().type("string"));
        
        return new Schema<ApiResponse<?>>()
                .type("object")
                .addProperty("success", new Schema<Boolean>().type("boolean"))
                .addProperty("message", new Schema<String>().type("string"))
                .addProperty("data", new Schema<>().$ref("#/components/schemas/" + dataSchemaRef))
                .addProperty("error", apiErrorSchema)
                .addProperty("timestamp", new Schema<String>().type("string").format("date-time"));
    }
} 