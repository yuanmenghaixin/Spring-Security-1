package com.mayikt.filter;

import com.mayikt.utils.MayiktJwtUtils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 验证JWT登录信息
 */
public class JWTValidationAuthorizationFilter extends BasicAuthenticationFilter {

    public JWTValidationAuthorizationFilter(AuthenticationManager authenticationManager) {
        super(authenticationManager);
    }

    /**
     * 拦截请求
     * @param request
     * @param response
     * @param chain
     * @throws IOException
     * @throws ServletException
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws IOException, ServletException {

        String tokenHeader = request.getHeader("token");
        // 如果请求头中有token，则进行解析，并且设置认证信息
        SecurityContextHolder.getContext().setAuthentication(getAuthentication(tokenHeader));
        super.doFilterInternal(request, response, chain);
    }

    // 这里从token中获取用户信息并新建一个token 验证tocken
    private UsernamePasswordAuthenticationToken getAuthentication(String tokenHeader) {

        String username = MayiktJwtUtils.getUsername(tokenHeader);
//        List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
        if (username != null) {
            //解析权限列表验证权限
//            authorities.add(new SimpleGrantedAuthority("addMember"));
//            authorities.add(new SimpleGrantedAuthority("showMember"));
//            authorities.add(new SimpleGrantedAuthority("updateMember"));
//            authorities.add(new SimpleGrantedAuthority("delMember"));
            List<SimpleGrantedAuthority> userRoles = MayiktJwtUtils.getUserRole(tokenHeader);
            return new UsernamePasswordAuthenticationToken(username, null, userRoles);
        }
        return null;
    }
}
