package org.spring.reserve.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Members {
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
