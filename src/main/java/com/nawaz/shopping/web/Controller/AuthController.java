package com.nawaz.shopping.web.Controller;

import com.nawaz.shopping.web.exceptions.UserNotFoundException;
import com.nawaz.shopping.web.payloads.UserDTO;
import com.nawaz.shopping.web.security.JwtTokenHelper;
import com.nawaz.shopping.web.services.UserService;
import com.nawaz.shopping.web.exceptions.APIException;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.Map;

@RestController
@RequestMapping("/api")
@AllArgsConstructor
@SecurityRequirement(name = "E-Commerce Application")
public class AuthController {

	private UserService userService;
	private AuthenticationManager authenticationManager;
	@Autowired
	private PasswordEncoder passwordEncoder;
	private JwtTokenHelper tokenHelper;

	@PostMapping("/register")
	public ResponseEntity<Map<String, Object>> registerHandler(@Valid @RequestBody UserDTO user) throws UserNotFoundException {
		user.setPassword(this.passwordEncoder.encode(user.getPassword()));
		UserDTO userDTO = userService.registerUser(user);
		String token = this.tokenHelper.generateToken(userDTO);
		return new ResponseEntity<Map<String, Object>>(Collections.singletonMap("jwt-token", token),
				HttpStatus.CREATED);
	}

	@PostMapping("/login")
	public Map<String, Object> loginHandler(@Valid @RequestBody UserDTO credentials) {
		UsernamePasswordAuthenticationToken authCredentials = new UsernamePasswordAuthenticationToken(
				credentials.getEmail(), credentials.getPassword());
		authenticationManager.authenticate(authCredentials);
		String token = this.tokenHelper.generateToken(credentials);
		return Collections.singletonMap("jwt-token", token);
	}

	private void authenticate(String username, String password) throws Exception {
		UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username,
				password);
		try {
			this.authenticationManager.authenticate(authenticationToken);
		} catch (BadCredentialsException e) {
			System.out.println("Invalid Detials !!");
			throw new APIException("Invalid username or password !!");
		}

	}

}