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
        ExpressionUrlAuthorizationConfigurer <HttpSecurity>.ExpressionInterceptUrlRegistry authorizeRequests = http
                .authorizeRequests();
        List <PermissionEntity> allPermission = permissionMapper.findAllPermission();
        allPermission.forEach((a) -> {
            authorizeRequests.antMatchers(a.getUrl()).hasAnyAuthority(a.getPermTag());
        });
        authorizeRequests.antMatchers("/login").permitAll().antMatchers("/**").fullyAuthenticated().and().formLogin()
                .loginPage("/login").and().csrf().disable();

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