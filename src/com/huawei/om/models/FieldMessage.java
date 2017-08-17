package com.huawei.om.models;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by z00383627 on 2017/3/7.
 *
 */
public class FieldMessage implements Serializable{
    private Long timestamp;
    private String time_string;      //DONE
    private String version;
    private String version_number;
    private String channel;
    private String OpID;        //DONE

    private Long request_time;
    private Long response_time;
    private Long timespan;
    private String netType;

    private String user_account;
    private String return_code;
    private String opDetail;
    private String Oobe;
    private String terminal_type;
    private String package_name;

    private String url;
    private String interface_name;
    private String service_ip;
    private String domain;
    private String ui_version;
    private String client_ip;


    public FieldMessage(ParsedMessage message) {
        if (null == message || null == message.getFields()) {
            return;
        }
        try {
            this.timestamp = Long.parseLong(message.getFields()[5]);
        } catch (ArrayIndexOutOfBoundsException | NumberFormatException e) {
//            this.timestamp = -1;
            e.printStackTrace();
        }
        try {
            this.version = message.getFields()[0].split("\\s+")[0];
        } catch (ArrayIndexOutOfBoundsException e) {
            e.printStackTrace();
        }
        try {
            this.version_number = message.getFields()[0].split("\\s+")[1];
        } catch (ArrayIndexOutOfBoundsException e) {
            e.printStackTrace();
        }
        try {
            this.channel = message.getFields()[2];
        } catch (ArrayIndexOutOfBoundsException e) {
            e.printStackTrace();
        }
        try {
            this.OpID = message.getFields()[3];
        } catch (ArrayIndexOutOfBoundsException e) {
            e.printStackTrace();
        }
        try {
            this.request_time = Long.parseLong(message.getFields()[4]);
        } catch (ArrayIndexOutOfBoundsException | NumberFormatException e) {
//            this.request_time = -1;
            e.printStackTrace();
        }
        try {
            this.response_time = Long.parseLong(message.getFields()[5]);
            SimpleDateFormat yyyyMMddHHmmssSSS = new SimpleDateFormat("yyyyMMddHHmmssSSS");
            Date parse = yyyyMMddHHmmssSSS.parse(response_time + "");

            DateFormat sdfIOS = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
            this.time_string = sdfIOS.format(parse);
        } catch (Exception e) {
 //            this.response_time = -1;
            e.printStackTrace();
        }
        try {
            this.timespan = Long.parseLong(message.getFields()[5]) - Long.parseLong(message.getFields()[4]);
        } catch (ArrayIndexOutOfBoundsException | NumberFormatException e) {
            this.timespan = -1L;
        }
        try {
            this.netType = message.getFields()[6];
        } catch (ArrayIndexOutOfBoundsException e) {
            e.printStackTrace();
        }
        try {
            this.user_account = message.getFields()[8];
        } catch (ArrayIndexOutOfBoundsException e) {
            e.printStackTrace();
        }
        try {
            this.return_code = message.getFields()[9];
        } catch (ArrayIndexOutOfBoundsException e) {
            e.printStackTrace();
        }
        try {
            this.opDetail = message.getFields()[11];
        } catch (ArrayIndexOutOfBoundsException e) {
            e.printStackTrace();
        }
        try {
            this.Oobe = message.getFields()[12];
        } catch (ArrayIndexOutOfBoundsException e) {
            e.printStackTrace();
        }
        try {
            this.terminal_type = message.getFields()[13];
        } catch (ArrayIndexOutOfBoundsException e) {
            e.printStackTrace();
        }
        try {
            this.package_name = message.getFields()[14];
        } catch (ArrayIndexOutOfBoundsException e) {
            e.printStackTrace();
        }
        try {
            this.url = message.getFields()[15];
        } catch (ArrayIndexOutOfBoundsException e) {
            e.printStackTrace();
        }
        try {
            this.interface_name = message.getFields()[15].split("[\\/?():]", -1)[9];
        } catch (ArrayIndexOutOfBoundsException e) {
            e.printStackTrace();
        }
        try {

            this.service_ip = message.getFields()[15].split("[\\/?():]", -1)[1];
        } catch (ArrayIndexOutOfBoundsException e) {
            e.printStackTrace();
        }
        try {
            this.domain = message.getFields()[15].split("[\\/?():]", -1)[5];
        } catch (ArrayIndexOutOfBoundsException e) {
            e.printStackTrace();
        }

        try {
            this.ui_version = message.getFields()[16];
        } catch (ArrayIndexOutOfBoundsException e) {
            e.printStackTrace();

        }
        try {
            this.client_ip = message.getFields()[18];
        } catch (ArrayIndexOutOfBoundsException e) {
            e.printStackTrace();
        }

    }


    public Long getTimestamp() {
        return timestamp;
    }

    public String getTime_string() {
        return time_string;
    }

    public String getVersion() {
        return version;
    }

    public String getVersion_number() {
        return version_number;
    }

    public String getChannel() {
        return channel;
    }

    public String getOpID() {
        return OpID;
    }

    public Long getRequest_time() {
        return request_time;
    }

    public Long getResponse_time() {
        return response_time;
    }

    public Long getTimespan() {
        return timespan;
    }

    public String getNetType() {
        return netType;
    }

    public String getUser_account() {
        return user_account;
    }

    public String getReturn_code() {
        return return_code;
    }

    public String getOpDetail() {
        return opDetail;
    }

    public String getOobe() {
        return Oobe;
    }

    public String getTerminal_type() {
        return terminal_type;
    }

    public String getPackage_name() {
        return package_name;
    }

    public String getUrl() {
        return url;
    }

    public String getInterface_name() {
        return interface_name;
    }

    public String getService_ip() {
        return service_ip;
    }

    public String getDomain() {
        return domain;
    }

    public String getUi_version() {
        return ui_version;
    }

    public String getClient_ip() {
        return client_ip;
    }

    @Override
    public String toString() {
        return "FieldMessage{" +
                "timestamp=" + timestamp +
                ", time_string='" + time_string + '\'' +
                ", version='" + version + '\'' +
                ", version_number='" + version_number + '\'' +
                ", channel='" + channel + '\'' +
                ", OpID='" + OpID + '\'' +
                ", request_time=" + request_time +
                ", response_time=" + response_time +
                ", timespan=" + timespan +
                ", netType='" + netType + '\'' +
                ", user_account='" + user_account + '\'' +
                ", return_code='" + return_code + '\'' +
                ", opDetail='" + opDetail + '\'' +
                ", Oobe='" + Oobe + '\'' +
                ", terminal_type='" + terminal_type + '\'' +
                ", package_name='" + package_name + '\'' +
                ", url='" + url + '\'' +
                ", interface_name='" + interface_name + '\'' +
                ", service_ip='" + service_ip + '\'' +
                ", domain='" + domain + '\'' +
                ", ui_version='" + ui_version + '\'' +
                ", client_ip='" + client_ip + '\'' +
                '}';
    }
}
