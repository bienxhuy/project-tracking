package POSE_Project_Tracking.Blog.config;

import POSE_Project_Tracking.Blog.util.SecurityUtil;
import com.nimbusds.jose.jwk.source.ImmutableSecret;
import com.nimbusds.jose.util.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import static POSE_Project_Tracking.Blog.util.SecurityUtil.JWT_ALGORITHM;

@Configuration
public class JwtConfig {
    @Value("${secrect-key}")
    private String jwtKey;

    @Value("${jwt.access-token-valid-in-seconds}")
    private long jwtValidInSeconds;



    @Bean
    public JwtEncoder jwtEncoder() {
        return new NimbusJwtEncoder(new ImmutableSecret<>(getSecretKey()));
    }


    public SecretKey getSecretKey() {
        byte[] keyBytes = Base64.from(jwtKey)
                                .decode();
        return new SecretKeySpec(keyBytes, 0, keyBytes.length, JWT_ALGORITHM.getName());
    }

    @Bean
    public JwtDecoder jwtDecoder() {
        NimbusJwtDecoder jwtDecoder = NimbusJwtDecoder.withSecretKey(
                                                              getSecretKey())
                                                      .macAlgorithm(SecurityUtil.JWT_ALGORITHM)
                                                      .build();
        return token -> {
            try {
                return jwtDecoder.decode(token);
            }
            catch (Exception e) {
                System.out.println(">>> JWT error: " + e.getMessage());
                throw e;
            }
        };
    }


}
