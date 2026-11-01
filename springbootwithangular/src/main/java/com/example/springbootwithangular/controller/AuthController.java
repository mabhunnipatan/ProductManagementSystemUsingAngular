package com.example.springbootwithangular.controller;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.example.springbootwithangular.dto.AuthResponse;
import com.example.springbootwithangular.dto.LoginRequest;
import com.example.springbootwithangular.dto.RegisterRequest;
import com.example.springbootwithangular.entity.UserAccount;
import com.example.springbootwithangular.repository.UserAccountRepository;
import com.example.springbootwithangular.service.AuthService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
	private final AuthService authService;
	private final UserAccountRepository accounts;

	public AuthController(AuthService authService, UserAccountRepository accounts) {
		this.authService = authService;
		this.accounts = accounts;
	}

	@PostMapping("/register")
	public AuthResponse register(@Valid @RequestBody RegisterRequest request) {
		return authService.register(request);
	}

	@PostMapping("/login")
	public AuthResponse login(@Valid @RequestBody LoginRequest request) {
		return authService.login(request);
	}

	@PostMapping("/me")
	public AuthResponse me(Authentication authentication) {
		UserAccount account = accounts.findByEmail(authentication.getName())
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));
		return authService.tokenFor(account);
	}
}
