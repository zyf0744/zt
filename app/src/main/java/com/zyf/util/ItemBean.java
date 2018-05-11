package com.zyf.util;

import java.util.Date;

public class ItemBean {
    public int itemImageResId;//图像资源ID
    public String userName;//标题
    public String itemContent;//内容
    public Date dateString;

    public ItemBean(int itemImageResId, String userName, String itemContent,Date dateString) {
        this.itemImageResId = itemImageResId;
        this.userName = userName;
        this.itemContent = itemContent;
        this.dateString = dateString;
    }
}