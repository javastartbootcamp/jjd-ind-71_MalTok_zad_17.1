package pl.javastart.streamsexercise;

import java.math.BigDecimal;

public class PaymentItem {

    private String name;
    private BigDecimal regularPrice;
    private BigDecimal finalPrice;

    public PaymentItem(String name, BigDecimal regularPrice, BigDecimal finalPrice) {
        this.name = name;
        this.regularPrice = regularPrice;
        this.finalPrice = finalPrice;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getRegularPrice() {
        return regularPrice;
    }

    public void setRegularPrice(BigDecimal regularPrice) {
        this.regularPrice = regularPrice;
    }

    public BigDecimal getFinalPrice() {
        return finalPrice;
    }

    public void setFinalPrice(BigDecimal finalPrice) {
        this.finalPrice = finalPrice;
    }

    public BigDecimal countDiscount() {
        return regularPrice.subtract(finalPrice);
    }
}
