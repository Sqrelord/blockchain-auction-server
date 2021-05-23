package edu.dhu.auction.web.common;

import com.alibaba.fastjson.JSONObject;
import edu.dhu.auction.web.util.ResultEntity;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import static edu.dhu.auction.web.util.ResultStatus.FAILURE;

@ControllerAdvice
public class WebResponseBodyAdvice implements ResponseBodyAdvice<Object> {
    @Override
    public boolean supports(@NotNull MethodParameter returnType,
                            @NotNull Class<? extends HttpMessageConverter<?>> converterType) {
        return true;
    }

    @Override
    public Object beforeBodyWrite(Object body, @NotNull MethodParameter returnType, @NotNull MediaType selectedContentType,
                                  @NotNull Class<? extends HttpMessageConverter<?>> selectedConverterType,
                                  @NotNull ServerHttpRequest request, @NotNull ServerHttpResponse response) {
        if (body instanceof ResultEntity) {
            return ((ResultEntity<?>) body).toJSONObject();
        } else {
            JSONObject json = new JSONObject(true);
            json.put("code", FAILURE.getCode());
            json.put("msg", FAILURE.getMsg());
            json.put("data", body);
            return json;
        }
    }
}
