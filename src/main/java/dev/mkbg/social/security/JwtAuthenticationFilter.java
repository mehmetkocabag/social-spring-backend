package dev.mkbg.social.security;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        try {
            String jwt = parseJwt(request);
            if (jwt != null) {
                String username = null;
                try {
                    username = jwtTokenUtil.extractUsername(jwt);
                } catch (JwtException e) {
                    logger.debug("Invalid JWT for [{} {}]: {}", request.getMethod(), request.getRequestURI(), e.getMessage());
                }

                if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                    try {
                        if (jwtTokenUtil.validateToken(jwt, userDetails)) {
                            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                                    userDetails, null, userDetails.getAuthorities());
                            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                            SecurityContextHolder.getContext().setAuthentication(authentication);
                        }
                    } catch (JwtException e) {
                        logger.debug("JWT validation failed for [{} {}]: {}", request.getMethod(), request.getRequestURI(), e.getMessage());
                    }
                }
            }
        } catch (Exception e) {
            logger.error("Unexpected error in JwtAuthenticationFilter", e);
        }

        filterChain.doFilter(request, response);
    }

    private String parseJwt(HttpServletRequest request) {
        String headerAuth = request.getHeader("Authorization");
        if (!StringUtils.hasText(headerAuth)) {
            return null;
        }

        headerAuth = headerAuth.trim();
        if (headerAuth.length() >= 7 && headerAuth.regionMatches(true, 0, "Bearer ", 0, 7)) {
            String token = headerAuth.substring(7).trim();
            return token.isEmpty() ? null : token;
        }

        return null;
    }
}