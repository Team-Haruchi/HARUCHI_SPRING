package umc.haruchi.config.login.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import umc.haruchi.apiPayload.ApiResponse;
import umc.haruchi.apiPayload.exception.handler.JwtExceptionHandler;

import java.io.IOException;

@Component
public class JwtExceptionHandlerFilter extends OncePerRequestFilter {
    // JwtAuthenticationFilter의 예외 처리를 해주는 Filter
    // 해당 Filter의 앞에 존재하며, 해당 Filter에서 발생하는 에러 메시지를 컨트롤러에 전달할 수 있게 해줌

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            filterChain.doFilter(request, response);
        } catch(JwtExceptionHandler ex) {
            setErrorResponse(HttpStatus.UNAUTHORIZED, request, response, ex);
        }
    }

    public void setErrorResponse(HttpStatus status, HttpServletRequest req,
                                 HttpServletResponse res, Throwable ex) throws IOException {
        ApiResponse<Object> apiResponse =
                ApiResponse.onFailure(HttpStatus.UNAUTHORIZED.name(), "COMMON401", ex.getMessage());
        String responseBody = new ObjectMapper().writeValueAsString(apiResponse);
        res.setStatus(status.value());
        res.setContentType("application/json");
        res.setCharacterEncoding("UTF-8");
        res.getWriter().write(responseBody);
    }
}