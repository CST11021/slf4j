package com.whz.example;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Author: wanghz
 * @Date: 2020/7/28 7:54 PM
 */
public class Log4jMain {

    private static final Logger logger = LoggerFactory.getLogger(Log4jMain.class);

    public static void main(String[] args) {
        logger.trace("测试trace");
        logger.debug("测试debug");
        logger.info("测试info");
        logger.warn("测试warn");
        logger.error("测试error");
    }

}
