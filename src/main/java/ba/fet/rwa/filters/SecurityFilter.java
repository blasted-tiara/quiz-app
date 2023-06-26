package ba.fet.rwa.filters;

import ba.fet.rwa.models.User;
import ba.fet.rwa.services.UserService;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebFilter(urlPatterns = {"/admin/*", "/api/admin/*"})
public class SecurityFilter implements Filter {

    UserService userService = new UserService();
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpSession session = httpRequest.getSession(false);

        boolean isLoggedIn = (session != null && session.getAttribute("currentUser") != null);

        User currentUser;
        boolean isAdmin = false;
        if (isLoggedIn) {
            currentUser = userService.getUserByUsername(session.getAttribute("currentUser").toString());
            isAdmin = currentUser.getRoles().contains(User.Role.ADMIN);
        }

        String loginURI = httpRequest.getContextPath() + "/login";

        boolean isLoginRequest = httpRequest.getRequestURI().equals(loginURI);

        boolean isLoginPage = httpRequest.getRequestURI().endsWith("login.html");

        if (isAdmin && (isLoginRequest || isLoginPage)) {
            // the admin is already logged in and he's trying to login again
            // then forwards to the admin's homepage
            RequestDispatcher dispatcher = request.getRequestDispatcher("/admin/index.html");
            dispatcher.forward(request, response);

        } else if (isAdmin || isLoginRequest) {
            // continues the filter chain
            // allows the request to reach the destination
            chain.doFilter(request, response);

        } else {
            // the admin is not logged in, so authentication is required
            // forwards to the Login page
            //RequestDispatcher dispatcher = request.getRequestDispatcher("/login.html");
            //dispatcher.forward(request, response);
            HttpServletResponse httpServletResponse = (HttpServletResponse) response;
            httpServletResponse.sendRedirect("/login.html");
        }

    }

    public SecurityFilter() {
    }

    public void destroy() {
    }

    public void init(FilterConfig fConfig) throws ServletException {
    }

}
