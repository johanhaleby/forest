package com.jayway.forest.reflection;

/**
 */
public class Touchable {
    private boolean touched;
    public Touchable() {
        this.touched = false;
    }

    protected void touch() {
        touched = true;
    }

    public boolean isTouched() {
        return touched;
    }

}
