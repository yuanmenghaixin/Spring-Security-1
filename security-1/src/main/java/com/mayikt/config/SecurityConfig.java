package com.mayikt.config;

import com.mayikt.entity.PermissionEntity;
import com.mayikt.mapper.PermissionMapper;
import com.mayikt.service.MyUserDetailsService;
import com.mayikt.utils.MD5Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 配置 Spring Security框架
 */
@Component
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private PermissionMapper permissionMapper;

    @Autowired
    private MyUserDetailsService myUserDetailsService;

    /**
     * 配置账号权限认证
     *
     * @param auth
     * @throws Exception
     */
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        // 设置用户账号信息和权限
//        auth.inMemoryAuthentication().withUser("admin").password("123456").authorities("/");
//        auth.inMemoryAuthentication().withUser("mayikt_add").password("mayikt").authorities("addMember", "delMember"
//                , "updateMember");
//        auth.inMemoryAuthentication().withUser("mayikt_show").password("mayikt").authorities("showMember");
        auth.userDetailsService(myUserDetailsService).passwordEncoder(new PasswordEncoder() {
            @Override
            public String encode(CharSequence charSequence) {
                return MD5Util.encode((String) charSequence);
            }

            @Override
            public boolean matches(CharSequence charSequence, String s) {
                String rawPass = MD5Util.encode((String) charSequence);
                System.out.println(rawPass + ",," + s);
                boolean result = rawPass.equals(s);
                return result;
            }
        });
    }

    /**
     * 配置HttpSecurity 拦截资源
     * denyAll	永远返回false
     * anonymous	当前用户是anonymous时返回true
     * rememberMe	当前用户是rememberMe用户时返回true
     * authenticated	当前用户不是anonymous时返回true
     * fullAuthenticated	当前用户既不是anonymous也不是rememberMe用户时返回true
     * hasRole（role）	用户拥有指定的角色权限时返回true
     * hasAnyRole（[role1，role2]）	用户拥有任意一个指定的角色权限时返回true
     * hasAuthority（authority）	用户拥有指定的权限时返回true
     * hasAnyAuthority（[authority1,authority2]）	用户拥有任意一个指定的权限时返回true
     * hasIpAddress（'192.168.1.0'）	请求发送的Ip匹配时返回true
     *
     * @param http
     * @throws Exception
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        //httpBasic
//        http.authorizeRequests().antMatchers("/**").fullyAuthenticated().and().httpBasic();
        // 以from表单的形式
//        http.authorizeRequests().antMatchers("/**").fullyAuthenticated().and().formLogin();

        // 设置对应权限名称
//        http.authorizeRequests().antMatchers("/addMember").hasAnyAuthority("addMember")
//                .antMatchers("/delMember").hasAnyAuthority("delMember")
//                .antMatchers("/login").permitAll()
//                .antMatchers("/updateMember").hasAnyAuthority("updateMember")
//                .antMatchers("/showMember").hasAnyAuthority("showMember").antMatchers("/**")
//                .fullyAuthenticated().and().formLogin().loginPage("/login").and().csrf().disable();
        // TODO authorizeRequests()配置路径拦截，表明路径访问所对应的权限，角色，认证信息。
        ExpressionUrlAuthorizationConfigurer <HttpSecurity>.ExpressionInterceptUrlRegistry authorizeRequests = http.authorizeRequests();
        List <PermissionEntity> allPermission = permissionMapper.findAllPermission();
        allPermission.forEach((a) -> {
            authorizeRequests.antMatchers(a.getUrl()).hasAnyAuthority(a.getPermTag());//TODO hasAnyAuthority和hasAnyRole两者二选一 一个有前缀ROLE_一个无前缀ROLE_
            //authorizeRequests.antMatchers(a.getUrl()).hasAnyRole(a.getPermTag());//TODO hasAnyAuthority和hasAnyRole两者二选一 hasAuthority 方法时，如果数据是从数据库中查询出来的，这里的权限和数据库中保存一致即可，可以不加 ROLE_ 前缀。即数据库中存储的用户角色如果是 admin，这里就是 admin。
        });
        authorizeRequests.antMatchers("/login").permitAll()
                .antMatchers("/**").fullyAuthenticated()//TODO 以上权限意外的请求只要认证成功即可访问
                .and().formLogin()// TODO formLogin()对应表单认证相关的配置并指定登录界面
                .loginPage("/login").and().csrf().disable();
        //TODO 通用配置
      /*  http.authorizeRequests()// TODO authorizeRequests()配置路径拦截，表明路径访问所对应的权限，角色，认证信息。
                .antMatchers("/resources/**", "/signup", "/about").permitAll() // TODO permitAll()放行权限
                .antMatchers("/admin/**").hasRole("ADMIN")
                .antMatchers("/db/**").access("hasRole('ADMIN') and hasRole('DBA')")
                .anyRequest().authenticated()
           .and().formLogin()// TODO formLogin()对应表单认证相关的配置
                .usernameParameter("username")
                .passwordParameter("password")
                .failureForwardUrl("/login?error")
                .loginPage("/login")
                .permitAll()
                //.successHandler(new MyAuthenticationSuccessHandler()) //TODO 当前使用注解方式
                //.failureHandler(new MyAuthenticationFailureHandler()) //TODO 当前使用注解方式
            .and().logout()// TODO logout()对应了注销相关的配置
                 .deleteCookies("remove")
                .logoutUrl("/logout")
                .logoutSuccessUrl("/index")
                .permitAll()
            .and().httpBasic()// TODO httpBasic()可以配置basic登录
                .disable();*/

    }

    /**
     * There is no PasswordEncoder mapped for the id "null"
     * 原因:升级为Security5.0以上密码支持多中加密方式，回复以前模式
     *
     * @return
     */
    @Bean
    public static NoOpPasswordEncoder passwordEncoder() {
        return (NoOpPasswordEncoder) NoOpPasswordEncoder.getInstance();
    }


}