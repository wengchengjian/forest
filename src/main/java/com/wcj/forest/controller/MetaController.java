package com.wcj.forest.controller;

import lombok.SneakyThrows;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author wengchengjian
 * @date 2023/4/7-16:29
 */
@RestController
@RequestMapping("/meta")
public class MetaController {

    @ResponseBody
    @RequestMapping("/findMetaData")
    public String findMetaData(String infoHash){
        FindNodeTask.queue.offer(infoHash);
        return "success";
    }

    @RequestMapping("/findTorrent")
    public Map<String,Object> findTorrent(String name){
        Map<String,Object> map=new HashMap<>();
        return map;
    }
}
