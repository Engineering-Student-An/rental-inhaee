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
                        .requestMatchers("/rental/**", "/board/new", "/board/*/like", "/board/*/reply/new", "/reply/*/like", "/proposal/**").authenticated()
                            .requestMatchers("/admin/**").hasRole(StudentRole.ADMIN.name())
                        // ** : 와일드카드, hasAnyRole => 여러 개의 role 설정 가능
                        // anyRequest => 위에서 처리하지 못한 나머지 경로에 대한 처리
                        .anyRequest().permitAll()
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

        // Remember-Me : 자동로그인
        http
                .rememberMe(rememberMe -> rememberMe
                        .key("anchangmin")
                        .rememberMeParameter("remember-me")
                        .tokenValiditySeconds(60*60*24*30)
                        .authenticationSuccessHandler(new CustomAuthenticationSuccessHandler())
        );


        return http.build();
    }


    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder(){

        return new BCryptPasswordEncoder();
    }




}
