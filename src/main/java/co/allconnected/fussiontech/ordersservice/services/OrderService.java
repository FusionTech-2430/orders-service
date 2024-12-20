package co.allconnected.fussiontech.ordersservice.services;

import co.allconnected.fussiontech.ordersservice.dtos.OrderCreateDTO;
import co.allconnected.fussiontech.ordersservice.dtos.OrderDTO;
import co.allconnected.fussiontech.ordersservice.dtos.ProductDTO;
import co.allconnected.fussiontech.ordersservice.model.Order;
import co.allconnected.fussiontech.ordersservice.model.Product;
import co.allconnected.fussiontech.ordersservice.model.ProductOrder;
import co.allconnected.fussiontech.ordersservice.repository.OrderRepository;
import co.allconnected.fussiontech.ordersservice.repository.ProductOrderRepository;
import co.allconnected.fussiontech.ordersservice.repository.ProductRepository;
import co.allconnected.fussiontech.ordersservice.utils.OperationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final ProductOrderRepository productOrderRepository;

    @Autowired
    public OrderService(OrderRepository orderRepository, ProductRepository productRepository, ProductOrderRepository productOrderRepository) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.productOrderRepository = productOrderRepository;
    }

    public OrderDTO createOrder(OrderCreateDTO orderCreateDTO) {
        Order order = new Order(orderCreateDTO);
        return new OrderDTO(orderRepository.save(order));
    }

    public OrderDTO getOrderByUUID(UUID id) {
        return orderRepository.findById(id)
                .map(OrderDTO::new)
                .orElseThrow(() -> new RuntimeException("Order not found"));
    }

    public OrderDTO [] getOrders() {
        return orderRepository.findAll().stream()
                .map(OrderDTO::new)
                .toArray(OrderDTO[]::new);
    }

    public OrderDTO [] getOrdersByUser(String idUser) {
        return orderRepository.findAll().stream()
                .filter(order -> order.getIdUser().equals(idUser))
                .map(OrderDTO::new)
                .toArray(OrderDTO[]::new);
    }

    public OrderDTO [] getOrdersByBusiness(UUID idBusiness) {
        return orderRepository.findAll().stream()
                .filter(order -> order.getIdBusiness().equals(idBusiness))
                .map(OrderDTO::new)
                .toArray(OrderDTO[]::new);
    }

    public OrderDTO markOrderAsConfirmed(UUID id) {
        Optional<Order> orderOptional = orderRepository.findById(id);

        if (orderOptional.isPresent()) {
            Order order = orderOptional.get();

            for (ProductOrder productOrder : order.getProductOrders()) {
                Product product = productOrder.getProduct();
                if (productOrder.getQuantity() <= 0) {
                    throw new OperationException(409, "The quantity of the product in the order is greater than the available stock for product: " + product.getName());
                }
            }
            order.setStatus("confirmed");

            return new OrderDTO(orderRepository.save(order));
        } else {
            throw new OperationException(404, "Order not found");
        }
    }

    public OrderDTO markOrderAsDelivered(UUID id) {
        Optional<Order> orderOptional = orderRepository.findById(id);
        if (orderOptional.isPresent()) {
            Order order = orderOptional.get();

            // Update status and delivery date
            order.setStatus("delivered");
            order.setDeliveryDate(Instant.now());
            return new OrderDTO(orderRepository.save(order));
        } else {
            throw new OperationException(404, "Order not found");
        }
    }

    public void deleteOrder(UUID id) {
        Optional<Order> orderOptional = orderRepository.findById(id);
        if (orderOptional.isPresent()) {
            Order order = orderOptional.get();

            // Eliminar los productos de la orden
            for (ProductOrder productOrder : order.getProductOrders()) {
                productOrderRepository.delete(productOrder);
                // Actualizar el stock de los productos
                Product product = productOrder.getProduct();
                int newStock = product.getStock() + productOrder.getQuantity();
                product.setStock(newStock);
                productRepository.save(product);
            }

            orderRepository.delete(order);
        } else {
            throw new OperationException(404, "Order not found");
        }
    }

    public OrderDTO addProductToOrder(UUID orderId, Integer productId, Integer quantity) {
        Optional<Order> orderOptional = orderRepository.findById(orderId);
        Optional<Product> productOptional = productRepository.findById(productId);

        if (!orderOptional.isPresent() || !productOptional.isPresent()) {
            throw new OperationException(404, "Order or Product not found");
        }

        Order order = orderOptional.get();
        Product product = productOptional.get();

        if (product.getStock() <= 0) {
            throw new OperationException(409, "The product is out of stock");
        }

        Optional<ProductOrder> existingProductOrder = order.getProductOrders().stream()
                .filter(productOrder -> productOrder.getProduct().getId().equals(product.getId()))
                .findFirst();

        if (existingProductOrder.isPresent()) {
            ProductOrder productOrder = existingProductOrder.get();

            // Verify the stock of the product
            if (quantity > product.getStock()) {
                throw new OperationException(409, "The quantity of the product in the order is greater than the stock");
            }

            // Update the total and stock
            double previousSubtotal = productOrder.getQuantity() * product.getPrice();
            double newSubtotal = product.getPrice() * quantity;
            order.setTotal(order.getTotal() - previousSubtotal + newSubtotal);

            int previousQuantity = productOrder.getQuantity();
            productOrder.setQuantity(quantity);
            productOrder.setSubtotal(newSubtotal);

            product.setStock(product.getStock() + previousQuantity - quantity);
            productOrderRepository.save(productOrder);
        } else {
            if (quantity > product.getStock()) {
                throw new OperationException(409, "The quantity of the product in the order is greater than the stock");
            }

            ProductOrder productOrder = new ProductOrder(order, product, quantity);
            order.setTotal(order.getTotal() + productOrder.getSubtotal());
            order.getProductOrders().add(productOrder);

            product.setStock(product.getStock() - quantity);
            productOrderRepository.save(productOrder);
        }

        productRepository.save(product);
        orderRepository.save(order);

        return new OrderDTO(order);
    }

    public OrderDTO deleteProductFromOrder(UUID orderId, Integer productId) {
        // Busca la orden y lanza excepción si no existe
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OperationException(404, "Order not found in the system"));

        // Busca el producto y lanza excepción si no existe
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new OperationException(404, "Product not found in the system"));

        // Busca la asociación entre el producto y la orden
        ProductOrder productOrder = order.getProductOrders().stream()
                .filter(po -> po.getProduct().getId().equals(product.getId()))
                .findFirst()
                .orElseThrow(() -> new OperationException(404, "Product not found in order"));

        // Calcula el nuevo total restando el subtotal del producto que se va a eliminar
        double previousSubtotal = productOrder.getQuantity() * product.getPrice();
        order.setTotal(order.getTotal() - previousSubtotal);

        // Incrementa el stock del producto eliminado
        int quantityToReturn = productOrder.getQuantity();
        product.setStock(product.getStock() + quantityToReturn);

        // Actualiza el stock del producto en el repositorio
        productRepository.save(product);

        // Elimina el ProductOrder, guarda la orden actualizada, y elimina la asociación de la base de datos
        order.getProductOrders().remove(productOrder);
        orderRepository.save(order);
        productOrderRepository.delete(productOrder);

        // Verifica si la orden quedó sin productos y la elimina
        if (order.getProductOrders().isEmpty()) {
            orderRepository.delete(order);
        }

        return new OrderDTO(order);
    }


}

