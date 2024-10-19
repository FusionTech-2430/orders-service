package co.allconnected.fussiontech.ordersservice.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.Hibernate;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Getter
@Setter
@Embeddable
public class ProductOrderId implements Serializable {
    private static final long serialVersionUID = 236471491802880335L;
    @Column(name = "id_order", nullable = false)
    private UUID idOrder;

    @Column(name = "id_product", nullable = false)
    private Integer idProduct;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        ProductOrderId entity = (ProductOrderId) o;
        return Objects.equals(this.idOrder, entity.idOrder) &&
                Objects.equals(this.idProduct, entity.idProduct);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idOrder, idProduct);
    }

}