#SLF4J

参考链接：

https://juejin.im/post/5ad1bb0a5188255cb07d8c06

https://www.jianshu.com/p/bbbdcb30bba8

## SLF4J 概述

什么是 SLF4J 呢？简而言之，他就是一个日志的门面，市场上的日志系统非常多，SLF4J 想做的一件事情，就是将这多种的日志系统包装起来，提供统一的 API 供调用，从而解决日志的列国争霸的情况。

## SLF4J 调用流程

在解决以上问题之前，让我们先对日志系统做一个大概的理解。到底什么是 log4j,什么是 slf4j-api,为什么有一个 slf4j-log4j12,又怎么有个log4j-over-slf4j，看上去头晕眼花，绕来绕去。先祭上一张官网图：

![image-20200801101349975](assets/image-20200801101349975.png)

相信大家都见过这张图，但是未必全都理解图上说的是什么意思。所以呢，我们先不讲图，先看看最上面的一排文字，SLF4J bound to xxxx,xxxx总共涉及：

1. null
2. logback-classic
3. log4j
4. java.util.logging
5. simple
6. no-operation

第1个顾名思义，不绑定，就是没有日志实现。第5，6可以看出来，是 SLF4J 自己的实现，这里就忽略不讲。所以着重讲一下2、3、4。他们有个共同的特点，就是他们都是日志的真正实现库，他们和SLF4J没有关系，你可以使用 logback 打印日志，也可以使用 log4j 打日志。

第一层， application 就是应用层。

第二层，统统都是 SLF4J API。那么这一层是干什么用的呢？这一层就是一个门面层，而和门面层息息相关的 jar 包是什么呢？对，就是图中的 slf4j-api.jar 。这个jar包中，提供了日志调用的所有接口，应用都是直接调用这个 jar 包中的接口进行日志调用。

第三层有点不同。第一列，无实现，第2，5，6都是原生的一个实现，而3，4都有一个适配层。这里说说 logback。logback 他为什么不需要适配层呢，因为他就是按照了 SLF4J 接口去实现的一个日志库，相当于亲儿子，自然不需要适配层。所以，logback-classic.jar 和 logback-core.jar 就是 logback  的底层实现。而3，4就不同了，他们的接口或多或少会有差异，调用方式也各不相同，所以，需要一个适配层。所以 slf4j-log412.jar 和 slf4j-jdk14.jar 包的作用就是一个适配，这里是一个桥接，应用通过 slf4j-api 的接口调用过来时，桥接类实际会调用其底层的实现，达到一个桥接的过程。所以，slf4j-log412.jar,slf4j-jdk14.jar,slf4j-simple.jar,slf4j-nop.jar 是同一类，就是把`-`右边的实现进行一个桥接。

第四层，可以看到第2，3对应的实现，log4j.jar 和 jvm 就是最后的实现。

至此，做一个小总结：

1. slf4j-api.jar 是上层的门面，里面提供接口供调用。
2. logback-classic.jar、logback-core.jar、log4j.jar 是同一类别，属于底层实现库。
3. slf4j-log412.jar、slf4j-jdk14.jar、slf4j-simple.jar、slf4j-nop.jar，可以看成 slf4j-xxxx.jar，属于同一个类别，就是对 - 后面的库做一个桥接，更简单的理解，从左到右读：把 - 左边的调用用右边的库实现。

此时再看看上面那张图，是否已经全部理解了呢？

##SLF4J 转换流程

![image-20200801101700341](assets/image-20200801101700341.png)

那这张图又是什么意思呢？这就要说到 SLF4J 的一个强大之处了。设想一下以下一种情况：新建了一个工程，引入的第一个库使用的是 java.util.logging 日志库，引入的第二个库使用的是 log4j 日志库，而你自己的工程，老板规定，必须要用 logback 。你怎么办呢？这个时候， SLF4J 就出场了。他能帮你把所有日志归拢到你所指定的一种日志实现。就是说，他可以把 jul 日志实现转成 logback，还能把 log4j 实现转成 logback。那他是怎么做的呢？回过头来看图吧。

着重讲解左上角这一部分，其他的类似。先看 application，这个是应用，可以看到，他也遇到了我说到的问题。他的依赖里面有使用 log4j 的，有用 commons logging 的，有用 java.util.logging 的。所以此时需要做一个替换，分别是通过 jcl-over-slf4j.jar 将commons-logging.jar 替换为sfl4j，通过 log4j-over-slf4j.jar 将 log4j.jar 替换为sfl4j，jul-to-slf4j.jar 包中安装 SLF4JBrindgeHandler 以解决替换掉jul，这样就把所有日志调用转接到 slf4j-api 上了，然后 slf4j-api 接口再调用底层实现，图上是 logback。文中说的替换是什么意思呢？就是把原日志实现库排除掉，引入 xxx-over-slf4j.jar 。

那么，xxx-over-slf4j.jar 是什么原理呢？先给大家看这张图：

![image-20200801102709636](assets/image-20200801102709636.png)

左边是 log4j.jar 的包结构，右边是 log4j-over-slf4j.jar 的包结构。发现猫腻了吗？他们的目录结构一模一样！所以用 log4j-over-slf4j 可以替换掉 log4j！且编译不会出错。log4j-over-slf4j.jar 实现了基本上所有 log4j 会被调用的 api 接口。所以替换之后，不会报错，编译也能通过，而底层实现却全转到 slf4j 这里去了。

再看其他两个图，底层分别是 log4j， jvm 实现。都是讲其他库 over 一下到 slf4j ，特别注意一下， logback 不需要转，因为logback本身就实现了slf4j-api接口。

所以再小总结一下：jcl-over-slf4j.jar、log4j-over-slf4j.jar、jul-to-slf4j.jar 这种形式类似 xxx-over-slf4j.jar 的，就是将 `-` 前的日志库替换为slf4j。而 xxx-to-slf4j.jar 比较特殊，这是因为 xxx 这个包无法被替换掉，比如  java.util.logging 系统的库，无法替换，所以只能采用别的手段。此类别的实现读者有兴趣可以去看看，本文不再分析。





## SLF4J常见问题

### StackOverflow 错误

为什么会出现这个问题呢？控制台输出上一般会比较清楚，就是你既使用了桥接库，又使用了over库（狸猫）。比如：

![image-20200801103131647](assets/image-20200801103131647.png)

试想一下：你先用 over 库把 log4j 转成了 slfj4 调用。紧接着，你又把 slf4j 适配到 log4j 上。这就构成了一个死循环，肯定是会出现堆栈溢出的问题。所以，一个工程里面，只能保留一个日志实现库，还有配套的桥接库，加上其他日志的 over 库，才是正确之道。

### 异常参数错误

![image-20200801103312561](assets/image-20200801103312561.png)

大致可以看出来吧，他缺少一个真正的实现。看你选择使用什么，就把什么库补充上去。加上 logback，去掉 slf4j-log4j 或者加上 log4j 都可以解决这个问题。

其他问题都比较类似，如果看懂了上文的介绍，应该可以着手解决此类问题了。







可以看成 slf4j-xxxx.jar,属于同一个类别，就是对 - 后面的库做一个桥接，更简单的理解，从左到右读：把 - 左边的调用用右边的库实现。




slf4j-simple：slf4j自己的简单实现
slf4j-nop：



他们的接口或多或少会有差异，调用方式也各不相同，所以，需要一个适配层。所以 slf4j-log412.jar 和 slf4j-jdk14.jar 包的作用就是一个适配，
这里是一个桥接，应用通过 slf4j-api 的接口调用过来时，桥接类实际会调用其底层的实现，达到一个桥接的过程。所以，
slf4j-log412.jar,slf4j-jdk14.jar,slf4j-simple.jar,slf4j-nop.jar 是同一类，就是把-右边的实现进行一个桥接。



# 项目模块介绍

### 门面类模块

 #### slf4j-api

提供统一的日志接口，应用都是直接调用这个jar包中的接口进行日志调用。



### 桥接类模块

slf4j-log412.jar、slf4j-jdk14.jar、slf4j-simple.jar、slf4j-nop.jar 可以看成slf4j-Xxx.jar，属于同一个类别，就是对第三日志库做一个桥接，这样上层业务就可以使用slf4j的api，然后通过适配器调用第三方日志库了。

#### slf4j-simple

slf4j自己实现的一个日志框架。

#### slf4j-nop

slf4j自己实现的一个日志框架，通过名称即无日志输出，及使用调用了打印日志接口也不会输出日志。

#### slf4j-jdk14  

用于通过slf4j接口来调用jdk14日志库的包。

#### slf4j-log4j12：  

用于通过slf4j接口来调用log4j日志库的包，关于log4j的几个版本这里不做阐述，有兴趣可以去看看。

#### slf4j-jcl：     

用于通过slf4j接口来调用commons-logging日志库的包。

#### slf4j-ext：
#### slf4j-android：
#### slf4j-site
#### slf4j-migrator





### 适配类模块

Xxx-over-slf4j的原理就是在各个第三方日志框架对应的Xxx-over-slf4j包中，定义一个和第三方日志类同包同名的类，然后通过改写该类的实现，将原本调用第三方日志库的实现改为调用slf4j的，然后通过slf4j在去调用相应第三方日志库。
    

#### log4j-over-slf4j

适配 将log4j 日志库的接口，将 将log4j 接口调用转为slf4j-api的调用方式

#### jcl-over-slf4j

适配 commons-logging 日志库的接口，将 commons-logging 接口调用转为slf4j-api的调用方式

#### osgi-over-slf4j









## 原理



### SLF4J调用过程

  LoggerFactory -> org.slf4j.impl.StaticLoggerBinder(绑定第三方日志库) -> ILoggerFactory -> Logger(获取第三方的Logger实现)



### slf4j适配器的运用

slf4j-api：定义标准的日志接口

slf4j-log4j12：定义一个适配器（XxxAdapter implement org.slf4j.Logger），例如：Log4jLoggerAdapter，内部的日志实现委托了log4j第三方日志库实现

log4j：Log4j第三方日志库实现



### xxx-over-slf4j模块的原理

例如，原始的第三方日志库log4j中，对应的日志接口是：org.apache.log4j.Logger#debug(String)，接口实现代码如下：

```java
public void debug(Object message) {
    if (!this.repository.isDisabled(10000)) {
        if (Level.DEBUG.isGreaterOrEqual(this.getEffectiveLevel())) {
            this.forcedLog(FQCN, Level.DEBUG, message, (Throwable)null);
        }

    }
}
```

而在log4j-over-slf4j中，也定义了一个同包名同类名 org.apache.log4j.Logger 类，并且对应的 org.apache.log4j.Logger#debug(String) 接口实现代码如下：

```java
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
```

通过代码我们发现，log4j-over-slf4j包中的 org.apache.log4j.Logger 改写了原始第三方日志库的 org.apache.log4j.Logger 并将实现代理到了 slf4j 对应的日志实现中。



## jul-to-slf4j模块

xxx-to-slf4j.jar 比较特殊，这是因为 xxx 这个包无法被替换掉，比如：java.util.logging 日志库，无法实现桥接，所以只能采用别的手段，此类别的实现读者有兴趣可以去看看，本文不再分析。









​    