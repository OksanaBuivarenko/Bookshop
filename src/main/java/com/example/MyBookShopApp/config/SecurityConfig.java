package com.example.MyBookShopApp.config;

import com.example.MyBookShopApp.security.BlackListToken;
import com.example.MyBookShopApp.security.CookieHandlerJwt;
import com.example.MyBookShopApp.security.jwt.JWTRequestFilter;
import com.example.MyBookShopApp.services.BookstoreUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    private final BookstoreUserDetailsService bookstoreUserDetailService;
    private final JWTRequestFilter filter;
    private BlackListToken blackListToken;
    private final CookieHandlerJwt cookieHandlerJwt;

    public SecurityConfig(BookstoreUserDetailsService bookstoreUserDetailService, JWTRequestFilter filter,
                          BlackListToken blackListToken, CookieHandlerJwt cookieHandlerJwt) {
        this.bookstoreUserDetailService = bookstoreUserDetailService;
        this.filter = filter;
        this.blackListToken = blackListToken;
        this.cookieHandlerJwt = cookieHandlerJwt;
    }

    @Bean
    PasswordEncoder getPasswordEncoder() {
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
                .userDetailsService(bookstoreUserDetailService)
                .passwordEncoder(getPasswordEncoder());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .authorizeRequests()
                .antMatchers("/my", "/profile", "/books/viewed").authenticated()//hasRole("USER")
                .antMatchers("/**").permitAll()
                .and().formLogin()
                .loginPage("/signin").failureUrl("/signin")
                .and().logout().logoutUrl("/logout")
                .addLogoutHandler(((request, response, authentication) -> {
                    String token = cookieHandlerJwt.getToken();
                    if (token != null) blackListToken.addTokenBlackList(token);
                }))
                .logoutSuccessUrl("/signin").deleteCookies("token")
                .and().oauth2Login()
                .and().oauth2Client();
        http.addFilterBefore(filter, UsernamePasswordAuthenticationFilter.class);
    }
}
