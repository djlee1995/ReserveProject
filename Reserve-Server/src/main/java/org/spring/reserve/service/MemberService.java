package org.spring.reserve.service;

import org.spring.reserve.vo.MemberVo;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

public interface MemberService {
    public List<MemberVo> selectMember(MemberVo memberVo);
    public int selectCountMember(MemberVo memberVo);
    public int addMember(MemberVo memberVo);
    public boolean comparePwd(MemberVo memberVo);
    public int updateMember(MemberVo memberVo);

}
