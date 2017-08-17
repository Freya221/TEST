package com.huawei.om.models;

/**
 * Created by z00383627 on 2017/3/5.
 *
 */
public enum ErrorType {
    NO_ERROR(0),

    NULL_SOURCE_STRING(1),
    JSON_EXCEPTION(2),
    ILLEGAL_LOG_TYPE(3),
    ILLEGAL_TYPE(4),
    NULL_MESSAGE(5),

    ILLEGAL_FIELD_LENGTH(1000),
    ILLEGAL_PROBE_TIME(2000),
    ILLEGAL_BUSINESS_TYPE(18000);

    private int errorCode;

    ErrorType(int errorCode) {
        this.errorCode = errorCode;
    }

    public int getErrorCode() {
        return errorCode;
    }

    @Override
    public String toString() {
        return "ErrorType{" +
                "errorCode=" + errorCode +
                '}';
    }
}
