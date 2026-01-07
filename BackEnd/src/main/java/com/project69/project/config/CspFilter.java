package com.project69.project.config;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * Servlet filter that adds a Content-Security-Policy (CSP) header
 * with a per-request nonce for inline scripts.
 */
@Component
public class CspFilter implements Filter {

    private static final SecureRandom secureRandom = new SecureRandom();
    private static final Base64.Encoder base64Encoder = Base64.getUrlEncoder().withoutPadding();

    private String generateNonce() {
        byte[] randomBytes = new byte[16];
        secureRandom.nextBytes(randomBytes);
        return base64Encoder.encodeToString(randomBytes);
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        String nonce = generateNonce();
        HttpServletResponse res = (HttpServletResponse) response;

        // Strong CSP header with per-request nonce
        String csp = "default-src 'self'; " +
                "script-src 'self' 'nonce-" + nonce + "'; " +
                "style-src 'self' 'unsafe-inline'; " + // tighten later if possible
                "img-src 'self' data:; " +
                "connect-src 'self' https://localhost:4200 https://localhost:8004 https://127.0.0.1:8004; " +
                "object-src 'none'; " +
                "base-uri 'self'; " +
                "frame-ancestors 'none';";

        res.setHeader("Content-Security-Policy", csp);

        // Make nonce available to templates or frontend
        request.setAttribute("cspNonce", nonce);

        chain.doFilter(request, response);
    }
}