package org.example.channelservice.config;
import org.example.channelservice.filter.CustomFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {
    private final String[] WHITE_LIST = {"/api/auth/login", "/api/auth/register", "/api/auth/swagger-ui/", "/api/auth/v3/api-docs/"};

    private final String[] WHITE_LIST = {"/api/auth/login", "/api/auth/register", "/api/auth/swagger-ui/**", "/api/auth/v3/api-docs/**"};

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
               .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorizeRequests ->
                        authorizeRequests
                                .requestMatchers(WHITE_LIST).permitAll()
                                .anyRequest().authenticated()
                )
                .addFilterBefore(new CustomFilter(),
                        UsernamePasswordAuthenticationFilter.class)
                .build();
    }
}

