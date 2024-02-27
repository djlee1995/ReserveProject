package org.spring.reserve.repository;

import jakarta.servlet.http.HttpServletRequest;
import org.apache.ibatis.annotations.Mapper;
import org.spring.reserve.dto.Members;

import java.util.List;

@Mapper
public interface MemberRepository {
    public int insert(Members members) throws Exception;

    public Members select(String memberId) throws Exception;

    public int update(Members members) throws Exception;

    public int delete(String memberId) throws Exception;

    public int count(String memberId) throws Exception;
}
