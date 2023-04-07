package com.wcj.forest.core.route;

import com.wcj.forest.util.DHTUtil;
import io.netty.buffer.Unpooled;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.SneakyThrows;
import org.springframework.util.CollectionUtils;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * @author wengchengjian
 * @date 2023/4/7-15:55
 */
public class RoutingTable {

    private Map<Integer, PriorityQueue<Node>> tableMap = new ConcurrentHashMap<>(16);

    private Bucket bucket = new Bucket(0, null);

    public void put(Node node) {
        int bucketIndex = getBucketIndex();

        // 如果是自己的话,就直接返回
        if(bucketIndex == 0) {
            return;
        }
        PriorityQueue<Node> pq = tableMap.get(bucketIndex);
        if(CollectionUtils.isEmpty(pq)){
            //如果是空那么找最近的那个节点加入
            boolean isAdd=false;
            while(bucket.next != null){
                if(bucketIndex > bucket.getK()
                        && bucketIndex < bucket.next.getK()){
                    //先往小的里面放
                    node.setCurrentK(bucket.getK());
                    isAdd = putAccurate(tableMap.get(bucket.getK()),node,false,bucket,tableMap);
                    if(!isAdd){
                        node.setCurrentK(bucket.next.getK());
                        isAdd = putAccurate(tableMap.get(bucket.next.getK()),node,true,bucket,tableMap);
                    }
                }
                bucket=bucket.next;
            }
            if(!isAdd){
                //没有添加成功 那么往最后一个节点添加
                node.setCurrentK(bucket.getK());
                putAccurate(tableMap.get(bucket.getK()), node, true, bucket, tableMap);
            }

        }else{//如果不空 那么直接加 简单点来吧
            if (pq.size()<8) {
                if (!pq.contains(node)) {
                    node.setCurrentK(node.getK());
                    pq.add(node);
                } else {
                    reAdd(pq,node);
                }
            } else {
                pq.add(node);
                pq.poll();
            }
        }
    }

    private int getBucketIndex() {
        return this.bucket.k;
    }

    /**
     * @param pq 当前bucket
     * @param node 需要插入的node
     * @param isSplit 是否需要分裂
     * @param bucket 需要插入的bucket的位置
     * @param tableMap 路由表
     * @return 返回是否添加成功
     */
    @SneakyThrows
    public boolean putAccurate(PriorityQueue<Node> pq,Node node,boolean isSplit,Bucket bucket,Map<Integer,PriorityQueue<Node>> tableMap){
        boolean isAdd = false;
        if (pq.contains(node)) {
            return reAdd(pq,node);
        }
        if(pq.size()<8){
            pq.add(node);
            isAdd=true;
        }
        if(isSplit && !isAdd){
            PriorityQueue<Node> priorityQueue=new PriorityQueue<Node>((x,y)-> x.getRank() - y.getRank());
            priorityQueue.add(node);
            tableMap.putIfAbsent(node.getK(),priorityQueue);
            //创建新的k桶后需要把两边的bucket距离比较近的都放到自己的k桶里面 如果超过8个就丢了 最好是可以ping一下
            mergeBucket(priorityQueue, node, bucket);
            mergeBucket(priorityQueue, node, bucket.next);
            Bucket nextBucket= new Bucket(node.getK(),bucket.next);
            bucket.next=nextBucket;
            isAdd=true;
            node.setCurrentK(node.getK());
        }
        return isAdd;
    }

    /**
     * 合并两个bucket较近的节点
     * @param currentQueue 主bucket
     * @param node 辅助节点
     * @param needMergedBucket 当前合并节点
     */
    public void mergeBucket(PriorityQueue<Node> currentQueue, Node node, Bucket needMergedBucket) {
        if(bucket != null && !CollectionUtils.isEmpty(tableMap.get(bucket.getK()))){
            PriorityQueue<Node> collect = new PriorityQueue<>();
            collect.addAll(tableMap.get(needMergedBucket.getK()).stream().filter(n -> {
                if (currentQueue.size() < 8 &&
                        Math.abs(n.getK() - n.getCurrentK()) > Math.abs(n.getK() - node.getK())) {
                    n.setCurrentK(node.getK());
                    currentQueue.add(n);
                    return false;
                }
                return true;
            }).collect(Collectors.toSet()));
            tableMap.put(needMergedBucket.getK(), !CollectionUtils.isEmpty(collect) ? collect: new PriorityQueue<Node>());
        }
    }

    /**
     * 根据nodeid 查找最近的8个node
     * @param trargetBytes 需要查找目标id
     * @return
     */
    public List<Node> getForTop8(byte[] trargetBytes) {
        int bucketIndex = getBucketIndex(trargetBytes);
        List<Node> res=new ArrayList<>();
        PriorityQueue<Node> pq = tableMap.get(bucketIndex);
        if (CollectionUtils.isEmpty(pq)) {
            while (bucket.next != null) {
                if (bucketIndex > bucket.getK()
                        && bucketIndex < bucket.next.getK()) {
                    tableMap.get(bucket.next.getK()).forEach(x -> {
                        if (res.size() < 8) {
                            res.add(x);
                        }
                    });
                }
                bucket = bucket.next;
            }
            if (CollectionUtils.isEmpty(res)) {
                tableMap.get(bucket.getK()).forEach(x -> {
                    if (res.size() < 8) {
                        res.add(x);
                    }
                });
            }

        } else {//如果不空 那么直接加 简单点来吧
            res.addAll(pq);
        }
        return res;
    }

    /**
     * find_node
     * @param sourceId 源id
     * @param targetId 目标id
     * @param address 地址
     * @param num
     */
    public void findNode(String sourceId, String targetId, InetAddress address, int num) {
        if(!channels.get(num).isWritable()){
            return;
        }
        FindNodeRequest findNodeRequest= new FindNodeRequest(node,target);
        channels.get(num).writeAndFlush(new DatagramPacket(Unpooled.copiedBuffer(bencode.encode(DHTUtil.beanToMap(findNodeRequest))), address));
    }

    private int getBucketIndex(byte[] trargetBytes) {

    }

    @Data
    @AllArgsConstructor
    public static class Bucket {
        private int k;

        private Bucket next;
    }
}
