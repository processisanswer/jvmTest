 -------
    Object obj=new Object();

    先在新生代eden区申请空间
    ---新生代参数---
        -Xms200M  starting
        -Xmx      max
        -Xmn      new

        -XX：SurvivorRatio=8
---linux下查看 jvm各种参数
---------------------------------------
    jps
    jmap -heap pid
    -XX 解释

    堆：假设 内存规整
    栈上分配
    分配空间时，
    多线程下，会出现同步过程，jvm使用CAS实现同步。
    当竞争激烈时，使用栈上分配策略，避免过多的锁。
    TLAB----Thread Local Allocation Buffer
    这样Eden区，每个线程都有自己的一个buffer
    有参数控制这个大小，不宜过大
    --------------
    标记整理算法 带压缩的算法 涉及内存规整
    Free List 类似文件系统 指针、和空间说明
    ------
    8:1:1是为了，最好让这个对象不要进入老年代。
    这样在1:1之间，循环转移。
    最大限度实现对象都在新生代区域。
    YoungGC 比 FullGC 快10倍。
    eden满了就会minor GC
    第二次回收 eden 和 s0 往s1区丢。
    ----------------------------
    进入老年代的情况：
      对象很大
        会有一个参数
        -XX：PerenureSizeThreshold=3145728 3M
      长期存活的对象，也有一个参数
        -XX:PretenureSizeThreshold=15
        默认15岁
      动态对象年龄判定
            相同年龄所有对象的大小总和>Survivor空间的一半，这些对象
            直接进入老年代
-----------------------------------------
    担保机制
    每次minorGC 之前，都会检查
        老年代最大可用连续空间是否大于新生代所有对象总空间。

    MinorGC 是新生代的
    MajorGC 是old区，老年代的
    FullGC=前两者之和
    所有垃圾回收器的目的是减少FullGC
--------什么时候可能会触发STW的FullGC？------------------
    1 Perm空间不足
    2 CMS GC时出现
    3 统计得到的Young GC晋升到老年代的平均
        大小大于老年代的剩余空间
    4 主动触发Full GC(执行jmap -histo:live [pid])来避免碎片问题。


    对象--分配--满了--回收
    ------引用------
    强-->new
    软-->SoftReference 大的缓存、图片加载时使用
         private static HashMap<?,?> global=
            new HashMap<String,SoftReference<?>>();
         软引用，做优化
         这个value是SoftReference
         内存不足的时候，下一次GC的时候就会被干掉。
         取值是取不到的。
    弱-->只要是GC，就会被回收。
    虚-->类似弱引用，最大功能是，被引用时会被通知到。
---------------------------------------------------
    回收怎么回收？
        标记清除算法
            效率不高、空间碎片
        复制回收算法
            简单高效、内存利用率低
        标记整理算法
            相对标记清除算法，无空间碎片
       ------垃圾回收器-----
       7种垃圾回收器
         STW  Stop The World
            是分代的
            新生代  基于复制回收算法
                    三种：Serial   单线程，打扫时，应用线程挂起
                          ParNew  多线程打扫
                                有参数控制
                                -XX：ParallelGCThreads=n
                          Parallel Scavenge 全局控制
                                关注的重点在于吞吐量，时间维度控制
                                吞吐量=运行用户代码时间/(运行用户代码时间+垃圾收集时间)
                                -XX：MaxGCPauseMillis=n
                                -XX: GCTimeRatio=n  是一个比率
                                如何实现的
                                -XX：UseAdaptiveSizePolicy 设置成一个生态
                                    基于历史数据和统计数据支持
----------------------------------------------------------------------
                                跟CMS有什么区别？
                                CMS是减少回收停顿的时间
            老年代  三种 CMS
                            使用的是标记清理算法。
                            标记的是GCroot可达的对象
                            并行清扫的过程。
                            初始标记-->并行标记-->重新标记-->并发清除
                            缺点：CPU敏感、需要更多的线程
                            减少了回收停顿时间
                            有碎片
                                --有参数，设置阈值
                                    -XX：CMSInitiationgOccupancyFraction
                                        Concurrent Mode Failure 启用Serial Old
                                    -XX：UseCMSCompactAtFullCollection
                                    -XX: CMSFullGCsBeforeCompaction
                                        执行多少次不压缩FullGC后，再来一次带压缩的
                                        默认是 0 表示每次都压缩
                                    -XX:+UseConcMarkSweep 设置显式的使用这个算法
                        Serial Old （MSC）
                            是CMS的备用预案,担保失败的的Full GC使用
                            使用的是标记整理算法。
                        Parallel Old
                            使用的是标记整理算法。
--------------------------------------------------------------------------------
                G1 垃圾回收器
大多数线上 基于 ParNew + CMS 模式的算法
    目标是减少回收停顿的时间。

    垃圾回收器和垃圾回收算法有什么关系？
        垃圾回收器就是算法的实现。
----------------------------------------------------------------------------------
        回收的时间节点
        jvm经典案例
        swap
        MAT

        jstack
        以及GC日志怎么看

        标配JVM调优





