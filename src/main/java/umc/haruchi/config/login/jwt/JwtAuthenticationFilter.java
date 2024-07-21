package umc.haruchi.config.login.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.GenericFilterBean;
import umc.haruchi.apiPayload.ApiResponse;
import umc.haruchi.config.login.auth.MemberDetail;
import umc.haruchi.web.dto.MemberRequestDTO;
import umc.haruchi.web.dto.MemberResponseDTO;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    // /member/login 요청을 하면, 로그인 시도를 위해 실행되는 함수
    @Override
    public Authentication attemptAuthentication(HttpServletRequest req, HttpServletResponse res)
        throws AuthenticationException {
        log.info("attemptAuthentication");

        // request에 있는 username과 password를 파싱해서 자바 Object로 받기
        ObjectMapper mapper = new ObjectMapper();
        MemberRequestDTO.MemberLoginDTO memberLoginDTO;

        try {
            memberLoginDTO = mapper.readValue(req.getInputStream(), MemberRequestDTO.MemberLoginDTO.class);
        } catch (IOException e) {
            throw new AuthenticationServiceException("Error of request body.");
        }

        //유저네임패스워드 토큰 생성
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(
                        memberLoginDTO.getEmail(),
                        memberLoginDTO.getPassword()
                );

        return authenticationManager.authenticate(authenticationToken);
    }

    // jwt 토큰 생성해서 response에 담아주기
    @Override
    protected void successfulAuthentication(HttpServletRequest req, HttpServletResponse res,
                                            FilterChain chain, Authentication authResult) throws IOException, ServletException {
        MemberDetail memberDetail = (MemberDetail) authResult.getPrincipal();

        log.info("[*] Login Success! - Login with " + memberDetail.getUsername());

        MemberResponseDTO.LoginJwtTokenDTO jwtTokenDTO= new MemberResponseDTO.LoginJwtTokenDTO(
                jwtUtil.createJwtAccessToken(memberDetail),
                jwtUtil.createJwtRefreshToken(memberDetail)
        );

        log.info("Access Token: " + jwtTokenDTO.getAccessToken());
        log.info("Refresh Token: " + jwtTokenDTO.getRefreshToken());

        ApiResponse.onSuccess(jwtTokenDTO);
    }
}

//@Slf4j
//@RequiredArgsConstructor
//public class JwtAuthenticationFilter extends GenericFilterBean {
//
//    private final JwtTokenProvider tokenProvider;
//    private final RedisTemplate redisTemplate;
//
//    @Override
//    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse,
//                         FilterChain filterChain) throws IOException, ServletException {
//
//        // 1. Request Header에서 JWT token 추출
//        String token = resolveToken((HttpServletRequest) servletRequest);
//
//        // 2. validateToken 메서드로 token 유효성 검사 + redis 관련 추가
//        if (token != null && tokenProvider.validateToken(token)) {
//            Authentication auth = tokenProvider.getAuthentication(token);
//            SecurityContextHolder.getContext().setAuthentication(auth);
//
////            // redis에 해당 accessToken logout 여부 확인
////            String isLogout = (String) redisTemplate.opsForValue().get(token);
////
////            if (ObjectUtils.isEmpty(isLogout)) {
////                // token이 유효할 경우 token에서 Authentication 객체를 가지고 와서 SecurityContext에 저장
////                Authentication auth = tokenProvider.getAuthentication(token);
////                SecurityContextHolder.getContext().setAuthentication(auth);
////            }
//        }
//
//        filterChain.doFilter(servletRequest, servletResponse);
//    }
//
//    // Request Header에서 토큰 정보 추출
//    private String resolveToken(HttpServletRequest request) {
//
//        String bearerToken = request.getHeader("Authorization");
//
//        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) { // 띄어쓰기 삭제
//            return bearerToken.substring(7);
//        }
//        return null;
//    }
//}
