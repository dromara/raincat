

package com.happylifeplat.transaction.common.enums;


public enum TransactionTypeEnum {

    ROOT(1),
    BRANCH(2);

    int id;

    TransactionTypeEnum(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public  static TransactionTypeEnum  valueOf(int id) {
        switch (id) {
            case 1:
                return ROOT;
            case 2:
                return BRANCH;
            default:
                return null;
        }
    }

}
