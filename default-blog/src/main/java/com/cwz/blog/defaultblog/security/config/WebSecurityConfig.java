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
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;

import javax.sql.DataSource;

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
    @Autowired
    private DataSource dataSource;
    @Autowired
    private UserDetailsService userDetailsService;

    @Bean("passwordEncoder")
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public PersistentTokenRepository persistentTokenRepository() {
        JdbcTokenRepositoryImpl tokenRepository = new JdbcTokenRepositoryImpl();
        tokenRepository.setDataSource(dataSource);
        //tokenRepository.setCreateTableOnStartup(true);
        return tokenRepository;
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

            .rememberMe()
                .tokenRepository(persistentTokenRepository())
                // 记住我token的有效时间 一周后过期（秒数）
                .tokenValiditySeconds(604800)
                .userDetailsService(userDetailsService)
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
                        "/lib/**", "/music/**", "/plugins/**", "/article/**").permitAll()
                .antMatchers("/code/image", "/code/sms").permitAll()
                .antMatchers("/", "/index", "/categories", "/tags", "/login", "/archives",
                        "/update", "/register", "/article/*").permitAll()
                .antMatchers("/checkCode", "/changePassword", "/getVisitorNumByPageName", "/myPublishArticles", "/newComment",
                        "/getSiteInfo", "/getCategoryArticle", "/findCategoriesNameAndArticleNum", "/getArchiveArticle", "/findArchiveNameAndArticleNum").permitAll()
                .antMatchers("/getArticleByArticleId", "/addArticleLike", "/addArticleFavorite").permitAll()
                .antMatchers("/editor", "/user").hasAnyRole("USER")
                .antMatchers("/superAdmin").hasAnyRole("SUPERADMIN")
                .anyRequest().authenticated()
                .and()
            .csrf().disable();
    }


}
