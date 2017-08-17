package com.huawei.om.models;

import java.io.Serializable;

/**
 * Created by z00383627 on 2017/3/6.
 *
 */
public class SubSecondMessage implements Serializable {
    private String[] fields;
    private String probeDate;
    private String probeTime;

    public SubSecondMessage(ParsedMessage message) {
        if (null == message) {
            return;
        }
        this.fields = message.getFields();
        //TODO
    }

    public String[] getFields() {
        return fields;
    }

    public String getProbeDate() {
        return probeDate;
    }

    public String getProbeTime() {
        return probeTime;
    }
}
