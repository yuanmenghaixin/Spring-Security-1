package com.mayikt.config;

import com.mayikt.entity.PermissionEntity;
import com.mayikt.filter.JWTValidationAuthorizationFilter;
import com.mayikt.filter.JWTLoginFilter;
import com.mayikt.mapper.PermissionMapper;
import com.mayikt.service.MemberUserDetailsService;
import com.mayikt.utils.MD5Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @ClassName SecurityConfig
 * @Author 蚂蚁课堂余胜军 QQ644064779 www.mayikt.com
 * @Version V1.0
 **/
@Component
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    private MemberUserDetailsService memberUserDetailsService;
    @Autowired
    private PermissionMapper permissionMapper;

    /**
     * 添加授权账户
     *
     * @param auth
     * @throws Exception
     */
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
//        // 设置用户账号信息和权限
//        auth.inMemoryAuthentication().withUser("mayikt_admin").password("mayikt")
//                .authorities("addMember", "delMember", "updateMember", "showMember");
//        // 如果mayikt_admin账户权限的情况 所有的接口都可以访问，如果mayikt_add 只能访问addMember
//        auth.inMemoryAuthentication().withUser("mayikt_add").password("mayikt")
//                .authorities("addMember");
        auth.userDetailsService(memberUserDetailsService).passwordEncoder(new PasswordEncoder() {
            /**
             * 对密码MD5
             * @param rawPassword
             * @return
             */
            @Override
            public String encode(CharSequence rawPassword) {
                return MD5Util.encode((String) rawPassword);
            }

            /**
             * rawPassword 用户输入的密码
             * encodedPassword 数据库DB的密码
             * @param rawPassword
             * @param encodedPassword
             * @return
             */
            public boolean matches(CharSequence rawPassword, String encodedPassword) {
                String rawPass = MD5Util.encode((String) rawPassword);
                boolean result = rawPass.equals(encodedPassword);
                return result;
            }
        });
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
//        //配置httpBasic Http协议认证
////        http.authorizeRequests().antMatchers("/**").fullyAuthenticated().and().httpBasic();
//        //配置httpBasic Http协议认证
//        http.authorizeRequests().antMatchers("/**").fullyAuthenticated().and()
//                .formLogin();
//        // 默认fromLog
        List <PermissionEntity> allPermission = permissionMapper.findAllPermission();
        ExpressionUrlAuthorizationConfigurer <HttpSecurity>.ExpressionInterceptUrlRegistry
                expressionInterceptUrlRegistry = http.authorizeRequests();
        allPermission.forEach((permission) -> {
            expressionInterceptUrlRegistry.antMatchers(permission.getUrl()).hasAnyAuthority(permission.getPermTag());

        });
        expressionInterceptUrlRegistry.antMatchers("/auth/login").permitAll()
                .antMatchers("/**").fullyAuthenticated()
                //.and().formLogin().loginPage("/login")//前后端分离没必要form表单登录
                //设置过滤器
                .and().addFilter(new JWTValidationAuthorizationFilter(authenticationManager()))
                .addFilter(new JWTLoginFilter(authenticationManager())).csrf().disable()
                // 不需要session 剔除session
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        //.antMatchers("/addMember") 配置addMember请求权限 hasAnyAuthority("addMember")

    }

    /**
     * There is no PasswordEncoder mapped for the id "null"
     * 原因:升级为Security5.0以上密码支持多中加密方式，恢复以前模式
     *
     * @return
     */
//    @Bean
//    public static NoOpPasswordEncoder passwordEncoder() {
//        return (NoOpPasswordEncoder) NoOpPasswordEncoder.getInstance();
//    }

}
