package com.gym.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.core.authority.mapping.SimpleAuthorityMapper;
import org.springframework.security.core.userdetails.UserDetailsService;
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
            // 获取用户角色
            boolean isAdmin = authentication.getAuthorities().stream()
                    .anyMatch(auth -> auth.getAuthority().equals("ADMIN"));
            boolean isTrainer = authentication.getAuthorities().stream()
                    .anyMatch(auth -> auth.getAuthority().equals("TRAINER"));
            boolean isMember = authentication.getAuthorities().stream()
                    .anyMatch(auth -> auth.getAuthority().equals("MEMBER"));

            // 根据角色跳转到不同页面
            if (isAdmin) {
                response.sendRedirect("/admin/dashboard");
            } else if (isTrainer) {
                response.sendRedirect("/coach/dashboard");
            } else if (isMember) {
                response.sendRedirect("/member/dashboard");
            } else {
                // 默认跳转到首页
                response.sendRedirect("/");
            }
        };
    }

    /**
     * 配置 HTTP 安全规则
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            // 禁用 CSRF 保护
            .csrf().disable()
            
            // 配置授权规则
            .authorizeRequests()
                // 匿名路径配置
                .antMatchers("/login", "/register", "/css/**", "/js/**").permitAll()
                // 会员权限路径
                .antMatchers("/member/**").hasRole("MEMBER")
                // 教练权限路径
                .antMatchers("/coach/**").hasRole("TRAINER")
                // 管理员权限路径
                .antMatchers("/admin/**").hasRole("ADMIN")
                // 其他所有请求需要认证
                .anyRequest().authenticated()
                .and()
            
            // 配置表单登录
            .formLogin()
                .loginPage("/login")
                .loginProcessingUrl("/login")
                .usernameParameter("username")
                .passwordParameter("password")
                .successHandler(successHandler())
                .failureUrl("/login?error=true")
                .and()
            
            // 配置登出
            .logout()
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout=true")
                .invalidateHttpSession(true)
                .clearAuthentication(true)
                .and()
            
            // 配置会话管理
            .sessionManagement()
                .maximumSessions(1)
                .expiredUrl("/login?expired=true");
    }

    /**
     * 配置认证管理器
     * 注：实际项目中应替换为自定义的 UserDetailsService
     */
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        // 这里使用内存认证作为示例，实际项目中应使用数据库认证
        auth
            .inMemoryAuthentication()
                .withUser("admin").password(passwordEncoder().encode("admin123")).roles("ADMIN")
                .and()
                .withUser("coach").password(passwordEncoder().encode("coach123")).roles("TRAINER")
                .and()
                .withUser("member").password(passwordEncoder().encode("member123")).roles("MEMBER");
    }
}