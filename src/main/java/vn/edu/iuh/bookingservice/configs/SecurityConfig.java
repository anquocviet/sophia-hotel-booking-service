package vn.edu.iuh.bookingservice.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;
import vn.edu.iuh.bookingservice.filters.JWTAuthenticationFilter;

/**
 * @description
 * @author: vie
 * @date: 8/4/25
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

   private final CorsConfigurationSource corsConfigurationSource;

   public SecurityConfig(CorsConfigurationSource corsConfigurationSource) {
      this.corsConfigurationSource = corsConfigurationSource;
   }

   @Bean
   public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
      http.csrf(AbstractHttpConfigurer::disable)
          .cors(cors -> cors.configurationSource(corsConfigurationSource))
          .authorizeHttpRequests(authorizeRequests -> authorizeRequests
              .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
              .anyRequest().authenticated()
          )
          .addFilterBefore(new JWTAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
          .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
      return http.build();
   }
}
