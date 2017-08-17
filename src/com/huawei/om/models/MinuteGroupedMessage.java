package com.huawei.om.models;

import com.huawei.om.utils.DateTimeUtils;

import java.io.Serializable;
import java.util.Iterator;

/**
 * Created by z00383627 on 2017/3/8.
 *
 */
public class MinuteGroupedMessage implements Serializable {
    private String server_ip;
    private String s_province;
    private String s_city;
    private String s_isp;

    private String c_province;
    private String c_city;
    private String c_isp;

    private String probe_time;
    private String pt_h;

    private String net_type;

    private String error_code;
    ///////////////////////////////////////////////////////////////
    private long speed_slice_64k_times = 0L;        //1 2 4
    private long speed_slice_128k_times = 0L;
    private long speed_slice_256k_times = 0L;
    private long speed_slice_512k_times = 0L;
    private long speed_slice_1m_times = 0L;
    private long speed_slice_max_times = 0L;
    private long speed_slice_resolve_times = 0L;

    private int avg_speed = 0;              //2 4

    private long total_bandwidth = 0L;       //2 4
    private long https_bandwidth = 0L;      //2 4

    private long success_times = 0L;             //2  4        200 or 206
    private long fail_times = 0L;                //2  4

    private long error_code_times = 0L;      //3
    private long legal_speed_times = 0L;

    public MinuteGroupedMessage(String groupKeysString, Iterator<BaseGroupedMessage> messageIterator) throws ArrayIndexOutOfBoundsException {
        String[] groupKeys = groupKeysString.split("\001");
        this.server_ip = groupKeys[0];
        this.s_province = groupKeys[1];
        this.s_city = groupKeys[2];
        this.s_isp = groupKeys[3];
        this.c_province = groupKeys[4];
        this.c_city = groupKeys[5];
        this.c_isp = groupKeys[6];
        this.net_type = groupKeys[7];
        this.error_code = groupKeys[8];
        this.probe_time = groupKeys[9];
        this.pt_h = DateTimeUtils.getDateAndHour(this.probe_time);

        long total_speed = 0L;

        while (messageIterator.hasNext()) {
            BaseGroupedMessage baseGroupedMessage = messageIterator.next();
            if (null == baseGroupedMessage) {
                continue;
            }
            speed_slice_64k_times += baseGroupedMessage.getSpeed_slice_64k_times();
            speed_slice_128k_times += baseGroupedMessage.getSpeed_slice_128k_times();
            speed_slice_256k_times += baseGroupedMessage.getSpeed_slice_256k_times();
            speed_slice_512k_times += baseGroupedMessage.getSpeed_slice_512k_times();
            speed_slice_1m_times += baseGroupedMessage.getSpeed_slice_1m_times();
            speed_slice_max_times += baseGroupedMessage.getSpeed_slice_max_times();
            total_bandwidth += baseGroupedMessage.getTotal_bandwidth();
            https_bandwidth += baseGroupedMessage.getHttps_bandwidth();
            success_times += baseGroupedMessage.getSuccess_times();
            fail_times += baseGroupedMessage.getFail_times();
            error_code_times += baseGroupedMessage.getError_code_times();
            legal_speed_times += baseGroupedMessage.getLegal_speed_times();
            if (baseGroupedMessage.getLegal_speed_times() > 0 && baseGroupedMessage.getAvg_speed() > 0) {
                total_speed += baseGroupedMessage.getLegal_speed_times() * baseGroupedMessage.getAvg_speed();
            }
        }
        if (legal_speed_times > 0) {
            avg_speed = (int) (total_speed / legal_speed_times);
        }
    }

    public String getServer_ip() {
        return server_ip;
    }

    public String getS_province() {
        return s_province;
    }

    public String getS_city() {
        return s_city;
    }

    public String getS_isp() {
        return s_isp;
    }

    public String getC_province() {
        return c_province;
    }

    public String getC_city() {
        return c_city;
    }

    public String getC_isp() {
        return c_isp;
    }

    public String getProbe_time() {
        return probe_time;
    }

    public String getPt_h() {
        return pt_h;
    }

    public String getNet_type() {
        return net_type;
    }

    public String getError_code() {
        return error_code;
    }

    public long getSpeed_slice_64k_times() {
        return speed_slice_64k_times;
    }

    public long getSpeed_slice_128k_times() {
        return speed_slice_128k_times;
    }

    public long getSpeed_slice_256k_times() {
        return speed_slice_256k_times;
    }

    public long getSpeed_slice_512k_times() {
        return speed_slice_512k_times;
    }

    public long getSpeed_slice_1m_times() {
        return speed_slice_1m_times;
    }

    public long getSpeed_slice_max_times() {
        return speed_slice_max_times;
    }

    public long getSpeed_slice_resolve_times() {
        return speed_slice_resolve_times;
    }

    public int getAvg_speed() {
        return avg_speed;
    }

    public long getTotal_bandwidth() {
        return total_bandwidth;
    }

    public long getHttps_bandwidth() {
        return https_bandwidth;
    }

    public long getSuccess_times() {
        return success_times;
    }

    public long getFail_times() {
        return fail_times;
    }

    public long getError_code_times() {
        return error_code_times;
    }

    public long getLegal_speed_times() {
        return legal_speed_times;
    }

    @Override
    public String toString() {
        return "server_ip='" + server_ip + '\'' +
                ", s_province='" + s_province + '\'' +
                ", s_city='" + s_city + '\'' +
                ", s_isp='" + s_isp + '\'' +
                ", c_province='" + c_province + '\'' +
                ", c_city='" + c_city + '\'' +
                ", c_isp='" + c_isp + '\'' +
                ", probe_time='" + probe_time + '\'' +
                ", pt_h='" + pt_h + '\'' +
                ", net_type='" + net_type + '\'' +
                ", error_code='" + error_code + '\'' +
                ", speed_slice_64k_times=" + speed_slice_64k_times +
                ", speed_slice_128k_times=" + speed_slice_128k_times +
                ", speed_slice_256k_times=" + speed_slice_256k_times +
                ", speed_slice_512k_times=" + speed_slice_512k_times +
                ", speed_slice_1m_times=" + speed_slice_1m_times +
                ", speed_slice_max_times=" + speed_slice_max_times +
                ", speed_slice_resolve_times=" + speed_slice_resolve_times +
                ", avg_speed=" + avg_speed +
                ", total_bandwidth=" + total_bandwidth +
                ", https_bandwidth=" + https_bandwidth +
                ", success_times=" + success_times +
                ", fail_times=" + fail_times +
                ", error_code_times=" + error_code_times +
                ", legal_speed_times=" + legal_speed_times;
    }
}
