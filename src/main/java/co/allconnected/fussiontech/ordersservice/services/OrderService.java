package co.allconnected.fussiontech.ordersservice.services;

import co.allconnected.fussiontech.ordersservice.dtos.OrderCreateDTO;
import co.allconnected.fussiontech.ordersservice.dtos.OrderDTO;
import co.allconnected.fussiontech.ordersservice.model.Order;
import co.allconnected.fussiontech.ordersservice.repository.OrderRepository;
import co.allconnected.fussiontech.ordersservice.repository.ProductOrderRepository;
import co.allconnected.fussiontech.ordersservice.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final ProductOrderRepository productOrderRepository;

    @Autowired
    public OrderService(OrderRepository orderRepository, ProductRepository productRepository, ProductOrderRepository productOrderRepository) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.productOrderRepository = productOrderRepository;
    }

    public OrderDTO createOrder(OrderCreateDTO orderCreateDTO) {
        Order order = new Order(orderCreateDTO);
        return new OrderDTO(orderRepository.save(order));
    }

    public OrderDTO getOrderByUUID(UUID id) {
        return orderRepository.findById(id)
                .map(OrderDTO::new)
                .orElseThrow(() -> new RuntimeException("Order not found"));
    }

    public OrderDTO [] getOrders() {
        return orderRepository.findAll().stream()
                .map(OrderDTO::new)
                .toArray(OrderDTO[]::new);
    }
}

