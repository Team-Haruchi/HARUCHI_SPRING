package umc.haruchi.config.login.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import umc.haruchi.apiPayload.ApiResponse;
import umc.haruchi.apiPayload.exception.handler.JwtExpiredHandler;
import umc.haruchi.apiPayload.exception.handler.JwtInvalidHandler;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final JwtTokenService jwtTokenService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
//        String accessToken = request.getHeader("Authorization");
        String token = resolveToken(request);
        if (token == null) {
            filterChain.doFilter(request, response);
            return;
        }
        if (jwtUtil.isExpired(token)) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            jwtUtil.validateToken(token);
            JwtUtil.validateAccessToken(token); // 생략해도 될까?

            Authentication authentication = jwtUtil.getAuthentication(token);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            jwtTokenService.checkExpired(token);
        } catch (JwtExpiredHandler e) {
            response.setContentType("application/json");
            ApiResponse<Object> apiResponse =
                    ApiResponse.onFailure(HttpStatus.NOT_FOUND.name(), "MEMBER4027", "Invalid token is not found.");
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.writeValue(response.getWriter(), apiResponse);
            return;
        } catch (JwtInvalidHandler e) {
            response.setContentType("application/json");
            ApiResponse<Object> apiResponse =
                    ApiResponse.onFailure(HttpStatus.UNAUTHORIZED.name(), "MEMBER4022", "Invalid token.");
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.writeValue(response.getWriter(), apiResponse);
            return;
        }
        filterChain.doFilter(request, response);

    }
        private String resolveToken(HttpServletRequest request) {

        String bearerToken = request.getHeader("Authorization");

        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) { // 띄어쓰기 삭제
            return bearerToken.substring(7);
        }
        return null;
    }


//    private final JwtTokenProvider jwtTokenProvider;
//
//    @Override
//    protected void doFilterInternal(HttpServletRequest request,
//                                    HttpServletResponse response,
//                                    FilterChain filterChain) throws ServletException, IOException {
//        String token = jwtTokenProvider.resolveToken(request);
//
//        response.setCharacterEncoding("UTF-8");
//
//        try {
//            if (token != null && jwtTokenProvider.isValidateToken(token)) {
//                Authentication authentication = jwtTokenProvider.getAuthentication(token);
//                SecurityContextHolder.getContext().setAuthentication(authentication);
//            } else {
//                log.error("토큰이 비어있습니다.");
//                request.setAttribute("exception", ErrorStatus.TOKEN_EMPTY.getMessage());
//            }
//        } catch (MalformedJwtException e) {
//            log.info("Invalid JWT token", e);
//            request.setAttribute("exception", ErrorStatus.NOT_VALID_TOKEN.getMessage());
//        } catch (ExpiredJwtException e) {
//            log.info("Expired JWT token", e);
//            request.setAttribute("exception", ErrorStatus.TOKEN_EXPIRED.getMessage());
//        } catch (UnsupportedJwtException e) {
//            log.info("Unsupported JWT token", e);
//            request.setAttribute("exception", ErrorStatus.WRONG_TYPE_TOKEN.getMessage());
//        } catch (IllegalArgumentException e) {
//            log.info("JWT claims string is empty.", e);
//            request.setAttribute("exception", ErrorStatus.EMPTY_CLAIMS_TOKEN.getMessage());
//        } catch (io.jsonwebtoken.security.SignatureException e) {
//            log.error("잘못된 JWT 서명입니다.");
//            request.setAttribute("exception", ErrorStatus.NOT_VALID_TOKEN.getMessage());
//        }
//
//        filterChain.doFilter(request, response);
//    }
}



//@Slf4j
//@RequiredArgsConstructor
//public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
//
//    private final AuthenticationManager authenticationManager;
//    private final JwtUtil jwtUtil;
//
//    // /member/login 요청을 하면, 로그인 시도를 위해 실행되는 함수
//    @Override
//    public Authentication attemptAuthentication(HttpServletRequest req, HttpServletResponse res)
//        throws AuthenticationException {
//        log.info("attemptAuthentication");
//
//        // request에 있는 username과 password를 파싱해서 자바 Object로 받기
//        ObjectMapper mapper = new ObjectMapper();
//        MemberRequestDTO.MemberLoginDTO memberLoginDTO;
//
//        try {
//            memberLoginDTO = mapper.readValue(req.getInputStream(), MemberRequestDTO.MemberLoginDTO.class);
//        } catch (IOException e) {
//            throw new AuthenticationServiceException("Error of request body.");
//        }
//
//        //유저네임패스워드 토큰 생성
//        UsernamePasswordAuthenticationToken authenticationToken =
//                new UsernamePasswordAuthenticationToken(
//                        memberLoginDTO.getEmail(),
//                        memberLoginDTO.getPassword()
//                );
//
//        return authenticationManager.authenticate(authenticationToken);
//    }
//
//    // jwt 토큰 생성해서 response에 담아주기
//    @Override
//    protected void successfulAuthentication(HttpServletRequest req, HttpServletResponse res,
//                                            FilterChain chain, Authentication authResult) throws IOException, ServletException {
//        MemberDetail memberDetail = (MemberDetail) authResult.getPrincipal();
//
//        log.info("[*] Login Success! - Login with " + memberDetail.getUsername());
//
//        MemberResponseDTO.LoginJwtTokenDTO jwtTokenDTO= new MemberResponseDTO.LoginJwtTokenDTO(
//                jwtUtil.createJwtAccessToken(memberDetail),
//                jwtUtil.createJwtRefreshToken(memberDetail)
//        );
//
//        log.info("Access Token: " + jwtTokenDTO.getAccessToken());
//        log.info("Refresh Token: " + jwtTokenDTO.getRefreshToken());
//
//        ApiResponse.onSuccess(jwtTokenDTO);
//    }
//}

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
