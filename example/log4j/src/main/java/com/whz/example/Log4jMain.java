package com.whz.example;

import org.apache.log4j.Logger;

/**
 * 原始的第三方log4j打印日志的demo
 *
 * @Author: wanghz
 * @Date: 2020/7/31 4:17 PM
 */
public class Log4jMain {

    public static void main(String[] args) {
        Logger logger = Logger.getLogger(Log4jMain.class);
        logger.debug("log4j debug");
        logger.info("log4j info");
        logger.error("log4j error");
    }

}
