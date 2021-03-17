package es.uniovi.security;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

public class JWTAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	public JWTAuthenticationFilter(AuthenticationManager authenticationManager) {
		setFilterProcessesUrl(JwtSecurityConstants.SIGN_UP_URL);
		setAuthenticationManager(authenticationManager);
	}

	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
			throws AuthenticationException {
		try {
			JwtUser credentials = getJWTUserFromRequest(request);
			List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
			authorities.add(new SimpleGrantedAuthority("USER"));
			return this.getAuthenticationManager().authenticate(
					new UsernamePasswordAuthenticationToken(
							credentials.getUsername(),
							credentials.getPassword(), 
							authorities));
		} catch (JsonMappingException | JsonParseException je) {
			throw new BadCredentialsException("Bad request: " + je.getMessage());
		} catch (IOException e) {
			throw new RuntimeException();
		}
	}
	
	private JwtUser getJWTUserFromRequest(HttpServletRequest request) throws IOException {
		return new ObjectMapper().readValue(request.getInputStream(), JwtUser.class);
	}

	@Override
	protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
			Authentication auth) throws IOException, ServletException {
		String subject = ((User) auth.getPrincipal()).getUsername();
		logger.info("Generated API token for user {}", subject);
		String token = Jwts.builder().setIssuedAt(new Date()).setIssuer(JwtSecurityConstants.ISSUER_INFO)
				.claim("authorities", 
						auth.getAuthorities()
						.stream().map(GrantedAuthority::getAuthority)
						.collect(Collectors.toList()))
				.setSubject(subject)
				.setExpiration(new Date(System.currentTimeMillis() + JwtSecurityConstants.EXPIRATION_TIME))
				.signWith(SignatureAlgorithm.HS512, JwtSecurityConstants.SECRET).compact();
		response.addHeader(JwtSecurityConstants.HEADER, JwtSecurityConstants.TOKEN_PREFIX + " " + token);
	}

	@Override
	protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException failed) throws IOException, ServletException {
		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
	}

}