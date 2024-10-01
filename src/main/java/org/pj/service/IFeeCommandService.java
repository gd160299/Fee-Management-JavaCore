package org.pj.service;

import org.pj.dto.request.FeeCommandDto;

public interface IFeeCommandService {
    void addFeeCommand(FeeCommandDto objInput) throws Exception;
}
