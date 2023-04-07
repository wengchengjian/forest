package com.wcj.forest.core.request;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author wengchengjian
 * @date 2023/4/7-16:19
 */
@Data
@AllArgsConstructor
public class BaseRequest {
    private String t;//messageid 2 byte

    private String y;//"q" for query, "r" for response, or "e" for error

    private String q;//method ping/find_node/get_peers/announce_peer
}
