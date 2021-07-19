package com.springsecurity.demo.config;

import com.springsecurity.demo.Filter.ValidateCodeFilter;
import com.springsecurity.demo.handler.MyAuthenticationFailureHandler;
import com.springsecurity.demo.handler.MyAuthenticationSuccessHandler;
import com.springsecurity.demo.session.MySessionExpiredStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;

import javax.sql.DataSource;

/**
 * @author xiawen yang
 * @date 2021/7/16 下午8:11
 */
@Configuration
public class SpringSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private UserDetailsService userDetailsService;

    //配置文件中已经配置了数据源DataSource
    @Autowired
    private DataSource dataSource;

    @Autowired
    private PersistentTokenRepository tokenRepository;

    //密码加密方式
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    //使用自己实现的userDetailsService
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
    }

    @Autowired
    private MyAuthenticationSuccessHandler authenticationSuccessHandler;

    @Autowired
    private MyAuthenticationFailureHandler authenticationFailureHandler;

    @Autowired
    private ValidateCodeFilter validateCodeFilter;

    @Autowired
    private MySessionExpiredStrategy mySessionExpiredStrategy;

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        //退出,退出的接口和登录接口类似，都是spring security帮我们做了，这个地址和前端的一样就行；logoutSuccessUrl退出后的跳转路径
        http.logout().logoutUrl("/logout").logoutSuccessUrl("/login.html").permitAll();

        //配置403跳转路径
        http.exceptionHandling().accessDeniedPage("/deny.html");

        //然后通过addFilterBefore方法将ValidateCodeFilter验证码校验过滤器添加到了UsernamePasswordAuthenticationFilter前面
        http.addFilterBefore(validateCodeFilter, UsernamePasswordAuthenticationFilter.class)
                .formLogin()//自定义表单登录
                //表单登录设置
                .loginPage("/login.html")//登录页面设置
                .loginProcessingUrl("/login")//登录接口设置（只要写一个url地址，内部逻辑spring security帮我们写了）
                .successHandler(authenticationSuccessHandler)//通过自定义的handler来自定义登录成功逻辑
                .failureHandler(authenticationFailureHandler)//通过自定义的handler来自定义登录失败逻辑
                //.defaultSuccessUrl("/success.html", true).permitAll()//登录成功后的跳转路径(在helloController里写一个index方法测试一下)
                //.successForwardUrl("/test/index").permitAll()//这个方法和上面的方法有区别，这个是重定向，登录前不管你到什么页面，登陆后肯定跳转到指定位置
                .and().authorizeRequests()//设置不需要认证的url
                .antMatchers("login.html", "/code/image", "/code/sms").permitAll()//无需认证的请求路径
                //角色和权限的访问控制
                .antMatchers("/admin/**").hasAuthority("admin")//具有admin权限才能访问这些路径
                .antMatchers("/test/**").hasAnyAuthority("admin,app")//多权限设置
                .antMatchers("/sale/**").hasRole("sale")//hasRole() hasAnyRole()：根据角色设定访问权限，这里假设有‘销售’这么一个权限，这里的角色不要加前缀ROLE_
                .anyRequest().authenticated()
                .and().csrf().disable();

        //记住我功能,还有一些其他方法，比如失效时间、
//        http.rememberMe()
//                //自定义登录逻辑
//                .userDetailsService(userDetailsService)
//                //指定存储位置（jdbc和内存两种方式，这里用jdbc，存到数据库，并指定了数据源）
//                .tokenRepository(tokenRepository);
        //session管理器
        http.sessionManagement()
                .invalidSessionUrl("/session/invalid")//session失效后，将跳转到这个链接
                .maximumSessions(1)//设置会话同时连接的最大数量
                .expiredSessionStrategy(mySessionExpiredStrategy)//用户同时在线数超过上限后，后者将前者踢出执行的策略
                .maxSessionsPreventsLogin(true);//除了后者将前者踢出的策略，我们也可以控制当Session达到最大有效数的时候，不再允许相同的账户登录
    }

    /**
     *
     */
    @Bean
    public PersistentTokenRepository tokenRepository() {
        JdbcTokenRepositoryImpl jdbcTokenRepository = new JdbcTokenRepositoryImpl();
        jdbcTokenRepository.setDataSource(dataSource);
        //第一次启动要创建表，后面要注释掉
        //jdbcTokenRepository.setCreateTableOnStartup(true);
        return jdbcTokenRepository;
    }
}
