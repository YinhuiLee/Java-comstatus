# 系统资源监控软件开发日志

## 参考资料
- https://www.cnblogs.com/lovehansong/p/7873245.html 从String里获取其中某一个字符
- https://www.cnblogs.com/longzhongren/p/4328661.html String转float
- https://blog.csdn.net/icer_wei/article/details/17709113 获得文件存储属性的两种方式
- https://blog.csdn.net/nongyan90/article/details/12849937 JLabel更改文字颜色和大小
- https://blog.csdn.net/z_cc_csdn/article/details/78146497?utm_source=blogxgwz4&utm_medium=distribute.pc_relevant.none-task-blog-title-2&spm=1001.2101.3001.4242 设置JLabel字体字号颜色
- https://bbs.csdn.net/topics/90137190 double转float
- https://www.jb51.net/article/90958.htm JFreeChart实现折线图
- https://blog.csdn.net/xiaozhendong123/article/details/50113051 图表数据刷新
- https://blog.csdn.net/weixin_35040169/article/details/86479522 线程停止
- https://blog.csdn.net/u011479200/article/details/81457282 饼图
- https://www.bilibili.com/video/BV1h7411v7Mq?p=37 使用图标
- https://blog.csdn.net/alovelee/article/details/52386193?utm_source=blogxgwz7&utm_medium=distribute.pc_relevant_bbs_down.none-task-blog-baidujs-1.nonecase&depth_1-utm_source=distribute.pc_relevant_bbs_down.none-task-blog-baidujs-1.nonecase 点否窗口不关闭
- http://blog.sina.com.cn/s/blog_7e9c5b6801015swz.html JFreeChart背景色
- https://bbs.csdn.net/topics/290030993 改坐标轴颜色
- https://blog.csdn.net/steppppup/article/details/79888624 char拼接
- https://bbs.csdn.net/topics/370057989 饼图突出显示
- https://zhidao.baidu.com/question/468587107.html 饼图标签背景边框
- https://blog.csdn.net/qq_42408149/article/details/82121067?utm_medium=distribute.pc_relevant_bbs_down.none-task-blog-baidujs-1.nonecase&depth_1-utm_source=distribute.pc_relevant_bbs_down.none-task-blog-baidujs-1.nonecase 鼠标事件

## 软件介绍
本软件为一个简单的系统资源监控软件，正于开发测试阶段，存在大量bug在所难免...

## 使用手册
1. 通过点击左侧CPU、内存、磁盘三个选项可以进入到该项目的监控页面，当鼠标移动选项上时会显示蓝色的边框和背景，移除时重新恢复白色。能力所限，点击后没有相应的显示特效。
2. 每个项目的监控页面都实现了动态变化（磁盘可能看不出orz），CPU和内存的页面还有对应的信息，同样是动态变化的。
3. 磁盘页面的大饼图为每个磁盘的总空间，当鼠标移动到某个饼块上时会出现互动效果，本来想实现移除后恢复的，后面忘记了0.0。
4. 点击磁盘页面的某个饼块，下方会显示对应磁盘的使用空间和剩余空间。
5. 右上角三个按钮都是有用的哦。

## 远景（展望/遗憾）
- 其实最初想的还有上方的工具栏、显示模式切换、帮助（相关信息）...太多愿望没有时间去实现了。当前版本简简单单的一个监控软件其实也花了好多时间。
- 代码真的又杂又乱。可能跟我想到什么就写什么有关。每一块都是增增补补删删改改，乱命名、超级耦合、大量重复，虽然有注解，但是我觉得我的代码还是无法让别人看懂，自己改代码的时候也十分费劲。
今后再做类似比较大的项目的时候一定要先规划，再开始。先想好每一部分的效果和细节，再一部分一部分慢慢按计划实现。
- 其他也没有什么好写的了，大家应该都是想得很多，但是真正做出来的很少。我觉得还是能力不行吧，每想实现一个功能都要百度试错，上面的参考资料其实也只记录了很少一部分的网页，很多是错的或者我没实现的，就不放上来了；还有些应该是叉掉太快后来就忘了往上记了。总的来说真正做好感觉还是有所收获的（虽然不知到swing真的有用嘛
- 有空会来更新的...
- 0点06了，希望老师助教不要介意超时这么几分钟（狗头保命