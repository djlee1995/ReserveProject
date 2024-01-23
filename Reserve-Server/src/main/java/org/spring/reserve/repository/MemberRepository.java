package org.spring.reserve.repository;

import org.apache.ibatis.annotations.Mapper;
import org.spring.reserve.vo.MemberVo;

import java.util.List;

@Mapper
public interface MemberRepository {
    List<MemberVo> getAllMember();
    MemberVo getOneMember(MemberVo memberVo);
    int addMember(MemberVo memberVo);
}
