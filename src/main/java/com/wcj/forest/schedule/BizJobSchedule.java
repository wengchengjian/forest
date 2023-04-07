package com.wcj.forest.schedule;

import com.wcj.forest.task.FindNodeTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * @author wengchengjian
 * @date 2023/4/7-16:32
 */
@Component
public class BizJobSchedule {

    @Autowired
    private FindNodeTask findNodeTask;

    /**
     * 每10秒执行一次
     */
    @Scheduled(cron = "0/10 * * * * ?")
    public void findNode() {
        findNodeTask.execute();
    }
}
