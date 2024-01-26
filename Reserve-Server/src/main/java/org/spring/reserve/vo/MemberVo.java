package org.spring.reserve.vo;

import lombok.Data;

import java.util.Date;

@Data
public class MemberVo {
    private int memberNo;
    private String memberId;
    private String password;
    private String phone;
    private String name;
    private String team;
    private String status;
    private String role;
    private String approver;
    private Date createdAt;
    private Date updatedAt;

}
