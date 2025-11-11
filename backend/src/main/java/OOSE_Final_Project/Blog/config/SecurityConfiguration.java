package OOSE_Final_Project.Blog.config;

import OOSE_Final_Project.Blog.service.impl.CustomOAuth2UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity()
@EnableMethodSecurity(securedEnabled = true, jsr250Enabled = true)
public class SecurityConfiguration {

        @Bean
        public PasswordEncoder passwordEncoder() {
                return new BCryptPasswordEncoder();
        }

        private static final String[] PUBLIC_URLS = { "/api/v1/auth/**", "/api/v1/otp/**" };

        @Autowired
        private CustomOAuth2UserService customOAuth2UserService;

        @Autowired
        private OAuth2SuccessHandler successHandler;

        @Bean
        public SecurityFilterChain securityFilterChain(
                        HttpSecurity http, CustomAuthenticationEntryPoint customAuthenticationEntryPoint)
                        throws Exception {
                http
                                .cors(withDefaults())
                                .csrf(csrf -> csrf.disable()) // Tắt CSRF để đơn giản hóa
                                .authorizeHttpRequests(authorize -> authorize
                                                // .requestMatchers(HttpMethod.GET, PUBLIC_URLS)
                                                // .permitAll() // Cho phép tất cả yêu cầu mà
                                                // không cần xác thực
                                                // .requestMatchers(HttpMethod.POST, PUBLIC_URLS)
                                                // .permitAll() // Cho phép tất cả yêu cầu mà
                                                // không cần xác thực
                                                .anyRequest()
                                                .permitAll()
                                // .authenticated()

                                )
                                .oauth2ResourceServer(oauth2 -> oauth2.jwt(withDefaults())
                                                .authenticationEntryPoint(
                                                                customAuthenticationEntryPoint)
                                                .jwt(jwt -> jwt.jwtAuthenticationConverter(
                                                                jwtAuthenticationConverter())))
                                .oauth2Login(oauth2 -> oauth2.userInfoEndpoint(userInfo -> userInfo.userService(
                                                customOAuth2UserService))
                                                .successHandler(successHandler))
                                .exceptionHandling(exception -> exception
                                                .authenticationEntryPoint(customAuthenticationEntryPoint))
                                .formLogin(f -> f.disable())
                                .sessionManagement(
                                                session -> session.sessionCreationPolicy(
                                                                SessionCreationPolicy.STATELESS));

                return http.build();
        }

        @Bean
        public JwtAuthenticationConverter jwtAuthenticationConverter() {
                JwtGrantedAuthoritiesConverter grantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
                grantedAuthoritiesConverter.setAuthorityPrefix("ROLE_");
                grantedAuthoritiesConverter.setAuthoritiesClaimName("permission");

                JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
                jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(grantedAuthoritiesConverter);
                return jwtAuthenticationConverter;
        }

}
