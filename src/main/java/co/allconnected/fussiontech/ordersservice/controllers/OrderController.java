package co.allconnected.fussiontech.ordersservice.controllers;

import co.allconnected.fussiontech.ordersservice.dtos.OrderCreateDTO;
import co.allconnected.fussiontech.ordersservice.dtos.OrderDTO;
import co.allconnected.fussiontech.ordersservice.dtos.Response;
import co.allconnected.fussiontech.ordersservice.services.OrderService;
import co.allconnected.fussiontech.ordersservice.utils.OperationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

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

    @GetMapping("/{id}")
    public ResponseEntity<OrderDTO> getOrder(@PathVariable UUID id) {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(orderService.getOrderByUUID(id));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping
    public ResponseEntity<OrderDTO[]> getOrders() {
        try {
            OrderDTO[] orders = orderService.getOrders();
            return ResponseEntity.status(HttpStatus.OK).body(orders);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PostMapping("/{id_order}/products/{id_product}")
    public ResponseEntity<?> addProductToOrder(@PathVariable UUID id_order, @PathVariable Integer id_product, @RequestParam Integer quantity) {
        try {
            /*
            System.out.println("id_order: " + id_order);
            System.out.println("id_product: " + id_product);
            System.out.println("quantity: " + quantity);
             */

            OrderDTO order = orderService.addProductToOrder(id_order, id_product, quantity);
            return ResponseEntity.status(HttpStatus.CREATED).body(order);
        }
        catch (OperationException e) {
            return ResponseEntity.status(e.getCode()).body(new Response(e.getCode(), e.getMessage()));
        }catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

}
