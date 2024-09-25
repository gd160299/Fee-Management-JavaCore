package org.pj.Service;

import org.pj.Dto.Request.FeeCommandDto;
import org.pj.Entity.FeeCommand;

public interface IFeeCommandService {
    void addFeeCommand(FeeCommandDto objInput) throws Exception;
}
