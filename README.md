# BleSingle
Android O + 8.0 蓝牙锁屏后台扫描唤醒，Ble Background Scan

一个Android 8以上的蓝牙后台扫描唤醒的demo，Ble Scan Background wake up

这个demo的作用是实现8.0以后的后台监测到特定蓝牙信号自动唤醒APP的功能，首先需要另外一个装了可以发射蓝牙信号软件的手机，我这边是选取的ios平台上的lightblue，然后在这个软件里面新建一个虚拟设备名称是要demo搜索的蓝牙模块名称。然后把app杀掉，过一段时间打开lightblue发射蓝牙信号，然后关掉，再次打开demo，会发现demo中记录的时间就是你发射蓝牙信号的时间，说明被杀后它还是在监测蓝牙信号的
手机是华为8.0系统、、、、实际上进程没有被唤醒，但是检测蓝牙的service被唤醒了，里面可以执行一些逻辑
gif在这里

![image](https://img-blog.csdnimg.cn/20190417170408507.gif)
