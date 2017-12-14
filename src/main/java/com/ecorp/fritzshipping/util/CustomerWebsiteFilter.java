package com.ecorp.fritzshipping.util;

import com.ecorp.fritzshipping.model.LoginModel;
import java.io.IOException;
import javax.inject.Inject;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Filter to restrict access on the customer website.
 */
public class CustomerWebsiteFilter implements Filter{
    @Inject
    private LoginModel loginModel;
    
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        if(loginModel.isLoggedIn()) {
            // let the user proceed
            chain.doFilter(request, response);
        } else {
            // Redirect the user to the login page
            String contextPath = ((HttpServletRequest) request).getContextPath();
            ((HttpServletResponse) response).sendRedirect(contextPath + "/customer-website/login.xhtml");
        }
    }
    
    @Override
    public void destroy() {}
    
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {}
}
