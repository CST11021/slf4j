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
package org.slf4j;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;

import org.slf4j.event.SubstituteLoggingEvent;
import org.slf4j.helpers.NOPLoggerFactory;
import org.slf4j.helpers.SubstituteLogger;
import org.slf4j.helpers.SubstituteLoggerFactory;
import org.slf4j.helpers.Util;
import org.slf4j.impl.StaticLoggerBinder;

/**
 * LoggerFactory为各种日志实现生成Logger，LoggerFactory本质上是在编译时对绑定的{@link ILoggerFactory}实例的包装，注意，LoggerFactory中的所有方法都是静态的
 * 
 * 
 * @author Alexander Dorokhine
 * @author Robert Elliot
 * @author Ceki G&uuml;lc&uuml;
 * 
 */
public final class LoggerFactory {

    static final String CODES_PREFIX = "http://www.slf4j.org/codes.html";

    static final String NO_STATICLOGGERBINDER_URL = CODES_PREFIX + "#StaticLoggerBinder";
    static final String MULTIPLE_BINDINGS_URL = CODES_PREFIX + "#multiple_bindings";
    static final String NULL_LF_URL = CODES_PREFIX + "#null_LF";
    static final String VERSION_MISMATCH = CODES_PREFIX + "#version_mismatch";
    static final String SUBSTITUTE_LOGGER_URL = CODES_PREFIX + "#substituteLogger";
    static final String LOGGER_NAME_MISMATCH_URL = CODES_PREFIX + "#loggerNameMismatch";
    static final String REPLAY_URL = CODES_PREFIX + "#replay";

    static final String UNSUCCESSFUL_INIT_URL = CODES_PREFIX + "#unsuccessfulInit";
    static final String UNSUCCESSFUL_INIT_MSG = "org.slf4j.LoggerFactory in failed state. Original exception was thrown EARLIER. See also " + UNSUCCESSFUL_INIT_URL;

    /** 表示加载绑定日志库的状态，这里为：未初始化 */
    static final int UNINITIALIZED = 0;
    /** 表示加载绑定日志库的状态，这里为：正在初始化 */
    static final int ONGOING_INITIALIZATION = 1;
    /** 表示加载绑定日志库的状态，这里为：初始化失败 */
    static final int FAILED_INITIALIZATION = 2;
    /** 表示加载绑定日志库的状态，这里为：初始化完成 */
    static final int SUCCESSFUL_INITIALIZATION = 3;
    /** 表示加载绑定日志库的状态，这里为：初始化为NOP */
    static final int NOP_FALLBACK_INITIALIZATION = 4;
    /** 表示加载绑定日志库的状态，初始值默认为：未初始化 */
    static volatile int INITIALIZATION_STATE = UNINITIALIZED;

    static final SubstituteLoggerFactory SUBST_FACTORY = new SubstituteLoggerFactory();
    static final NOPLoggerFactory NOP_FALLBACK_FACTORY = new NOPLoggerFactory();

    /** 用于配置是否检测不匹配的Log的名称的属性名 */
    static final String DETECT_LOGGER_NAME_MISMATCH_PROPERTY = "slf4j.detectLoggerNameMismatch";
    static final String JAVA_VENDOR_PROPERTY = "java.vendor.url";

    /** 是否检测不匹配的Log的名称 */
    static boolean DETECT_LOGGER_NAME_MISMATCH = Util.safeGetBooleanSystemProperty(DETECT_LOGGER_NAME_MISMATCH_PROPERTY);

    /**
     * It is LoggerFactory's responsibility to track version changes and manage
     * the compatibility list.
     * <p/>
     * <p/>
     * It is assumed that all versions in the 1.6 are mutually compatible.
     */
    static private final String[] API_COMPATIBILITY_LIST = new String[] { "1.6", "1.7" };


    private LoggerFactory() {
    }




    /**
     * Return a logger named corresponding to the class passed as parameter,
     * using the statically bound {@link ILoggerFactory} instance.
     *
     * <p>
     * In case the the <code>clazz</code> parameter differs from the name of the
     * caller as computed internally by SLF4J, a logger name mismatch warning
     * will be printed but only if the
     * <code>slf4j.detectLoggerNameMismatch</code> system property is set to
     * true. By default, this property is not set and no warnings will be
     * printed even in case of a logger name mismatch.
     *
     * @param clazz
     *            the returned logger will be named after clazz
     * @return logger
     *
     *
     * @see <a
     *      href="http://www.slf4j.org/codes.html#loggerNameMismatch">Detected
     *      logger name mismatch</a>
     */
    public static Logger getLogger(Class<?> clazz) {

        Logger logger = getLogger(clazz.getName());

        if (DETECT_LOGGER_NAME_MISMATCH) {
            Class<?> autoComputedCallingClass = Util.getCallingClass();

            if (autoComputedCallingClass != null && nonMatchingClasses(clazz, autoComputedCallingClass)) {
                // 向控制台输出警告
                Util.report(String.format("Detected logger name mismatch. Given name: \"%s\"; computed name: \"%s\".",
                                          logger.getName(), autoComputedCallingClass.getName()));
                Util.report("See " + LOGGER_NAME_MISMATCH_URL + " for an explanation");
            }
        }
        return logger;
    }

    /**
     * 使用静态绑定的{@link ILoggerFactory}实例返回根据name参数命名的Log
     *
     * @param name  The name of the logger.
     *
     * @return logger
     */
    public static Logger getLogger(String name) {
        // 获取与LoggerFactory绑定的ILoggerFactory实例
        ILoggerFactory iLoggerFactory = getILoggerFactory();
        return iLoggerFactory.getLogger(name);
    }


    /**
     * Force LoggerFactory to consider itself uninitialized.
     * <p/>
     * <p/>
     * This method is intended to be called by classes (in the same package) for
     * testing purposes. This method is internal. It can be modified, renamed or
     * removed at any time without notice.
     * <p/>
     * <p/>
     * You are strongly discouraged from calling this method in production code.
     */
    static void reset() {
        INITIALIZATION_STATE = UNINITIALIZED;
    }

    /**
     * 初始化slf4j绑定的日志库
     */
    private final static void performInitialization() {
        // 绑定日志库
        bind();
        // 如果初始化完成了，则做一下校验
        if (INITIALIZATION_STATE == SUCCESSFUL_INITIALIZATION) {
            versionSanityCheck();
        }
    }

    private static boolean messageContainsOrgSlf4jImplStaticLoggerBinder(String msg) {
        if (msg == null)
            return false;
        if (msg.contains("org/slf4j/impl/StaticLoggerBinder"))
            return true;
        if (msg.contains("org.slf4j.impl.StaticLoggerBinder"))
            return true;
        return false;
    }

    /**
     * 绑定日志库
     */
    private final static void bind() {
        try {

            Set<URL> staticLoggerBinderPathSet = null;
            // 在android下跳过检查，另请参阅http://jira.qos.ch/browse/SLF4J-328
            if (!isAndroid()) {
                // 通过类加载器获取 org/slf4j/impl/StaticLoggerBinder.class 资源文件路径，每个slf4j日志实现，都会有一个{@link StaticLoggerBinder}类，正常这里只会返回一个url，但是有时候由于引入多个日志实现包，则会加载多个
                staticLoggerBinderPathSet = findPossibleStaticLoggerBinderPathSet();
                // 如果在类路径上找到多个绑定，则在控制台上打印警告消息，否则不进行报告
                reportMultipleBindingAmbiguity(staticLoggerBinderPathSet);
            }

            // 注意：这里会使用类加载器最先加载的包的 org.slf4j.impl.StaticLoggerBinder 类
            StaticLoggerBinder.getSingleton();

            // 更新状态为初始化成功
            INITIALIZATION_STATE = SUCCESSFUL_INITIALIZATION;

            // 如果引入了多个日志实现，则控制台输出当前使用的日志框架
            // 每个slf4j的
            reportActualBinding(staticLoggerBinderPathSet);

        } catch (NoClassDefFoundError ncde) {
            String msg = ncde.getMessage();
            if (messageContainsOrgSlf4jImplStaticLoggerBinder(msg)) {
                INITIALIZATION_STATE = NOP_FALLBACK_INITIALIZATION;
                Util.report("Failed to load class \"org.slf4j.impl.StaticLoggerBinder\".");
                Util.report("Defaulting to no-operation (NOP) logger implementation");
                Util.report("See " + NO_STATICLOGGERBINDER_URL + " for further details.");
            } else {
                failedBinding(ncde);
                throw ncde;
            }
        } catch (java.lang.NoSuchMethodError nsme) {
            String msg = nsme.getMessage();
            if (msg != null && msg.contains("org.slf4j.impl.StaticLoggerBinder.getSingleton()")) {
                INITIALIZATION_STATE = FAILED_INITIALIZATION;
                Util.report("slf4j-api 1.6.x (or later) is incompatible with this binding.");
                Util.report("Your binding is version 1.5.5 or earlier.");
                Util.report("Upgrade your binding to version 1.6.x.");
            }
            throw nsme;
        } catch (Exception e) {
            failedBinding(e);
            throw new IllegalStateException("Unexpected initialization failure", e);
        } finally {
            postBindCleanUp();
        }
    }

	private static void postBindCleanUp() {
		fixSubstituteLoggers();
		replayEvents();
		// release all resources in SUBST_FACTORY
		SUBST_FACTORY.clear();
	}

    private static void fixSubstituteLoggers() {
        synchronized (SUBST_FACTORY) {
            SUBST_FACTORY.postInitialization();
            for (SubstituteLogger substLogger : SUBST_FACTORY.getLoggers()) {
                Logger logger = getLogger(substLogger.getName());
                substLogger.setDelegate(logger);
            }
        }

    }

    static void failedBinding(Throwable t) {
        INITIALIZATION_STATE = FAILED_INITIALIZATION;
        Util.report("Failed to instantiate SLF4J LoggerFactory", t);
    }

    private static void replayEvents() {
        final LinkedBlockingQueue<SubstituteLoggingEvent> queue = SUBST_FACTORY.getEventQueue();
        final int queueSize = queue.size();
        int count = 0;
        final int maxDrain = 128;
        List<SubstituteLoggingEvent> eventList = new ArrayList<SubstituteLoggingEvent>(maxDrain);
        while (true) {
            int numDrained = queue.drainTo(eventList, maxDrain);
            if (numDrained == 0)
                break;
            for (SubstituteLoggingEvent event : eventList) {
                replaySingleEvent(event);
                if (count++ == 0)
                    emitReplayOrSubstituionWarning(event, queueSize);
            }
            eventList.clear();
        }
    }

    private static void emitReplayOrSubstituionWarning(SubstituteLoggingEvent event, int queueSize) {
        if (event.getLogger().isDelegateEventAware()) {
            emitReplayWarning(queueSize);
        } else if (event.getLogger().isDelegateNOP()) {
            // nothing to do
        } else {
            emitSubstitutionWarning();
        }
    }

    private static void replaySingleEvent(SubstituteLoggingEvent event) {
        if (event == null)
            return;

        SubstituteLogger substLogger = event.getLogger();
        String loggerName = substLogger.getName();
        if (substLogger.isDelegateNull()) {
            throw new IllegalStateException("Delegate logger cannot be null at this state.");
        }

        if (substLogger.isDelegateNOP()) {
            // nothing to do
        } else if (substLogger.isDelegateEventAware()) {
            substLogger.log(event);
        } else {
            Util.report(loggerName);
        }
    }

    private static void emitSubstitutionWarning() {
        Util.report("The following set of substitute loggers may have been accessed");
        Util.report("during the initialization phase. Logging calls during this");
        Util.report("phase were not honored. However, subsequent logging calls to these");
        Util.report("loggers will work as normally expected.");
        Util.report("See also " + SUBSTITUTE_LOGGER_URL);
    }

    private static void emitReplayWarning(int eventCount) {
        Util.report("A number (" + eventCount + ") of logging calls during the initialization phase have been intercepted and are");
        Util.report("now being replayed. These are subject to the filtering rules of the underlying logging system.");
        Util.report("See also " + REPLAY_URL);
    }

    private final static void versionSanityCheck() {
        try {
            String requested = StaticLoggerBinder.REQUESTED_API_VERSION;

            boolean match = false;
            for (String aAPI_COMPATIBILITY_LIST : API_COMPATIBILITY_LIST) {
                if (requested.startsWith(aAPI_COMPATIBILITY_LIST)) {
                    match = true;
                }
            }

            if (!match) {
                Util.report("The requested version " + requested + " by your slf4j binding is not compatible with " + Arrays.asList(API_COMPATIBILITY_LIST).toString());
                Util.report("See " + VERSION_MISMATCH + " for further details.");
            }
        } catch (java.lang.NoSuchFieldError nsfe) {
            // given our large user base and SLF4J's commitment to backward
            // compatibility, we cannot cry here. Only for implementations
            // which willingly declare a REQUESTED_API_VERSION field do we
            // emit compatibility warnings.
        } catch (Throwable e) {
            // we should never reach here
            Util.report("Unexpected problem occured during version sanity check", e);
        }
    }

    /** 我们需要使用StaticLoggerBinder类的名称，但不能引用该类本身 */
    private static String STATIC_LOGGER_BINDER_PATH = "org/slf4j/impl/StaticLoggerBinder.class";

    /**
     * 通过类加载器获取 org/slf4j/impl/StaticLoggerBinder.class 资源文件路径，每个slf4j日志实现，都会有一个{@link StaticLoggerBinder}类
     * 例如：
     * file:/Users/wanghongzhan/IdeaProjects/slf4j/slf4j-simple/target/classes/org/slf4j/impl/StaticLoggerBinder.class
     * file:/Users/wanghongzhan/IdeaProjects/slf4j/slf4j-api/target/classes/org/slf4j/impl/StaticLoggerBinder.class
     *
     * @return
     */
    static Set<URL> findPossibleStaticLoggerBinderPathSet() {
        // 使用Set而不是list来处理错误，这里适合使用LinkedHashSet，因为它在迭代过程中保留插入顺序
        Set<URL> staticLoggerBinderPathSet = new LinkedHashSet<URL>();
        try {
            ClassLoader loggerFactoryClassLoader = LoggerFactory.class.getClassLoader();
            Enumeration<URL> paths;
            if (loggerFactoryClassLoader == null) {
                // org/slf4j/impl/StaticLoggerBinder.class
                paths = ClassLoader.getSystemResources(STATIC_LOGGER_BINDER_PATH);
            } else {
                paths = loggerFactoryClassLoader.getResources(STATIC_LOGGER_BINDER_PATH);
            }

            while (paths.hasMoreElements()) {
                URL path = paths.nextElement();
                staticLoggerBinderPathSet.add(path);
            }
        } catch (IOException ioe) {
            Util.report("Error getting resources from path", ioe);
        }
        return staticLoggerBinderPathSet;
    }

    /**
     * 是否存在多个Log实现
     *
     * @param binderPathSet
     * @return
     */
    private static boolean isAmbiguousStaticLoggerBinderPathSet(Set<URL> binderPathSet) {
        return binderPathSet.size() > 1;
    }

    /**
     * 如果在类路径上找到多个绑定，则在控制台上打印警告消息，否则不进行报告
     *
     * @param binderPathSet
     */
    private static void reportMultipleBindingAmbiguity(Set<URL> binderPathSet) {
        if (isAmbiguousStaticLoggerBinderPathSet(binderPathSet)) {
            Util.report("Class path contains multiple SLF4J bindings.");
            for (URL path : binderPathSet) {
                Util.report("Found binding in [" + path + "]");
            }
            Util.report("See " + MULTIPLE_BINDINGS_URL + " for an explanation.");
        }
    }

    private static boolean isAndroid() {
        String vendor = Util.safeGetSystemProperty(JAVA_VENDOR_PROPERTY);
        if (vendor == null)
            return false;
        return vendor.toLowerCase().contains("android");
    }

    /**
     * 如果引入了多个日志实现，则控制台输出当前使用的日志框架
     *
     * @param binderPathSet
     */
    private static void reportActualBinding(Set<URL> binderPathSet) {
        // 在Android下，bindPathSet可以为null
        if (binderPathSet != null && isAmbiguousStaticLoggerBinderPathSet(binderPathSet)) {
            Util.report("Actual binding is of type [" + StaticLoggerBinder.getSingleton().getLoggerFactoryClassStr() + "]");
        }
    }

    /**
     * 如果clazz不是为autoComputedCallingClass的实例，则返回true
     *
     * @param clazz
     * @param autoComputedCallingClass
     * @return
     */
    private static boolean nonMatchingClasses(Class<?> clazz, Class<?> autoComputedCallingClass) {
        return !autoComputedCallingClass.isAssignableFrom(clazz);
    }

    /**
     * 返回正在使用的{@link ILoggerFactory}实例, ILoggerFactory实例在编译时与此类绑定。
     * 
     * @return the ILoggerFactory instance in use
     */
    public static ILoggerFactory getILoggerFactory() {
        if (INITIALIZATION_STATE == UNINITIALIZED) {
            synchronized (LoggerFactory.class) {
                if (INITIALIZATION_STATE == UNINITIALIZED) {

                    // 设置状态为初始化中
                    INITIALIZATION_STATE = ONGOING_INITIALIZATION;

                    // 执行初始化：初始化一个StaticLoggerBinder，StaticLoggerBinder在具体的日志实现的包中
                    performInitialization();
                }
            }
        }


        switch (INITIALIZATION_STATE) {

            case SUCCESSFUL_INITIALIZATION:
                // 成功初始化
                return StaticLoggerBinder.getSingleton().getLoggerFactory();

            case NOP_FALLBACK_INITIALIZATION:
                // NOP后初始化
                return NOP_FALLBACK_FACTORY;

            case FAILED_INITIALIZATION:
                // 初始化失败
                throw new IllegalStateException(UNSUCCESSFUL_INIT_MSG);
            case ONGOING_INITIALIZATION:
                // 支持重入行为
                return SUBST_FACTORY;

        }

        throw new IllegalStateException("Unreachable code");
    }
}
