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
    public List<MemberVo> getAllMember(){
        return memberRepository.getAllMember();
    }

    @Override
    public MemberVo getOneMember(MemberVo memberVo) {
        return memberRepository.getOneMember(memberVo);
    }

    @Override
    public int addMember(MemberVo memberVo) {
        memberVo.setPassword(bCryptPasswordEncoder.encode(memberVo.getPassword()));
        return memberRepository.addMember(memberVo);
    }
}