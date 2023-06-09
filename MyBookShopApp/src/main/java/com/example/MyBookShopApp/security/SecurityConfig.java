package com.example.MyBookShopApp.security;

import com.example.MyBookShopApp.security.jwt.JWTRequestFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.http.Cookie;
import java.util.Arrays;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter
{

    private final BookstoreUserDetailsService bookstoreUserDetailsService;

    private final JWTRequestFilter filter;
    private BlackListService blackListService;

    @Autowired
    public SecurityConfig(BookstoreUserDetailsService bookstoreUserDetailsService, JWTRequestFilter filter, BlackListService blackListService){
        this.bookstoreUserDetailsService = bookstoreUserDetailsService;
        this.filter = filter;
        this.blackListService = blackListService;
    }

    @Bean
    PasswordEncoder getPasswordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth
                .userDetailsService(bookstoreUserDetailsService)
                .passwordEncoder(getPasswordEncoder());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .authorizeRequests()
                .antMatchers("/my", "/profile", "/books/pay").authenticated()//hasRole("USER")
                .antMatchers("/**").permitAll()
                .and().formLogin()
                .loginPage("/signin").failureUrl("/signin")
                .and().logout().logoutUrl("/logout").logoutSuccessUrl("/signin").addLogoutHandler((request, response, authentication)-> {
                    blackListService.addToken(Arrays.stream(request.getCookies()).filter(e->e.getName().equals("token")).findFirst().map(Cookie::getValue).orElse(null));
                }).deleteCookies("token")
                        .and().oauth2Login()
                        .and().oauth2Client();

        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        http.addFilterBefore(filter, UsernamePasswordAuthenticationFilter.class);
    }



}
