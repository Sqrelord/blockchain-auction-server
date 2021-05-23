package edu.dhu.auction.web.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;

public class ResultEntity<T> {
    private final ResultStatus status;
    private final T data;

    public static <T> ResultEntity<T> ok() {
        return ok(null);
    }

    public static <T> ResultEntity<T> ok(T data) {
        return build(ResultStatus.SUCCESS, data);
    }

    public static <T> ResultEntity<T> fail() {
        return fail(null);
    }

    public static <T> ResultEntity<T> fail(T data) {
        return build(ResultStatus.FAILURE, data);
    }

    public static <T> ResultEntity<T> build(ResultStatus status) {
        return build(status, null);
    }

    public static <T> ResultEntity<T> build(ResultStatus status, T data) {
        return new ResultEntity<>(status, data);
    }

    public ResultEntity(ResultStatus status, T data) {
        this.status = status;
        this.data = data;
    }

    public ResultStatus getStatus() {
        return status;
    }

    public T getData() {
        return data;
    }

    public JSONObject toJSONObject() {
        JSONObject json = new JSONObject(true);
        json.put("code", status.getCode());
        json.put("msg", status.getMsg());
        json.put("data", data);
        return json;
    }

    public String toJSONString() {
        return JSON.toJSONString(toJSONObject(),
                SerializerFeature.WRITE_MAP_NULL_FEATURES,
                SerializerFeature.SkipTransientField,
                SerializerFeature.QuoteFieldNames);
    }
}
