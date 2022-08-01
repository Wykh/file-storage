package com.example.filevault.config;

import com.example.filevault.jwt.JwtTokenVerifier;
import com.example.filevault.jwt.JwtUsernameAndPasswordAuthenticationFilter;
import com.example.filevault.service.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import static com.example.filevault.config.UserSecurityPermission.*;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final PasswordEncoder passwordEncoder;
    private final UserServiceImpl userService;
    private final ApplicationContext context;

    @Autowired
    public SecurityConfig(PasswordEncoder passwordEncoder, UserServiceImpl userService, ApplicationContext context) {
        this.passwordEncoder = passwordEncoder;
        this.userService = userService;
        this.context = context;
    }


    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setPasswordEncoder(passwordEncoder);
        provider.setUserDetailsService(userService);
        return provider;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authenticationProvider(daoAuthenticationProvider())
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .addFilter(new JwtUsernameAndPasswordAuthenticationFilter(context.getBean(AuthenticationManager.class)))
                .addFilterAfter(new JwtTokenVerifier(), JwtUsernameAndPasswordAuthenticationFilter.class)
                .authorizeRequests()
                .antMatchers(HttpMethod.GET, "/api/file/**").hasAnyAuthority(FILE_READ.getPermission())
                .antMatchers(HttpMethod.POST, "/api/file/**").hasAnyAuthority(FILE_WRITE.getPermission())
                .antMatchers(HttpMethod.PUT, "/api/file/**").hasAnyAuthority(FILE_WRITE.getPermission())
                .antMatchers(HttpMethod.DELETE, "/api/file/**").hasAnyAuthority(FILE_WRITE.getPermission())
                .antMatchers(HttpMethod.POST, "/api/name").permitAll()
                .antMatchers(HttpMethod.PUT, "/api/name/*").hasAnyAuthority(CHANGE_ROLE.getPermission(), BLOCK.getPermission())
                .and()
                .csrf().disable()
                .formLogin().disable();
        return http.build();
    }


}
