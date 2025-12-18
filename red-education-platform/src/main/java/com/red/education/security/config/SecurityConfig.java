package com.red.education.security.config;

import com.red.education.security.filter.JwtAuthenticationFilter;
import com.red.education.security.handler.AccessDeniedHandlerImpl;
import com.red.education.security.handler.AuthenticationEntryPointImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

/**
 * Spring Security配置类
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Autowired
    private AuthenticationEntryPointImpl authenticationEntryPoint;

    @Autowired
    private AccessDeniedHandlerImpl accessDeniedHandler;

    /**
     * 密码编码器
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * 认证管理器
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    /**
     * CORS配置
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(Arrays.asList("*"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    /**
     * 注册CORS过滤器，确保优先级最高
     */
    @Bean
    public org.springframework.web.filter.CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.addAllowedOriginPattern("*");
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");
        source.registerCorsConfiguration("/**", config);
        return new org.springframework.web.filter.CorsFilter(source);
    }

    /**
     * Security过滤链配置
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // 禁用CSRF
                .csrf().disable()
                // 允许iframe嵌套（解决PDF预览问题）
                .headers().frameOptions().disable().and()
                // 配置CORS
                .cors().and()
                // 配置Session管理为无状态
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
                // 配置异常处理
                .exceptionHandling()
                .authenticationEntryPoint(authenticationEntryPoint)
                .accessDeniedHandler(accessDeniedHandler).and()
                // 配置请求权限
                .authorizeRequests()
                // 允许访问Swagger文档
                .antMatchers("/doc.html", "/webjars/**", "/swagger-resources/**", "/v2/api-docs", "/v3/api-docs")
                .permitAll()
                // 允许访问用户注册和登录接口
                .antMatchers("/user/register", "/user/login", "/user/reset-code", "/user/reset-password").permitAll()
                // 允许访问公共配置
                .antMatchers("/public/config").permitAll()
                // 允许访问公开统计接口（支持带/api前缀和不带前缀）
                .antMatchers("/api/statistics/public", "/statistics/public").permitAll()
                // 允许访问资源模块的公开接口（列表、详情、分类）
                .antMatchers("/resource/list", "/resource/**", "/resource/categories").permitAll()
                // 允许访问课程模块的公开接口（列表、详情）
                .antMatchers("/course/list", "/course/**").permitAll()
                // 允许访问考试模块的公开接口（试卷列表、排行榜）
                .antMatchers("/exam/list", "/exam/**/leaderboard").permitAll()
                // 允许访问社区模块的公开接口（帖子列表、详情、搜索、评论列表、热门话题）
                .antMatchers("/post/list", "/post/search", "/post/**", "/interaction/comment/**",
                        "/admin/post/topics/hot")
                .permitAll()
                // 允许访问积分商城的公开接口（商品列表、详情、排行榜）
                .antMatchers("/product/list", "/product/**", "/points/leaderboard").permitAll()
                // 允许访问已上传的文件（查看），但上传需要认证
                .antMatchers("/files/upload").authenticated()
                .antMatchers("/files/**").permitAll()
                // 其他所有请求需要认证
                .anyRequest().authenticated();

        // 添加JWT过滤器
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
