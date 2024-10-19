package co.allconnected.fussiontech.ordersservice.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "\"order\"", schema = "all_connected_products")
public class Order {
    @Id
    @Column(name = "id_order", nullable = false)
    private UUID id;

    @Column(name = "creation_date", nullable = false)
    private Instant creationDate;

    @Column(name = "delivery_date")
    private Instant deliveryDate;

    @Column(name = "id_user", nullable = false, length = 28)
    private String idUser;

    @Column(name = "total", nullable = false)
    private Double total;

    @Column(name = "status", nullable = false, length = Integer.MAX_VALUE)
    private String status;

    @OneToMany(mappedBy = "order")
    private Set<ProductOrder> productOrders = new LinkedHashSet<>();

}