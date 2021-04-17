package es.uniovi.security;

import java.util.Arrays;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
@Order(Ordered.HIGHEST_PRECEDENCE)
public class ApiSecurityConfig extends WebSecurityConfigurerAdapter {

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http
	            .requiresChannel().anyRequest().requiresSecure()
				.and()
            	.antMatcher("/api/**")
	            	.csrf().disable()
	            	.cors().and()
	            	.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
	            	.authorizeRequests()
						.antMatchers(HttpMethod.POST, JwtSecurityConstants.SIGN_UP_URL).permitAll()
						.antMatchers("/api/**").hasAnyAuthority("USER")
						.anyRequest().authenticated()
					.and()
						.addFilterBefore(new JWTAuthenticationFilter(authenticationManager()), UsernamePasswordAuthenticationFilter.class)
						.addFilterBefore(new JWTAuthorizationFilter(authenticationManager()), UsernamePasswordAuthenticationFilter.class)
					.headers().frameOptions().sameOrigin().cacheControl(); // No cache
            
        }
        
        @Bean
        public CorsConfigurationSource corsConfigurationSource() {
            CorsConfiguration configuration = new CorsConfiguration();
            configuration.setAllowedOrigins(Arrays.asList("*"));
            configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE"));
            configuration.setAllowedHeaders(Arrays.asList("*"));
            configuration.setExposedHeaders(Arrays.asList("Authorization"));
            configuration.getAllowCredentials();
            configuration.setMaxAge(3600L);
            UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
            source.registerCorsConfiguration("/api/**", configuration);
            return source; 
       }
        
    	@Bean
    	@Override
    	public AuthenticationManager authenticationManagerBean() throws Exception {
    		return super.authenticationManagerBean();
    	}
    	

}
