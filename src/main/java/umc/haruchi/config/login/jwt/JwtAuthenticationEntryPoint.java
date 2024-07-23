package umc.haruchi.config.login.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import umc.haruchi.apiPayload.ApiResponse;

import java.io.IOException;

@Slf4j
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        log.error("JwtAuthenticationEntryPoint 실행");
        response.setContentType("application/json");
        ApiResponse<Object> apiResponse =
                ApiResponse.onFailure(HttpStatus.NOT_FOUND.name(), "MEMBER4027", "유효한 JWT 토큰이 없습니다.");
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.writeValue(response.getWriter(), apiResponse);
    }
}
