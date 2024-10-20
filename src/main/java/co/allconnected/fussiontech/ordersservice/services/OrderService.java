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

    public void deleteOrder(UUID id) {
        Optional<Order> orderOptional = orderRepository.findById(id);
        if (orderOptional.isPresent()) {
            Order order = orderOptional.get();

            // Eliminar los productos de la orden
            for (ProductOrder productOrder : order.getProductOrders()) {
                productOrderRepository.delete(productOrder);
            }

            orderRepository.delete(order);
        } else {
            throw new OperationException(404, "Order not found");
        }
    }

    public OrderDTO addProductToOrder(UUID orderId, Integer productId, Integer quantity) {
        Optional<Order> orderOptional = orderRepository.findById(orderId);
        Optional<Product> productOptional = productRepository.findById(productId);

        if (orderOptional.isPresent() && productOptional.isPresent()) {
            Order order = orderOptional.get();
            Product product = productOptional.get();
            Optional<ProductOrder> existingProductOrder = order.getProductOrders().stream()
                    .filter(productOrder -> productOrder.getProduct().getId().equals(product.getId()))
                    .findFirst();

            if (existingProductOrder.isPresent()) {
                ProductOrder productOrder = existingProductOrder.get();
                System.out.println("Stock: " + product.getStock());
                System.out.println("Cantidad: " + quantity);

                // Verify the stock of the product
                if (productOrder.getQuantity() > product.getStock()) {
                    throw new OperationException(409, "The quantity of the product in the order is greater than the stock");
                }

                // For update the total is necessary to sustract the previous subtotal of the combination of product and quantity in the order
                double previousSubtotal = productOrder.getQuantity() * product.getPrice();
                double newSubtotal = product.getPrice() * quantity;

                double newTotal = order.getTotal() - previousSubtotal + newSubtotal;
                System.out.println("Nuevo total (producto existente): " + newTotal);
                order.setTotal(newTotal);

                productOrder.setQuantity(quantity);
                productOrder.setSubtotal(newSubtotal);

                productOrderRepository.save(productOrder);
                orderRepository.save(order);

                // Update the stock of the product
                product.setStock(product.getStock() - quantity);
                productRepository.save(product);
            } else {
                ProductOrder productOrder = new ProductOrder(order, product, quantity);
                double newTotal = order.getTotal() + productOrder.getSubtotal();
                //System.out.println("Nuevo total (nuevo producto): " + newTotal);
                order.setTotal(newTotal);
                order.getProductOrders().add(productOrder);
                productOrderRepository.save(productOrder);
                orderRepository.save(order);
            }
            return new OrderDTO(order);
        } else {
            throw new OperationException(404, "Order or Product not found");
        }
    }

    public OrderDTO deleteProductFromOrder(UUID orderId, Integer productId) {
        Optional<Order> orderOptional = orderRepository.findById(orderId);
        Optional<Product> productOptional = productRepository.findById(productId);

        if (orderOptional.isPresent() && productOptional.isPresent()) {
            Order order = orderOptional.get();
            Product product = productOptional.get();
            Optional<ProductOrder> productOrderOptional = order.getProductOrders().stream()
                    .filter(productOrder -> productOrder.getProduct().getId().equals(product.getId()))
                    .findFirst();

            if (productOrderOptional.isPresent()) {
                ProductOrder productOrder = productOrderOptional.get();
                double previousSubtotal = productOrder.getQuantity() * product.getPrice();
                double newTotal = order.getTotal() - previousSubtotal;
                //System.out.println("Nuevo total (producto eliminado): " + newTotal);
                order.setTotal(newTotal);
                order.getProductOrders().remove(productOrder);
                productOrderRepository.delete(productOrder);
                orderRepository.save(order);
                return new OrderDTO(order);
            } else {
                throw new OperationException(404, "Product not found in order");
            }
        } else {
            throw new OperationException(404, "Order or Product not found in the system");
        }
    }

    public OrderDTO markOrderAsConfirmed(UUID id) {
        Optional<Order> orderOptional = orderRepository.findById(id);

        if (orderOptional.isPresent()) {
            Order order = orderOptional.get();

            for (ProductOrder productOrder : order.getProductOrders()) {
                Product product = productOrder.getProduct();
                if (productOrder.getQuantity() > product.getStock()) {
                    throw new OperationException(409, "The quantity of the product in the order is greater than the available stock for product: " + product.getName());
                }
            }

            for (ProductOrder productOrder : order.getProductOrders()) {
                Product product = productOrder.getProduct();
                int newStock = product.getStock() - productOrder.getQuantity();

                if (newStock < 0) {
                    throw new OperationException(500, "Error: Stock cannot be negative for product: " + product.getName());
                }

                product.setStock(newStock);
                productRepository.save(product);
            }
            order.setStatus("confirmed");

            return new OrderDTO(orderRepository.save(order));
        } else {
            throw new OperationException(404, "Order not found");
        }
    }


}

