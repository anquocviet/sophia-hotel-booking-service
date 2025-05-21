package vn.edu.iuh.bookingservice.utils;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * @description Utility class for security-related operations
 * @author: vie
 * @date: 27/6/25
 */
@Component
public class SecurityUtils {

    /**
     * Get the current user's authentication token from the security context
     * @return the bearer token or null if not available
     */
    public static String getCurrentUserToken() {
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                String authHeader = request.getHeader("Authorization");
                if (authHeader != null && authHeader.startsWith("Bearer ")) {
                    return authHeader.substring(7);
                }
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }
    
    /**
     * Get the current user ID from security context
     * @return the user ID
     */
    public static String getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            return authentication.getName();
        }
        return null;
    }
    
    /**
     * Get the full authorization header for service-to-service communication
     * @return the complete authorization header with Bearer prefix or null
     */
    public static String getAuthorizationHeader() {
        String token = getCurrentUserToken();
        if (token != null) {
            return "Bearer " + token;
        }
        return null;
    }
}
