package com.shuishou.kitchen.bean;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Administrator on 2017/9/22.
 */

public class HttpResult<T> {
    public String result;
    public boolean success;
    @SerializedName(value = "data", alternate={"children"})
    public T data;

}
