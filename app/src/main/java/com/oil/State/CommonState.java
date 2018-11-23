package com.oil.State;

public class CommonState extends Operation {

    @Override
    public void onHandle(State state) {
        state.setOperation(new BuyerState());
    }
}
