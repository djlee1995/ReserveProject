package org.spring.reserve.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spring.reserve.service.MemberService;
import org.spring.reserve.util.ApiResponse;
import org.spring.reserve.vo.MemberVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@ResponseBody
@RequestMapping("/api/member/*")
public class MemberController {
    private static Logger log = LoggerFactory.getLogger(MemberController.class);

    @Autowired
    private MemberService memberService;

    @GetMapping(value="/selectMember")
    public ApiResponse selectMember(MemberVo memberVo) {

        List<MemberVo> memberList = memberService.selectMember(memberVo);

        return ApiResponse.ok(memberList);
    }
    @GetMapping(value="/selectCountMember")
    public ApiResponse selectCountMember(MemberVo memberVo) {
        int memberCount = memberService.selectCountMember(memberVo);
        return ApiResponse.ok(memberCount);
    }

    @PostMapping(value="/addMember")
    public ApiResponse addMember(MemberVo memberVo) {
        try {
            int message = memberService.addMember(memberVo);
            if(message == 1){
                return ApiResponse.ok();
            }else{
                return ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR,"Failed to add member");
            }
        }catch (Exception e) {
            log.error("Failed to insert attach",e);
            return ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR,"Failed to add member");
        }

    }

}