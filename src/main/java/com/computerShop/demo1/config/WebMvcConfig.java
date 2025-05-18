package com.computerShop.demo1.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Đăng ký thư mục uploads/images/avatar làm nơi lưu trữ tài nguyên tĩnh
        registry.addResourceHandler("/uploads/images/avatar/**")
                .addResourceLocations("file:uploads/images/avatar/");

        registry.addResourceHandler("/uploads/images/product/**")
                .addResourceLocations("file:uploads/images/product/");
    }
}
