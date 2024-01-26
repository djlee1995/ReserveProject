package org.spring.reserve.jwt;

import org.spring.reserve.repository.MemberRepository;
import org.spring.reserve.vo.MemberVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomMemberDetailsService implements UserDetailsService {

    @Autowired
    private MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        MemberVo memberVo = new MemberVo();
        memberVo.setMemberId(username);

        List<MemberVo> memberData = memberRepository.selectMember(memberVo);
        if(memberData !=null){
            return new CustomMemberDetails(memberData.get(0));
        }
        return null;
    }
}