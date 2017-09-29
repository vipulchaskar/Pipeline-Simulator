package com.company;

public class Register {

    private int value;
    private boolean valid;

    public Register() {
        value = 0;
        valid = true;
    }

    public Register(int value, boolean valid) {
        this.value = value;
        this.valid = valid;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }
}
