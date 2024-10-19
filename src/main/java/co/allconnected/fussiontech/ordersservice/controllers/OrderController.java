package co.allconnected.fussiontech.ordersservice.controllers;

import co.allconnected.fussiontech.ordersservice.dtos.OrderCreateDTO;
import co.allconnected.fussiontech.ordersservice.dtos.OrderDTO;
import co.allconnected.fussiontech.ordersservice.model.Order;
import co.allconnected.fussiontech.ordersservice.services.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {
    private final OrderService orderService;

    @Autowired
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity <OrderDTO> createOrder (@RequestBody OrderCreateDTO orderDTO) {
        try{
            OrderDTO order = orderService.createOrder(orderDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(order);
        }
        catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}
