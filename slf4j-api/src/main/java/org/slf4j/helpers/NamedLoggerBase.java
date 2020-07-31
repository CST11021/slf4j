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
package org.slf4j.helpers;

import java.io.ObjectStreamException;
import java.io.Serializable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 用作命名记录器实现的基类。更重要的是，此类建立反序列化行为。
 * 
 * @author Ceki Gulcu
 * @see #readResolve
 * @since 1.5.3
 */
abstract class NamedLoggerBase implements Logger, Serializable {

    private static final long serialVersionUID = 7535258609338176893L;

    /** 对应的打印日志所在的类，例如：
     * public class Wombat {
     *
     *   final static Logger logger = LoggerFactory.getLogger(Wombat.class);
     *   省略...
     * }
     * 这里的name = com.test.Wombat
     */
    protected String name;

    public String getName() {
        return name;
    }

    /**
     * 用LoggerFactory返回的同名（同名）记录器替换此实例。请注意，仅在反序列化期间调用此方法。
     *
     * 如果所需的ILoggerFactory是LoggerFactory引用的一个，则此方法将很好地工作，但是，如果用户通过其他（非静态）机制来管理其记录器层次结构，例如依赖注入，那么这种方法将适得其反。
     * 
     * @return logger with same name as returned by LoggerFactory
     * @throws ObjectStreamException
     */
    protected Object readResolve() throws ObjectStreamException {
        // 使用getName（）代替this.name甚至适用于NOPLogger
        return LoggerFactory.getLogger(getName());
    }

}
