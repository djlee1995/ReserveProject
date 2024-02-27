package org.spring.reserve.jwt;

import lombok.extern.slf4j.Slf4j;
import org.spring.reserve.dto.Members;
import org.spring.reserve.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class CustomMemberDetailsService  implements UserDetailsService {

    @Autowired
    private MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String memberId) {
        log.info("login - loadUserByMemberId : " + memberId);
        Members member;

        try {
            member = memberRepository.select(memberId);
        } catch (Exception e) {
            log.info("사용자 없음... (일치하는 아이디가 없음)");
            throw new UsernameNotFoundException("사용자를 찾을 수 없습니다 : " + memberId);
        }
        log.info("member : ");
        log.info(member.toString());

        CustomMemberDetails customMember = new CustomMemberDetails(member);

        log.info("customMember : ");
        log.info(customMember.toString());

        return customMember;
    }


}
