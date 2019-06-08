package es.uniovi;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;

public class CustomAuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {

	@Override
	public void onAuthenticationFailure(
			HttpServletRequest request, 
			HttpServletResponse response, 
			AuthenticationException exception) throws IOException, ServletException {
		// Set error code depending on the response
		getRedirectStrategy().sendRedirect(request, response, "/login?error");
		//request.getRequestDispatcher("/login?error=" + errorMsg).forward(request, response);
	}

}
