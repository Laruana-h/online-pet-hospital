package com.phms.pojo;

import java.util.Date;


/**
 * 【请填写功能名称】对象 order
 * 
 * @author ruoyi
 * @date 2022-04-22
 */
public class Order extends BaseBean
{
    private static final long serialVersionUID = 1L;

    /** $column.columnComment */
    private Long id;

    /** $column.columnComment */

    private String goodsName;

    /** $column.columnComment */

    private Long goodsId;

    /** $column.columnComment */

    private Long userId;

    /** $column.columnComment */

    private String userName;

    /** $column.columnComment */

    private String price;

    /** $column.columnComment */

    private Date buyTime;

    /** $column.columnComment */

    private Integer isPickedUp;

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }
    public void setGoodsName(String goodsName) 
    {
        this.goodsName = goodsName;
    }

    public String getGoodsName() 
    {
        return goodsName;
    }
    public void setGoodsId(Long goodsId) 
    {
        this.goodsId = goodsId;
    }

    public Long getGoodsId() 
    {
        return goodsId;
    }
    public void setUserId(Long userId) 
    {
        this.userId = userId;
    }

    public Long getUserId() 
    {
        return userId;
    }
    public void setUserName(String userName) 
    {
        this.userName = userName;
    }

    public String getUserName() 
    {
        return userName;
    }
    public void setPrice(String price) 
    {
        this.price = price;
    }

    public String getPrice() 
    {
        return price;
    }
    public void setBuyTime(Date buyTime) 
    {
        this.buyTime = buyTime;
    }

    public Date getBuyTime() 
    {
        return buyTime;
    }
    public void setIsPickedUp(Integer isPickedUp) 
    {
        this.isPickedUp = isPickedUp;
    }

    public Integer getIsPickedUp() 
    {
        return isPickedUp;
    }

}
