package com.financedomain.pricing;

import com.financedomain.pricing.controller.PassInternetController;
import com.financedomain.pricing.controller.PurchaseController;
import com.financedomain.pricing.service.PassInternetService;
import com.financedomain.pricing.service.PurchaseService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(properties = {
		"server.port=8201",
		"pricing-service.uriport=8201",
		"pricing-service.showsql=true",
		"spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1",
		"spring.datasource.driver-class-name=org.h2.Driver",
		"spring.datasource.username=sa",
		"spring.datasource.password=",
		"spring.jpa.database-platform=org.hibernate.dialect.H2Dialect",
		"eureka.client.enabled=false",
		"spring.cloud.config.enabled=false"
})
class PricingServiceApplicationTests {

	@Autowired
	private ApplicationContext applicationContext;

	@Autowired
	private PassInternetController passInternetController;

	@Autowired
	private PurchaseController purchaseController;

	@Autowired
	private PassInternetService passInternetService;

	@Autowired
	private PurchaseService purchaseService;

	@Test
	@DisplayName("Vérifie le chargement du contexte Spring Boot et l'injection des beans du microservice pricing")
	void contextLoads() {
		assertNotNull(applicationContext, "Le contexte Spring ne doit pas être nul.");
		assertThat(passInternetController).isNotNull();
		assertThat(purchaseController).isNotNull();
		assertThat(passInternetService).isNotNull();
		assertThat(purchaseService).isNotNull();
	}
}
