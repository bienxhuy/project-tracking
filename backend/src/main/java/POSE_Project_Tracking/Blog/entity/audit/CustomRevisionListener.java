package POSE_Project_Tracking.Blog.entity.audit;

import jakarta.servlet.http.HttpServletRequest;
import org.hibernate.envers.RevisionListener;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * Custom Revision Listener
 * 
 * This listener is called automatically by Envers whenever a new revision is created.
 * It captures additional context information:
 * - Username from Spring Security context
 * - IP address from HTTP request
 * 
 * This allows us to track WHO made changes and FROM WHERE.
 */
public class CustomRevisionListener implements RevisionListener {
    
    @Override
    public void newRevision(Object revisionEntity) {
        CustomRevisionEntity customRevision = (CustomRevisionEntity) revisionEntity;
        
        // Capture username from Spring Security
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            // Get username (email) from authentication
            String username = authentication.getName();
            customRevision.setUsername(username);
        } else {
            customRevision.setUsername("anonymous");
        }
        
        // Capture IP address from HTTP request
        try {
            ServletRequestAttributes attributes = 
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                String ipAddress = getClientIpAddress(request);
                customRevision.setIpAddress(ipAddress);
            }
        } catch (Exception e) {
            // If we can't get request context (e.g., from async jobs), set null
            customRevision.setIpAddress("unknown");
        }
    }
    
    /**
     * Get client IP address, handling proxies and load balancers
     */
    private String getClientIpAddress(HttpServletRequest request) {
        String[] headerNames = {
            "X-Forwarded-For",
            "Proxy-Client-IP",
            "WL-Proxy-Client-IP",
            "HTTP_X_FORWARDED_FOR",
            "HTTP_X_FORWARDED",
            "HTTP_X_CLUSTER_CLIENT_IP",
            "HTTP_CLIENT_IP",
            "HTTP_FORWARDED_FOR",
            "HTTP_FORWARDED",
            "HTTP_VIA",
            "REMOTE_ADDR"
        };
        
        for (String header : headerNames) {
            String ip = request.getHeader(header);
            if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
                // X-Forwarded-For can contain multiple IPs, take the first one
                if (ip.contains(",")) {
                    ip = ip.split(",")[0];
                }
                return ip.trim();
            }
        }
        
        return request.getRemoteAddr();
    }
}
