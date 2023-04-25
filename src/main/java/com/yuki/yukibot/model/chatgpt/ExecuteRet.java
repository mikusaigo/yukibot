package com.yuki.yukibot.model.chatgpt;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.commons.httpclient.HttpMethod;

@Data
@AllArgsConstructor
public class ExecuteRet {
    /**
     * 操作是否成功
     */
    private final boolean success;

    /**
     * 返回的内容
     */
    private final String respStr;

    /**
     * 请求的地址
     */
    private final HttpMethod method;

    /**
     * statusCode
     */
    private final int statusCode;


    @Override
    public String toString() {
        return String.format("[success:%s,respStr:%s,statusCode:%s]", success, respStr, statusCode);
    }

    /**
     *@returnthe isSuccess
     */
    public boolean isSuccess() {
        return success;
    }

    /**
     *@returnthe !isSuccess
     */
    public boolean isNotSuccess() {
        return !success;
    }

    /**
     *@returnthe respStr
     */
    public String getRespStr() {
        return respStr;
    }

    /**
     *@returnthe statusCode
     */
    public int getStatusCode() {
        return statusCode;
    }

    /**
     *@returnthe method
     */
    public HttpMethod getMethod() {
        return method;
    }
}
