package es.uniovi.security;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

public class JWTAuthorizationFilter extends BasicAuthenticationFilter {

	public JWTAuthorizationFilter(AuthenticationManager authManager) {
		super(authManager);
	}

	@Override
	protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
			throws IOException, ServletException {
		String header = req.getHeader(JwtSecurityConstants.HEADER);
		if (header == null || !header.startsWith(JwtSecurityConstants.TOKEN_PREFIX)) {
			res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		}
		try {
			UsernamePasswordAuthenticationToken authentication = getAuthentication(req);
			SecurityContextHolder.getContext().setAuthentication(authentication);
			chain.doFilter(req, res);
		} catch (Exception e) {
			res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		}
	}

	private UsernamePasswordAuthenticationToken getAuthentication(HttpServletRequest request) {
		String token = request.getHeader(JwtSecurityConstants.HEADER);
		if (token != null) {
			Claims claims = Jwts.parser()
					.setSigningKey(JwtSecurityConstants.SECRET)
					.parseClaimsJws(token.replace(JwtSecurityConstants.TOKEN_PREFIX, "")).getBody();
			// Get claims values
			String user = claims.getSubject();
			@SuppressWarnings("unchecked")
			List<String> auths = claims.get("authorities", List.class);
			if (user != null) {
				List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
				for (String auth : auths)
					authorities.add(new SimpleGrantedAuthority(auth));
				return new UsernamePasswordAuthenticationToken(user, null, authorities);
			}
			return null;
		}
		return null;
	}
}