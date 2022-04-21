package org.prgrms.kdt.customer;

import java.time.LocalDateTime;
import java.util.UUID;

public record CustomerDto(
        UUID customerId,
        String name,
        String email,
        LocalDateTime lastLoginAt,
        LocalDateTime createdAt
) {
    static CustomerDto of(Customer customer) {
        return new CustomerDto(
                customer.getCustomerId(),
                customer.getName(),
                customer.getEmail(),
                customer.getLastLoginAt(),
                customer.getCreatedAt());
    }

    //여기서 Customer 생성을 둘지는 취향 차이. 그러나 Customer create 시의 validation 은 Entity 내에 존재해야 함.
    static Customer to(CustomerDto dto) {
        return new Customer(
                dto.customerId(),
                dto.name(),
                dto.email(),
                dto.lastLoginAt(),
                dto.createdAt()
        );
    }
}
