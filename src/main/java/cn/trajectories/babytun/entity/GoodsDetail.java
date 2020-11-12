package cn.trajectories.babytun.entity;

public class GoodsDetail {

    private Long gdId;
    private Long goodsId;
    private String gdPicUrl;
    private Integer gdOrder;

    public Long getGdId() {
        return gdId;
    }

    public void setGdId(Long gdId) {
        this.gdId = gdId;
    }

    public Long getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(Long goodsId) {
        this.goodsId = goodsId;
    }

    public String getGdPicUrl() {
        return gdPicUrl;
    }

    public void setGdPicUrl(String gdPicUrl) {
        this.gdPicUrl = gdPicUrl;
    }

    public Integer getGdOrder() {
        return gdOrder;
    }

    public void setGdOrder(Integer gdOrder) {
        this.gdOrder = gdOrder;
    }
}
