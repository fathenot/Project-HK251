package com.example.supermarket.mapper;

import com.example.supermarket.dto.response.customer.CustomerResponse;
import com.example.supermarket.entity.Customer;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CustomerMapper {
    CustomerResponse toResponse(Customer customer);

}
