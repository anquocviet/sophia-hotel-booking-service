package vn.edu.iuh.bookingservice.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.web.client.RestTemplate;
import vn.edu.iuh.bookingservice.utils.SecurityUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @description Application configuration
 * @author: vie
 * @date: 27/6/25
 */
@Configuration
public class AppConfig {
    
    @Bean
    public RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        
        // Add an interceptor for service-to-service communication
        // This is alternative to manually adding headers in each request
        List<ClientHttpRequestInterceptor> interceptors = new ArrayList<>();
        interceptors.add((request, body, execution) -> {
            String authHeader = SecurityUtils.getAuthorizationHeader();
            if (authHeader != null) {
                request.getHeaders().set("Authorization", authHeader);
            }
            return execution.execute(request, body);
        });
        
        restTemplate.setInterceptors(interceptors);
        return restTemplate;
    }
}
