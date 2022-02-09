package com.jseric.simple_product_rest.model.product;

import com.jseric.simple_product_rest.model.base.BaseEntity;
import java.math.BigDecimal;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

@Entity
@Table(schema = "public", name = "product")
@NoArgsConstructor @AllArgsConstructor @Getter @Setter @ToString @Accessors(chain = true)
public class Product extends BaseEntity {
    @Column(name = "code", nullable = false, length = 10)
    private String code;

    @Column(name = "name", nullable = false, length = 64)
    private String name;

    @Column(name = "price_hrk", nullable = false, precision = 12, scale = 2)
    private BigDecimal priceHrk;

    @Column(name = "price_eur", nullable = false, precision = 12, scale = 2)
    private BigDecimal priceEur;

    @Column(name = "description", length = 1024)
    private String description;

    @Column(name = "is_available", nullable = false)
    private Boolean isAvailable = false;
}
