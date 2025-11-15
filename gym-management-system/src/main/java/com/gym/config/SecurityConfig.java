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

    @Autowired
    @org.springframework.context.annotation.Lazy
    private org.springframework.security.core.userdetails.UserDetailsService userDetailsService;


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
            // 记录登录成功的用户名，便于调试
            org.slf4j.LoggerFactory.getLogger(SecurityConfig.class)
                    .info("认证成功，用户：{}", authentication.getName());
            // Spring Security 中角色权限格式为 ROLE_XXX
            boolean isAdmin = authentication.getAuthorities().stream()
                    .anyMatch(auth -> "ROLE_ADMIN".equals(auth.getAuthority()));
            boolean isTrainer = authentication.getAuthorities().stream()
                    .anyMatch(auth -> "ROLE_TRAINER".equals(auth.getAuthority()));
            boolean isMember = authentication.getAuthorities().stream()
                    .anyMatch(auth -> "ROLE_MEMBER".equals(auth.getAuthority()));

            if (isAdmin) {
                // 管理员首页在项目中映射为 /admin
                response.sendRedirect("/admin");
            } else if (isTrainer) {
                // 教练首页映射为 /coach
                response.sendRedirect("/coach");
            } else if (isMember) {
                // 会员首页映射为 /member（该路径会进一步重定向到课程列表）
                response.sendRedirect("/member");
            } else {
                // 无有效角色时跳回登录页并提示错误
                response.sendRedirect("/login?error=无有效用户角色");
            }
        };
    }

    /**
     * 自定义失败处理器：记录失败原因并重定向到带友好消息的登录页
     */
    @Bean
    public org.springframework.security.web.authentication.AuthenticationFailureHandler failureHandler() {
        return (request, response, exception) -> {
            org.slf4j.LoggerFactory.getLogger(SecurityConfig.class)
                    .warn("认证失败：{}", exception.getMessage());
            // 将通用错误信息返回到登录页，避免直接暴露异常详情
            // Location header 必须为可编码的 ASCII 字符串，先进行 URL 编码
            try {
                String msg = java.net.URLEncoder.encode("用户名或密码错误", java.nio.charset.StandardCharsets.UTF_8.name());
                response.sendRedirect("/login?error=" + msg);
            } catch (java.io.UnsupportedEncodingException e) {
                // 回退到不含中文的错误标识
                response.sendRedirect("/login?error=true");
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
                .failureHandler(failureHandler())   // 登录失败处理器（记录并友好提示）
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

    /**
     * 配置认证管理，使用自定义的 UserDetailsService 和 BCrypt 密码编码器
     */
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
    }
}