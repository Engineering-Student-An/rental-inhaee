package an.rentalinhaee.config;

import an.rentalinhaee.domain.Student;
import an.rentalinhaee.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final StudentRepository studentRepository;

    @Override
    public UserDetails loadUserByUsername(String stuId) throws UsernameNotFoundException {
        Student student = studentRepository.findByStuId(stuId);
        if(student != null) {
            return new CustomUserDetails(student);
        }
        return null;
    }
}
