package org.olegi.testbankapi.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class OperationHistoryDTO {
    private String accountNumber;
    private LocalDateTime from;
    private LocalDateTime to;
}