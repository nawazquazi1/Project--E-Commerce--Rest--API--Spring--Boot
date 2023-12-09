package com.nawaz.shopping.web.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
//
//	@Bean
//	public OpenAPI springShopOpenAPI() {
//		return new OpenAPI().info(new Info().title("E-Commerce Application")
//			.description("Backend APIs for E-Commerce Application")
//			.version("v1.0.0")
//			.contact(new Contact().name("Nawaz Quazi").url("https://nawazquazi1.github.io/").email("nawazquazi356@gmail.com"))
//			.license(new License().name("License").url("/")))
//			.externalDocs(new ExternalDocumentation().description("E-Commerce App Documentation")
//			.url("http://localhost:8080/swagger-ui/index.html"));
//	}

	String schemeName = "bearer";

	@Bean
	public OpenAPI api() {
		return new OpenAPI()
				.addSecurityItem(new SecurityRequirement()
						.addList(schemeName))
				.components(new Components()
						.addSecuritySchemes(schemeName,
								new SecurityScheme()
										.name(schemeName)
										.type(SecurityScheme.Type.HTTP)
										.bearerFormat("JWT")
										.scheme("bearer")))
				.info(new Info()
						.title("E-Commerce Application")
						.description("Rest API")
						.version("1.0")
						.contact(new Contact()
								.name("Nawaz Quazi")
								.url("https://github.com/nawazquazi1")
								.email("nawazquazi356@gmail.com")))
				.externalDocs(new ExternalDocumentation());
	}
	
}
