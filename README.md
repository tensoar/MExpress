# MExpress
基于`Material`风格设计的物流查询app样例

* 原生`Material`库
* 接入的快递鸟`API`
* 本地`SqlLite`存储数据
* 编译`apk`时，克隆或下载源码后，需要到快[递鸟官网](https://www.kdniao.com/)注册账号，获得一个自己的用户`ID`和`APIKey`并获取物流查询套餐(免费)，填入`app/src/main/res/values/string.xml`中。如下：
    ```xml
    <string name="ebusiness_id">用户id</string>
    <string name="app_key">APIKey</string>
    ```


# 图

<img src="https://album.wteng.top/images/2019/08/27/mexpress_main.png" alt="mexpress_main.jpg" width="35%" />
<img src="https://album.wteng.top/images/2019/09/24/mexpress2.jpg" alt="mexpress2.jpg" width="35%" />
