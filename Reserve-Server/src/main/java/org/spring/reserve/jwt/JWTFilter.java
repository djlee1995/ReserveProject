package org.spring.reserve.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.spring.reserve.util.ApiResponse;
import org.spring.reserve.vo.MemberVo;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.lang.reflect.Member;
import java.security.SignatureException;

public class JWTFilter extends OncePerRequestFilter {

    private final JWTUtil jwtUtil;

    public JWTFilter(JWTUtil jwtUtil) {

        this.jwtUtil = jwtUtil;
    }


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {

        //request에서 Authorization 헤더를 찾음
        String authorization= request.getHeader("Authorization");

        String token;
        String memberId = null;
        String role = null;

        // JWT 토큰은 "Beare token"에 있다. Bearer단어를 제거하고 토큰만 받는다.
        if(authorization != null && authorization.startsWith("Bearer ")){
            //Bearer 부분 제거 후 순수 토큰만 획득
             token = authorization.split(" ")[1];
            try{
                //토큰에서 memberId role 획득
                memberId = jwtUtil.getMemberId(token);
                role = jwtUtil.getRole(token);

            } catch (IllegalArgumentException exception){
                new ObjectMapper().writeValue(response.getOutputStream(), ApiResponse.error(HttpStatus.UNAUTHORIZED,"Unable to get JWT token"));
            } catch (ExpiredJwtException exception){
                new ObjectMapper().writeValue(response.getOutputStream(), ApiResponse.error(HttpStatus.UNAUTHORIZED,"Token is expired"));
            } catch (MalformedJwtException exception){
                new ObjectMapper().writeValue(response.getOutputStream(), ApiResponse.error(HttpStatus.UNAUTHORIZED,"Token is malformed"));
            } catch (UnsupportedJwtException exception){
                new ObjectMapper().writeValue(response.getOutputStream(), ApiResponse.error(HttpStatus.UNAUTHORIZED,"Token is unsupported"));
            }
        }else{
            new ObjectMapper().writeValue(response.getOutputStream(), ApiResponse.error(HttpStatus.UNAUTHORIZED,"Authorization is null or Token does not begin with Bearer String"));
        }

        //MemberVo를 생성하여 값 set
        MemberVo memberVo = new MemberVo();
        memberVo.setMemberId(memberId);
        memberVo.setPassword("temppassword");
        memberVo.setRole(role);

        //UserDetails에 회원 정보 객체 담기
        CustomMemberDetails customUserDetails = new CustomMemberDetails(memberVo);

        //스프링 시큐리티 인증 토큰 생성
        Authentication authToken = new UsernamePasswordAuthenticationToken(customUserDetails, null, customUserDetails.getAuthorities());
        //세션에 사용자 등록
        SecurityContextHolder.getContext().setAuthentication(authToken);

        filterChain.doFilter(request, response);
    }
}
