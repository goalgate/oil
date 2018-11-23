package com.oil.Func_IDCard.mvp.module;

import android.graphics.Bitmap;

import cbdi.drv.card.CardInfoRk123x;
import cbdi.drv.card.ICardInfo;


/**
 * Created by zbsz on 2017/6/4.
 */

public interface IIDCard {
    void onOpen(IIdCardListener mylistener);

    void onReadCard();

    void onStopReadCard();

    void onClose();

    interface IIdCardListener {
        void onSetImg(Bitmap bmp);

        void onGetwltlib(byte[] bytes);
//        void onSetInfo(CardInfoRk123x cardInfo);

        void onSetInfo(ICardInfo cardInfo);
    }


}
