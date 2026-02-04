package com.po.f1search;

import java.net.http.HttpClient;
import java.time.Duration;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class F1SearchServiceApplication {

	// Allow to mock the httpClient in tests
	@Bean
	public HttpClient httpClient() {
		return HttpClient.newBuilder()
				.connectTimeout(Duration.ofSeconds(10))
				.followRedirects(HttpClient.Redirect.NORMAL)
				.build();
	}

	public static void main(String[] args) {
		SpringApplication.run(F1SearchServiceApplication.class, args);
	}

}
