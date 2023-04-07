package com.wcj.forest.task;

import cn.hutool.cron.task.Task;
import com.wcj.forest.core.route.RoutingTable;
import com.wcj.forest.util.DHTUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author wengchengjian
 * @date 2023/4/7-16:30
 */
@Slf4j
@Component
public class FindNodeTask implements Task {

    private List<RoutingTable> routingTables;

    private BlockingQueue<InetSocketAddress> queue = new LinkedBlockingQueue<>();

    public FindNodeTask(Sender sender, List<RoutingTable> routingTables){
        this.sender=sender;
        this.routingTables=routingTables;
    }

    public void put(InetSocketAddress address) {
        queue.offer(address);
    }
    @Override
    public void execute() {
        while(!queue.isEmpty()) {
            try{
                InetSocketAddress take = queue.take();
                sender.findNode(routingTables.get(i).getNodeIdStr(), DHTUtil.generateNodeIdString(),take, i);
            }catch (Exception e){
                e.printStackTrace();
                log.error("error in FindNodeTask msg={}",e.getMessage());
            }
        }
    }
}
