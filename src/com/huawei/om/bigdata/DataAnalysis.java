package com.huawei.om.bigdata;

import com.alibaba.fastjson.JSONObject;
import com.huawei.om.common.Config;
import com.huawei.om.common.Constant;
import com.huawei.om.models.FieldMessage;
import com.huawei.om.models.ParsedMessage;
import com.huawei.om.utils.LoginUtil;
import com.huawei.om.utils.OffsetUtil;
import com.huawei.spark.streaming.kafka.JavaDStreamKafkaWriterFactory;
import kafka.common.TopicAndPartition;
import kafka.message.MessageAndMetadata;
import kafka.producer.KeyedMessage;
import kafka.serializer.StringDecoder;
import org.apache.log4j.Logger;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.streaming.Durations;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.apache.spark.streaming.kafka.HasOffsetRanges;
import org.apache.spark.streaming.kafka.KafkaUtils;

import java.util.Map;
import java.util.Properties;

/**
 * Created by z00383627 on 2017/2/9.
 *
 */
public class DataAnalysis {
    private static final Logger LOG = Logger.getLogger(DataAnalysis.class);

    public static void main(String[] args) {
//        System.out.println(DateTimeUtils.getTime("2016-01-03 17:20:52"));
//        System.out.println(DateTimeUtils.isLegalDateTime("2017-03-07 23:00:31"));
//        if (!Config.getInstance().getBooleanConfig("aaaa")) {
//            return;
//        }

        Config.getInstance().init(args);
        LoginUtil.checkLogin(Config.getInstance());

        String topics = Config.getInstance().getStringConfig(Constant.TOPIC_LIST);
        if (null == topics) {
            LOG.error("Spark Streaming App need a list of topics");
            return;
        }

        int batchTime = Config.getInstance().getIntegerConfig(Constant.BATCH_TIME);
        if (batchTime <= 0) {
            LOG.error("The value of batchTime is illegal");
            return;
        }

        final Properties kafkaProducerProperties = new Properties();
        kafkaProducerProperties.put(Constant.SERIALIZER_CLASS, "kafka.serializer.DefaultEncoder");
        kafkaProducerProperties.put(Constant.KEY_SERIALIZER_CLASS, "kafka.serializer.StringEncoder");
        kafkaProducerProperties.put(Constant.METADATA_BROKER_LIST, Config.getInstance().getStringConfig(Constant.METADATA_BROKER_LIST));
        kafkaProducerProperties.put(Constant.REQUEST_REQUIRED_ACKS, "1");

        SparkConf sparkConf = new SparkConf().setAppName(Config.getInstance().getStringConfig(Constant.APP_NAME));
        JavaSparkContext javaSparkContext = new JavaSparkContext(sparkConf);
        JavaStreamingContext javaStreamingContext = new JavaStreamingContext(javaSparkContext, Durations.seconds(batchTime));
        javaStreamingContext.checkpoint(Config.getInstance().getStringConfig(Constant.CHECK_POINT));

        Map<TopicAndPartition, Long> offsets = OffsetUtil.getOffsets(Config.getInstance().getStringConfig(Constant.METADATA_BROKER_LIST),
                Config.getInstance().getStringConfig(Constant.ZOOKEEPER_CONNECT), topics, Config.getInstance().getStringConfig(Constant.GROUP_ID));

//        for(Map.Entry<TopicAndPartition,Long> entry : offsets.entrySet()){
//            LOG.error(entry.getKey().topic() + "\t" + entry.getKey().partition() + "\t" + entry.getValue());
//        }

        JavaDStream<String> kafkaDStream = KafkaUtils.createDirectStream(javaStreamingContext, String.class, String.class, StringDecoder.class,
                StringDecoder.class, String.class, Config.getInstance().getKafkaInputParams(), offsets, new Function<MessageAndMetadata<String, String>, String>() {
                    @Override
                    public String call(MessageAndMetadata<String, String> v1) {
                        return v1.message();
                    }
                }
        ).transform(new Function<JavaRDD<String>, JavaRDD<String>>() {
            @Override
            public JavaRDD<String> call(JavaRDD<String> v1) {
                if (null != v1) {
                    OffsetUtil.saveOffsetToZookeeper(((HasOffsetRanges) v1.rdd()).offsetRanges(),
                            Config.getInstance().getStringConfig(Constant.ZOOKEEPER_CONNECT), Config.getInstance().getStringConfig(Constant.GROUP_ID));
                }
                return v1;
            }
        });

        JavaDStream<ParsedMessage> parsedMessageDStream = kafkaDStream.map(new Function<String, ParsedMessage>() {
            @Override
            public ParsedMessage call(String s) {
                return new ParsedMessage(s);

            }
        });


        JavaDStream<FieldMessage> fieldMessageDStream = parsedMessageDStream.map(new Function<ParsedMessage, FieldMessage>() {
            @Override
            public FieldMessage call(ParsedMessage message) {
                if(message.getLogType().equals("as-oplog"))
                    return new FieldMessage(message);
                else
                    return null;
            }
        }).filter(new Function<FieldMessage, Boolean>() {
            @Override
            public Boolean call(FieldMessage fieldMessage) throws Exception {
                return null != fieldMessage;
            }
        });


        JavaDStreamKafkaWriterFactory.fromJavaDStream(fieldMessageDStream).writeToKafka(kafkaProducerProperties, new Function<FieldMessage, KeyedMessage<String, byte[]>>() {
            @Override
            public KeyedMessage<String, byte[]> call(FieldMessage fieldMessage) throws Exception {
                return new KeyedMessage<String, byte[]>("BI-platform-200-up-client-i", null, JSONObject.toJSONString(fieldMessage).getBytes());
            }
        });
        /*JavaDStream<Row> parsedIPMessageDStream = fieldMessageDStream.transform(new Function<JavaRDD<FieldMessage>, JavaRDD<Row>>() {
            @Override
            public JavaRDD<Row> call(JavaRDD<FieldMessage> fieldMessageRDD) {
                if (fieldMessageRDD.isEmpty()) {
                    return fieldMessageRDD.map(new Function<FieldMessage, Row>() {
                        @Override
                        public Row call(FieldMessage fieldMessage) {
                            return null;
                        }
                    });
                }
            }
        });*/


       /* JavaDStreamKafkaWriterFactory.fromJavaDStream(fieldMessageDStream).writeToKafka(kafkaProducerProperties, new Function<ParsedMessage, KeyedMessage<String, byte[]>>() {
            @Override
            public KeyedMessage<String, byte[]> call(ParsedMessage parsedMessage) throws Exception {

                return new KeyedMessage<String, byte[]>("BI-platform-200-up-client-i", null, JSONObject.toJSONString(parsedMessage.getMessage()).getBytes());
            }
        });*/
        javaStreamingContext.start();
        javaStreamingContext.awaitTermination();
        javaStreamingContext.close();
    }
}
