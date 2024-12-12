package org.example.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
//import org.springframework.http.HttpMethod;
//import org.springframework.security.config.Customizer;
//import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
//import org.springframework.security.config.web.server.ServerHttpSecurity;
//import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
//
import java.util.Arrays;
//import java.util.List;
//
//@EnableWebFluxSecurity
@Configuration
public class SecurityConfig {

//    @Bean
//    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
//        return http
//                .authorizeExchange(auth ->
//                        auth
//                                .pathMatchers(HttpMethod.POST, "/books").authenticated()
//                                .pathMatchers(HttpMethod.GET, "/books").authenticated()
//                                .pathMatchers("/reviews/**").permitAll()
//                )
//                // .oauth2Login(Customizer.withDefaults())
//                .build();
//    }
//

//    @Bean
//    UrlBasedCorsConfigurationSource corsConfigurationSource() {
//        CorsConfiguration configuration = new CorsConfiguration();
//        configuration.setAllowedOrigins(Arrays.asList("http://localhost:3000"));
//        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "HEAD", "OPTIONS", "PATCH"));
//        configuration.setAllowCredentials(true);
//        configuration.setAllowedHeaders(
//                Arrays.asList("Authorization", "Cache-Control", "Content-Type", "Origin"));
//        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//        source.registerCorsConfiguration("/**", configuration);
//        return source;
//    }

}
