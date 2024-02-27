package org.spring.reserve.service;

import jakarta.servlet.http.HttpServletRequest;
import org.spring.reserve.dto.Members;


public interface MemberService {
    public int insert(Members members) throws Exception;

    public Members select(String memberId) throws Exception;

    public void login(Members members, HttpServletRequest request) throws Exception;

    public int update(Members members) throws Exception;

    public int delete(String memberId) throws Exception;

    public int count(String memberId) throws Exception;

}
