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

import org.slf4j.ILoggerFactory;

/**
 * An internal interface which helps the static {@link org.slf4j.LoggerFactory} 
 * class bind with the appropriate {@link ILoggerFactory} instance. 
 * 
 * @author Ceki G&uuml;lc&uuml;
 */
public interface LoggerFactoryBinder {

    /**
     * 返回{@link org.slf4j.LoggerFactory}类应绑定的{@link ILoggerFactory}实例。
     * 
     * @return
     */
    public ILoggerFactory getLoggerFactory();

    /**
     * 此LoggerFactoryBinder实例要返回的{@link ILoggerFactory}对象的String形式。
     * 
     * 此方法允许开发人员询问此绑定器的意图，该意图可能与它在实践中能够产生的{@link ILoggerFactory}实例不同，差异仅在出现错误的情况下发生。
     * 
     * @return the class name of the intended {@link ILoggerFactory} instance
     */
    public String getLoggerFactoryClassStr();
}
