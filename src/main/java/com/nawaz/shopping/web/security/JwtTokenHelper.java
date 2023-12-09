package com.nawaz.shopping.web.security;

import com.nawaz.shopping.web.Entity.User;
import com.nawaz.shopping.web.config.AppConstants;
import com.nawaz.shopping.web.payloads.UserDTO;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtTokenHelper {
	private static final String SECRET_KEY = "QrY/tnOqtPjDa5gSoHQ2dA0uQwSLKidLHJfU/l9O2Hw9/eF5rOzIIE9+a2OmbTnF";
	
	// extract Username from Token
	public String getUsernameFromToken(String token) {
		return getClaimFromToken(token, Claims::getSubject);
	}

	// extract Expiration date from Token
	private Date getExpirationDateFromToken(String token) {
		return getClaimFromToken(token, Claims::getExpiration);
	}

	// extract any claim from Token
	public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
		final Claims claims = extractAllClaims(token);
		return claimsResolver.apply(claims);
	}
	
	// extract all claims from Token
	private Claims extractAllClaims(String token) {
		return Jwts
				.parserBuilder()
				.setSigningKey(getSignInKey())
				.build()
				.parseClaimsJws(token)
				.getBody();
	}
	
	// get sign in key
	private Key getSignInKey() {
		byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
		return Keys.hmacShaKeyFor(keyBytes);
	}
	
	// check if Token is expired
	private boolean isTokenExpired(String token) {
		return getExpirationDateFromToken(token).before(new Date());
	}
	
	// generate token from UserDetails
	public String generateToken(UserDTO userDTO) {
		Map<String, Object> claims = new HashMap<>();
		if(userDTO.getEmail() != null) {
			claims.put("userEmail", userDTO.getEmail());
		}
		
		return generateToken(claims, userDTO.getEmail());
	}
	
	// Implementation of generating Token
	public String generateToken(Map<String, Object> extraClaims, String subject) {
		return Jwts
				.builder()
				.setClaims(extraClaims)
				.setSubject(subject)
				.setIssuedAt(new Date(System.currentTimeMillis()))
				.setExpiration(new Date(System.currentTimeMillis() + AppConstants.JWT_TOKEN_VALIDITY * 100))
				.signWith(getSignInKey(), SignatureAlgorithm.HS256)
				.compact();
	}
	
	// validate Token
	public boolean validateToken(String token, UserDetails userDetails) {
		final String username = getUsernameFromToken(token);
		return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
	}
}
