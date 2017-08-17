package com.huawei.om.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import kafka.api.PartitionOffsetRequestInfo;
import kafka.cluster.Broker;
import kafka.common.TopicAndPartition;
import kafka.javaapi.*;
import kafka.javaapi.consumer.SimpleConsumer;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.RetryUntilElapsed;
import org.apache.log4j.Logger;
import org.apache.spark.streaming.kafka.OffsetRange;

import java.security.SecureRandom;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by z00383627 on 2017/3/5.
 * version 1.1
 */
public class OffsetUtil {
    private static final SecureRandom random = new SecureRandom();

    private static final Logger LOG = Logger.getLogger(OffsetUtil.class);

    public static Map<TopicAndPartition, Long> getOffsets(String brokerListString, String zkListString, String topicListString, String groupID) {
        Map<TopicAndPartition,Long> topicOffsets = new HashMap<>();
        Map<TopicAndPartition, Long> consumerOffsets = new HashMap<>();

        if (null == brokerListString || null == zkListString || null == topicListString || null == groupID) {
            LOG.error("params null");
            return topicOffsets;
        }

        String[] brokerList = brokerListString.split(",");
        String[] zkList = zkListString.split(",");
        String[] topicList = topicListString.split(",");

        if (brokerList.length < 1 || zkList.length < 1 || topicList.length < 1) {
            return topicOffsets;
        }

        topicOffsets.putAll(getTopicOffsets(brokerList, topicList));

        int n = random.nextInt(zkList.length);
        for(String topic : topicList){
            Map<TopicAndPartition, Long> tmpMap = getConsumerOffsets(zkList[n], groupID, topic);
            if (null == tmpMap || tmpMap.size() < 1) {
                continue;
            }
            consumerOffsets.putAll(tmpMap);
        }

        if(consumerOffsets.size() > 0){
            topicOffsets.putAll(consumerOffsets);
        }
        return topicOffsets;
    }

    private static Map<TopicAndPartition, Long> getTopicOffsets(String[] brokerList, String[] topicList){
        Map<TopicAndPartition,Long> topicOffsets = new HashMap<>();
        if (null == brokerList || brokerList.length < 1 || null == topicList || topicList.length < 1) {
            return topicOffsets;
        }

        for(String broker : brokerList){
            if (null == broker) {
                continue;
            }
            String[] brokerInfo = broker.split(":");
            if (brokerInfo.length != 2) {
                continue;
            }

            int port = Integer.parseInt(brokerInfo[1]);                 //no catch

            SimpleConsumer simpleConsumer = new SimpleConsumer(brokerInfo[0], port, 10000, 1024, "consumer");
            TopicMetadataRequest topicMetadataRequest = new TopicMetadataRequest(Arrays.asList(topicList));
            TopicMetadataResponse topicMetadataResponse = simpleConsumer.send(topicMetadataRequest);

            LOG.error("B:\t" + broker + "\ttopicsMetadata.size\t" + topicMetadataResponse.topicsMetadata().size());

            for (TopicMetadata metadata : topicMetadataResponse.topicsMetadata()) {
                String nowTopic = metadata.topic();

                LOG.error("B:\t" + broker + "\t" + nowTopic + "\tmetadata.partitionsMetadata\t" + metadata.partitionsMetadata().size());

                for (PartitionMetadata part : metadata.partitionsMetadata()) {
                    Broker leader = part.leader();
                    if (leader != null) {
                        TopicAndPartition topicAndPartition = new TopicAndPartition(nowTopic, part.partitionId());

                        PartitionOffsetRequestInfo partitionOffsetRequestInfo = new PartitionOffsetRequestInfo(kafka.api.OffsetRequest.LatestTime(), 10000);
                        OffsetRequest offsetRequest = new OffsetRequest(ImmutableMap.of(topicAndPartition, partitionOffsetRequestInfo), kafka.api.OffsetRequest.CurrentVersion(), simpleConsumer.clientId());
                        OffsetResponse offsetResponse = simpleConsumer.getOffsetsBefore(offsetRequest);

                        if (!offsetResponse.hasError()) {
                            long[] offsets = offsetResponse.offsets(nowTopic, part.partitionId());
                            topicOffsets.put(topicAndPartition, offsets[0]);
                            if (offsets.length == 1) {
                                LOG.error("B:\t" + broker + "\t" + topicAndPartition.topic() + "\t" + topicAndPartition.partition() + "\t" + offsets.length + offsets[0]);
                            }else if (offsets.length >= 2) {
                                LOG.error("B:\t" + broker + "\t" + topicAndPartition.topic() + "\t" + topicAndPartition.partition() + "\t" + offsets.length + "\t"  + offsets[0] + "\t" + offsets[1]);
                            }
                        }else {
                            LOG.error("B:\t" + broker + "\t"  + topicAndPartition.topic() + "\t" + "offsetResponse.hasError");
                        }
                    }else {
                        LOG.error("B:\t" + broker + "\t"  + nowTopic + "\t" + part.partitionId() + "\t" + "leader is null");
                    }
                }
            }
            simpleConsumer.close();
        }
        return topicOffsets;
    }

    private static Map<TopicAndPartition, Long> getConsumerOffsets(String zkServer, String groupID, String topic) {
        Map<TopicAndPartition,Long> consumerOffsets = new HashMap<>();

        if (null == zkServer || null == groupID || null == topic) {
            return consumerOffsets;
        }

        ObjectMapper objectMapper = new ObjectMapper();
        CuratorFramework curatorFramework = CuratorFrameworkFactory.builder()
                .connectString(zkServer).connectionTimeoutMs(1000)
                .sessionTimeoutMs(10000).retryPolicy(new RetryUntilElapsed(1000, 1000)).build();

        if (null == curatorFramework) {
            return consumerOffsets;
        }
        curatorFramework.start();

        List<String> partitions = null;

        String nodePath = "/consumers/"+groupID+"/offsets/" + topic;
        try{
            if (null == curatorFramework.checkExists().forPath(nodePath)) {				//throw Exception
                return consumerOffsets;
            }
            partitions = curatorFramework.getChildren().forPath(nodePath);		//throw Exception
        }catch(Exception e){
            LOG.error("Exception--curatorFramework.checkExists.forPath error");
        }

        if (partitions == null || partitions.isEmpty()) {
            return consumerOffsets;
        }

        Long offset = -1L;

        for(String partition : partitions){
            int partitionL = Integer.valueOf(partition);

            try {
                offset = objectMapper.readValue(curatorFramework.getData().forPath(nodePath + "/" + partition), Long.class);
                LOG.error("Z:\t" + topic + "\t" + partitionL + "\t" + offset);
            } catch (Exception e) {
                LOG.error("Exception--objectMapper.readValue error");
                continue;
            }

            TopicAndPartition topicAndPartition = new TopicAndPartition(topic, partitionL);
            consumerOffsets.put(topicAndPartition, offset);
        }

        curatorFramework.close();

        return consumerOffsets;
    }

    public static void saveOffsetToZookeeper(OffsetRange[] offsetRanges, String zkListString, String groupId) {
        if (null == offsetRanges || offsetRanges.length < 1 || null == zkListString || null == groupId) {
            return;
        }
        List<String> zkServerList = Arrays.asList(zkListString.split(","));
        int n = random.nextInt(zkServerList.size());
        ObjectMapper objectMapper = new ObjectMapper();
        CuratorFramework curatorFramework = CuratorFrameworkFactory.builder()
                .connectString(zkServerList.get(n)).connectionTimeoutMs(1000)
                .sessionTimeoutMs(10000).retryPolicy(new RetryUntilElapsed(1000, 1000)).build();

        curatorFramework.start();

        for (OffsetRange offsetRange : offsetRanges) {
            byte[] offsetBytes;
            try {
                offsetBytes = objectMapper.writeValueAsBytes(offsetRange.untilOffset());
            } catch (JsonProcessingException e) {
                LOG.error("JsonProcessingException--objectMapper.writeValueAsBytes error");
                continue;
            }
            String nodePath = "/consumers/" + groupId + "/offsets/" + offsetRange.topic()+ "/" + offsetRange.partition();
            try {
                if(curatorFramework.checkExists().forPath(nodePath) != null){														//throw Exception
                    curatorFramework.setData().forPath(nodePath,offsetBytes);													//throw Exception
                }else{
                    curatorFramework.create().creatingParentsIfNeeded().forPath(nodePath, offsetBytes);							//throw Exception
                }
            } catch (Exception e) {
                LOG.error("Exception--curatorFramework write offset error");
            }

        }

        curatorFramework.close();
    }
}