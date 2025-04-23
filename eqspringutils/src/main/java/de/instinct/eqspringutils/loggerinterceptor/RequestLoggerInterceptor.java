package de.instinct.eqspringutils.loggerinterceptor;

import java.util.Enumeration;

import org.apache.commons.io.IOUtils;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;

import ch.qos.logback.classic.Logger;
import de.instinct.eqspringutils.loggerinterceptor.wrapper.CachedBodyHttpServletRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Configuration
public class RequestLoggerInterceptor implements HandlerInterceptor {
	
	private static Logger LOGGER = (Logger) LoggerFactory.getLogger(RequestLoggerInterceptor.class);
	private int MESSAGE_MAX_LENGTH = 500;

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		CachedBodyHttpServletRequest cachedRequest = new CachedBodyHttpServletRequest(request);
		String requestLog = "[preHandle][" + cachedRequest.getMethod() + "]" + cachedRequest.getRequestURI()
							+ getParameters(cachedRequest) + " - Body: " + IOUtils.toString(cachedRequest.getReader());
		if (!cachedRequest.getRequestURI().contains("save")) {
			LOGGER.info(limitWithAppendix(requestLog));
		}
		return true;
	}
	
	private String limitWithAppendix(String message) {
		return message.length() > MESSAGE_MAX_LENGTH ? message.substring(0, MESSAGE_MAX_LENGTH) + "... (cut string length: " + (message.length() - MESSAGE_MAX_LENGTH) + ")" : message;
	}

	private String getParameters(final HttpServletRequest request) {
		final StringBuffer posted = new StringBuffer();
		final Enumeration<?> e = request.getParameterNames();

		while (e != null && e.hasMoreElements()) {
			if (posted.length() > 1)
				posted.append("&");
			final String curr = (String) e.nextElement();
			posted.append(curr).append("=");
			if (curr.contains("password") || curr.contains("answer") || curr.contains("pwd")) {
				posted.append("*****");
			} else {
				posted.append(request.getParameter(curr));
			}
		}

		return posted.toString();
	}

}
