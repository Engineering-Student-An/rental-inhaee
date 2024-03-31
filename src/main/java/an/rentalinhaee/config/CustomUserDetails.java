package an.rentalinhaee.config;

import an.rentalinhaee.domain.Student;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;

public class CustomUserDetails implements UserDetails {
    private final Student student;


    public CustomUserDetails(Student student) {
        this.student = student;
    }

    // 현재 user의 role을 반환 (ex. "ROLE_ADMIN" / "ROLE_USER" 등)
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> collection = new ArrayList<>();
        collection.add(new GrantedAuthority() {
            @Override
            public String getAuthority() {
                return "ROLE_" + student.getRole().name();
            }
        });

        return collection;
    }

    // student의 비밀번호 반환
    @Override
    public String getPassword() {
        return student.getPassword();
    }

    // student의 stuId 반환
    @Override
    public String getUsername() {
        return student.getStuId();
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


    public Student getStudent() {
        return this.student;
    }
}
