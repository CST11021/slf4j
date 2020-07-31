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

import static org.slf4j.event.EventConstants.NA_SUBST;

import java.io.Serializable;

import org.apache.log4j.Level;
import org.apache.log4j.spi.LocationInfo;
import org.apache.log4j.spi.ThrowableInformation;
import org.slf4j.Logger;
import org.slf4j.Marker;
import org.slf4j.event.LoggingEvent;
import org.slf4j.helpers.FormattingTuple;
import org.slf4j.helpers.MarkerIgnoringBase;
import org.slf4j.helpers.MessageFormatter;
import org.slf4j.spi.LocationAwareLogger;

/**
 * 将 {@link org.apache.log4j.Logger} 包装为符合 {@link org.slf4j.Logger}接口的实现。
 * 
 * 请注意，此类中提到的日志记录级别是指 {@link org.slf4j.Logger}中对应的日志级别，参数{@link org.apache.log4j.Level}类。
 * 
 * 在log4j版本1.2.12中引入了TRACE级别。为了避免使主机应用程序崩溃，在使用的log4j版本早于1.2.12的情况下，TRACE级别将映射为DEBUG。另请参见<a href="http://jira.qos.ch/browse/SLF4J-59"> SLF4J-59 </a>.
 * 
 * @author Ceki G&uuml;lc&uuml;
 */
public final class Log4jLoggerAdapter extends MarkerIgnoringBase implements LocationAwareLogger, Serializable {

    private static final long serialVersionUID = 6182834493563598289L;

    /** 真正的日志实现委托给了：org.apache.log4j.Logger */
    final transient org.apache.log4j.Logger logger;

    /** 遵循“完整的log4j手册”第162页至第168页中讨论的模式。*/
    final static String FQCN = Log4jLoggerAdapter.class.getName();

    /** 使用中的log4j版本可以识别trace级别吗，trace别在log4j 1.2.12中引入 */
    final boolean traceCapable;

    /** 警告：Log4jLoggerAdapter构造函数应只具有包访问权限，以便只有Log4jLoggerFactory可以创建包 */
    Log4jLoggerAdapter(org.apache.log4j.Logger logger) {
        this.logger = logger;
        this.name = logger.getName();
        traceCapable = isTraceCapable();
    }

    private boolean isTraceCapable() {
        try {
            logger.isTraceEnabled();
            return true;
        } catch (NoSuchMethodError e) {
            return false;
        }
    }


    // trace


    /**
     * Is this logger instance enabled for the TRACE level?
     * 
     * @return True if this Logger is enabled for level TRACE, false otherwise.
     */
    public boolean isTraceEnabled() {
        if (traceCapable) {
            return logger.isTraceEnabled();
        } else {
            return logger.isDebugEnabled();
        }
    }

    /**
     * Log a message object at level TRACE.
     * 
     * @param msg
     *          - the message object to be logged
     */
    public void trace(String msg) {
        logger.log(FQCN, traceCapable ? Level.TRACE : Level.DEBUG, msg, null);
    }

    /**
     * Log a message at level TRACE according to the specified format and
     * argument.
     * 
     * <p>
     * This form avoids superfluous object creation when the logger is disabled
     * for level TRACE.
     * </p>
     * 
     * @param format
     *          the format string
     * @param arg
     *          the argument
     */
    public void trace(String format, Object arg) {
        if (isTraceEnabled()) {
            FormattingTuple ft = MessageFormatter.format(format, arg);
            logger.log(FQCN, traceCapable ? Level.TRACE : Level.DEBUG, ft.getMessage(), ft.getThrowable());
        }
    }

    /**
     * Log a message at level TRACE according to the specified format and
     * arguments.
     * 
     * <p>
     * This form avoids superfluous object creation when the logger is disabled
     * for the TRACE level.
     * </p>
     * 
     * @param format
     *          the format string
     * @param arg1
     *          the first argument
     * @param arg2
     *          the second argument
     */
    public void trace(String format, Object arg1, Object arg2) {
        if (isTraceEnabled()) {
            FormattingTuple ft = MessageFormatter.format(format, arg1, arg2);
            logger.log(FQCN, traceCapable ? Level.TRACE : Level.DEBUG, ft.getMessage(), ft.getThrowable());
        }
    }

    /**
     * Log a message at level TRACE according to the specified format and
     * arguments.
     * 
     * <p>
     * This form avoids superfluous object creation when the logger is disabled
     * for the TRACE level.
     * </p>
     * 
     * @param format
     *          the format string
     * @param arguments
     *          an array of arguments
     */
    public void trace(String format, Object... arguments) {
        if (isTraceEnabled()) {
            FormattingTuple ft = MessageFormatter.arrayFormat(format, arguments);
            logger.log(FQCN, traceCapable ? Level.TRACE : Level.DEBUG, ft.getMessage(), ft.getThrowable());
        }
    }

    /**
     * Log an exception (throwable) at level TRACE with an accompanying message.
     * 
     * @param msg
     *          the message accompanying the exception
     * @param t
     *          the exception (throwable) to log
     */
    public void trace(String msg, Throwable t) {
        logger.log(FQCN, traceCapable ? Level.TRACE : Level.DEBUG, msg, t);
    }



    /**
     * Is this logger instance enabled for the DEBUG level?
     * 
     * @return True if this Logger is enabled for level DEBUG, false otherwise.
     */
    public boolean isDebugEnabled() {
        return logger.isDebugEnabled();
    }

    /**
     * Log a message object at level DEBUG.
     * 
     * @param msg
     *          - the message object to be logged
     */
    public void debug(String msg) {
        logger.log(FQCN, Level.DEBUG, msg, null);
    }

    /**
     * Log a message at level DEBUG according to the specified format and
     * argument.
     * 
     * <p>
     * This form avoids superfluous object creation when the logger is disabled
     * for level DEBUG.
     * </p>
     * 
     * @param format
     *          the format string
     * @param arg
     *          the argument
     */
    public void debug(String format, Object arg) {
        if (logger.isDebugEnabled()) {
            FormattingTuple ft = MessageFormatter.format(format, arg);
            logger.log(FQCN, Level.DEBUG, ft.getMessage(), ft.getThrowable());
        }
    }

    /**
     * Log a message at level DEBUG according to the specified format and
     * arguments.
     * 
     * <p>
     * This form avoids superfluous object creation when the logger is disabled
     * for the DEBUG level.
     * </p>
     * 
     * @param format
     *          the format string
     * @param arg1
     *          the first argument
     * @param arg2
     *          the second argument
     */
    public void debug(String format, Object arg1, Object arg2) {
        if (logger.isDebugEnabled()) {
            FormattingTuple ft = MessageFormatter.format(format, arg1, arg2);
            logger.log(FQCN, Level.DEBUG, ft.getMessage(), ft.getThrowable());
        }
    }

    /**
     * Log a message at level DEBUG according to the specified format and
     * arguments.
     * 
     * <p>
     * This form avoids superfluous object creation when the logger is disabled
     * for the DEBUG level.
     * </p>
     * 
     * @param format
     *          the format string
     * @param arguments an array of arguments
     */
    public void debug(String format, Object... arguments) {
        if (logger.isDebugEnabled()) {
            FormattingTuple ft = MessageFormatter.arrayFormat(format, arguments);
            logger.log(FQCN, Level.DEBUG, ft.getMessage(), ft.getThrowable());
        }
    }

    /**
     * Log an exception (throwable) at level DEBUG with an accompanying message.
     * 
     * @param msg
     *          the message accompanying the exception
     * @param t
     *          the exception (throwable) to log
     */
    public void debug(String msg, Throwable t) {
        logger.log(FQCN, Level.DEBUG, msg, t);
    }

    /**
     * Is this logger instance enabled for the INFO level?
     * 
     * @return True if this Logger is enabled for the INFO level, false otherwise.
     */
    public boolean isInfoEnabled() {
        return logger.isInfoEnabled();
    }

    /**
     * Log a message object at the INFO level.
     * 
     * @param msg
     *          - the message object to be logged
     */
    public void info(String msg) {
        logger.log(FQCN, Level.INFO, msg, null);
    }

    /**
     * Log a message at level INFO according to the specified format and argument.
     * 
     * <p>
     * This form avoids superfluous object creation when the logger is disabled
     * for the INFO level.
     * </p>
     * 
     * @param format
     *          the format string
     * @param arg
     *          the argument
     */
    public void info(String format, Object arg) {
        if (logger.isInfoEnabled()) {
            FormattingTuple ft = MessageFormatter.format(format, arg);
            logger.log(FQCN, Level.INFO, ft.getMessage(), ft.getThrowable());
        }
    }

    /**
     * Log a message at the INFO level according to the specified format and
     * arguments.
     * 
     * <p>
     * This form avoids superfluous object creation when the logger is disabled
     * for the INFO level.
     * </p>
     * 
     * @param format
     *          the format string
     * @param arg1
     *          the first argument
     * @param arg2
     *          the second argument
     */
    public void info(String format, Object arg1, Object arg2) {
        if (logger.isInfoEnabled()) {
            FormattingTuple ft = MessageFormatter.format(format, arg1, arg2);
            logger.log(FQCN, Level.INFO, ft.getMessage(), ft.getThrowable());
        }
    }

    /**
     * Log a message at level INFO according to the specified format and
     * arguments.
     * 
     * <p>
     * This form avoids superfluous object creation when the logger is disabled
     * for the INFO level.
     * </p>
     * 
     * @param format
     *          the format string
     * @param argArray
     *          an array of arguments
     */
    public void info(String format, Object... argArray) {
        if (logger.isInfoEnabled()) {
            FormattingTuple ft = MessageFormatter.arrayFormat(format, argArray);
            logger.log(FQCN, Level.INFO, ft.getMessage(), ft.getThrowable());
        }
    }

    /**
     * Log an exception (throwable) at the INFO level with an accompanying
     * message.
     * 
     * @param msg
     *          the message accompanying the exception
     * @param t
     *          the exception (throwable) to log
     */
    public void info(String msg, Throwable t) {
        logger.log(FQCN, Level.INFO, msg, t);
    }

    /**
     * Is this logger instance enabled for the WARN level?
     * 
     * @return True if this Logger is enabled for the WARN level, false otherwise.
     */
    public boolean isWarnEnabled() {
        return logger.isEnabledFor(Level.WARN);
    }

    /**
     * Log a message object at the WARN level.
     * 
     * @param msg
     *          - the message object to be logged
     */
    public void warn(String msg) {
        logger.log(FQCN, Level.WARN, msg, null);
    }

    /**
     * Log a message at the WARN level according to the specified format and
     * argument.
     * 
     * <p>
     * This form avoids superfluous object creation when the logger is disabled
     * for the WARN level.
     * </p>
     * 
     * @param format
     *          the format string
     * @param arg
     *          the argument
     */
    public void warn(String format, Object arg) {
        if (logger.isEnabledFor(Level.WARN)) {
            FormattingTuple ft = MessageFormatter.format(format, arg);
            logger.log(FQCN, Level.WARN, ft.getMessage(), ft.getThrowable());
        }
    }

    /**
     * Log a message at the WARN level according to the specified format and
     * arguments.
     * 
     * <p>
     * This form avoids superfluous object creation when the logger is disabled
     * for the WARN level.
     * </p>
     * 
     * @param format
     *          the format string
     * @param arg1
     *          the first argument
     * @param arg2
     *          the second argument
     */
    public void warn(String format, Object arg1, Object arg2) {
        if (logger.isEnabledFor(Level.WARN)) {
            FormattingTuple ft = MessageFormatter.format(format, arg1, arg2);
            logger.log(FQCN, Level.WARN, ft.getMessage(), ft.getThrowable());
        }
    }

    /**
     * Log a message at level WARN according to the specified format and
     * arguments.
     * 
     * <p>
     * This form avoids superfluous object creation when the logger is disabled
     * for the WARN level.
     * </p>
     * 
     * @param format
     *          the format string
     * @param argArray
     *          an array of arguments
     */
    public void warn(String format, Object... argArray) {
        if (logger.isEnabledFor(Level.WARN)) {
            FormattingTuple ft = MessageFormatter.arrayFormat(format, argArray);
            logger.log(FQCN, Level.WARN, ft.getMessage(), ft.getThrowable());
        }
    }

    /**
     * Log an exception (throwable) at the WARN level with an accompanying
     * message.
     * 
     * @param msg
     *          the message accompanying the exception
     * @param t
     *          the exception (throwable) to log
     */
    public void warn(String msg, Throwable t) {
        logger.log(FQCN, Level.WARN, msg, t);
    }

    /**
     * Is this logger instance enabled for level ERROR?
     * 
     * @return True if this Logger is enabled for level ERROR, false otherwise.
     */
    public boolean isErrorEnabled() {
        return logger.isEnabledFor(Level.ERROR);
    }

    /**
     * Log a message object at the ERROR level.
     * 
     * @param msg
     *          - the message object to be logged
     */
    public void error(String msg) {
        logger.log(FQCN, Level.ERROR, msg, null);
    }

    /**
     * Log a message at the ERROR level according to the specified format and
     * argument.
     * 
     * <p>
     * This form avoids superfluous object creation when the logger is disabled
     * for the ERROR level.
     * </p>
     * 
     * @param format
     *          the format string
     * @param arg
     *          the argument
     */
    public void error(String format, Object arg) {
        if (logger.isEnabledFor(Level.ERROR)) {
            FormattingTuple ft = MessageFormatter.format(format, arg);
            logger.log(FQCN, Level.ERROR, ft.getMessage(), ft.getThrowable());
        }
    }

    /**
     * Log a message at the ERROR level according to the specified format and
     * arguments.
     * 
     * <p>
     * This form avoids superfluous object creation when the logger is disabled
     * for the ERROR level.
     * </p>
     * 
     * @param format
     *          the format string
     * @param arg1
     *          the first argument
     * @param arg2
     *          the second argument
     */
    public void error(String format, Object arg1, Object arg2) {
        if (logger.isEnabledFor(Level.ERROR)) {
            FormattingTuple ft = MessageFormatter.format(format, arg1, arg2);
            logger.log(FQCN, Level.ERROR, ft.getMessage(), ft.getThrowable());
        }
    }

    /**
     * Log a message at level ERROR according to the specified format and
     * arguments.
     * 
     * <p>
     * This form avoids superfluous object creation when the logger is disabled
     * for the ERROR level.
     * </p>
     * 
     * @param format
     *          the format string
     * @param argArray
     *          an array of arguments
     */
    public void error(String format, Object... argArray) {
        if (logger.isEnabledFor(Level.ERROR)) {
            FormattingTuple ft = MessageFormatter.arrayFormat(format, argArray);
            logger.log(FQCN, Level.ERROR, ft.getMessage(), ft.getThrowable());
        }
    }

    /**
     * Log an exception (throwable) at the ERROR level with an accompanying
     * message.
     * 
     * @param msg
     *          the message accompanying the exception
     * @param t
     *          the exception (throwable) to log
     */
    public void error(String msg, Throwable t) {
        logger.log(FQCN, Level.ERROR, msg, t);
    }

    public void log(Marker marker, String callerFQCN, int level, String msg, Object[] argArray, Throwable t) {
        Level log4jLevel = toLog4jLevel(level);
        logger.log(callerFQCN, log4jLevel, msg, t);
    }

    private Level toLog4jLevel(int level) {
        Level log4jLevel;
        switch (level) {
        case LocationAwareLogger.TRACE_INT:
            log4jLevel = traceCapable ? Level.TRACE : Level.DEBUG;
            break;
        case LocationAwareLogger.DEBUG_INT:
            log4jLevel = Level.DEBUG;
            break;
        case LocationAwareLogger.INFO_INT:
            log4jLevel = Level.INFO;
            break;
        case LocationAwareLogger.WARN_INT:
            log4jLevel = Level.WARN;
            break;
        case LocationAwareLogger.ERROR_INT:
            log4jLevel = Level.ERROR;
            break;
        default:
            throw new IllegalStateException("Level number " + level + " is not recognized.");
        }
        return log4jLevel;
    }

    public void log(LoggingEvent event) {
        Level log4jLevel = toLog4jLevel(event.getLevel().toInt());
        if (!logger.isEnabledFor(log4jLevel))
            return;

        org.apache.log4j.spi.LoggingEvent log4jevent = toLog4jEvent(event, log4jLevel);
        logger.callAppenders(log4jevent);

    }

    private org.apache.log4j.spi.LoggingEvent toLog4jEvent(LoggingEvent event, Level log4jLevel) {

        FormattingTuple ft = MessageFormatter.format(event.getMessage(), event.getArgumentArray(), event.getThrowable());

        LocationInfo locationInfo = new LocationInfo(NA_SUBST, NA_SUBST, NA_SUBST, "0");

        ThrowableInformation ti = null;
        Throwable t = ft.getThrowable();
        if (t != null)
            ti = new ThrowableInformation(t);

        org.apache.log4j.spi.LoggingEvent log4jEvent = new org.apache.log4j.spi.LoggingEvent(FQCN, logger, event.getTimeStamp(), log4jLevel, ft.getMessage(),
                        event.getThreadName(), ti, null, locationInfo, null);

        return log4jEvent;
    }

}
