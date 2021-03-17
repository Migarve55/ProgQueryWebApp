package es.uniovi.security;

public class JwtSecurityConstants {

	public static final String SECRET = "H4K3RS3CR3T";
	public static final long EXPIRATION_TIME = 3600 * 1000; // 1 hour
	public static final String TOKEN_PREFIX = "Bearer";
	public static final String HEADER = "Authorization";
	public static final String SIGN_UP_URL = "/api/login";
	public static final String ISSUER_INFO = "ProgQuery";
	
}
