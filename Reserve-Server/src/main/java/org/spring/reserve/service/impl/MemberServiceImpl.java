package org.spring.reserve.service.impl;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.spring.reserve.repository.MemberRepository;
import org.spring.reserve.service.MemberService;
import org.spring.reserve.dto.Members;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class MemberServiceImpl implements MemberService {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;


    @Override
    public int insert(Members members) throws Exception {
        String password = members.getPassword();
        String encodePw = passwordEncoder.encode(password);
        members.setPassword(encodePw);

        return memberRepository.insert(members);
    }

    @Override
    public Members select(String memberId) throws Exception {
        return memberRepository.select(memberId);
    }

    @Override
    public void login(Members members, HttpServletRequest request) throws Exception {
        String memberId = members.getMemberId();
        String password = members.getPassword();

        //아이디,패스워드 인증토큰 생성
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(memberId,password);
        //토큰에 요청정보 등록
        token.setDetails(new WebAuthenticationDetails(request));
        //토근을 이용하여 인증 요청 - 로그인
        Authentication authentication = authenticationManager.authenticate(token);
        log.info("인증여부: "+authentication.isAuthenticated());

        User authUser = (User) authentication.getPrincipal();
        log.info("인증된 사용자 아이디: "+authUser.getUsername());
        //시큐리티컨텍스트
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @Override
    public int update(Members members) throws Exception {
        String password = members.getPassword();
        String encodePw = passwordEncoder.encode(password);
        members.setPassword(encodePw);

        return memberRepository.update(members);
    }

    @Override
    public int delete(String memberId) throws Exception {
        return memberRepository.delete(memberId);
    }

    @Override
    public int count(String memberId) throws Exception {
        return memberRepository.count(memberId);
    }
}