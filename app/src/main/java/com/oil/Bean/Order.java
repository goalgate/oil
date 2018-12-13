package com.oil.Bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class Order {


    @Id(autoincrement = true)
    private long id;
    
    private String goumaizenhao;

    private String qiyoubiaohao;

    private String sulian;

    private String cardid;

    private String name;

    private String goumairenzhaopian;

    private String cardpic;

    private String xiaoshouren;

    private String xiaoshourenhao;

    private String zhiwen;

    @Generated(hash = 95303717)
    public Order(long id, String goumaizenhao, String qiyoubiaohao, String sulian,
            String cardid, String name, String goumairenzhaopian, String cardpic,
            String xiaoshouren, String xiaoshourenhao, String zhiwen) {
        this.id = id;
        this.goumaizenhao = goumaizenhao;
        this.qiyoubiaohao = qiyoubiaohao;
        this.sulian = sulian;
        this.cardid = cardid;
        this.name = name;
        this.goumairenzhaopian = goumairenzhaopian;
        this.cardpic = cardpic;
        this.xiaoshouren = xiaoshouren;
        this.xiaoshourenhao = xiaoshourenhao;
        this.zhiwen = zhiwen;
    }



    @Generated(hash = 1105174599)
    public Order() {
    }

    public long getId() {
        return this.id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getGoumaizenhao() {
        return this.goumaizenhao;
    }

    public void setGoumaizenhao(String goumaizenhao) {
        this.goumaizenhao = goumaizenhao;
    }

    public String getQiyoubiaohao() {
        return this.qiyoubiaohao;
    }

    public void setQiyoubiaohao(String qiyoubiaohao) {
        this.qiyoubiaohao = qiyoubiaohao;
    }

    public String getSulian() {
        return this.sulian;
    }

    public void setSulian(String sulian) {
        this.sulian = sulian;
    }

    public String getCardid() {
        return this.cardid;
    }

    public void setCardid(String cardid) {
        this.cardid = cardid;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGoumairenzhaopian() {
        return this.goumairenzhaopian;
    }

    public void setGoumairenzhaopian(String goumairenzhaopian) {
        this.goumairenzhaopian = goumairenzhaopian;
    }

    public String getCardpic() {
        return this.cardpic;
    }

    public void setCardpic(String cardpic) {
        this.cardpic = cardpic;
    }

    public String getXiaoshouren() {
        return this.xiaoshouren;
    }

    public void setXiaoshouren(String xiaoshouren) {
        this.xiaoshouren = xiaoshouren;
    }

    public String getXiaoshourenhao() {
        return this.xiaoshourenhao;
    }

    public void setXiaoshourenhao(String xiaoshourenhao) {
        this.xiaoshourenhao = xiaoshourenhao;
    }

    public String getZhiwen() {
        return this.zhiwen;
    }

    public void setZhiwen(String zhiwen) {
        this.zhiwen = zhiwen;
    }









}
