package org.example.channelservice.filter;


import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Objects;

public class CustomFilter extends OncePerRequestFilter {
    //    private final String SECRET_KEY = "qwefklsdfj3roif134adsj14fdslkjf1234fiodwq";
//
//    @Override
//    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
//        String authorizationHeader = request.getHeader("Authorization");
//        // Token mavjudligini tekshirish
//        if (Objects.isNull(authorizationHeader) || !authorizationHeader.startsWith("Bearer ")) {
//            filterChain.doFilter(request, response);
//            return;
//        }
//
//        String token = authorizationHeader.substring(7);
//        try {
//            // JWT dan username olish
//            Claims claims = Jwts.parser()
//                    .setSigningKey(SECRET_KEY)
//                    .parseClaimsJws(token)
//                    .getBody();
//
//            String username = claims.getSubject();
//
//            // Foydalanuvchini SecurityContext ga qo'shish
//            UsernamePasswordAuthenticationToken auth =
//                    new UsernamePasswordAuthenticationToken(username, null, null);
//            SecurityContextHolder.getContext().setAuthentication(auth);
//        } catch (Exception e) {
//            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
//            return;
//        }
//
//        filterChain.doFilter(request, response);
//    }
//}





    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String username = request.getHeader("Authorization");
        if (Objects.isNull(username)) {
            filterChain.doFilter(request, response);
            return;
        }
        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken
                        (username, null, null);

        SecurityContextHolder.getContext().setAuthentication(auth);
        filterChain.doFilter(request, response);
    }
}



