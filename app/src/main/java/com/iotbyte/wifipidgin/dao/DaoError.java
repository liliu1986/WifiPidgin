package com.iotbyte.wifipidgin.dao;

/**
 * Dao error code
 */
public enum DaoError {
    NO_ERROR(0),
    /** Error on saving record */
    ERROR_SAVE(1),
    /** No such record */
    ERROR_NO_RECORD(2),
    /** Record was never saved - usually when attempting to
     *  update a record that has not been saved
     */
    ERROR_RECORD_NEVER_SAVED(3);

    public int getValue() {
        return value;
    }

    private DaoError(int value) {
        this.value = value;
    }

    private final int value;
}
