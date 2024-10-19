package co.allconnected.fussiontech.ordersservice.dtos;

import co.allconnected.fussiontech.ordersservice.model.Order;
import co.allconnected.fussiontech.ordersservice.model.ProductOrder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class OrderDTO {
    UUID id;
    Instant creationDate;
    Instant deliveryDate;
    String idUser;
    Double total;
    String status;
    String [] products;

    public OrderDTO(Order order){
        this.id = order.getId();
        this.creationDate = order.getCreationDate();
        this.deliveryDate = order.getDeliveryDate();
        this.idUser = order.getIdUser();
        this.total = order.getTotal();
        this.status = order.getStatus();
        this.products = order.getProductOrders().stream().map(ProductOrder::getProduct).toArray(String[]::new);
    }
}