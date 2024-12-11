package org.olegi.testbankapi.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class OperationHistoryDTO {
    private Long accountId;
    private LocalDateTime fromDate;
    private LocalDateTime toDate;
}