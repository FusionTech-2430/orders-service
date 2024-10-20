package co.allconnected.fussiontech.ordersservice.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Getter
@Setter
@Embeddable
public class ProductOrderId implements Serializable {

    @Column(name = "id_order", nullable = false)
    private UUID idOrder;

    @Column(name = "id_product", nullable = false)
    private Integer idProduct;

    public ProductOrderId() {}

    public ProductOrderId(UUID idOrder, Integer idProduct) {
        this.idOrder = idOrder;
        this.idProduct = idProduct;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProductOrderId that = (ProductOrderId) o;
        return Objects.equals(idOrder, that.idOrder) &&
                Objects.equals(idProduct, that.idProduct);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idOrder, idProduct);
    }
}