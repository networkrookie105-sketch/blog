---
version: alpha
colors:
  background: '#f6f8fb'
  paper: '#ffffff'
  ink: '#15253d'
  muted: '#52627a'
  border: '#dbe3ed'
  primary: '#2457ca'
  soft: '#eaf0fc'
typography:
  display:
    fontFamily: 'Avenir Next, PingFang SC, Microsoft YaHei, sans-serif'
  body:
    fontFamily: 'PingFang SC, Microsoft YaHei, sans-serif'
  utility:
    fontFamily: 'SFMono-Regular, Consolas, monospace'
rounded:
  card: '12px'
  button: '7px'
spacing:
  section: '56px'
  card: '24px'
components:
  projectCard:
    backgroundColor: '{colors.paper}'
    textColor: '{colors.ink}'
    rounded: '{rounded.card}'
    padding: '{spacing.card}'
---
# Personal portfolio

## Overview
中文开发者个人窗口，面向招聘者、技术同行与合作伙伴。首页介绍人物，项目提供能力证据，文章承载持续表达。虚构的林序、公司、经历和示例文章必须明确标注，用户可在后台更换并移除示例标识；演示内容仅作为 admin 数据库种子。运行时个人资料和项目从 /site 获取，src/content/profile.js 只负责动态状态与路径；不以静态人物数据回退，不编造真实履历或外链。

## Colors
前台改为浅灰纸面、深蓝文字与克制的蓝色强调。运行时规范值在 rookie-blog-vue3/src/assets/portfolio.css 的 --personal-* 变量中，Header、Footer、首页及作品页面共享。后台管理页继续沿用 main.css 的深色主题；登录页为浅色入口，输入框、标签与按钮在 login.vue 内使用 --personal-* 变量，不继承深色白字覆盖。旧博客页通过 .site-dark 兼容层使用前台浅色基调。

## Typography
系统字体避免外部字体下载。标题 38–56px，正文 14–16px，中文行高 1.9；等宽字体只用于日期、技术标识。首页的纸面工作笔记是视觉签名，以真实内容和排版体现工程师身份，不使用虚假人物照片。

## Layout
最大宽度 1120px，桌面留边 32px；手机留边 18px。首屏两列，手机单列。项目三列在 900px 以下改为单列。页面随文档滚动，保留可操作滚动条；标题、图片和错误区域不得挤压主要操作。

## Elevation & Depth
页面以边框和留白区分层级。仅首屏工作笔记使用小角度倾斜和纸张阴影；项目卡片只在 hover 轻微上移。

## Shapes
卡片 12px，按钮 7px。图标使用现有 Element Plus 图标，不添加素材依赖。

## Components
导航：首页、项目、文章、关于我，联系为单独入口。个人博客只展示站长内容，公开导航不提供注册或多用户入口；后台登录仅放在页脚，登录后进入 /admin/index。个人资料 /admin/profile、项目管理 /admin/projects、站点设置 /admin/blog/settings 采用现有深色管理员布局。个人资料归本人身份、头像、经历、技能与个人链接，站点设置归博客品牌、网站描述、图标与页脚；各自独立保存。项目采用列表和编辑弹窗，逐条保存、公开、首页精选及排序；旧 /admin/site 跳转个人资料。不得复用前台 .site-dark、page-title 或 inner-page 样式在后台形成浅色大标题；表单、卡片、工具栏使用 Element Plus 深色变量，admin-editor.css 只定义共享布局。文章使用 /admin/article/list 的完整 Markdown 后台，分类和标签有独立管理页。文章列表、首页最新文章、搜索、分类、标签、归档和详情全部读取同一套已发布文章数据，草稿不可公开；归档按时间自动生成，不能受置顶排序影响。个人资料和项目保留版本号保存；旧 notes JSON 仅作为迁移备份，不再提供另一套文章编辑。封面可选，无封面文章以文字正常展示。文章搜索与分页保存在 URL，读取失败显示重试，不回退到示例内容。旧 /notes/slug 链接通过迁移映射跳转文章详情。简历下载读取当前后台资料，示例标识由后台控制。

## Do's and Don'ts
以语义链接导航，以按钮执行动作。所有交互提供键盘焦点与 hover，移动菜单标注展开状态，遵守 reduced-motion。不要使用技能百分比、假业务指标、假备案号或未经授权的外部发送。个人主页与后台认证及数据库彼此独立。

个人主页公开由站点设置 personalHomepagePublic 控制。关闭时首页采用博客名称、网站简介与文章列表，不展示个人英雄区、作品、作者卡片、简历与联系入口；个人页面直达展示未公开提示。开关沿用 Element Plus 表单样式与显式保存，不新增视觉体系。后台提供仅管理员可见的已保存内容预览。
