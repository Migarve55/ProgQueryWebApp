package es.uniovi;

import java.io.IOException;
import java.util.Date;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

public class JWTAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
	
	public static final byte[] SUPER_SECRET_KEY = "H4K3RS3CR3T".getBytes();
	public static final String HEADER_AUTHORIZACION_KEY = "Authorization";
	public static final String TOKEN_BEARER_PREFIX = "Bearer";
	
	private static final long TOKEN_EXPIRATION_TIME = 1000 * 60 * 30; //30 minutes
	private static final String ISSUER_INFO = "ProgQuery";

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	public JWTAuthenticationFilter(AuthenticationManager authenticationManager) {
		setFilterProcessesUrl("/api/login");
		setAuthenticationManager(authenticationManager);
	}

	@Override	
	protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
			Authentication auth) throws IOException, ServletException {
		String subject = ((User) auth.getPrincipal()).getUsername();
		logger.info("Generated API token for user {}", subject);
		String token = Jwts.builder().setIssuedAt(new Date()).setIssuer(ISSUER_INFO)
				.setSubject(subject)
				.setExpiration(new Date(System.currentTimeMillis() + TOKEN_EXPIRATION_TIME))
				.signWith(SignatureAlgorithm.HS512, SUPER_SECRET_KEY).compact();
		response.addHeader(HEADER_AUTHORIZACION_KEY, TOKEN_BEARER_PREFIX + " " + token);
	}
	
}