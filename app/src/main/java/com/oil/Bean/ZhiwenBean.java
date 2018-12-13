package com.oil.Bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class ZhiwenBean {

    @Id(autoincrement = true)
    private Long id;

    private String zhujian;

    private String name;

    private String cardid;

    private int zhiwenId;

    @Generated(hash = 1593218266)
    public ZhiwenBean(Long id, String zhujian, String name, String cardid,
            int zhiwenId) {
        this.id = id;
        this.zhujian = zhujian;
        this.name = name;
        this.cardid = cardid;
        this.zhiwenId = zhiwenId;
    }

    @Generated(hash = 1107234645)
    public ZhiwenBean() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCardid() {
        return this.cardid;
    }

    public void setCardid(String cardid) {
        this.cardid = cardid;
    }

    public int getZhiwenId() {
        return this.zhiwenId;
    }

    public void setZhiwenId(int zhiwenId) {
        this.zhiwenId = zhiwenId;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getZhujian() {
        return this.zhujian;
    }

    public void setZhujian(String zhujian) {
        this.zhujian = zhujian;
    }
}
