package org.spring.reserve.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.spring.reserve.dto.Members;
import org.spring.reserve.util.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;


    // 생성자
    public JwtAuthenticationFilter( AuthenticationManager authenticationManager, JwtTokenProvider jwtTokenProvider ) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
        // 필터 URL 경로 설정 : /api/member/login
        setFilterProcessesUrl(JwtConstants.AUTH_LOGIN_URL);
    }


    /**
     *  인증 시도 메소드
     *  : /api/member/login 경로로 요청하면, 필터로 걸러서 인증을 시도
     *
     */
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException {
        Members member;
        try {
             member = new ObjectMapper().readValue(request.getInputStream(), Members.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        String memberId = member.getMemberId();
        String password = member.getPassword();

        log.info("memberId : " + memberId);
        log.info("password : " + password);

        // 사용자 인증정보 객체 생성
        Authentication authentication = new UsernamePasswordAuthenticationToken(memberId, password);

        // 사용자 인증 (로그인)
        authentication = authenticationManager.authenticate(authentication);

        log.info("인증 여부 : " + authentication.isAuthenticated());

        // 인증 실패 (username, password 불일치)
        if( !authentication.isAuthenticated() ) {
            log.info("인증 실패 : 아이디 또는 비밀번호가 일치하지 않습니다.");
            response.setStatus(401);            // 401 UNAUTHORIZED (인증 실패)
        }

        return authentication;
    }

    /**
     *  인증 성공 메소드
     *
     *  - JWT 을 생성
     *  - JWT 를 응답 헤더에 설정
     */
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
                                            Authentication authentication) throws IOException, ServletException {

        log.info("인증 성공...");

        CustomMemberDetails details = (CustomMemberDetails) authentication.getPrincipal();
        String memberId = details.getMember().getMemberId();
        String role = details.getMember().getRole();
        // JWT 토큰 생성 요청
        String jwt = jwtTokenProvider.createToken(memberId, role);

        // Authorization : Bearer + {jwt}
        response.addHeader(JwtConstants.TOKEN_HEADER, JwtConstants.TOKEN_PREFIX+ jwt);
        response.setStatus(HttpStatus.OK.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("token",JwtConstants.TOKEN_PREFIX+ jwt);

        new ObjectMapper().writeValue(response.getOutputStream(),ApiResponse.ok(body));
    }
    //로그인 실패시 실행하는 메소드
    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException {
        String errorMessage;
        if(failed instanceof BadCredentialsException){
            errorMessage = "아이디 또는 비밀번호가 맞지 않습니다. 다시 확인해 주세요.";
            logger.error(failed);
        }else if(failed instanceof LockedException){
            errorMessage = "승인이 거절된 계정입니다. 관리자에게 문의하세요.";
            logger.error(failed);
        }else if(failed instanceof DisabledException){
            errorMessage = "승인이 진행중인 계정입니다. 관리자에게 문의하세요.";
            logger.error(failed);
        }else{
            errorMessage = "알 수 없는 이유로 로그인에 실패하였습니다 관리자에게 문의하세요.";
            logger.error(failed);
        }
        new ObjectMapper().writeValue(response.getOutputStream(), ApiResponse.error(HttpStatus.UNAUTHORIZED, errorMessage));
    }

}
