package co.allconnected.fussiontech.ordersservice.repository;

import co.allconnected.fussiontech.ordersservice.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Integer> {
}