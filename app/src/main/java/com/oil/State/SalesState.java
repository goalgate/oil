package com.oil.State;

public class SalesState extends Operation{

    @Override
    public void onHandle(State state) {
        state.setOperation(new CommonState());
    }
}
