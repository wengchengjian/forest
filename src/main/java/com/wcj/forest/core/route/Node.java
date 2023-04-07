package com.wcj.forest.core.route;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author wengchengjian
 * @date 2023/4/7-15:51
 */
@Data
public class Node implements Comparable<Node>, Serializable {
    private String nodeId;//16进制字符串

    private String ip; //node的ip

    private Integer port; //node的端口

    private LocalDateTime updateTime;//最后更新时间

    private byte[] nodeIdBytes; //20字节

    private Integer k = 0;//k桶应该有的位置

    private Integer currentK = 0;//当前的位置

    private Integer rank = 0; //node的rank分值 ，路由表满的时候，优先移除分值低的

    @Override
    public int compareTo(Node o) {
        return 0;
    }




}
