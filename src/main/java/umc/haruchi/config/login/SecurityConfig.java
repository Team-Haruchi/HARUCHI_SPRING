package umc.haruchi.config.login;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import umc.haruchi.config.login.jwt.JwtAuthenticationEntryPoint;
import umc.haruchi.config.login.jwt.JwtAuthenticationFilter;
import umc.haruchi.config.login.jwt.JwtUtil;
import umc.haruchi.config.redis.RedisUtil;

@Configuration
@EnableWebSecurity
@AllArgsConstructor
public class SecurityConfig {

    private final JwtUtil jwtUtil;
    private final RedisUtil redisUtil;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final AuthenticationConfiguration authenticationConfiguration;

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http/*, DispatcherServlet dispatcherServlet*/) throws Exception {

        // CSRF, CORS
        http.cors(cors -> cors
                .configurationSource(CorsConfig.apiConfigurationSource()));
        http.csrf(AbstractHttpConfigurer::disable);
        //http.csrf(csrf -> csrf.disable());
        //http.cors(Customizer.withDefaults());

        // 세션 관리 상태 없음으로 구성, Spring Security가 세션 생성 or 사용 X
        http.sessionManagement(session -> session.sessionCreationPolicy(
                SessionCreationPolicy.STATELESS));

        // FormLogin, BasicHttp 비활성화
        //http.formLogin((form) -> form.disable());
        http.formLogin(AbstractHttpConfigurer::disable);
        http.httpBasic(AbstractHttpConfigurer::disable);

        // jwt filter with login
        JwtAuthenticationFilter loginFilter = new JwtAuthenticationFilter(
                authenticationManager(authenticationConfiguration), jwtUtil);
        loginFilter.setFilterProcessesUrl("member/login");
        http.addFilterAt(loginFilter, UsernamePasswordAuthenticationFilter.class);

        // JwtAuthFilter를 UsernamePasswordAuthenticationFilter 앞에 추가
        //http.addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider, redisTemplate), UsernamePasswordAuthenticationFilter.class);

        http.exceptionHandling((exceptionHandling) -> exceptionHandling
                .authenticationEntryPoint(jwtAuthenticationEntryPoint));

        // 권한 규칙 작성
        http.authorizeHttpRequests(authorize -> authorize
//                .requestMatchers("/member/signup/**").permitAll()
//                .requestMatchers("/member/login").permitAll()
                .requestMatchers("/member/logout").authenticated()
                .requestMatchers("/member/delete").authenticated()
                .anyRequest().permitAll()
        );

        return http.build();
    }

    private static final String[] AUTH_WHITELIST = {
            "/v2/api-docs",
            "/v3/api-docs/**",
            "/configuration/ui",
            "/swagger-resources/**",
            "/configuration/security",
            "/swagger-ui.html",
            "/webjars/**",
            "/file/**",
            "/image/**",
            "/swagger/**",
            "/swagger-ui/**",
            "/h2/**"
    };

    public void configure(WebSecurity web) throws Exception {
        web.ignoring().requestMatchers(AUTH_WHITELIST);
    }
}
