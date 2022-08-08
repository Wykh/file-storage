package com.example.filevault.config.security;

import com.example.filevault.config.jwt.JwtConfig;
import com.example.filevault.config.jwt.JwtTokenVerifier;
import com.example.filevault.config.jwt.JwtUsernameAndPasswordAuthenticationFilter;
import com.example.filevault.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;

import static com.example.filevault.config.security.UserPermission.*;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final PasswordEncoder passwordEncoder;
    private final UserService userService;
    private final JwtConfig jwtConfig;

    @Autowired
    public SecurityConfig(PasswordEncoder passwordEncoder, UserService userService, JwtConfig jwtConfig) {
        this.passwordEncoder = passwordEncoder;
        this.userService = userService;
        this.jwtConfig = jwtConfig;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .addFilter(new JwtUsernameAndPasswordAuthenticationFilter(authenticationManager(), jwtConfig))
                .addFilterAfter(new JwtTokenVerifier(jwtConfig), JwtUsernameAndPasswordAuthenticationFilter.class)
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
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) {
        auth.authenticationProvider(daoAuthenticationProvider());
    }

    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setPasswordEncoder(passwordEncoder);
        provider.setUserDetailsService(userService);
        return provider;
    }
}
