package com.orderservice.Controllers;

import com.orderservice.Models.Order;
import com.orderservice.Models.orderitem.OrderItem;
import com.orderservice.Services.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    @Autowired
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping
    public ResponseEntity<List<Order>> getAllOrders() {
        return ResponseEntity.ok(orderService.getAllOrders());
    }

    @PostMapping
    public ResponseEntity<Order> createOrder(@RequestBody Order order) {
        try {
            // validate the customer
            if (!orderService.isValidCustomer(order.getCustomerId())) {
                return ResponseEntity.badRequest().build();
            }

            // validate the product IDs in the order items
            for (OrderItem item : order.getOrderItems()) {
                if (!orderService.isValidProduct(item.getProductId())) {
                    System.out.println("Product validation error");
                    return ResponseEntity.badRequest().build();
                }
            }

            // save the order if all validations pass
            Order createdOrder = orderService.createOrder(order);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdOrder);
        } catch (Exception e) {
            System.out.println("Error creating order: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
