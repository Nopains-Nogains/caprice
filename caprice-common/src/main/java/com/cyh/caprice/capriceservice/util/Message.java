package com.cyh.caprice.capriceservice.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Message<T> {
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<T> list;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private T data;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Map<String, Object> map;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String message;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private boolean status;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String code;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String errcode;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String errmsg;

    public Message(boolean status, String message) {
        this.message = message;
        this.status = status;
    }

    public Message(boolean status, String message, T data) {
        this.message = message;
        this.status = status;
        this.data = data;
    }

    public Message(boolean status, String message, String code) {
        this.message = message;
        this.status = status;
        this.code = code;
    }

    public Message(boolean status, String message, List<T> list) {
        this.message = message;
        this.status = status;
        this.list = list;
    }


}
