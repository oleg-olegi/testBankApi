package org.olegi.testbankapi.service;

import org.olegi.testbankapi.dto.TransferRequestDTO;

public interface TransferService {
    void transferMoney(TransferRequestDTO transferRequestDTO);
}
