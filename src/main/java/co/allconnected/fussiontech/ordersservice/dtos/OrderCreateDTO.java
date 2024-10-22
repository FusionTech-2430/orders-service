package co.allconnected.fussiontech.ordersservice.dtos;

import java.util.UUID;

public record OrderCreateDTO(String idUser, UUID idBusiness) {
}