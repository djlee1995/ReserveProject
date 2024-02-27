package org.spring.reserve.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.spring.reserve.dto.Members;
import org.spring.reserve.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Date;

@Slf4j
@Component
public class JwtTokenProvider {

    @Value("${spring.jwt.secret}")
    private String jwtKey;

    @Autowired
    MemberRepository memberRepository;

    public String createToken(String memberId, String role) {


        String jwt = Jwts.builder()
                .signWith( Keys.hmacShaKeyFor(jwtKey.getBytes()), Jwts.SIG.HS512 )      // 서명에 사용할 키와 알고리즘 설정
                .header()                                                      // update (version : after 1.0)
                .add("typ", JwtConstants.TOKEN_TYPE)                   // 헤더 설정 (JWT)
                .and()
                .expiration(new Date(System.currentTimeMillis() + 864000000))  // 토큰 만료 시간 설정 (10일)
                .claim("memberId", memberId)                                     // 클레임 설정: 사용자 아이디
                .claim("role", role)                                      // 클레임 설정: 권한
                .compact();

        log.info("jwt : " + jwt);

        return jwt;
    }

    /**
     * 토큰 해석
     *
     * Authorization : Bearer + {jwt}  (authHeader)
     * ➡ jwt 추출
     * ➡ UsernamePasswordAuthenticationToken
     * @param authHeader
     * @return
     * @throws Exception
     */
    public UsernamePasswordAuthenticationToken getAuthentication(String authHeader) {
        if(authHeader == null || authHeader.length() == 0 )
            return null;

        try {

            // jwt 추출 (Bearer + {jwt}) ➡ {jwt}
            String jwt = authHeader.replace(JwtConstants.TOKEN_PREFIX, "");

            // 🔐➡👩‍💼 JWT 파싱
            Jws<Claims> parsedToken = Jwts.parser()
                    .verifyWith(Keys.hmacShaKeyFor(jwtKey.getBytes()))
                    .build()
                    .parseSignedClaims(jwt);

            log.info("parsedToken : " + parsedToken);

            // 인증된 사용자 아이디
            String memberId = parsedToken.getPayload().get("memberId").toString();
            log.info("memberId : " + memberId);

            // 인증된 사용자 권한
            String role = parsedToken.getPayload().get("role").toString();
            log.info("role : " + role);


            // 토큰에 userId 있는지 확인
            if( memberId == null || memberId.length() == 0 )
                return null;


            // 유저 정보 세팅
            Members member = new Members();
            member.setMemberId(memberId);
            member.setRole(role);

            // 토큰 유효하면
            // name, team 도 담아주기
            try {
                Members memberInfo = memberRepository.select(memberId);
                if( memberInfo != null ) {
                    member.setName(memberInfo.getName());
                    member.setTeam(memberInfo.getTeam());
                }
            } catch (Exception e) {
                log.error(e.getMessage());
                log.error("토큰 유효 -> DB 추가 정보 조회시 에러 발생...");
            }

            UserDetails userDetails = new CustomMemberDetails(member);

            // OK
            // new UsernamePasswordAuthenticationToken( 사용자정보객체, 비밀번호, 사용자의 권한(목록)  );
            return new UsernamePasswordAuthenticationToken(userDetails, null,userDetails.getAuthorities());

        } catch (ExpiredJwtException exception) {
            log.warn("Request to parse expired JWT : {} failed : {}", authHeader, exception.getMessage());
        } catch (UnsupportedJwtException exception) {
            log.warn("Request to parse unsupported JWT : {} failed : {}", authHeader, exception.getMessage());
        } catch (MalformedJwtException exception) {
            log.warn("Request to parse invalid JWT : {} failed : {}", authHeader, exception.getMessage());
        } catch (IllegalArgumentException exception) {
            log.warn("Request to parse empty or null JWT : {} failed : {}", authHeader, exception.getMessage());
        }

        return null;
    }

    /**
     * 토큰 유효성 검사
     * @param jwt
     * @return
     *  true     : 유효
     *  false    : 만료
     */
    public boolean validateToken(String jwt) {

        try {
            // 🔐➡👩‍💼 JWT 파싱
            Jws<Claims> parsedToken = Jwts.parser()
                    .verifyWith(Keys.hmacShaKeyFor(jwtKey.getBytes()))
                    .build()
                    .parseSignedClaims(jwt);

            log.info("##### 토큰 만료기간 #####");
            log.info("-> " + parsedToken.getPayload().getExpiration());
            Date exp = parsedToken.getPayload().getExpiration();

            // 만료시간과 현재시간 비교
            // 2023.12.01 vs 2023.12.14  --> 만료  : true  --->  false
            // 2023.12.30 vs 2023.12.14  --> 유효  : false --->  true
            return !exp.before(new Date());

        } catch (ExpiredJwtException exception) {
            log.error("Token Expired");                 // 토큰 만료
            return false;
        } catch (JwtException exception) {
            log.error("Token Tampered");                // 토큰 손상
            return false;
        } catch (NullPointerException exception) {
            log.error("Token is null");                 // 토큰 없음
            return false;
        } catch (Exception e) {
            return false;
        }


    }

}

