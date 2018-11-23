package com.oil.State;

public class BuyerState extends Operation {

    @Override
    public void onHandle(State state) {
        state.setOperation(new SalesState());
    }
}
