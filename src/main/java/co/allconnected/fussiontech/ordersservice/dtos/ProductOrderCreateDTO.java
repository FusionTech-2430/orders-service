package co.allconnected.fussiontech.ordersservice.dtos;

import java.util.UUID;

public record ProductOrderCreateDTO(UUID idOrder, Integer idProduct, Integer quantity) {
}
