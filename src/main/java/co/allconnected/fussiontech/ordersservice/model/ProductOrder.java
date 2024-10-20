package co.allconnected.fussiontech.ordersservice.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name = "product_order", schema = "all_connected_products")
public class ProductOrder {

    @EmbeddedId
    private ProductOrderId id;

    @ManyToOne
    @MapsId("idOrder") // Relaciona con el campo idOrder de ProductOrderId
    @JoinColumn(name = "id_order", insertable = false, updatable = false)
    private Order order;

    @ManyToOne
    @MapsId("idProduct") // Relaciona con el campo idProduct de ProductOrderId
    @JoinColumn(name = "id_product", insertable = false, updatable = false)
    private Product product;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Column(name = "subtotal", nullable = false)
    private Double subtotal;

    public ProductOrder(Order order, Product product, Integer quantity) {
        this.id = new ProductOrderId(order.getId(), product.getId());
        this.order = order;
        this.product = product;
        this.quantity = quantity;
        this.subtotal = this.product.getPrice() * this.quantity;
    }
}