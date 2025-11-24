package com.example.demo.entity.compositePK;
import java.io.Serializable;
import java.util.Objects;

public class ProductStoreID implements Serializable {
    private long productID;
    private long storeID;
    public ProductStoreID(long oid, long pid){
        this.productID = oid;
        this.storeID = pid;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProductStoreID that = (ProductStoreID) o;
        return this.productID == that.productID && this.storeID == that.storeID;
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(productID, storeID);
    }
}
