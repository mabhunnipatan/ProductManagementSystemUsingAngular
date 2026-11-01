package com.example.springbootwithangular.config;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.example.springbootwithangular.entity.AccountRole;
import com.example.springbootwithangular.entity.UserAccount;
import com.example.springbootwithangular.repository.UserAccountRepository;
import com.example.springbootwithangular.service.AuthService;
import com.nimbusds.jose.jwk.source.ImmutableSecret;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {
	private static final Logger logger = LoggerFactory.getLogger(SecurityConfiguration.class);

	@Bean
	SecretKey jwtSecretKey(@Value("${app.jwt.secret:}") String configuredSecret) {
		byte[] keyBytes;
		if (configuredSecret.isBlank()) {
			keyBytes = new byte[32];
			new SecureRandom().nextBytes(keyBytes);
			logger.warn("AUTH_JWT_SECRET is not set; access tokens will be invalidated when the service restarts.");
		} else {
			if (configuredSecret.getBytes(StandardCharsets.UTF_8).length < 32) {
				throw new IllegalStateException("AUTH_JWT_SECRET must contain at least 32 UTF-8 bytes.");
			}
			try {
				keyBytes = MessageDigest.getInstance("SHA-256")
						.digest(configuredSecret.getBytes(StandardCharsets.UTF_8));
			} catch (NoSuchAlgorithmException exception) {
				throw new IllegalStateException("SHA-256 is required to initialize JWT signing.", exception);
			}
		}
		return new SecretKeySpec(keyBytes, "HmacSHA256");
	}

	@Bean
	JwtEncoder jwtEncoder(SecretKey jwtSecretKey) {
		return new NimbusJwtEncoder(new ImmutableSecret<>(jwtSecretKey));
	}

	@Bean
	JwtDecoder jwtDecoder(SecretKey jwtSecretKey) {
		return NimbusJwtDecoder.withSecretKey(jwtSecretKey).macAlgorithm(MacAlgorithm.HS256).build();
	}

	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	JwtAuthenticationConverter jwtAuthenticationConverter() {
		JwtGrantedAuthoritiesConverter authorities = new JwtGrantedAuthoritiesConverter();
		authorities.setAuthoritiesClaimName("roles");
		authorities.setAuthorityPrefix("");
		JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
		converter.setJwtGrantedAuthoritiesConverter(authorities);
		return converter;
	}

	@Bean
	SecurityFilterChain securityFilterChain(
			HttpSecurity http,
			JwtAuthenticationConverter jwtAuthenticationConverter) throws Exception {
		http
				.cors(Customizer.withDefaults())
				.csrf(csrf -> csrf.disable())
				.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.authorizeHttpRequests(authorize -> authorize
						.requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
						.requestMatchers("/api/auth/register", "/api/auth/login").permitAll()
						.requestMatchers(HttpMethod.GET, "/api/products", "/api/products/**").permitAll()
						.requestMatchers("/api/auth/me").authenticated()
						.requestMatchers(HttpMethod.POST, "/api/products", "/api/products/**").hasRole("ADMIN")
						.requestMatchers(HttpMethod.PUT, "/api/products", "/api/products/**").hasRole("ADMIN")
						.requestMatchers(HttpMethod.DELETE, "/api/products", "/api/products/**").hasRole("ADMIN")
						.anyRequest().denyAll())
				.oauth2ResourceServer(resource -> resource
						.jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter)));
		return http.build();
	}

	@Bean
	CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration configuration = new CorsConfiguration();
		configuration.setAllowedOriginPatterns(java.util.List.of("*"));
		configuration.setAllowedMethods(java.util.List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
		configuration.setAllowedHeaders(java.util.List.of("Authorization", "Content-Type"));
		configuration.setMaxAge(3600L);
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/api/**", configuration);
		return source;
	}

	@Bean
	ApplicationRunner seedAdmin(
			UserAccountRepository accounts,
			PasswordEncoder passwordEncoder,
			@Value("${app.admin.email:}") String adminEmail,
			@Value("${app.admin.password:}") String adminPassword) {
		return args -> {
			if (adminEmail.isBlank() && adminPassword.isBlank()) {
				logger.warn("Admin bootstrap is not configured; set ADMIN_EMAIL and ADMIN_PASSWORD in Render.");
				return;
			}
			if (adminEmail.isBlank() || adminPassword.isBlank() || adminPassword.length() < 12) {
				throw new IllegalStateException("Configure both ADMIN_EMAIL and ADMIN_PASSWORD; the admin password must be at least 12 characters.");
			}
			if (adminPassword.getBytes(StandardCharsets.UTF_8).length > 72) {
				throw new IllegalStateException("ADMIN_PASSWORD must not exceed 72 UTF-8 bytes.");
			}
			String normalizedEmail = AuthService.normalizeEmail(adminEmail);
			UserAccount account = accounts.findByEmail(normalizedEmail).orElseGet(UserAccount::new);
			account.setEmail(normalizedEmail);
			account.setPasswordHash(passwordEncoder.encode(adminPassword));
			account.setRole(AccountRole.ADMIN);
			accounts.save(account);
			logger.info("Admin account configured for {}", normalizedEmail);
		};
	}
}
