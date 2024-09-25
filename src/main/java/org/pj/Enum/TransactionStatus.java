package org.pj.Enum;

import lombok.Getter;

@Getter
public enum TransactionStatus {
    KHOI_TAO("1"),
    THU_PHI("2"),
    DUNG_THU("3");

    private final String code;

    TransactionStatus(String code) {
        this.code = code;
    }

}
