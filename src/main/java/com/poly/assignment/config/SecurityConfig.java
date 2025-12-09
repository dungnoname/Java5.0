package com.poly.assignment.config;

import com.poly.assignment.security.CustomUserDetailsService;
import com.poly.assignment.security.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;


import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    // 1. Cấu hình Password Encoder
    // LƯU Ý: Dùng NoOp vì DB của bạn đang lưu pass là "123" (chưa mã hóa).
    // Khi nào chạy xong, bạn hãy đổi sang BCryptPasswordEncoder sau.
    @Bean
    public PasswordEncoder passwordEncoder() {
        return NoOpPasswordEncoder.getInstance();
    }

    // 2. Cấu hình AuthenticationManager
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    // 3. Cấu hình Provider (Liên kết UserDetailsService và PasswordEncoder)
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    // 4. Cấu hình Filter Chain (Cốt lõi bảo mật)
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // Tắt CSRF
                .cors(Customizer.withDefaults()) // Bật CORS mặc định
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // Không lưu Session
                .authorizeHttpRequests(auth -> auth
                        // Cho phép truy cập công khai Login và các file tĩnh
                        .requestMatchers("/api/auth/**").permitAll()

                        // chỉ được xem
                        .requestMatchers(HttpMethod.GET, "/api/products/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/products/categories").permitAll()

                        //chỉ admin mới có thể sửa
                        .requestMatchers(HttpMethod.POST, "/api/products/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/products/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/products/**").hasRole("ADMIN")

                        //trang quản trị dành cho admin
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")

                        // Giỏ hàng (Xem, Thêm, Xóa, Sửa)
                        .requestMatchers("/api/cart/**").authenticated()

                        // Đơn hàng (Đặt hàng, Xem lịch sử, Hủy đơn)
                        .requestMatchers("/api/orders/**").authenticated()

                        // Thông tin cá nhân (Xem profile, Đổi mật khẩu)
                        .requestMatchers("/api/profile/**").authenticated()


                        // Những đường dẫn lạ nào chưa khai báo ở trên thì mặc định phải đăng nhập mới được vào
                        .anyRequest().authenticated()
                );

        // Đặt JwtFilter ĐỨNG TRƯỚC UsernamePasswordAuthenticationFilter
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        // Đăng ký Provider
        http.authenticationProvider(authenticationProvider());

        return http.build();
    }

    // 5. Cấu hình CORS chi tiết cho Vue.js
    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();

        // Cho phép credentials (cookies, headers authorization)
        config.setAllowCredentials(true);
        // Chỉ cho phép cổng của Vue
        config.setAllowedOrigins(List.of("http://localhost:5173"));
        // Cho phép mọi header
        config.addAllowedHeader("*");
        // Cho phép mọi method (GET, POST, PUT, DELETE...)
        config.addAllowedMethod("*");

        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }
}