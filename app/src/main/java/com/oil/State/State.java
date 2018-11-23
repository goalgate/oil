package com.oil.State;

public class State {

    public State(Operation operation) {
        this.operation = operation;
    }

    private Operation operation;

    public void setOperation(Operation operation) {
        this.operation = operation;
    }

    public Operation getOperation() {
        return operation;
    }

    public void setState(Operation state) {
        this.operation = state;
    }

    public void doNext(){
        operation.onHandle(this);
    }
}
