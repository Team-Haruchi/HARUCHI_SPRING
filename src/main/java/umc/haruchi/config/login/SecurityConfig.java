package umc.haruchi.config.login;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import umc.haruchi.config.login.jwt.*;

import java.util.Collections;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;
    private final JwtUtil jwtUtil;
    private final JwtTokenService jwtTokenService;

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception{
        return configuration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
        http
                .cors(cors -> cors.configurationSource(new CorsConfigurationSource() {
                    @Override
                    public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {
                        CorsConfiguration configuration = new CorsConfiguration();
                        configuration.setAllowedOrigins(Collections.singletonList("*"));
                        configuration.setAllowedMethods(Collections.singletonList("*"));
                        configuration.setAllowCredentials(true);
                        configuration.setAllowedHeaders(Collections.singletonList("*"));
                        configuration.setMaxAge(3600L);
                        configuration.setExposedHeaders(Collections.singletonList("Authorization"));
                        configuration.addExposedHeader("Authorization");
                        return configuration;
                    }
                }))
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .sessionManagement((session) -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                .exceptionHandling((exception) -> exception
                        .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                        .accessDeniedHandler(jwtAccessDeniedHandler))

                // 런칭 시 Controller URL 3개 풀기 + anyRequest()는 authenticated()로 두기 (위에 거 주석 처리, 아래 거 주석 해제)
                .authorizeHttpRequests((request) -> request
                        .requestMatchers("/member/signup/**").permitAll()
                        .requestMatchers("/member/login").permitAll()
                        .requestMatchers("/swagger-ui/**").permitAll()
                        .requestMatchers("/v3/api-docs/**").permitAll()
                        .requestMatchers("/member/logout").authenticated()
                        .requestMatchers("/member/delete").authenticated()
                        .requestMatchers("/member/test").authenticated()
//                        .requestMatchers("/daily-budget/**").authenticated()
//                        .requestMatchers("/monthly-budget/**").authenticated()
//                        .requestMatchers("/budget-redistribution/**").authenticated()
                        .anyRequest().permitAll())
//                        .anyRequest().authenticated())
                .addFilterBefore(new JwtAuthenticationFilter(jwtUtil, jwtTokenService), UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}

//@Configuration
//@EnableWebSecurity
//@AllArgsConstructor
//public class SecurityConfig {
//
//    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
//    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;
//
//    @Bean
//    public BCryptPasswordEncoder passwordEncoder() {
//        return new BCryptPasswordEncoder();
//    }
//
//    @Bean
//    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
//        return configuration.getAuthenticationManager();
//    }
//
//    @Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity http,/*, DispatcherServlet dispatcherServlet*/JwtTokenProvider jwtTokenProvider) throws Exception {
//
//        // CSRF, CORS
////        http.cors(cors -> cors
////                .configurationSource(CorsConfig.corsConfigurationSource()));
//        http.csrf(AbstractHttpConfigurer::disable);
//        //http.csrf(csrf -> csrf.disable());
//        http.cors(Customizer.withDefaults());
//
//        // 세션 관리 상태 없음으로 구성, Spring Security가 세션 생성 or 사용 X
//        http.sessionManagement(session -> session.sessionCreationPolicy(
//                SessionCreationPolicy.STATELESS));
//
//        // FormLogin, BasicHttp 비활성화
//        //http.formLogin((form) -> form.disable());
//        http.formLogin(AbstractHttpConfigurer::disable);
//        http.httpBasic(AbstractHttpConfigurer::disable);
//
////        // jwt filter with login
////        JwtAuthenticationFilter loginFilter = new JwtAuthenticationFilter(
////                authenticationManager(authenticationConfiguration), jwtUtil);
////        loginFilter.setFilterProcessesUrl("member/login");
////        http.addFilterAt(loginFilter, UsernamePasswordAuthenticationFilter.class);
//
//        // JwtAuthFilter를 UsernamePasswordAuthenticationFilter 앞에 추가
//        http.addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class);
//
//        http.exceptionHandling((exceptionHandling) -> exceptionHandling
//                .authenticationEntryPoint(jwtAuthenticationEntryPoint)
//                .accessDeniedHandler(jwtAccessDeniedHandler));
//
//        // 권한 규칙 작성
//        http.authorizeHttpRequests(authorize -> authorize
////                .requestMatchers("/member/signup/**").permitAll()
////                .requestMatchers("/member/login").permitAll()
//                .requestMatchers("/member/logout").hasRole("USER")
//                .requestMatchers("/member/delete").hasRole("USER")
//                .anyRequest().permitAll()
//        );
//
//        return http.build();
//    }
//
//    private static final String[] AUTH_WHITELIST = {
//            "/v2/api-docs",
//            "/v3/api-docs/**",
//            "/configuration/ui",
//            "/swagger-resources/**",
//            "/configuration/security",
//            "/swagger-ui.html",
//            "/webjars/**",
//            "/file/**",
//            "/image/**",
//            "/swagger/**",
//            "/swagger-ui/**",
//            "/h2/**"
//    };
//
//    public void configure(WebSecurity web) throws Exception {
//        web.ignoring().requestMatchers(AUTH_WHITELIST);
//    }
//}
