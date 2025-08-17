package com.proxyproject.shop.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

public class JwtAuthFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(JwtAuthFilter.class);
    private final JwtService jwt;

    // Rutas públicas que deben pasar sin autenticar ni 401
    private static final List<String> PUBLIC_PATH_PREFIXES = List.of(
            "/v3",            // /v3/api-docs...
            "/swagger-ui",    // UI
            "/swagger",       // /swagger (shortcut)
            "/auth",          // /auth/**
            "/api/auth",      // /api/auth/**
            "/h2-console"     // H2
    );

    public JwtAuthFilter(JwtService jwt) {
        this.jwt = jwt;
    }

    private boolean isPublic(String uri) {
        for (String p : PUBLIC_PATH_PREFIXES) {
            if (uri.startsWith(p)) return true;
        }
        return false;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest req,
                                    HttpServletResponse res,
                                    FilterChain chain) throws ServletException, IOException {
        String uri = req.getRequestURI();

        // 1) Rutas públicas -> seguir sin tocar contexto
        if (isPublic(uri)) {
            chain.doFilter(req, res);
            return;
        }

        // 2) Si no hay Authorization Bearer, no autenticamos (deja que las reglas decidan)
        String auth = req.getHeader("Authorization");
        if (auth == null || !auth.startsWith("Bearer ")) {
            log.debug("No Bearer header for {}", uri);
            chain.doFilter(req, res);
            return;
        }

        // 3) Hay token: validar. Si falla, NO respondemos 401 aquí; dejamos que Security maneje.
        try {
            var jws = jwt.parse(auth.substring(7).trim());
            var subject = jws.getBody().getSubject();

            @SuppressWarnings("unchecked")
            var roles = (List<Object>) jws.getBody().get("roles");
            var authorities = roles == null
                    ? List.<SimpleGrantedAuthority>of()
                    : roles.stream().map(r -> new SimpleGrantedAuthority(String.valueOf(r))).toList();

            var authentication = new UsernamePasswordAuthenticationToken(subject, null, authorities);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (Exception e) {
            log.warn("JWT parse failed: {}", e.getMessage());
            // seguimos la cadena; access-control lo decide SecurityConfig
        }

        chain.doFilter(req, res);
    }
}
