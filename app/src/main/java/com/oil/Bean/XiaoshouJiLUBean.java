package com.oil.Bean;



public class XiaoshouJiLUBean {


    private String sellername;

    private String buyername;

    private String volume;

    private String salesDate;

    public String getSellername() {
        return sellername;
    }

    public String getBuyername() {
        return buyername;
    }

    public String getVolume() {
        return volume;
    }

    public String getSalesDate() {
        return salesDate;
    }

    public void setSellername(String sellername) {
        this.sellername = sellername;
    }

    public void setBuyername(String buyername) {
        this.buyername = buyername;
    }

    public void setVolume(String volume) {
        this.volume = volume;
    }

    public void setSalesDate(String salesDate) {
        this.salesDate = salesDate;
    }

    public XiaoshouJiLUBean(String sellername, String buyername, String volume, String salesDate) {

        this.sellername = sellername;
        this.buyername = buyername;
        this.volume = volume;
        this.salesDate = salesDate;
    }
}
