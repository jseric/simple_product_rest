package com.jseric.simple_product_rest.model.fe;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.io.Serializable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@JsonPropertyOrder({
        "errorMessage",
        "product"
})
@NoArgsConstructor @Getter @Setter @ToString @EqualsAndHashCode
public class CreateProductResponse implements Serializable {
    @JsonProperty("product")
    private ProductWrapper product;

    @JsonProperty("errorMessage")
    private String errorMessage;
}
