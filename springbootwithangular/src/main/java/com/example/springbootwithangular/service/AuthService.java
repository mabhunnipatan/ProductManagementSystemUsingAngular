package com.example.springbootwithangular.service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Locale;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.example.springbootwithangular.dto.AuthResponse;
import com.example.springbootwithangular.dto.LoginRequest;
import com.example.springbootwithangular.dto.RegisterRequest;
import com.example.springbootwithangular.entity.AccountRole;
import com.example.springbootwithangular.entity.UserAccount;
import com.example.springbootwithangular.repository.UserAccountRepository;

import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@Service
public class AuthService {
	private final UserAccountRepository accounts;
	private final PasswordEncoder passwordEncoder;
	private final JwtEncoder jwtEncoder;

	public AuthService(
			UserAccountRepository accounts,
			PasswordEncoder passwordEncoder,
			JwtEncoder jwtEncoder) {
		this.accounts = accounts;
		this.passwordEncoder = passwordEncoder;
		this.jwtEncoder = jwtEncoder;
	}

	@Transactional
	public AuthResponse register(RegisterRequest request) {
		String email = normalizeEmail(request.email());
		if (request.password().getBytes(java.nio.charset.StandardCharsets.UTF_8).length > 72) {
			throw new ResponseStatusException(BAD_REQUEST, "Password must not exceed 72 UTF-8 bytes.");
		}
		if (accounts.findByEmail(email).isPresent()) {
			throw new ResponseStatusException(CONFLICT, "An account with this email already exists.");
		}
		UserAccount account = new UserAccount();
		account.setEmail(email);
		account.setPasswordHash(passwordEncoder.encode(request.password()));
		account.setRole(AccountRole.USER);
		return tokenFor(accounts.save(account));
	}

	@Transactional(readOnly = true)
	public AuthResponse login(LoginRequest request) {
		UserAccount account = accounts.findByEmail(normalizeEmail(request.email()))
				.filter(found -> passwordEncoder.matches(request.password(), found.getPasswordHash()))
				.orElseThrow(() -> new ResponseStatusException(UNAUTHORIZED, "Email or password is incorrect."));
		return tokenFor(account);
	}

	public AuthResponse tokenFor(UserAccount account) {
		Instant now = Instant.now();
		JwtClaimsSet claims = JwtClaimsSet.builder()
				.issuer("stockroom")
				.subject(account.getEmail())
				.issuedAt(now)
				.expiresAt(now.plus(12, ChronoUnit.HOURS))
				.claim("roles", List.of("ROLE_" + account.getRole().name()))
				.build();
		String token = jwtEncoder.encode(JwtEncoderParameters.from(
				JwsHeader.with(MacAlgorithm.HS256).build(), claims)).getTokenValue();
		return new AuthResponse(token, account.getEmail(), account.getRole().name());
	}

	public static String normalizeEmail(String email) {
		return email.trim().toLowerCase(Locale.ROOT);
	}
}
