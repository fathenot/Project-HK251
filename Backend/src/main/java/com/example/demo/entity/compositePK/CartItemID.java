package com.example.demo.entity.compositePK;
import java.io.Serializable;
import java.util.Objects;

public class CartItemID implements Serializable {
    private int cartID;
    private int productID;

    public CartItemID(int cid, int iid){
        this.cartID = cid;
        this.productID = iid;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CartItemID that = (CartItemID) o;
        return this.cartID == that.cartID && this.productID == that.productID;
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(cartID, productID);
    }
}
