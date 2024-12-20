package co.allconnected.fussiontech.ordersservice.controllers;

import co.allconnected.fussiontech.ordersservice.dtos.OrderCreateDTO;
import co.allconnected.fussiontech.ordersservice.dtos.OrderDTO;
import co.allconnected.fussiontech.ordersservice.dtos.ProductDTO;
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

    /*
    CREATE ORDER
     */
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

    /*
    GET METHODS: By id, all orders, orders by user
     */
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

    @GetMapping("/{id_user}/user")
    public ResponseEntity<OrderDTO[]> getOrdersByUser(@PathVariable String id_user) {
        try {
            OrderDTO[] orders = orderService.getOrdersByUser(id_user);
            return ResponseEntity.status(HttpStatus.OK).body(orders);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/{id_business}/business")
    public ResponseEntity<OrderDTO[]> getOrdersByBusiness(@PathVariable UUID id_business) {
        try {
            OrderDTO[] orders = orderService.getOrdersByBusiness(id_business);
            return ResponseEntity.status(HttpStatus.OK).body(orders);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    /*
    ENDPOINTS FOR UPDATE STATUS AND VALUE OF ORDERS - PRODUCTS IF NEEDED
     */
    @PutMapping("/{id}/confirmed")
    public ResponseEntity<?> markOrderAsConfirmed(@PathVariable UUID id) {
        try {
            OrderDTO order = orderService.markOrderAsConfirmed(id);
            return ResponseEntity.status(HttpStatus.OK).body(order);
        } catch (OperationException e) {
            return ResponseEntity.status(e.getCode()).body(new Response(e.getCode(), e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PutMapping("/{id}/delivered")
    public ResponseEntity<?> markOrderAsDelivered(@PathVariable UUID id) {
        try {
            OrderDTO order = orderService.markOrderAsDelivered(id);
            return ResponseEntity.status(HttpStatus.OK).body(order);
        } catch (OperationException e) {
            return ResponseEntity.status(e.getCode()).body(new Response(e.getCode(), e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

   /*
   DELETE ORDER
    */
   @DeleteMapping("/{id}")
   public ResponseEntity<?> deleteOrder(@PathVariable UUID id) {
       try {
           orderService.deleteOrder(id);
           return ResponseEntity.status(HttpStatus.OK).body(new Response(HttpStatus.OK.value(), "Order deleted"));
       } catch (OperationException e) {
           return ResponseEntity.status(e.getCode()).body(new Response(e.getCode(), e.getMessage()));
       } catch (Exception e) {
           return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
       }
   }

    /*
    ENDPOINTS FOR ADDING AND DELETING PRODUCTS FROM ORDERS
     */

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

    @DeleteMapping("/{id_order}/products/{id_product}/delete")
    public ResponseEntity<?> deleteProductFromOrder(@PathVariable UUID id_order, @PathVariable Integer id_product) {
        try {
            OrderDTO order = orderService.deleteProductFromOrder(id_order, id_product);
            return ResponseEntity.status(HttpStatus.OK).body(order);
        } catch (OperationException e) {
            return ResponseEntity.status(e.getCode()).body(new Response(e.getCode(), e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

}
