package com.gym.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.core.authority.mapping.SimpleAuthorityMapper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

/**
 * Spring Security 配置类
 * 根据需求规格说明书 2.1-2.3 配置安全规则
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {


    /**
     * 配置密码编码器 - 使用 BCryptPasswordEncoder
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * 配置权限映射器 - 保持角色名称大小写一致性
     */
    @Bean
    public GrantedAuthoritiesMapper grantedAuthoritiesMapper() {
        SimpleAuthorityMapper mapper = new SimpleAuthorityMapper();
        mapper.setConvertToUpperCase(true);
        return mapper;
    }

    /**
     * 配置登录成功处理器 - 根据不同角色跳转到不同页面
     */
    @Bean
    public AuthenticationSuccessHandler successHandler() {
        return (request, response, authentication) -> {
            // Spring Security 中角色权限格式为 ROLE_XXX
            boolean isAdmin = authentication.getAuthorities().stream()
                    .anyMatch(auth -> "ROLE_ADMIN".equals(auth.getAuthority()));
            boolean isTrainer = authentication.getAuthorities().stream()
                    .anyMatch(auth -> "ROLE_TRAINER".equals(auth.getAuthority()));
            boolean isMember = authentication.getAuthorities().stream()
                    .anyMatch(auth -> "ROLE_MEMBER".equals(auth.getAuthority()));

            if (isAdmin) {
                response.sendRedirect("/admin/dashboard");
            } else if (isTrainer) {
                response.sendRedirect("/coach/dashboard");
            } else if (isMember) {
                response.sendRedirect("/member/dashboard");
            } else {
                // 无有效角色时跳回登录页并提示错误
                response.sendRedirect("/login?error=无有效用户角色");
            }
        };
    }

    /**
     * 配置 HTTP 安全规则
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            // 禁用 CSRF 保护（适用于传统表单提交，若需更高安全可启用）
            .csrf().disable()

            // 授权规则配置
            .authorizeRequests()
                // 匿名可访问路径
                .antMatchers("/login", "/register", "/css/**", "/js/**", "/images/**").permitAll()
                // 会员权限路径
                .antMatchers("/member/**").hasRole("MEMBER")
                // 教练权限路径
                .antMatchers("/coach/**").hasRole("TRAINER")
                // 管理员权限路径
                .antMatchers("/admin/**").hasRole("ADMIN")
                // 其他所有请求必须认证
                .anyRequest().authenticated()
                .and()

            // 表单登录配置
            .formLogin()
                .loginPage("/login")               // 自定义登录页
                .loginProcessingUrl("/login")      // 登录处理 URL（Spring Security 拦截）
                .usernameParameter("username")     // 表单中用户名字段名
                .passwordParameter("password")     // 表单中密码字段名
                .successHandler(successHandler())  // 登录成功处理器
                .failureUrl("/login?error=true")   // 登录失败跳转
                .permitAll()
                .and()

            // 登出配置
            .logout()
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout=true")
                .invalidateHttpSession(true)
                .clearAuthentication(true)
                .permitAll()
                .and()

            // 会话管理：同一用户只允许一个会话
            .sessionManagement()
                .maximumSessions(1)
                .expiredUrl("/login?expired=true");
    }
}