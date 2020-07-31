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
package org.slf4j.spi;

import org.slf4j.Logger;
import org.slf4j.Marker;

/**
 * An <b>optional</b> interface helping integration with logging systems capable of 
 * extracting location information. This interface is mainly used by SLF4J bridges 
 * such as jcl-over-slf4j, jul-to-slf4j and log4j-over-slf4j or {@link Logger} wrappers
 * which need to provide hints so that the underlying logging system can extract
 * the correct location information (method name, line number).
 *
 * @author Ceki Gulcu
 * @since 1.3
 */
public interface LocationAwareLogger extends Logger {

    // 这些常量应该在EventContant中。但是，为了保持二进制向后兼容性，我们在此处保留这些常量

    final public int TRACE_INT = 00;
    final public int DEBUG_INT = 10;
    final public int INFO_INT = 20;
    final public int WARN_INT = 30;
    final public int ERROR_INT = 40;

    /**
     * 支持位置信息的打印方法
     * 
     * @param marker    用于此事件的标记可以为null
     * @param fqcn      记录器实例的完全限定的类名称，通常是记录器类，记录器桥或记录器包装器.
     * @param level     此接口中定义的级别整数之一
     * @param message   日志事件的消息
     * @param t         与日志事件关联的Throwable，可以为null
     */
    public void log(Marker marker, String fqcn, int level, String message, Object[] argArray, Throwable t);

}
