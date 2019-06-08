package es.uniovi;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import es.uniovi.entities.User;
import es.uniovi.services.UsersService;

public class UserSessionHandler extends HandlerInterceptorAdapter {

	@Autowired
	private HttpSession session;
	
	@Autowired
	private UsersService userService;
	
	@Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		super.preHandle(request, response, handler);
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = userService.getUserByEmail(auth.getName());
		session.setAttribute("currentUser", user);
        return true;
    }
}
