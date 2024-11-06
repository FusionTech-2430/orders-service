package co.allconnected.fussiontech.ordersservice.repository;

import co.allconnected.fussiontech.ordersservice.model.ProductOrder;
import co.allconnected.fussiontech.ordersservice.model.ProductOrderId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductOrderRepository extends JpaRepository<ProductOrder, ProductOrderId> {
}