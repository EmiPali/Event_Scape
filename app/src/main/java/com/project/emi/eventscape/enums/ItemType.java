package com.project.emi.eventscape.enums;



public enum ItemType {LOAD(10), ITEM(11), TEXT(12);
    private final int typeCode;

    ItemType(int typeCode) {
        this.typeCode = typeCode;
    }

    public int getTypeCode() {
        return this.typeCode;
    }
}