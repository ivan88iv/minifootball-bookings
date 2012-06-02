package com.futsall.filters;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

public class ViewExpiredFilter implements Filter{
	public void doFilter(ServletRequest request, ServletResponse response, 
			FilterChain chain) throws ServletException, IOException {
		try {
			HttpServletResponse res = (HttpServletResponse) response;
		    res.setHeader("Cache-Control", "no-cache, no-store, must-revalidate"); // HTTP 1.1.
		    res.setHeader("Pragma", "no-cache"); // HTTP 1.0.
		    res.setDateHeader("Expires", 0); // Proxies.
			chain.doFilter(request, response);
		} catch (ServletException e) {
			Throwable rootCause = e.getRootCause();
			if (rootCause instanceof RuntimeException) { 
				// This is true for any FacesException.
				// Throw wrapped RuntimeException instead of ServletException.
				throw (RuntimeException) rootCause; 
			} else {
				throw e;
			}
		} 
	}

	@Override
	public void destroy() {
		// TODO Auto-generated method stub
	}

	@Override
	public void init(FilterConfig arg0) throws ServletException {
		// TODO Auto-generated method stub
	}
}

