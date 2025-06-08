package com.poc.microservices.main.app.config;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import com.poc.microservices.main.app.util.TestMASHelper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JwtAuthFilterMASTest {

    @Spy
    TestMASHelper testMASHelper;

    @InjectMocks
    private JwtAuthFilterMAS jwtAuthFilterMAS;

    @Mock
    private FilterChain filterChain;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    private ListAppender<ILoggingEvent> logAppender;

    @BeforeEach
    void setUp() {
//        MockitoAnnotations.openMocks(this); // no need when using @InjectMocks
        System.setProperty("SECRET_KEY", "someUsefulLargeEnoughSecretKeyToBeAtLeast256Bits");//injecting into properties

        // Configure log capturing
        Logger logger = (Logger) org.slf4j.LoggerFactory.getLogger(JwtAuthFilterMAS.class);
        logger.setLevel(Level.DEBUG); // Ensure debug logs are captured
        logAppender = new ListAppender<>();
        logAppender.start();
        logger.addAppender(logAppender);
        SecurityContextHolder.getContext().setAuthentication(null);
    }

    @Test
    void testLogging_WhenValidTokenIsProvided() throws ServletException, IOException {
        when(request.getRequestURI()).thenReturn("/some/uri");
        String validToken = getValidToken();
        when(request.getHeader("Authorization")).thenReturn("Bearer " + validToken);

        jwtAuthFilterMAS.doFilterInternal(request, response, filterChain);
        List<String> logMessages = logAppender.list.stream().map(ILoggingEvent::getFormattedMessage).toList();

        verify(filterChain).doFilter(request, response);

        // Verify the expected log message
        assertTrue(logMessages.contains("Injecting role USER in security context"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"/mas/mas-users/login", "/mas/mas-users/register"})
    void testSkippingJwtAuthentication_ForExcludedEndpoints(String excludedEndpoint) throws ServletException, IOException {
        when(request.getRequestURI()).thenReturn(excludedEndpoint);

        jwtAuthFilterMAS.doFilterInternal(request, response, filterChain);

        // Ensure that no authentication is set
        assertNull(SecurityContextHolder.getContext().getAuthentication());

        // Verify expected log message
        List<String> logMessages = logAppender.list.stream().map(ILoggingEvent::getFormattedMessage).toList();
        assertTrue(logMessages.contains("Skipping JWT authentication for endpoint: " + excludedEndpoint));

        verify(filterChain).doFilter(request, response);
    }

    private String getValidToken() {
        return testMASHelper.getValidToken();
    }
}