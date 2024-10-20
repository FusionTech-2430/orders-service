package co.allconnected.fussiontech.ordersservice.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "label", schema = "all_connected_products")
public class Label {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_label", nullable = false)
    private Integer id;

    @Column(name = "label", nullable = false, length = 45)
    private String label;

}