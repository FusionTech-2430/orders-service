package co.allconnected.fussiontech.ordersservice.model;

import co.allconnected.fussiontech.ordersservice.dtos.OrderCreateDTO;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name = "\"order\"", schema = "all_connected_products")
public class Order {
    public Order (OrderCreateDTO dto){
        this.id = UUID.randomUUID();
        this.creationDate = Instant.now();
        this.idUser = dto.idUser();
        this.idBusiness = dto.idBusiness();
        this.total = 0.0;
        this.status = "in_progress";
    }
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

    @Column(name = "id_business", nullable = false, length = 28)
    private UUID idBusiness;

    @OneToMany(mappedBy = "order")
    private Set<ProductOrder> productOrders = new LinkedHashSet<>();

}