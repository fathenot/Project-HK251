package com.example.supermarket.service.impl;

import com.example.supermarket.dto.PageResponse;
import com.example.supermarket.dto.request.order.OrderCreateRequest;
import com.example.supermarket.dto.request.order.OrderSearchRequest;
import com.example.supermarket.dto.request.order.OrderUpdateStatusRequest;
import com.example.supermarket.dto.response.order.OrderDetailResponse;
import com.example.supermarket.dto.response.order.OrderResponse;
import com.example.supermarket.dto.response.order.OrderStatisticsResponse;
import com.example.supermarket.entity.Customer;
import com.example.supermarket.entity.Order;
import com.example.supermarket.entity.OrderDetail;
import com.example.supermarket.entity.Product;
import com.example.supermarket.exception.BadRequestError;
import com.example.supermarket.exception.NotFoundError;
import com.example.supermarket.mapper.OrderMapper;
import com.example.supermarket.repository.CustomerRepository;
import com.example.supermarket.repository.OrderDetailRepository;
import com.example.supermarket.repository.OrderRepository;
import com.example.supermarket.repository.ProductRepository;
import com.example.supermarket.service.OrderServiceI;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService implements OrderServiceI {
    private final OrderRepository orderRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;
    private final OrderMapper orderMapper;

    private static final int POINTS_PER_DOLLAR = 1; // 1 point per dollar spent
    private static final BigDecimal POINT_VALUE = new BigDecimal("0.01"); // 1 point = $0.01

    /**
     * Create new order
     */
    @Transactional
    public OrderDetailResponse createOrder(OrderCreateRequest request) {
        log.info("Creating new order for customer ID: {}", request.getCustomerId());

        // Validate customer
        Customer customer = customerRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new NotFoundError("Customer not found"));

        // Calculate total money
        BigDecimal totalMoney = BigDecimal.ZERO;
        List<OrderDetail> orderDetails = new ArrayList<>();

        for (var itemRequest : request.getItems()) {
            Product product = productRepository.findById(itemRequest.getProductId())
                    .orElseThrow(() -> new NotFoundError("Product not found with ID: " + itemRequest.getProductId()));

            BigDecimal subTotal = product.getPrice().multiply(BigDecimal.valueOf(itemRequest.getQuantity()));
            totalMoney = totalMoney.add(subTotal);

            OrderDetail detail = OrderDetail.builder()
                    .quantity(itemRequest.getQuantity())
                    .subtotal(subTotal.floatValue())
                    .build();

            detail.setProductId(product.getId());
            orderDetails.add(detail);
        }

        // Apply loyalty points discount if requested
        Integer pointsUsed = 0;
        if (request.getUsePoints() && customer.getLoyaltyPoints() > 0) {
            BigDecimal discount = BigDecimal.valueOf(customer.getLoyaltyPoints()).multiply(POINT_VALUE);
            if (discount.compareTo(totalMoney) > 0) {
                discount = totalMoney;
            }
            totalMoney = totalMoney.subtract(discount);
            pointsUsed = discount.divide(POINT_VALUE).intValue();

            // Deduct points from customer
            customer.setLoyaltyPoints(customer.getLoyaltyPoints() - pointsUsed);
        }

        // Create order
        Order order = Order.builder()
                .customerId(customer.getId())
                .createdAt(LocalDateTime.now())
                .status("Pending")
                .totalMoney(totalMoney.doubleValue())
                .build();

        order = orderRepository.save(order);

        // Save order details
        for (OrderDetail detail : orderDetails) {
            detail.getId().setOrderID(order.getId()); // Set orderId vào composite key
            orderDetailRepository.save(detail);
        }

        // Calculate and add loyalty points earned
        Integer pointsEarned = totalMoney.intValue() * POINTS_PER_DOLLAR;
        customer.setLoyaltyPoints(customer.getLoyaltyPoints() + pointsEarned);
        customerRepository.save(customer);

        log.info("Order created successfully with ID: {}", order.getId());

        return orderMapper.toDetailResponse(order, orderDetails, pointsEarned, pointsUsed);
    }

    /**
     * Get order by ID
     */
    @Transactional(readOnly = true)
    public OrderDetailResponse getOrderById(Long id) {
        log.info("Getting order with ID: {}", id);

        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new NotFoundError("Order not found with ID: " + id));

        List<OrderDetail> orderDetails = orderDetailRepository.findByIdOrderID(id);

        Integer pointsEarned = order.getTotalMoney().intValue() * POINTS_PER_DOLLAR;

        return orderMapper.toDetailResponse(order, orderDetails, pointsEarned, 0);
    }

    /**
     * Search orders with filters
     */
    @Transactional(readOnly = true)
    public PageResponse<OrderResponse> searchOrders(OrderSearchRequest request) {
        log.info("Searching orders with filters");

        Sort sort = request.getSortDirection().equalsIgnoreCase("DESC")
                ? Sort.by(request.getSortBy()).descending()
                : Sort.by(request.getSortBy()).ascending();

        Pageable pageable = PageRequest.of(request.getPage(), request.getSize(), sort);

        Page<Order> orderPage;

        // Apply filters
        if (request.getCustomerId() != null && request.getStatus() != null) {
            orderPage = orderRepository.findByCustomerIdAndStatus(
                    request.getCustomerId(),
                    request.getStatus(),
                    pageable
            );
        } else if (request.getCustomerId() != null) {
            orderPage = orderRepository.findByCustomerId(request.getCustomerId(), pageable);
        } else if (request.getStatus() != null) {
            orderPage = orderRepository.findByStatus(request.getStatus(), pageable);
        } else if (request.getFromDate() != null && request.getToDate() != null) {
            orderPage = orderRepository.findByCreatedAtBetween(
                    request.getFromDate(),
                    request.getToDate(),
                    pageable
            );
        } else {
            orderPage = orderRepository.findAll(pageable);
        }

        List<OrderResponse> content = orderPage.getContent().stream()
                .map(order -> {
                    Long totalItems = orderDetailRepository.countByIdOrderID(order.getId());
                    Integer pointsEarned = order.getTotalMoney().intValue() * POINTS_PER_DOLLAR;
                    return orderMapper.toResponse(order, totalItems.intValue(), pointsEarned);
                })
                .toList();

        return PageResponse.<OrderResponse>builder()
                .content(content)
                .pageNumber(orderPage.getNumber())
                .pageSize(orderPage.getSize())
                .totalElements(orderPage.getTotalElements())
                .totalPages(orderPage.getTotalPages())
                .isLast(orderPage.isLast())
                .build();
    }

    /**
     * Get customer's orders
     */
    @Transactional(readOnly = true)
    public PageResponse<OrderResponse> getMyOrders(Long customerId, Integer page, Integer size) {
        log.info("Getting orders for customer ID: {}", customerId);

        OrderSearchRequest searchRequest = new OrderSearchRequest();
        searchRequest.setCustomerId(customerId);
        searchRequest.setPage(page);
        searchRequest.setSize(size);
        searchRequest.setSortBy("createdAt");
        searchRequest.setSortDirection("DESC");

        return searchOrders(searchRequest);
    }

    /**
     * Update order status
     */
    @Transactional
    public OrderResponse updateOrderStatus(Long id, OrderUpdateStatusRequest request) {
        log.info("Updating order status for ID: {} to {}", id, request.getStatus());

        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new NotFoundError("Order not found with ID: " + id));

        // Validate status transition
        String currentStatus = order.getStatus();
        String newStatus = request.getStatus();

        if (!isValidStatusTransition(currentStatus, newStatus)) {
            throw new BadRequestError("Invalid status transition from " + currentStatus + " to " + newStatus);
        }

        order.setStatus(newStatus);
        order = orderRepository.save(order);

        log.info("Order status updated successfully");

        Long totalItems = orderDetailRepository.countByIdOrderID(id);
        Integer pointsEarned = order.getTotalMoney().intValue() * POINTS_PER_DOLLAR;

        return orderMapper.toResponse(order, totalItems.intValue(), pointsEarned);
    }

    /**
     * Cancel order
     */
    @Transactional
    public void cancelOrder(Long id) {
        log.info("Cancelling order with ID: {}", id);

        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new NotFoundError("Order not found with ID: " + id));

        if (!order.getStatus().equals("Pending")) {
            throw new BadRequestError("Only pending orders can be cancelled");
        }

        order.setStatus("Cancelled");

        // Refund loyalty points if used
        // This would require storing points used, which we should add to Order entity

        orderRepository.save(order);
        log.info("Order cancelled successfully");
    }

    /**
     * Delete order (soft delete)
     */
    @Transactional
    public void deleteOrder(Long id) {
        log.info("Deleting order with ID: {}", id);

        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new NotFoundError("Order not found with ID: " + id));

        if (order.getStatus().equals("Processing") || order.getStatus().equals("Completed")) {
            throw new BadRequestError("Cannot delete order with status: " + order.getStatus());
        }

        orderRepository.delete(order);
        log.info("Order deleted successfully");
    }

    /**
     * Validate status transition
     */
    private boolean isValidStatusTransition(String currentStatus, String newStatus) {
        return switch (currentStatus) {
            case "Pending" -> newStatus.equals("Processing") || newStatus.equals("Cancelled");
            case "Processing" -> newStatus.equals("Completed") || newStatus.equals("Cancelled");
            case "Completed", "Cancelled" -> false;
            default -> false;
        };
    }

    /**
     * Get order statistics
     */
    @Transactional(readOnly = true)
    public OrderStatisticsResponse getOrderStatistics(LocalDateTime fromDate, LocalDateTime toDate) {
        log.info("Getting order statistics from {} to {}", fromDate, toDate);

        Long totalOrders = orderRepository.countByCreatedAtBetween(fromDate, toDate);
        BigDecimal totalRevenue = orderRepository.sumTotalMoneyByCreatedAtBetween(fromDate, toDate);

        Long pendingOrders = orderRepository.countByStatusAndCreatedAtBetween("Pending", fromDate, toDate);
        Long completedOrders = orderRepository.countByStatusAndCreatedAtBetween("Completed", fromDate, toDate);

        return OrderStatisticsResponse.builder()
                .totalOrders(totalOrders)
                .totalRevenue(totalRevenue != null ? totalRevenue : BigDecimal.ZERO)
                .pendingOrders(pendingOrders)
                .completedOrders(completedOrders)
                .averageOrderValue(totalOrders > 0 ?
                        (totalRevenue != null ? totalRevenue : BigDecimal.ZERO).divide(BigDecimal.valueOf(totalOrders), 2, BigDecimal.ROUND_HALF_UP)
                        : BigDecimal.ZERO)
                .build();
    }
}
