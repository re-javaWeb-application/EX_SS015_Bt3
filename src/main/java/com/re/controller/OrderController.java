package com.re.controller;

import com.re.entity.Order;
import com.re.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class OrderController {

    @Autowired
    private OrderService orderService;

    @GetMapping("/orders")
    public String getOrders(
            @RequestParam(name = "status", required = false, defaultValue = "ALL") String status,
            @RequestParam(name = "page", required = false, defaultValue = "1") int page,
            @RequestParam(name = "size", required = false, defaultValue = "10") int size,
            @RequestParam(name = "sortBy", required = false, defaultValue = "createdAt") String sortBy,
            @RequestParam(name = "sortDir", required = false, defaultValue = "desc") String sortDir,
            Model model) {
            
        Long currentUserId = 1L;

        try {
            Page<Order> orderPage = orderService.getOrderHistory(currentUserId, status, page, size, sortBy, sortDir);

            model.addAttribute("orders", orderPage.getContent());
            model.addAttribute("currentPage", orderPage.getNumber() + 1);
            model.addAttribute("totalPages", orderPage.getTotalPages());
            model.addAttribute("totalItems", orderPage.getTotalElements());
            model.addAttribute("status", status);
            model.addAttribute("sortBy", sortBy);
            model.addAttribute("sortDir", sortDir);

            return "orders";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Đã xảy ra lỗi khi tải danh sách đơn hàng.");
            return "error"; 
        }
    }
}
