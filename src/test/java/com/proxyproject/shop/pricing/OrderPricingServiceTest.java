package com.proxyproject.shop.pricing;

import com.proxyproject.shop.orders.domain.OrderItem; // si tus entidades no están en .domain, cambia este import a: import com.proxyproject.shop.orders.OrderItem;
import com.proxyproject.shop.orders.pricing.OrderPricingService;
import com.proxyproject.shop.products.ProductClient;
import com.proxyproject.shop.products.dto.ProductDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderPricingServiceTest {

    @Mock
    private ProductClient productClient;

    @InjectMocks
    private OrderPricingService pricingService;

    @Test
    @DisplayName("Reprice items and return total with scale(2)")
    void shouldRepriceItemsAndReturnTotal() {
        // Arrange
        Long productId = 1L;
        int qty = 2;

        ProductDto p = new ProductDto();
        p.setId(productId);
        p.setTitle("Any");
        p.setPrice(new BigDecimal("109.95"));

        when(productClient.getById(productId)).thenReturn(p);

        OrderItem item = new OrderItem();
        item.setProductId(productId);
        item.setQuantity(qty);
        // client-provided price should be ignored
        item.setPrice(new BigDecimal("0.01"));

        List<OrderItem> items = List.of(item);

        // Act
        BigDecimal total = pricingService.applyCatalogPricesAndReturnTotal(items);

        // Assert
        assertEquals(new BigDecimal("109.95"), items.get(0).getPrice(),
                "Item price must be overwritten by catalog price");
        assertEquals(new BigDecimal("219.90"), total,
                "Total must be price * qty with scale(2)");
    }

    @Test
    @DisplayName("quantity <= 0 must throw IllegalArgumentException")
    void quantityZeroOrNegativeShouldThrow() {
        // Arrange (NO stubbing aquí: el servicio valida qty antes de llamar a Feign)
        Long productId = 1L;

        OrderItem item = new OrderItem();
        item.setProductId(productId);
        item.setQuantity(0); // invalid

        List<OrderItem> items = List.of(item);

        // Act + Assert
        assertThrows(IllegalArgumentException.class,
                () -> pricingService.applyCatalogPricesAndReturnTotal(items),
                "Should reject items with non-positive quantity");
    }

    @Test
    @DisplayName("Null product price from catalog must throw IllegalArgumentException")
    void nullCatalogPriceShouldThrow() {
        // Arrange
        Long productId = 1L;

        ProductDto p = new ProductDto();
        p.setId(productId);
        p.setTitle("Any");
        p.setPrice(null); // invalid catalog data
        when(productClient.getById(productId)).thenReturn(p);

        OrderItem item = new OrderItem();
        item.setProductId(productId);
        item.setQuantity(1);

        List<OrderItem> items = List.of(item);

        // Act + Assert
        assertThrows(IllegalArgumentException.class,
                () -> pricingService.applyCatalogPricesAndReturnTotal(items),
                "Should reject products without a valid catalog price");
    }
}
