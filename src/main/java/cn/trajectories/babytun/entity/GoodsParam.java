package cn.trajectories.babytun.entity;

import java.io.Serializable;

public class GoodsParam implements Serializable {

    private Long gdId;
    private String gpParamName;
    private String gpParamValue;
    private Long goodsId;
    private Integer gdOrder;

    public Long getGdId() {
        return gdId;
    }

    public void setGdId(Long gdId) {
        this.gdId = gdId;
    }

    public String getGpParamName() {
        return gpParamName;
    }

    public void setGpParamName(String gpParamName) {
        this.gpParamName = gpParamName;
    }

    public String getGpParamValue() {
        return gpParamValue;
    }

    public void setGpParamValue(String gpParamValue) {
        this.gpParamValue = gpParamValue;
    }

    public Long getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(Long goodsId) {
        this.goodsId = goodsId;
    }

    public Integer getGdOrder() {
        return gdOrder;
    }

    public void setGdOrder(Integer gdOrder) {
        this.gdOrder = gdOrder;
    }
}
