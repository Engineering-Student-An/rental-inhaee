package an.rentalinhaee.config;

import an.rentalinhaee.domain.StudentRole;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests((auth) -> auth
                        .requestMatchers("/", "/login/**", "/findPassword/**", "/join/**","/css/**", "/js/**", "/images/**", "/mobile/**").permitAll()
                        .requestMatchers("/admin/**").hasRole(StudentRole.ADMIN.name())
                        // ** : 와일드카드, hasAnyRole => 여러 개의 role 설정 가능
                        .requestMatchers("**").hasAnyRole(StudentRole.ADMIN.name(), StudentRole.USER.name())
                        // anyRequest => 위에서 처리하지 못한 나머지 경로에 대한 처리
                        .anyRequest().authenticated()
                );
        http
                .formLogin((auth) -> auth.loginPage("/login")

                        .loginProcessingUrl("/login")
                        .defaultSuccessUrl("/", true)
                        .usernameParameter("stuId")
                        .passwordParameter("password")
                        .permitAll()
                        .successHandler(new CustomAuthenticationSuccessHandler())
                        .failureHandler(new CustomAuthenticationFailureHandler()) // 커스텀 실패 핸들러 설정

                );

        // 로그아웃 URL 설정
        http
                .logout((auth) -> auth
                        .logoutUrl("/logout")
                );

        http
                .csrf((auth) -> auth.disable());

        return http.build();
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder(){

        return new BCryptPasswordEncoder();
    }
}
