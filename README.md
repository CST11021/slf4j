#SLF4J

什么是 SLF4J 呢？简而言之，他就是一个日志的门面，市场上的日志系统非常多， SLF4J 想做的一件事情，就是将这多种的日志系统包装起来，提供统一的 API 供调用，从而解决日志的列国争霸的情况。



参考链接：https://juejin.im/post/5ad1bb0a5188255cb07d8c06

https://www.jianshu.com/p/bbbdcb30bba8



可以看成 slf4j-xxxx.jar,属于同一个类别，就是对 - 后面的库做一个桥接，更简单的理解，从左到右读：把 - 左边的调用用右边的库实现。




slf4j-simple：slf4j自己的简单实现
slf4j-nop：



他们的接口或多或少会有差异，调用方式也各不相同，所以，需要一个适配层。所以 slf4j-log412.jar 和 slf4j-jdk14.jar 包的作用就是一个适配，
这里是一个桥接，应用通过 slf4j-api 的接口调用过来时，桥接类实际会调用其底层的实现，达到一个桥接的过程。所以，
slf4j-log412.jar,slf4j-jdk14.jar,slf4j-simple.jar,slf4j-nop.jar 是同一类，就是把-右边的实现进行一个桥接。











##项目模块介绍



###门面类模块

提供了日志调用的所有接口，应用都是直接调用这个jar包中的接口进行日志调用

 #### slf4j-api：







###桥接类模块

slf4j-log412.jar、slf4j-jdk14.jar、slf4j-simple.jar、slf4j-nop.jar 可以看成slf4j-Xxx.jar，属于同一个类别，就是对第三日志库做一个桥接，这样上层业务就可以使用slf4j的api，然后通过适配器调用第三方日志库

####slf4j-simple
####slf4j-nop

####slf4j-jdk14：    

slf4j接入jdk14日志库

####slf4j-log4j12：  

slf4j接入log4j12日志库

####slf4j-jcl：     

slf4j接入commons-logging日志库

####slf4j-ext：
####slf4j-android：
####slf4j-site
####slf4j-migrator





###适配类模块

Xxx-over-slf4j的原理就是在各个第三方日志框架对应的Xxx-over-slf4j包中，定义一个和第三方日志类同包同名的类，然后通过改写该类的实现，将原本调用第三方日志库的实现改为调用slf4j的，然后通过slf4j在去调用相应第三方日志库

例如，原始的第三方日志库log4j中，对应的日志接口是：org.apache.log4j.Logger#debug(String)，接口实现代码如下：
    
    public void debug(Object message) {
        if (!this.repository.isDisabled(10000)) {
            if (Level.DEBUG.isGreaterOrEqual(this.getEffectiveLevel())) {
                this.forcedLog(FQCN, Level.DEBUG, message, (Throwable)null);
            }

        }
    }

而在log4j-over-slf4j中，也定义了一个同包名同类名 org.apache.log4j.Logger 类，并且对应的 org.apache.log4j.Logger#debug(String) 接口实现代码如下：

    public void debug(Object message) {
        differentiatedLog(null, CATEGORY_FQCN, LocationAwareLogger.DEBUG_INT, message, null);
    }
    
    void differentiatedLog(Marker marker, String fqcn, int level, Object message, Throwable t) {
    
            String m = convertToString(message);
            if (locationAwareLogger != null) {
                locationAwareLogger.log(marker, fqcn, level, m, null, t);
            } else {
                switch (level) {
                case LocationAwareLogger.TRACE_INT:
                    slf4jLogger.trace(marker, m);
                    break;
                case LocationAwareLogger.DEBUG_INT:
                    slf4jLogger.debug(marker, m);
                    break;
                case LocationAwareLogger.INFO_INT:
                    slf4jLogger.info(marker, m);
                    break;
                case LocationAwareLogger.WARN_INT:
                    slf4jLogger.warn(marker, m);
                    break;
                case LocationAwareLogger.ERROR_INT:
                    slf4jLogger.error(marker, m);
                    break;
                }
            }
        }
        
通过代码我们发现，log4j-over-slf4j包中的 org.apache.log4j.Logger 改写了原始第三方日志库的 org.apache.log4j.Logger 并将实现代理到了 slf4j 对应的日志实现中
    

####log4j-over-slf4j

适配 将log4j 日志库的接口，将 将log4j 接口调用转为slf4j-api的调用方式

####jcl-over-slf4j

适配 commons-logging 日志库的接口，将 commons-logging 接口调用转为slf4j-api的调用方式

####osgi-over-slf4j





jul-to-slf4j：

xxx-to-slf4j.jar 比较特殊，这是因为 xxx 这个包无法被替换掉，比如：java.util.logging 日志库，无法实现桥接，所以只能采用别的手段，此类别的实现读者有兴趣可以去看看，本文不再分析。







##原理



###SLF4J调用过程

  LoggerFactory -> org.slf4j.impl.StaticLoggerBinder(绑定第三方日志库) -> ILoggerFactory -> Logger(获取第三方的Logger实现)

###slf4j适配器的运用

  slf4j-api：定义标准的日志接口

  slf4j-log4j12：定义一个适配器（XxxAdapter implement org.slf4j.Logger），例如：Log4jLoggerAdapter，内部的日志实现委托了log4j第三方日志库实现

  log4j：Log4j第三方日志库实现







​    