package co.allconnected.fussiontech.ordersservice.dtos;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ProductDTO {
    private Integer id;
    private String idBusiness;
    private String name;
    private String description;
    private String photoUrl;
    private Integer stock;
    private Double price;
    private String status;
    private String [] labels;
    private String [] orders;
}