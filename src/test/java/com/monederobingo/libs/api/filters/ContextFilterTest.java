package com.monederobingo.libs.api.filters;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import com.monederobingo.libs.common.context.ThreadContextService;
import com.monederobingo.libs.common.environments.DevEnvironment;
import com.monederobingo.libs.common.environments.EnvironmentFactory;
import com.monederobingo.libs.common.environments.FunctionalTestEnvironment;
import com.monederobingo.libs.common.environments.ProdEnvironment;
import com.monederobingo.libs.common.environments.UATEnvironment;
import com.monederobingo.libs.common.environments.UnitTestEnvironment;

import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ContextFilterTest
{
    private ContextFilter contextFilter;
    @Mock
    private ThreadContextService threadContextService;
    @Mock
    private EnvironmentFactory environmentFactory;
    @Mock
    private HttpServletRequest httpServletRequest;
    @Mock
    private HttpServletResponse httpServletResponse;
    @Mock
    private FilterChain filterChain;
    @Mock
    private ProdEnvironment prodEnvironment;
    @Mock
    private UATEnvironment uatEnvironment;
    @Mock
    private UnitTestEnvironment unitTestEnvironment;
    @Mock
    private DevEnvironment devEnvironment;
    @Mock
    private FunctionalTestEnvironment functionalTestEnvironment;

    @Before
    public void setUp() throws Exception
    {
        contextFilter = new ContextFilter(threadContextService, environmentFactory);
        given(httpServletRequest.getServerName()).willReturn("localhost");
        given(environmentFactory.getProdEnvironment()).willReturn(prodEnvironment);
        given(environmentFactory.getUatEnvironment()).willReturn(uatEnvironment);
        given(environmentFactory.getFunctionalTestEnvironment()).willReturn(functionalTestEnvironment);
        given(environmentFactory.getDevEnvironment()).willReturn(devEnvironment);
    }

    @Test
    public void shouldCallInitializeContext() throws IOException, ServletException
    {
        // when
        contextFilter.doFilter(httpServletRequest, httpServletResponse, filterChain);

        // then
        verify(threadContextService).initializeContext(any());
    }

    @Test
    public void shouldInitializeProdEnvironmentContext() throws IOException, ServletException
    {
        // given
        given(httpServletRequest.getServerName()).willReturn("prod.monederobingo.com");

        // when
        contextFilter.doFilter(httpServletRequest, httpServletResponse, filterChain);

        // then
        verify(threadContextService).initializeContext(any(ProdEnvironment.class));
    }

    @Test
    public void shouldInitializeUATEnvironmentContext() throws IOException, ServletException
    {
        // given
        given(httpServletRequest.getServerName()).willReturn("uat.services.monederobingo.com");

        // when
        contextFilter.doFilter(httpServletRequest, httpServletResponse, filterChain);

        // then
        verify(threadContextService).initializeContext(any(UATEnvironment.class));
    }

    @Test
    public void shouldInitializeFunctionalTestEnvironmentContext() throws Exception
    {
        // given
        given(httpServletRequest.getServerName()).willReturn("test.localhost");

        // when
        contextFilter.doFilter(httpServletRequest, httpServletResponse, filterChain);

        // then
        verify(threadContextService).initializeContext(any(FunctionalTestEnvironment.class));
    }

    @Test
    public void shouldInitializeDevEnvironmentContext() throws IOException, ServletException
    {
        // given
        given(httpServletRequest.getServerName()).willReturn("localhost");

        // when
        contextFilter.doFilter(httpServletRequest, httpServletResponse, filterChain);

        // then
        verify(threadContextService).initializeContext(any(DevEnvironment.class));
    }

    @Test
    public void shouldInitializeDevEnvironmentContextForUnknownServerName() throws IOException, ServletException
    {
        // given
        given(httpServletRequest.getServerName()).willReturn("Unknown");

        // when
        contextFilter.doFilter(httpServletRequest, httpServletResponse, filterChain);

        // then
        verify(threadContextService).initializeContext(any(DevEnvironment.class));
    }

    @Test
    public void shouldSetAccessControlHeadersForValidClientsUsingOrigin() throws IOException, ServletException
    {
        verifyAccessControlHeadersUsingOrigin("http://localhost:8080");
        verifyAccessControlHeadersUsingOrigin("http://test.localhost:8080");
        verifyAccessControlHeadersUsingOrigin("http://www.monederobingo.com");
        verifyAccessControlHeadersUsingOrigin("http://test.monederobingo.com");
    }

    private void verifyAccessControlHeadersUsingOrigin(String origin) throws IOException, ServletException
    {
        // given
        given(httpServletRequest.getHeader("origin")).willReturn(origin);

        // when
        contextFilter.doFilter(httpServletRequest, httpServletResponse, filterChain);

        // then
        verify(httpServletResponse, atLeast(1)).addHeader("Access-Control-Allow-Headers", "Content-Type,x-requested-with,Authorization,Api-Key,User-Id,language");
        verify(httpServletResponse, atLeast(1)).addHeader("Access-Control-Max-Age", "360");
        verify(httpServletResponse, atLeast(1)).addHeader("Access-Control-Allow-Methods", "GET,POST,DELETE,PUT");
        verify(httpServletResponse, atLeast(1)).addHeader("Access-Control-Allow-Origin", origin);
        verify(httpServletResponse, atLeast(1)).addHeader("Access-Control-Allow-Credentials", "true");
    }

    @Test
    public void shouldSetAccessControlHeadersForValidClientsUsingReferer() throws IOException, ServletException
    {
        verifyAccessControlHeadersUsingReferer("http://localhost:8080");
        verifyAccessControlHeadersUsingReferer("http://test.localhost:8080");
        verifyAccessControlHeadersUsingReferer("http://www.monederobingo.com");
        verifyAccessControlHeadersUsingReferer("http://test.monederobingo.com");
    }

    private void verifyAccessControlHeadersUsingReferer(String origin) throws IOException, ServletException
    {
        // given
        given(httpServletRequest.getHeader("origin")).willReturn(null);
        given(httpServletRequest.getHeader("referer")).willReturn(origin);

        // when
        contextFilter.doFilter(httpServletRequest, httpServletResponse, filterChain);

        // then
        verify(httpServletResponse, atLeast(1)).addHeader("Access-Control-Allow-Headers", "Content-Type,x-requested-with,Authorization,Api-Key,User-Id,language");
        verify(httpServletResponse, atLeast(1)).addHeader("Access-Control-Max-Age", "360");
        verify(httpServletResponse, atLeast(1)).addHeader("Access-Control-Allow-Methods", "GET,POST,DELETE,PUT");
        verify(httpServletResponse, atLeast(1)).addHeader("Access-Control-Allow-Origin", origin);
        verify(httpServletResponse, atLeast(1)).addHeader("Access-Control-Allow-Credentials", "true");
    }


    @Test
    public void shouldNotSetAccessControlHeadersWhenOriginAndRefererAreNull() throws IOException, ServletException
    {
        verifyAccessControlHeadersNotSetWhenOriginAndRefererAreNull("http://localhost:8080");
        verifyAccessControlHeadersNotSetWhenOriginAndRefererAreNull("http://test.localhost:8080");
        verifyAccessControlHeadersNotSetWhenOriginAndRefererAreNull("http://www.monederobingo.com");
        verifyAccessControlHeadersNotSetWhenOriginAndRefererAreNull("http://test.monederobingo.com");
    }

    private void verifyAccessControlHeadersNotSetWhenOriginAndRefererAreNull(String origin) throws IOException, ServletException
    {
        // given
        given(httpServletRequest.getHeader("origin")).willReturn(null);
        given(httpServletRequest.getHeader("referer")).willReturn(null);

        // when
        contextFilter.doFilter(httpServletRequest, httpServletResponse, filterChain);

        // then
        verify(httpServletResponse, never()).addHeader("Access-Control-Allow-Headers", "Content-Type,x-requested-with,Authorization,Api-Key,User-Id,language");
        verify(httpServletResponse, never()).addHeader("Access-Control-Max-Age", "360");
        verify(httpServletResponse, never()).addHeader("Access-Control-Allow-Methods", "GET,POST,DELETE,PUT");
        verify(httpServletResponse, never()).addHeader("Access-Control-Allow-Origin", origin);
        verify(httpServletResponse, never()).addHeader("Access-Control-Allow-Credentials", "true");
    }

    @Test
    public void shouldNotSetAccessControlHeadersForNonValidClientUrls() throws IOException, ServletException
    {
        // given
        given(httpServletRequest.getHeader("origin")).willReturn("http://not_valid.com");

        // when
        contextFilter.doFilter(httpServletRequest, httpServletResponse, filterChain);

        // then
        verify(httpServletResponse, never()).addHeader("Access-Control-Allow-Headers", "Content-Type,x-requested-with,Authorization,Api-Key,User-Id,language");
        verify(httpServletResponse, never()).addHeader("Access-Control-Max-Age", "360");
        verify(httpServletResponse, never()).addHeader("Access-Control-Allow-Methods", "GET,POST,DELETE,PUT");
        verify(httpServletResponse, never()).addHeader("Access-Control-Allow-Origin", "http://not_valid.com");
        verify(httpServletResponse, never()).addHeader("Access-Control-Allow-Credentials", "true");
    }

    @Test
    public void shouldNotSetAccessControlHeadersForEmptyClientUrls() throws IOException, ServletException
    {
        // given
        given(httpServletRequest.getHeader("origin")).willReturn("");
        // when
        contextFilter.doFilter(httpServletRequest, httpServletResponse, filterChain);

        // then
        verify(httpServletResponse, never()).addHeader("Access-Control-Allow-Headers", "Content-Type,x-requested-with,Authorization,Api-Key,User-Id,language");
        verify(httpServletResponse, never()).addHeader("Access-Control-Max-Age", "360");
        verify(httpServletResponse, never()).addHeader("Access-Control-Allow-Methods", "GET,POST,DELETE,PUT");
        verify(httpServletResponse, never()).addHeader("Access-Control-Allow-Origin", "");
        verify(httpServletResponse, never()).addHeader("Access-Control-Allow-Credentials", "true");
    }
}
