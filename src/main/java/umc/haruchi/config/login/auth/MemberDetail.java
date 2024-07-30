package umc.haruchi.config.login.auth;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import umc.haruchi.domain.Member;

import java.util.ArrayList;
import java.util.Collection;

@RequiredArgsConstructor
@Getter
public class MemberDetail implements UserDetails {

    private final Member member;

    public static MemberDetail createMemberDetail(Member member) {
        return new MemberDetail(member);
    }


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> collect = new ArrayList<>();
        collect.add(new GrantedAuthority() {
            @Override
            public String getAuthority() {
                return null;
            }
        });
        return collect;
    }

    @Override
    public String getPassword() {
        return member.getPassword();
    }

    @Override
    public String getUsername() {
        return member.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}

//@RequiredArgsConstructor
//public class MemberDetail implements UserDetails {
//
////    private final Member member;
//    private String email;
//    private String password;
//    private String role;
//
//    public static UserDetails of(Member member) {
//        return MemberDetail.builder()
//                .email(member.getEmail())
//                .password(member.getPassword())
//                .role(member.getRole())
//                .build();
//    }
//
//    @Override
//    public Collection<? extends GrantedAuthority> getAuthorities() {
//        SimpleGrantedAuthority authority = new SimpleGrantedAuthority(role);
//        return Collections.singleton(authority);
//    }
//
//    @Override
//    public String getPassword() {
//        return password;
//    }
//
//    @Override
//    public String getUsername() {
//        return email    ;
//    }
//
//    @Override
//    public boolean isAccountNonExpired() {
//        return true;
//    }
//
//    @Override
//    public boolean isAccountNonLocked() {
//        return true;
//    }
//
//    @Override
//    public boolean isCredentialsNonExpired() {
//        return true;
//    }
//
//    @Override
//    public boolean isEnabled() {
//        return true;
//    }
//    // 해당 유저의 권한을 리턴하는 곳
//    @Override
//    public Collection<? extends GrantedAuthority> getAuthorities() {
//        Collection<GrantedAuthority> collection = new ArrayList<>();
//
//        collection.add(new GrantedAuthority() {
//            @Override
//            public String getAuthority() {
//                return member.getRole();
//            }
//        });
//
//        return collection;
//            List<GrantedAuthority> authorities = new ArrayList<>();
//
//            // 역할 목록
//            GrantedAuthority roleAuthority = new SimpleGrantedAuthority("ROLE_USER");
//            authorities.add(roleAuthority);
//
//            return authorities;
//    }
//
//}
