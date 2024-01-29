package org.spring.reserve.service.impl;

import org.spring.reserve.repository.MemberRepository;
import org.spring.reserve.service.MemberService;
import org.spring.reserve.vo.MemberVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MemberServiceImpl implements MemberService {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    public List<MemberVo> selectMember(MemberVo memberVo){
        return memberRepository.selectMember(memberVo);
    }

    @Override
    public int selectCountMember(MemberVo memberVo) {
        return memberRepository.selectCountMember(memberVo);
    }

    @Override
    public int addMember(MemberVo memberVo) {
        memberVo.setPassword(bCryptPasswordEncoder.encode(memberVo.getPassword()));
        return memberRepository.addMember(memberVo);
    }

    @Override
    public boolean comparePwd(MemberVo memberVo) {
        List<MemberVo> list = memberRepository.selectMember(memberVo);
        if(list.size()==1){
            MemberVo member = list.get(0);
            return bCryptPasswordEncoder.matches(memberVo.getPassword(),member.getPassword());
        }
        return false;
    }

    @Override
    public int updateMember(MemberVo memberVo) {
        memberVo.setPassword(bCryptPasswordEncoder.encode(memberVo.getPassword()));
        return memberRepository.updateMember(memberVo);
    }

}