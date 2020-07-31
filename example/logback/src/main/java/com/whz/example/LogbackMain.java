package com.whz.example;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Author: wanghz
 * @Date: 2020/7/28 8:08 PM
 */
public class LogbackMain {

    private static final Logger logger = LoggerFactory.getLogger(LogbackMain.class);

    public static void main(String[] args) {
        logger.info("logback 成功了");
        logger.error("logback 成功了");
        logger.debug("logback 成功了");
    }

}
