package com.computerShop.demo1.controller.client;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;

public class CustomErrorController implements ErrorController {

    @RequestMapping("/error")
    public String handleErrorRequest(HttpServletRequest request) {
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);

        if (status != null) {
            int statusCode = Integer.parseInt(status.toString());
            if (statusCode == 404 || statusCode == 403) {
                return "client/homepage/not-found";
            }
        }
        // Khi truy cập /error trực tiếp hoặc không có mã lỗi trả về trang lỗi chung
        /*return "client/homepage/not-found";*/

        // Nếu không phải lỗi 404, ném ngoại lệ để Spring Boot xử lý
        throw new RuntimeException("An error occurred: " + (status != null ? status : "Unknown error"));
    }
}

