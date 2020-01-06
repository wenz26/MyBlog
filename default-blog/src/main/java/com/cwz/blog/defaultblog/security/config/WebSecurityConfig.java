package com.cwz.blog.defaultblog.security.config;

import com.cwz.blog.defaultblog.redis.HashRedisServiceImpl;
import com.cwz.blog.defaultblog.security.code.filter.ImageCodeFilter;
import com.cwz.blog.defaultblog.security.code.filter.SmsCodeFilter;
import com.cwz.blog.defaultblog.security.code.sms.SmsCodeAuthenticationSecurityConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

/**
 * @author: 陈文振
 * @date: 2020/1/1
 * @description: Spring Security配置类
 */
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private AuthenticationSuccessHandler successHandler;
    @Autowired
    private SimpleUrlAuthenticationFailureHandler failureHandler;
    @Autowired
    private LogoutSuccessHandler myLogoutSuccessHandler;
    @Autowired
    private SmsCodeAuthenticationSecurityConfig smsCodeAuthenticationSecurityConfig;
    @Autowired
    private HashRedisServiceImpl hashRedisService;

    @Bean("passwordEncoder")
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        failureHandler.setDefaultFailureUrl("/login?error");

        ImageCodeFilter imageCodeFilter = new ImageCodeFilter(failureHandler, hashRedisService);
        SmsCodeFilter smsCodeFilter = new SmsCodeFilter(failureHandler, hashRedisService);

        http
            .addFilterBefore(imageCodeFilter, UsernamePasswordAuthenticationFilter.class)

            .addFilterBefore(smsCodeFilter, UsernamePasswordAuthenticationFilter.class)
                .apply(smsCodeAuthenticationSecurityConfig)
                .and()

            .formLogin()
                .loginPage("/login")
                //.failureUrl("/login?error")
                .loginProcessingUrl("/loginFrom")
                .defaultSuccessUrl("/")
                .successHandler(successHandler)
                .failureHandler(failureHandler)
                .and()

            .headers()
                .frameOptions().sameOrigin()
                .and()

            .logout()
                .logoutUrl("/logout")
                .logoutSuccessUrl("/")
                .deleteCookies("JSESSIONID")
                .logoutSuccessHandler(myLogoutSuccessHandler)
                .and()

            .authorizeRequests()
                .antMatchers("/html/**", "/bar").permitAll()
                // 配置静态资源可允许访问
                .antMatchers("/css/**", "/emoji/**", "/img/**", "/js/**", "/languages/**",
                        "/lib/**", "/music/**", "/plugins/**").permitAll()
                .antMatchers("/code/image", "/code/sms").permitAll()
                .antMatchers("/", "/index", "/categories", "/tags", "/login",
                        "/update", "/register", "/article/*").permitAll()
                .antMatchers("/editor", "/user").hasRole("USER")
                .antMatchers("/superAdmin").hasAnyAuthority("SUPERADMIN")
                .anyRequest().authenticated()
                .and()
            .csrf().disable();
    }


}
