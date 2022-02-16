package com.jseric.simple_product_rest.model.fe;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.io.Serializable;
import java.util.List;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@JsonPropertyOrder({
        "errorMessage",
        "products"
})
@NoArgsConstructor @Getter @Setter @ToString @EqualsAndHashCode
public class FetchProductResponse implements Serializable {
    @JsonProperty("products")
    private List<ProductWrapper> products;

    @JsonProperty("errorMessage")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String errorMessage;
}
