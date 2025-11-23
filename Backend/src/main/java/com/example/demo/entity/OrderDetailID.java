package com.example.demo.entity;
import java.io.Serializable;
import java.util.Objects;

public class OrderDetailID implements Serializable {
    private long orderID;
    private long productID;
    public OrderDetailID(long oid, long pid){
        this.orderID = oid;
        this.productID = pid;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderDetailID that = (OrderDetailID) o;
        return this.orderID == that.orderID && this.productID == that.productID;
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(orderID, productID);
    }
}


