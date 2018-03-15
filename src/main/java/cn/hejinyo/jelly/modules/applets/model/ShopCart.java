package cn.hejinyo.jelly.modules.applets.model;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author : heshuangshuang
 * @date : 2018/3/15 10:15
 */
@Data
public class ShopCart implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 是否选择
     */
    private boolean ischecked;
    /**
     * 商品图标
     */
    private String thumLogo;
    /**
     * 商品名称
     */
    private String goodsName;
    /**
     * 商品库存名
     */
    private String goodsSkuValName;
    /**
     * 商品id
     */
    private String goodsId;
    /**
     * 类型
     */
    private Integer type;
    /**
     * 价格
     */
    private BigDecimal price;
    /**
     * 数量
     */
    private Integer num;
    /**
     * 购物车id
     */
    private Integer id;
}
