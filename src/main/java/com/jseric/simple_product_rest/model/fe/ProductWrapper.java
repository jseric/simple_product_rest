package com.jseric.simple_product_rest.model.fe;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.jseric.simple_product_rest.model.product.Product;
import java.io.Serializable;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@JsonSerialize
@JsonIgnoreProperties(ignoreUnknown=true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "id",
        "code",
        "name",
        "priceHrk",
        "priceEur",
        "description",
        "isAvailable"
})
@NoArgsConstructor @AllArgsConstructor @Getter @Setter @ToString @EqualsAndHashCode
public class ProductWrapper implements Serializable {
    @JsonProperty("id")
    private Long id;

    @JsonProperty("code")
    private String code;

    @JsonProperty("name")
    private String name;

    @JsonProperty("priceHrk")
    private BigDecimal priceHrk;

    @JsonProperty("priceEur")
    private BigDecimal priceEur;

    @JsonProperty("description")
    private String description;

    @JsonProperty("isAvailable")
    private Boolean isAvailable;

    public ProductWrapper(final Product product) {
        this.id = product.getId();
        this.code = product.getCode();
        this.name = product.getName();
        this.priceHrk = product.getPriceHrk();
        this.priceEur = product.getPriceEur();
        this.description = product.getDescription();
        this.isAvailable = product.getIsAvailable();
    }
}
