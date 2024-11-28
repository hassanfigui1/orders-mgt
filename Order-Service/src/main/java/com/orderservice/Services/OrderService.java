package com.orderservice.Services;

import com.orderservice.Models.Order;
import com.orderservice.Repositories.OrderRepo;
import com.orderservice.interfaces.CustomerClient;
import com.orderservice.interfaces.ProductClient;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class OrderService {
    @Autowired
    private OrderRepo orderRepo;

    @Autowired
    private ProductClient productClient;

    @Autowired
    private CustomerClient customerClient;

    public Order createOrder(Order order) {
        order.setOrderDate(new Date());
        double totalAmount = order.getOrderItems().stream()
                .mapToDouble(item -> item.getPrice() * item.getQuantity())
                .sum();
        order.setTotalAmount(totalAmount);
        return orderRepo.save(order);
    }

    public Order getOrderById(int id) {
        return orderRepo.findById(id).orElse(null);
    }

    public List<Order> getAllOrders() {
        return orderRepo.findAll();
    }

    @CircuitBreaker(name = "customerService", fallbackMethod = "customerFallback")
    public boolean isValidCustomer(int customerId) {
        Object customer = customerClient.getCustomerById(customerId);
        if (customer == null) {
            System.out.println("Customer with ID " + customerId + " not found.");
        }
        return customer != null;
    }

    @CircuitBreaker(name = "productService", fallbackMethod = "productFallback")
    public boolean isValidProduct(int productId) {
        Object product = productClient.getProductById(productId);
        if (product == null) {
            System.out.println("Product with ID " + productId + " not found.");
        }
        return product != null;
    }

    // Fallback methods
    public boolean customerFallback(int customerId, Throwable t) {
        System.out.println("Customer Service is currently unavailable.");
        return false;
    }

    public boolean productFallback(int productId, Throwable t) {
        System.out.println("Product Service is currently unavailable.");
        return false;
    }
}
