<h1 align="center">TiePlugin-AutoBackup</h1>

<p align="center">
<img alt="Version" src="https://img.shields.io/badge/version-2.2-3f51b5.svg?style=flat-square"/>
<a href="https://flyhigher.top"><img alt="Author" src="https://img.shields.io/badge/author-WYstudio-red.svg?style=flat-square"/></a>
<a href="https://mdx.flyhigher.top"><img alt="Download" src="https://img.shields.io/badge/download-8.62M-brightgreen.svg?style=flat-square"/></a>
<a href="https://github.com/wystudio001/TiePlugin-AutoBackup/blob/main/LICENSE"><img alt="License" src="https://img.shields.io/badge/license-MIT-orange.svg?style=flat-square"/></a>
</p>

# 介绍
本项目结绳4.0自动备份插件(TiePlugin-AutoBackup),是专门用于 结绳(https://tiecode.cn) 工程项目备份使用的插件

目前已实现功能：

- 本地备份
- 云端蓝奏云备份
- 支持打包成压缩包格式备份
- 本地备份支持保留最新的5个文件
- 在线检查版本并更新
- 可修改本地备份路径(等等...)

相关特性：

- 纯手工打造代码
- 采用结绳进行编写和编译
- 纯手工完善蓝奏云文件上传代码
(后面发现OKHttp可以轻松实现  PS:白写了)
- 等.....

# 为什么开源
目前 结绳4.0 的生态**已经基本成熟**</br>
结绳开发团队的新产品——**书契** 已经出现</br>

而 结绳5.0 将依托于 书契 架构开发(正在紧锣密鼓地开发)</br>
**意思就是 4.0 的插件在 5.0 不能用！**</br>
结绳5.0 的自动备份插件将重新编写</br>

**书契 和 结绳5.0 的插件可以通用**</br>

故此将 结绳4.0 自动备份插件开源于此，以供大家学习</br>


# 文件说明

因为是采用的 结绳 进行编写和编译，本项目可能在Andorid Studio等IDE上无法运行(没有试过)

核心文件：

- 插件入口：[省略包名]\App.java</br>
(注册插件相关Action，以及初始化插件等)

- 本地备份：[省略包名]\backup\LocalBackup.java</br>
(本地备份功能线程信息处理)

- 本地备份线程：[省略包名]\thread\LocalBackupThread.java</br>
(本地备份功能实现，包括 保留备份 压缩备份 等)

- 云端备份：[省略包名]\backup\YunBackup.java</br>
(云端备份HTTP请求信息处理)

- 云端备份HTTP请求：[省略包名]\util\HttpUtils.java</br>
(云端备份HTTP请求实现，包括 蓝奏云文件上传 等)

- [省略包名]\util 文件夹：</br>
LogUtils.java (日志记录的关键实现)</br>
EncryUtils.java (网络请求加密 以及 判断插件是否为正版)</br>
FileUtils.java (文件相关操作)</br>
StringUtils.java (字符串相关操作)</br>
ViewUtils.java (视图相关操作 如：加载框 等待框 等)</br>
Base64Utlis.java (Base64加密相关操作)</br>

- [省略包名]\action 文件夹：**(此文件夹为 结绳 插件特有)**</br>
MyCodePageAction.java (当用户打开工程时根据设置进行备份)</br>
MySettingPageAction.java (结绳插件管理中的设置页面实现)</br>

- [省略包名]\controller 文件夹：**(此文件夹为 结绳 插件特有)**</br>
MyActionController.java (用于管理以及加载其他Action)</br>

- [省略包名]\EasyHttp.java：此文件为插件检查更新时的Http请求框架

- [省略包名]\FileUtils.java: 此文件为 util 文件夹中 FileUtils 的补充

# License 
<a href="https://github.com/wystudio001/TiePlugin-AutoBackup/blob/main/LICENSE"><img alt="License" src="https://img.shields.io/badge/license-MIT-orange.svg?style=flat-square"/></a>

根据 MIT 许可证开源
