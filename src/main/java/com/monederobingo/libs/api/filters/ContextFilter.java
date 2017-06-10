package com.monederobingo.libs.api.filters;

import static java.util.Arrays.asList;
import static java.util.Objects.nonNull;

import com.monederobingo.libs.common.context.ThreadContextService;
import com.monederobingo.libs.common.environments.Environment;
import com.monederobingo.libs.common.environments.EnvironmentFactory;

import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

@Component
public class ContextFilter extends GenericFilterBean
{
    private final ThreadContextService threadContextService;
    private final EnvironmentFactory environmentFactory;

    @Autowired
    public ContextFilter(ThreadContextService threadContextService, EnvironmentFactory environmentFactory)
    {
        this.threadContextService = threadContextService;
        this.environmentFactory = environmentFactory;
    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException
    {
        final HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        final HttpServletResponse httpServletResponse = (HttpServletResponse) response;
        initializeContext(httpServletRequest, httpServletResponse);
        chain.doFilter(request, response);
    }

    private void initializeContext(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse)
    {
        threadContextService.initializeContext(getEnvironment(httpServletRequest));
        setHeaders(httpServletResponse, httpServletRequest);
    }

    private void setHeaders(HttpServletResponse response, HttpServletRequest request)
    {
        if (isValidClientUrl(request))
        {
            response.addHeader("Access-Control-Allow-Headers", "Content-Type,x-requested-with,Authorization,Api-Key,User-Id,language");
            response.addHeader("Access-Control-Max-Age", "360");
            response.addHeader("Access-Control-Allow-Methods", "GET,POST,DELETE,PUT");
            response.addHeader("Access-Control-Allow-Origin", getClientUrl(request));
            response.addHeader("Access-Control-Allow-Credentials", "true");
        }
    }

    private boolean isValidClientUrl(HttpServletRequest request)
    {
        return asList(
                "http://localhost:8080",
                "http://test.localhost:8080",
                "http://www.monederobingo.com",
                "http://test.monederobingo.com").indexOf(getClientUrl(request)) > -1;
    }

    private String getClientUrl(HttpServletRequest request)
    {
        if (nonNull(request.getHeader("origin")))
        {
            return request.getHeader("origin");
        }
        else if (nonNull(request.getHeader("referer")))
        {
            return request.getHeader("referer");
        }
        return "";
    }

    private Environment getEnvironment(HttpServletRequest request)
    {
        switch (request.getServerName())
        {
            case "services.monederobingo.com":
                return environmentFactory.getProdEnvironment();
            case "test.services.monederobingo.com":
                return environmentFactory.getUatEnvironment();
            case "test.localhost":
                return environmentFactory.getFunctionalTestEnvironment();
            default:
                return environmentFactory.getDevEnvironment();
        }
    }
}
