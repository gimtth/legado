# [English](English.md) [中文](README.md)

> **本项目基于 [gedoor/legado](https://github.com/gedoor/legado) 进行二次开发**
> 
> 在原版阅读 3.0 的基础上，新增了 AI 智能功能，包括章节摘要和智能找书。

[![icon_android](https://github.com/gedoor/gedoor.github.io/blob/master/static/img/legado/icon_android.png)](https://play.google.com/store/apps/details?id=io.legado.play.release)
<a href="https://jb.gg/OpenSourceSupport" target="_blank">
<img width="24" height="24" src="https://resources.jetbrains.com/storage/products/company/brand/logos/jb_beam.svg?_gl=1*135yekd*_ga*OTY4Mjg4NDYzLjE2Mzk0NTE3MzQ.*_ga_9J976DJZ68*MTY2OTE2MzM5Ny4xMy4wLjE2NjkxNjMzOTcuNjAuMC4w&_ga=2.257292110.451256242.1669085120-968288463.1639451734" alt="idea"/>
</a>

<div align="center">
<img width="125" height="125" src="https://github.com/gedoor/legado/raw/master/app/src/main/res/mipmap-xxxhdpi/ic_launcher.png" alt="legado"/>  
  
Legado / 开源阅读
<br>
<a href="https://gedoor.github.io" target="_blank">gedoor.github.io</a> / <a href="https://www.legado.top/" target="_blank">legado.top</a>
<br>
Legado is a free and open source novel reader for Android.
</div>

[![](https://img.shields.io/badge/-Contents:-696969.svg)](#contents) [![](https://img.shields.io/badge/-Function-F5F5F5.svg)](#Function-主要功能-) [![](https://img.shields.io/badge/-AI_Features-00CED1.svg)](#AI-Features-AI功能-) [![](https://img.shields.io/badge/-Community-F5F5F5.svg)](#Community-交流社区-) [![](https://img.shields.io/badge/-API-F5F5F5.svg)](#API-) [![](https://img.shields.io/badge/-Other-F5F5F5.svg)](#Other-其他-) [![](https://img.shields.io/badge/-Grateful-F5F5F5.svg)](#Grateful-感谢-) [![](https://img.shields.io/badge/-Interface-F5F5F5.svg)](#Interface-界面-)

>新用户？
>
>软件不提供内容，需要您自己手动添加，例如导入书源等。
>看看 [官方帮助文档](https://www.yuque.com/legado/wiki)，也许里面就有你要的答案。

# Function-主要功能 [![](https://img.shields.io/badge/-Function-F5F5F5.svg)](#Function-主要功能-)
[English](English.md)

<details><summary>中文</summary>
1.自定义书源，自己设置规则，抓取网页数据，规则简单易懂，软件内有规则说明。<br>
2.列表书架，网格书架自由切换。<br>
3.书源规则支持搜索及发现，所有找书看书功能全部自定义，找书更方便。<br>
4.订阅内容,可以订阅想看的任何内容,看你想看<br>
5.支持替换净化，去除广告替换内容很方便。<br>
6.支持本地TXT、EPUB阅读，手动浏览，智能扫描。<br>
7.支持高度自定义阅读界面，切换字体、颜色、背景、行距、段距、加粗、简繁转换等。<br>
8.支持多种翻页模式，覆盖、仿真、滑动、滚动等。<br>
9.软件开源，持续优化，无广告。
</details>

<a href="#readme">
    <img src="https://img.shields.io/badge/-返回顶部-orange.svg" alt="#" align="right">
</a>

# AI Features-AI功能 [![](https://img.shields.io/badge/-AI_Features-00CED1.svg)](#AI-Features-AI功能-)

本版本在原版阅读 3.0 的基础上，新增了强大的 AI 智能功能，让阅读体验更加智能化。

###  AI 章节摘要

在阅读界面，点击底部菜单栏的 **AI 摘要** 按钮，即可为当前章节生成智能摘要。

**功能特点：**
- 自动生成 100-200 字的章节摘要
- 摘要自动保存，下次查看无需重新生成
- 支持重新生成摘要
- 智能内容截取，保留完整句子
- 支持 DeepSeek、GLM-4、Gemini 三种 AI 服务商

**使用方法：**
1. 进入 **我的** → **AI 设置**
2. 选择 AI 服务商（DeepSeek / GLM-4 / Gemini）
3. 输入对应的 API Key
4. 在阅读界面点击 **AI 摘要** 按钮即可使用

### AI 智能找书

在搜索界面，点击 **AI 智能找书** 入口，与 AI 对话描述你想看的书，AI 会为你推荐合适的小说。

**功能特点：**
- 自然语言对话，描述你的阅读需求
- AI 智能推荐书籍，包含书名、作者、推荐理由和标签
- 点击推荐的书籍，自动跳转搜索
- 对话历史自动保存，随时查看之前的推荐
- 支持清空历史记录

**使用示例：**
- "我想看玄幻升级流小说"
- "推荐一些都市爽文"
- "有没有好看的历史架空小说"

### AI 配置说明

**支持的 AI 服务商：**

1. **DeepSeek** - 性价比高，推荐使用
   - 官网：https://platform.deepseek.com/
   - 价格：输入 ¥1/百万tokens，输出 ¥2/百万tokens

2. **GLM-4（智谱清言）** - 国内服务，稳定可靠
   - 官网：https://open.bigmodel.cn/
   - 价格：根据套餐不同

3. **Gemini（Google）** - 功能强大
   - 官网：https://ai.google.dev/
   - 价格：根据地区和套餐不同

**获取 API Key：**
- 在 **AI 设置** 页面，每个服务商都有对应的帮助教程链接
- 点击即可查看详细的 API Key 获取步骤

### 技术实现

- **API 调用方式**：仅使用 HTTP API，不依赖本地模型
- **数据存储**：摘要保存在本地数据库，对话历史使用 SharedPreferences
- **隐私保护**：API Key 加密存储，仅在本地使用
- **智能截取**：章节内容超过 3000 字时，智能截取并保留完整句子

<a href="#readme">
    <img src="https://img.shields.io/badge/-返回顶部-orange.svg" alt="#" align="right">
</a>

# Community-交流社区 [![](https://img.shields.io/badge/-Community-F5F5F5.svg)](#Community-交流社区-)

#### Telegram
[![Telegram-group](https://img.shields.io/badge/Telegram-%E7%BE%A4%E7%BB%84-blue)](https://t.me/yueduguanfang) [![Telegram-channel](https://img.shields.io/badge/Telegram-%E9%A2%91%E9%81%93-blue)](https://t.me/legado_channels)

#### Discord
[![Discord](https://img.shields.io/discord/560731361414086666?color=%235865f2&label=Discord)](https://discord.gg/VtUfRyzRXn)

#### Other
https://www.yuque.com/legado/wiki/community

<a href="#readme">
    <img src="https://img.shields.io/badge/-返回顶部-orange.svg" alt="#" align="right">
</a>

# API [![](https://img.shields.io/badge/-API-F5F5F5.svg)](#API-)
* 阅读3.0 提供了2种方式的API：`Web方式`和`Content Provider方式`。您可以在[这里](api.md)根据需要自行调用。 
* 可通过url唤起阅读进行一键导入,url格式: legado://import/{path}?src={url}
* path类型: bookSource,rssSource,replaceRule,textTocRule,httpTTS,theme,readConfig,dictRule,[addToBookshelf](/app/src/main/java/io/legado/app/ui/association/AddToBookshelfDialog.kt)
* path类型解释: 书源,订阅源,替换规则,本地txt小说目录规则,在线朗读引擎,主题,阅读排版,添加到书架

<a href="#readme">
    <img src="https://img.shields.io/badge/-返回顶部-orange.svg" alt="#" align="right">
</a>

# Other-其他 [![](https://img.shields.io/badge/-Other-F5F5F5.svg)](#Other-其他-)
##### 免责声明
https://gedoor.github.io/Disclaimer

##### 阅读3.0
* [书源规则](https://mgz0227.github.io/The-tutorial-of-Legado/)
* [更新日志](/app/src/main/assets/updateLog.md)
* [帮助文档](/app/src/main/assets/web/help/md/appHelp.md)
* [web端书架](https://github.com/gedoor/legado_web_bookshelf)
* [web端源编辑](https://github.com/gedoor/legado_web_source_editor)

<a href="#readme">
    <img src="https://img.shields.io/badge/-返回顶部-orange.svg" alt="#" align="right">
</a>

# Grateful-感谢 [![](https://img.shields.io/badge/-Grateful-F5F5F5.svg)](#Grateful-感谢-)
> * org.jsoup:jsoup
> * cn.wanghaomiao:JsoupXpath
> * com.jayway.jsonpath:json-path
> * com.github.gedoor:rhino-android
> * com.squareup.okhttp3:okhttp
> * com.github.bumptech.glide:glide
> * org.nanohttpd:nanohttpd
> * org.nanohttpd:nanohttpd-websocket
> * cn.bingoogolapple:bga-qrcode-zxing
> * com.jaredrummler:colorpicker
> * org.apache.commons:commons-text
> * io.noties.markwon:core
> * io.noties.markwon:image-glide
> * com.hankcs:hanlp
> * com.positiondev.epublib:epublib-core
<a href="#readme">
    <img src="https://img.shields.io/badge/-返回顶部-orange.svg" alt="#" align="right">
</a>

# Interface-界面 [![](https://img.shields.io/badge/-Interface-F5F5F5.svg)](#Interface-界面-)
<img src="https://github.com/gedoor/gedoor.github.io/blob/master/static/img/legado/%E9%98%85%E8%AF%BB%E7%AE%80%E4%BB%8B1.jpg" width="270"><img src="https://github.com/gedoor/gedoor.github.io/blob/master/static/img/legado/%E9%98%85%E8%AF%BB%E7%AE%80%E4%BB%8B2.jpg" width="270"><img src="https://github.com/gedoor/gedoor.github.io/blob/master/static/img/legado/%E9%98%85%E8%AF%BB%E7%AE%80%E4%BB%8B3.jpg" width="270">
<img src="https://github.com/gedoor/gedoor.github.io/blob/master/static/img/legado/%E9%98%85%E8%AF%BB%E7%AE%80%E4%BB%8B4.jpg" width="270"><img src="https://github.com/gedoor/gedoor.github.io/blob/master/static/img/legado/%E9%98%85%E8%AF%BB%E7%AE%80%E4%BB%8B5.jpg" width="270"><img src="https://github.com/gedoor/gedoor.github.io/blob/master/static/img/legado/%E9%98%85%E8%AF%BB%E7%AE%80%E4%BB%8B6.jpg" width="270">

<a href="#readme">
    <img src="https://img.shields.io/badge/-返回顶部-orange.svg" alt="#" align="right">
</a>
