package com.kingbo.petserver.config;


import com.kingbo.petserver.interceptor.MyInterceptor;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Resource
    private MyInterceptor myInterceptor;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                // 指定允许的来源，支持多个,也可以偷懒,直接写  *
                /*.allowedOriginPatterns(
                        "http://localhost:*",
                        "http://127.0.0.1:*",
                        "http://xq.com",
                        "https://xq.com",
                        "http://*.xq.com",
                        "https://*.xq.com"
                )*/
                .allowedOriginPatterns("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH")
                .allowedHeaders("*")
                .exposedHeaders("Authorization", "Content-Disposition")
                .allowCredentials(true)
                .maxAge(3600);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(myInterceptor).addPathPatterns("/**")
                .excludePathPatterns(          // 排除不需要登录的路径
                "/login/accountLogin",      // 登录接口不能拦截
                "/org/shop/fileUpload",         // 图片上传通常不拦截
                "/org/shop/onboarding",   // 店铺入驻注册也不拦截
                "org/shop/sendVerifyCode"
        );
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        WebMvcConfigurer.super.addResourceHandlers(registry);
    }
}