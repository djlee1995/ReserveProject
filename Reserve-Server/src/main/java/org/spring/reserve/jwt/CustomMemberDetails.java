package org.spring.reserve.jwt;

import org.spring.reserve.vo.MemberVo;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;

public class CustomMemberDetails implements UserDetails {
    private final MemberVo memberVo;

    public CustomMemberDetails(MemberVo memberVo) {

        this.memberVo = memberVo;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> collection = new ArrayList<>();

        collection.add(new GrantedAuthority() {

            @Override
            public String getAuthority() {

                return memberVo.getRole();
            }
        });

        return collection;
    }

    @Override
    public String getPassword() {
        return memberVo.getPassword();
    }

    @Override
    public String getUsername() {
        return memberVo.getMemberId();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !memberVo.getStatus().equals("J");
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return memberVo.getStatus().equals("A");
    }
}
