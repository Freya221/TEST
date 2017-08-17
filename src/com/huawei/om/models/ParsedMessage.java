package com.huawei.om.models;

import com.alibaba.fastjson.JSONObject;
import org.apache.log4j.Logger;

import java.io.Serializable;
import java.util.Arrays;

/**
 * Created by z00383627 on 2017/3/3.
 *
 */
public class ParsedMessage implements Serializable {
    private static final Logger LOG = Logger.getLogger(ParsedMessage.class);

    private String sourceString;
    private JSONObject jsonObject;
    private String message;
    private String logType;
    private String[] fields;

    public ParsedMessage(String sourceString) {
        this.sourceString = sourceString;
        jsonObject=JSONObject.parseObject(this.sourceString);
        this.logType = this.jsonObject.getString("@logType");
        this.message = this.jsonObject.getString("message");

        this.fields = this.message.split("\\|", -1);
    }


    public String getSourceString() {
        return sourceString;
    }

    public JSONObject getJsonObject() {
        return jsonObject;
    }

    public String getMessage() {
        return message;
    }

    public String getLogType() {
        return logType;
    }


    public String[] getFields() {
        return fields;
    }


    @Override
    public String toString() {
        return "ParsedMessage{" +
                "sourceString='" + sourceString + '\'' +
                ", jsonObject=" + jsonObject +
                ", message='" + message + '\'' +
                ", logType='" + logType + '\'' +
                ", fields=" + Arrays.toString(fields) +
                '}';
    }
}
