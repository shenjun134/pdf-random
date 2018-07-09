package com.poc.pdf.enums;

public enum FieldType {
    DATE,
    FUND,

    NUMBER,

    STRING,

    /**
     * 6 digital
     */
    AGREEMENT,

    /**
     * CP3A-OW02
     */
    W_FUND,

    /**
     * Sell-Payment, Securities Internal,
     */
    TXN_TYPE,

    /**
     *
     */
    ISIN,

    /**
     * Euroclear, CREST, GBV
     */
    LOCATION,

    /**
     * SW-SEC-1
     */
    REFERENCE,

    /**
     * ACCOUNTING ONLY/CUSTODY ONLY
     */
    TYPE,

    /**
     * 5 digital
     */
    BROKER,

    /**
     * 9digital _F1
     */
    BLOCK_ID,

    /**
     * 9 string
     */
    ASSET_ID,

    /**
     * VANGUARD-JAPAN STK IND-GBP ACC
     * VANGUARD DEV WORLD XUK EI-A
     * VANGUARD-JAPAN STK INDX-GBPA
     * VANGUARD-US EQUITY INDEX-A
     * BLOCKROCK CONF EUR INC-A ACC
     * BLOCKROCK CIF-EM NKT EQ-D
     *
     *
     */
    SECURITY_DESC,

    /**
     * GBP, USD, RMB, EUR
     */
    CURRENCY,





}
