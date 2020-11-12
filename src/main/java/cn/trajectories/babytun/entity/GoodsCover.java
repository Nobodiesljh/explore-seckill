package cn.trajectories.babytun.entity;

public class GoodsCover {

    private Long gcId;
    private Long goodsId;
    private String gcPicUrl;
    private String gcThumbUrl;
    private Long gcOrder;

    public Long getGcId() {
        return gcId;
    }

    public void setGcId(Long gcId) {
        this.gcId = gcId;
    }

    public Long getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(Long goodsId) {
        this.goodsId = goodsId;
    }

    public String getGcPicUrl() {
        return gcPicUrl;
    }

    public void setGcPicUrl(String gcPicUrl) {
        this.gcPicUrl = gcPicUrl;
    }

    public String getGcThumbUrl() {
        return gcThumbUrl;
    }

    public void setGcThumbUrl(String gcThumbUrl) {
        this.gcThumbUrl = gcThumbUrl;
    }

    public Long getGcOrder() {
        return gcOrder;
    }

    public void setGcOrder(Long gcOrder) {
        this.gcOrder = gcOrder;
    }
}
