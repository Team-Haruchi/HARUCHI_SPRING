package umc.haruchi.config.login.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import umc.haruchi.apiPayload.ApiResponse;
import umc.haruchi.apiPayload.code.ErrorReasonDTO;
import umc.haruchi.apiPayload.code.status.ErrorStatus;

import java.io.IOException;
import java.time.LocalDateTime;

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
//    private final ObjectMapper objectMapper;
//
//    @Override
//    public void commence(HttpServletRequest request,
//                         HttpServletResponse response,
//                         AuthenticationException authException) throws IOException {
//        String exception = (String)request.getAttribute("exception");
//
//        if (exception.equals(ErrorStatus.TOKEN_EMPTY.getMessage())) {
//            setResponse(response, ErrorStatus.TOKEN_EMPTY);
//        }
//        // 유효한 토큰이 아닌 경우
//        else if(exception.equals(ErrorStatus.NOT_VALID_TOKEN.getMessage())) {
//            setResponse(response, ErrorStatus.NOT_VALID_TOKEN);
//        }
//        // 토큰 만료된 경우
//        else if(exception.equals(ErrorStatus.TOKEN_EXPIRED.getMessage())) {
//            setResponse(response, ErrorStatus.TOKEN_EXPIRED);
//        }
//        // 지원되지 않는 토큰인 경우
//        else if(exception.equals(ErrorStatus.WRONG_TYPE_TOKEN.getMessage())) {
//            setResponse(response, ErrorStatus.WRONG_TYPE_TOKEN);
//        }
//        // 잘못된 JWT 서명인 경우
//        else if(exception.equals(ErrorStatus.WRONG_TYPE_SIGNATURE.getMessage())) {
//            setResponse(response, ErrorStatus.WRONG_TYPE_SIGNATURE);
//        }
//        // JWT Claims 문자열이 비어있는 경우
//        else if(exception.equals(ErrorStatus.EMPTY_CLAIMS_TOKEN.getMessage())) {
//            setResponse(response, ErrorStatus.EMPTY_CLAIMS_TOKEN);
//        }

//    }

    //한글 출력을 위해 getWriter() 사용
//    private void setResponse(HttpServletResponse response, ErrorStatus errorCode) throws IOException {
//        response.setContentType("application/json;charset=UTF-8");
//        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//
//        JSONObject responseJson = new JSONObject();
//        responseJson.put("message", errorCode.getMessage());
//        responseJson.put("status", errorCode.getHttpStatus());
//
//        response.getWriter().print(responseJson);
//    }
}
