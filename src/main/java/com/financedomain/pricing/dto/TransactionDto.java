package com.financedomain.pricing.dto;

import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TransactionDto {

    private Long id;
    private String sender;
    private String receiver;
    private Double amount;
    private String type;
    private LocalDateTime createdAt;
}
