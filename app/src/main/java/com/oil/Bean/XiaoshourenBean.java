package com.oil.Bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class XiaoshourenBean {

    @Id(autoincrement = true)
    private Long id;

    private String name;

    private String cardId;

    private int fingerprintId;

    @Generated(hash = 1390479377)
    public XiaoshourenBean(Long id, String name, String cardId, int fingerprintId) {
        this.id = id;
        this.name = name;
        this.cardId = cardId;
        this.fingerprintId = fingerprintId;
    }

    @Generated(hash = 1745039976)
    public XiaoshourenBean() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCardId() {
        return this.cardId;
    }

    public void setCardId(String cardId) {
        this.cardId = cardId;
    }

    public int getFingerprintId() {
        return this.fingerprintId;
    }

    public void setFingerprintId(int fingerprintId) {
        this.fingerprintId = fingerprintId;
    }


}
