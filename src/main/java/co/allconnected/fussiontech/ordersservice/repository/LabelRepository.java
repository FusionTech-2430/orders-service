package co.allconnected.fussiontech.ordersservice.repository;

import co.allconnected.fussiontech.ordersservice.model.Label;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LabelRepository extends JpaRepository<Label, Integer> {
}