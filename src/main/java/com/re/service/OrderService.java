package com.re.service;

import com.re.entity.Order;
import com.re.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    // Các trường cho phép sắp xếp để tránh bẫy 2
    private static final List<String> ALLOWED_SORT_FIELDS = Arrays.asList("createdAt", "totalAmount");

    public Page<Order> getOrderHistory(Long userId, String status, int page, int size, String sortBy, String sortDir) {
        
        // Bẫy 2 - Lỗi tham số sắp xếp: Kiểm tra cột có hợp lệ không
        if (!ALLOWED_SORT_FIELDS.contains(sortBy)) {
            sortBy = "createdAt"; // fallback về mặc định
        }

        Sort.Direction direction = sortDir.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
        Sort sort = Sort.by(direction, sortBy);

        // Bẫy 1 - Tràn viền phân trang (số âm):
        if (page < 1) {
            page = 1;
        }

        Pageable pageable = PageRequest.of(page - 1, size, sort); // Spring Data JPA đếm từ 0
        Page<Order> resultPage = fetchOrders(userId, status, pageable);

        // Bẫy 1 - Tràn viền phân trang (số lớn hơn tổng trang):
        if (page > resultPage.getTotalPages() && resultPage.getTotalPages() > 0) {
            // Lấy trang cuối cùng
            pageable = PageRequest.of(resultPage.getTotalPages() - 1, size, sort);
            resultPage = fetchOrders(userId, status, pageable);
        }

        return resultPage;
    }

    private Page<Order> fetchOrders(Long userId, String status, Pageable pageable) {
        if (status == null || status.equalsIgnoreCase("ALL") || status.isEmpty()) {
            return orderRepository.findByUserId(userId, pageable);
        } else {
            return orderRepository.findByUserIdAndStatus(userId, status, pageable);
        }
    }
}
