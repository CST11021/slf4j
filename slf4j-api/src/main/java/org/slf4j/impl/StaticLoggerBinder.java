/**
 * Copyright (c) 2004-2011 QOS.ch
 * All rights reserved.
 *
 * Permission is hereby granted, free  of charge, to any person obtaining
 * a  copy  of this  software  and  associated  documentation files  (the
 * "Software"), to  deal in  the Software without  restriction, including
 * without limitation  the rights to  use, copy, modify,  merge, publish,
 * distribute,  sublicense, and/or sell  copies of  the Software,  and to
 * permit persons to whom the Software  is furnished to do so, subject to
 * the following conditions:
 *
 * The  above  copyright  notice  and  this permission  notice  shall  be
 * included in all copies or substantial portions of the Software.
 *
 * THE  SOFTWARE IS  PROVIDED  "AS  IS", WITHOUT  WARRANTY  OF ANY  KIND,
 * EXPRESS OR  IMPLIED, INCLUDING  BUT NOT LIMITED  TO THE  WARRANTIES OF
 * MERCHANTABILITY,    FITNESS    FOR    A   PARTICULAR    PURPOSE    AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE,  ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */
package org.slf4j.impl;

import org.slf4j.ILoggerFactory;

/**
 * 使用此类返回的信息将{@link org.slf4j.LoggerFactory}类与{@link ILoggerFactory}的实际实例进行绑定。
 *
 * 此类的作用是为slf4j-api模块提供一个虚拟的StaticLoggerBinder，防止在源码调试的时候报错，但是实际编译成jar包时，org.slf4j.impl 包及
 * 该包下面的类并不存在，相应的类都放到每个具体的实现包中，在每个SLF4J绑定项目中（即实现slf4j的日志实现框架）都对应的 org.slf4j.impl 包，
 * 和包下面的类，例如slf4j-nop，slf4j-log4j12等
 *
 * @author Ceki G&uuml;lc&uuml;
 */
public class StaticLoggerBinder {

    /** 此类的唯一实例 */
    private static final StaticLoggerBinder SINGLETON = new StaticLoggerBinder();

    /**
     * 返回此类的单例。
     *
     * @return StaticLoggerBinder单例
     */
    public static final StaticLoggerBinder getSingleton() {
        return SINGLETON;
    }

    /** 声明此实现针对的SLF4J API版本, 每个主要版本都会修改此字段的值, 注意：为避免编译器不断折叠，此字段不可以声明为final */
    public static String REQUESTED_API_VERSION = "1.6.99";

    private StaticLoggerBinder() {
        // This code should have never made it into slf4j-api.jar：这段代码永远都不会放入slf4j-api.jar
        throw new UnsupportedOperationException("This code should have never made it into slf4j-api.jar");
    }

    public ILoggerFactory getLoggerFactory() {
        throw new UnsupportedOperationException("This code should never make it into slf4j-api.jar");
    }

    public String getLoggerFactoryClassStr() {
        throw new UnsupportedOperationException("This code should never make it into slf4j-api.jar");
    }

}
