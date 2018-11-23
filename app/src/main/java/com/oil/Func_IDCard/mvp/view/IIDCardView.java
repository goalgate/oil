package com.oil.Func_IDCard.mvp.view;


import android.graphics.Bitmap;

import cbdi.drv.card.ICardInfo;


/**
 * Created by zbsz on 2017/6/9.
 */

public interface IIDCardView {
    //    void onsetCardInfo(CardInfoRk123x cardInfo);
    void onsetCardInfo(ICardInfo cardInfo);

    void onsetCardImg(Bitmap bmp);

    void onGetwltlib(byte[] bytes);
}
