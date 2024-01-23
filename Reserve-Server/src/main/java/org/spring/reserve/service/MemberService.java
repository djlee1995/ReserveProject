package org.spring.reserve.service;

import org.spring.reserve.vo.MemberVo;

import java.util.List;

public interface MemberService {
    public List<MemberVo> getAllMember();
    public MemberVo getOneMember(MemberVo memberVo);
    public int addMember(MemberVo memberVo);
}
