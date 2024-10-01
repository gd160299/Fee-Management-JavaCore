package org.pj.constant;

import lombok.Getter;

@Getter
public enum EnumTransactionStatus {
    KHOI_TAO("1"),
    THU_PHI("2"),
    DUNG_THU("3");

    private final String code;

    EnumTransactionStatus(String code) {
        this.code = code;
    }

}
