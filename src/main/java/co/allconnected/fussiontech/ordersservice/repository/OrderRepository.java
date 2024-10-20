package co.allconnected.fussiontech.ordersservice.repository;

import co.allconnected.fussiontech.ordersservice.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface OrderRepository extends JpaRepository<Order, UUID> {

}