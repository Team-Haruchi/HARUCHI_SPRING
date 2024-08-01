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