package com.parkinglot.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI parkingLotOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Smart Parking Lot Management System RESTful API")
                        .description("Production-ready backend service providing real-time slot allocation, active parking sessions, checkout billing, dynamic fare calculations, and revenue reporting.")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Parking Lot Backend Team")
                                .email("backend-support@parkinglot.local"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://springdoc.org")));
    }
}
