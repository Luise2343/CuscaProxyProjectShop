package com.proxyproject.shop.orders.pricing;

import com.proxyproject.shop.products.ProductClient;
import com.proxyproject.shop.products.dto.ProductDto;
import com.proxyproject.shop.orders.domain.OrderItem;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
public class OrderPricingService {

    private final ProductClient productClient;

    public OrderPricingService(ProductClient productClient) {
        this.productClient = productClient;
    }

    @Transactional(readOnly = true)
    public BigDecimal applyCatalogPricesAndReturnTotal(List<OrderItem> items) {
        BigDecimal total = BigDecimal.ZERO;

        for (OrderItem item : items) {
            if (item == null) throw new IllegalArgumentException("Item cannot be null");
            if (item.getProductId() == null) throw new IllegalArgumentException("Item.productId is required");
            Integer qty = item.getQuantity();
            if (qty == null || qty <= 0) {
                throw new IllegalArgumentException("Item.quantity must be > 0 for productId=" + item.getProductId());
            }

            ProductDto product = productClient.getById(item.getProductId());
            if (product == null || product.getPrice() == null) {
                throw new IllegalArgumentException("Catalog price not available for product id=" + item.getProductId());
            }

            // Sobrescribe el precio del request con el de catálogo
            BigDecimal catalogPrice = product.getPrice();
            item.setPrice(catalogPrice);

            total = total.add(catalogPrice.multiply(BigDecimal.valueOf(qty)));
        }

        return total.setScale(2, RoundingMode.HALF_UP);
    }
}
