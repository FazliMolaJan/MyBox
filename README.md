## [User Guide in English](https://mararsh.github.io/MyBox/english_interface.html)

## MyBox：简易工具集

这是利用JavaFx开发的图形化界面程序，目标是提供简单易用的功能。免费开源。

每个版本编译好的包已发布在Release目录下（点击上面的releases页签）。

可以下载exe包，在Windows上无需java环境、无需安装、解包可用：
[MyBox-4.9-exe.zip](https://github.com/Mararsh/MyBox/releases/download/v4.9/MyBox-4.9-exe.zip) 。
双击“MyBox.exe”即可运行MyBox。
可以把图片/文本/PDF文件的打开方式关联到MyBox.exe，这样双击文件名就直接是用MyBox打开了。

在Linux和Mac上缺省有Java环境，因此只提供jar包而未制作平台安装包。


在已安装JRE或者JDK（Java8/9/10）的环境下，可以下载jar包 
[MyBox-4.9-jar.zip](https://github.com/Mararsh/MyBox/releases/download/v4.9/MyBox-4.9-jar.zip) ，执行以下命令来启动程序：
<PRE><CODE>     java   -jar   MyBox-4.9.jar</CODE></PRE>
程序可以跟一个文件名作为参数、以用MyBox直接打开此文件。例如以下命令是打开此图片：
<PRE><CODE>     java   -jar   MyBox-4.9.jar   /tmp/a1.jpg</CODE></PRE>

## 资源地址
[项目主页：https://github.com/Mararsh/MyBox](https://github.com/Mararsh/MyBox)

[源代码和编译好的包：https://github.com/Mararsh/MyBox/releases](https://github.com/Mararsh/MyBox/releases)

[在线提交软件需求和问题报告：https://github.com/Mararsh/MyBox/issues](https://github.com/Mararsh/MyBox/issues)

[云盘地址：https://pan.baidu.com/s/1fWMRzym_jh075OCX0D8y8A#list/path=%2F](https://pan.baidu.com/s/1fWMRzym_jh075OCX0D8y8A#list/path=%2F)

[在线帮助：https://mararsh.github.io/MyBox/mybox_help_zh.html](https://mararsh.github.io/MyBox/mybox_help_zh.html)


## 用户手册
[综述 https://github.com/Mararsh/MyBox/releases/download/v4.8/MyBox-UserGuide-4.8-Overview-zh.pdf](https://github.com/Mararsh/MyBox/releases/download/v4.8/MyBox-UserGuide-4.8-Overview-zh.pdf)

[图像工具 https://github.com/Mararsh/MyBox/releases/download/v4.5/MyBox-UserGuide-4.5-ImageTools-zh.pdf](https://github.com/Mararsh/MyBox/releases/download/v4.5/MyBox-UserGuide-4.5-ImageTools-zh.pdf)

[PDF工具 https://github.com/Mararsh/MyBox/releases/download/v4.8/MyBox-UserGuide-4.8-PdfTools-zh.pdf](https://github.com/Mararsh/MyBox/releases/download/v4.8/MyBox-UserGuide-4.8-PdfTools-zh.pdf)

[桌面工具 https://github.com/Mararsh/MyBox/releases/download/v4.8/MyBox-UserGuide-4.8-DesktopTools-zh.pdf](https://github.com/Mararsh/MyBox/releases/download/v4.8/MyBox-UserGuide-4.8-DesktopTools-zh.pdf)

[网络工具 https://github.com/Mararsh/MyBox/releases/download/v3.9/MyBox-UserGuide-3.9-NetworkTools-zh.pdf](https://github.com/Mararsh/MyBox/releases/download/v3.9/MyBox-UserGuide-3.9-NetworkTools-zh.pdf)


## 当前版本
当前是版本4.9。我家急事，来不及更新4.9的文档和介绍，以后补上。

版本4.8已实现的特点：
```
1. PDF工具：
	A. 以图像模式查看PDF文件，可以设置dpi以调整清晰度，可以把页面剪切保存为图片。
	B. 将PDF文件的每页转换为一张图片，包含图像密度、色彩、格式、压缩、质量、色彩转换等选项。
	C. 将多个图片合成PDF文件，可以设置压缩选项、页面尺寸、页边、页眉、作者等。
	  支持中文，程序自动定位系统中的字体文件，用户也可以输入ttf字体文件路径。
	D. 压缩PDF文件的图片，设置JPEG质量或者黑白色阈值。
	E. 合并多个PDF文件。
	F. 分割PDF文件为多个PDF文件，可按页数或者文件数来均分，也可以设置起止列表。
	G. 将PDF中的图片提取出来。可以指定页码范围。
	H. 将PDF文件中的文字提取出来，可以定制页的分割行。
	I. PDF的批量处理。
	J. 可设置PDF处理的主内存使用量。
2. 图像工具：
	A. 图像处理
		1）调整图像的大小：按比例收缩、或设置像素。四种保持宽高比的选项。
		2）调整图像的颜色：针对红/蓝/绿/黄/青/紫通道、饱和度、明暗、色相，
			进行增加、减少、设值、过滤、取反色的操作
		3）制作效果：模糊、清晰、锐化、浮雕、边沿检测、海报（减色）、阈值化、灰色、黑白色、褐色。
			也可以通过定义和选择卷积核来制作效果。
		4）其它图像处理操作：剪裁、文字、覆盖（马赛克/磨砂/图片）、圆角、阴影、斜拉、镜像、旋转、切边、加边。
			多种参数可设置。
		5）图像操作的范围：全部、矩形、圆形、抠图、颜色匹配、矩形中颜色匹配、圆形中颜色匹配。
			颜色匹配可以针对：红/蓝/绿通道、饱和度、明暗、色相，色距可定义。
			范围可作用于：颜色调整、效果、和卷积。可简单通过左右键点击来确定范围，
			范围的参数（如抠图的点集和颜色集合）可设置。均可反选。
		6）对上一步的撤销和重做。也可以随时恢复原图。修改历史可自动保存，以便退回到前面的修改；
			可设置修改历史的个数。
		7）同目录下图像的导览。
		8）选择是否显示对照图。可以选择其它图片为对照图。
	B. 图像的批量处理。
	C. 多帧图像文件
		1）查看、提取多帧图像文件
		2）创建、编辑多帧tiff文件
		3）查看/提取/创建/编辑动画Gif文件。可设置间隔、是否循环、图片尺寸
	D. 多图合一
		1）图片的合并。支持排列选项、背景颜色、间隔、边沿、和尺寸选项。
		2）图片的混合。支持选择交叉区域、多种混合模式。
		3）将多个图片合成PDF文件
	E. 图像局部化
		1）图像的分割。支持均等分割和定制分割。可以保存为多个图像文件、多帧Tiff文件、或者PDF。
		2）图像的降采样。可以设置采样区域、采样比例。
	F. 大图片的处理
		1）评估加载整个图像所需内存,判断能否加载整个图像。
		2）若可用内存足够载入整个图像，则读取图像所有数据做下一步处理。尽可能内存操作而避免文件读写。
		3）若内存可能溢出，则采样读取图像数据做下一步处理。
		4）采样比的选择：即要保证采样图像足够清晰、又要避免采样数据占用过多内存。
		5）采样图像主要用于显示图像。已被采样的大图像，不适用于图像整体的操作和图像合并操作。
		6）一些操作，如分割图像、降采样图像，可以局部读取图像数据、边读边写，因此适用于大图像：显示的是采样图像、而处理的是原图像。
	G. 可将图像或图像的选中部分复制到系统粘贴板（Ctrl-c）。
	H. 查看图像的元数据和属性。
	I. 同屏查看多图，可以分别或者同步旋转和缩放。支持导览。
	J. 将图片转换为其它格式，包含色彩、长宽、压缩、质量等选项。
	K. 调色盘
	L. 像素计算器
	M.卷积核管理器
	N.快捷键
3. 文件和目录工具：
	A. 目录/文件重命名，包含文件名和排序的选项。被重命名的文件可以全部恢复或者指定恢复原来的名字。
	B. 目录同步，包含复制子目录、新文件、特定时间以后已修改文件、原文件属性，以及删除源目录不存在文件和目录，等选项。
	C. 整理文件，将文件按修改时间或者生成时间重新归类在新目录下。
	   此功能可用于处理照片、游戏截图、和系统日志等需要按时间归档的批量文件。
	D. 编辑文本
		1) 自动检测或手动设置文件编码；设置新字符集以实现转码；支持BOM设置。
		2) 自动检测换行符；转换换行符；显示行号。支持LF（Unix/Linux）、CR（Apple）、CRLF（Windows）。
		3) 查找与替换。可只本页查找、或整个文件查找。计数功能。
		4) 定位。跳转到指定的字符位置或行号。
		5) 行过滤。条件：“包含任一”、“不含所有”、“包含所有”、“不含任一”。可累加过滤。可保存过滤结果。可选是否包含行号。
		6) 字符集对应的编码：字节的十六进制同步显示、同步滚动、同步选择。
		7) 分页。可用于查看和编辑非常大的文件，如几十G的运行日志。
			（1）设置页尺寸。
			（2）页面导航。
			（3）先加载显示首页，同时后端扫描文件以统计字符数和行数；统计期间部分功能不可用；统计完毕自动刷新界面。			
			（4）对于跨页字符串，确保查找、替换、过滤的正确性。
		8) 通用的编辑功能（复制/粘贴/剪切/删除/全选/撤销/重做/恢复）及其快捷键。
	E. 批量转换文件的字符集。
	F. 批量转换文件的换行符。
	G. 编辑字节
		1）字节被表示为两个十六进制字符。所有空格、换行、非法值将被忽略。
		2）常用ASCII字符的输入选择框。
		3）换行。仅用于显示、无实际影响。显示行号。可按字节数换行、或按一组字节值来换行。
		4）查找与替换。可只本页查找/替换、或整个文件查找/替换。计数功能。
		5）定位。跳转到指定的字节位置或行号。
		6）行过滤。条件：“包含任一”、“不含所有”、“包含所有”、“不含任一”。可累加过滤。可保存过滤结果。可选是否包含行号。
		7）选择字符集来解码：同步显示、同步滚动、同步选择。非字符显示为问号。
		8）分页。可用于查看和编辑非常大的文件，如几十G的二进制文件。
			（1）可以设置页尺寸。
			（2）页面导航。
			（3）先加载显示首页，同时后端扫描文件以统计字节数和行数；统计期间部分功能不可用；统计完毕自动刷新界面。
			（4）对于跨页字节组，确保查找、替换、过滤的正确性。若按字节数换行，则行过滤时不考虑跨页。
		9）通用的编辑功能（复制/粘贴/剪切/删除/全选/撤销/重做/恢复）及其快捷键。
	H. 切割文件。切割方式可以是：按文件数、按字节数、或按起止列表。
	I. 合并文件。 
4. 网络工具：
	A. 网页编辑器
		1）以富文本方式编辑本地网页或在线网页。（不支持FrameSet）
		2）直接编辑HTML代码。（支持FrameSet）
		3）网页浏览器显示编辑器内容、或在线网页。
			支持前后导览、缩放字体、截图页面为整图或者PDF文件。
		4）富文本页面、HTML代码、浏览器内容，这三者自动同步。
	B. 微博截图工具
		1）自动保存任意微博账户的任意月份的微博内容
		2）设置起止月份。
		3）确保页面完全加载，可以展开页面包含的评论、可以展开页面包含的所有图片。
		4）将页面保存为本地html文件。由于微博是动态加载内容，本地网页无法正常打开，仅供获取其中的文本内容。
		5）将页面截图保存为PDF。可以设置页尺寸、边距、作者、以及图片格式。
		6）将页面包含的所有图片的原图全部单独保存下来。
		7）实时显示处理进度。
		8）可以随时中断处理。程序自动保存上次中断的月份并填入作本次的开始月份。
		9）可以设置错误时重试次数。
5. 支持图像格式：png,jpg,bmp,tif,gif,wbmp,pnm,pcx。
6. 闹钟，包括时间选项和音乐选项，支持铃音“喵”、wav铃音、和MP3铃音，可以在后端运行。
7. 设置：切换语言、是否显示注释、PDF处理选项、图像处理选项、退出程序时是否关闭闹钟、清除个人设置，以及更多的参数设置。
8. 多种界面皮肤。
9. 国际化。实时切换。目前支持中文、英文。扩展语言只需编写资源文件。
```

## 开发日志
```
2019-2-20 版本4.9 图像对比度处理，可选算法。
图像的统计数据分析，包含各颜色通道的均值/方差/斜率/中值/众数以及直方图。
系统粘贴板内图像的记录工具。
（稍后更新准确的描述）

2019-1-29 版本4.8 以图像模式查看PDF文件，可以设置dpi以调整清晰度，可以把页面剪切保存为图片。
文本/字节编辑器的“定位”功能：跳转到指定的字符/字节位置、或跳转到指定的行号。
切割文件：按文件数、按字节数、或按起止列表把文件切割为多个文件。
合并文件：把多个文件按字节合并为一个新文件。
程序可以跟一个文件名作为参数，以用MyBox直接打开此文件。
在Windows上可以把图片/文本/PDF文件的打开方式缺省关联到MyBox.exe，可以在以双击文件时直接用MyBox打开。

2019-1-15 版本4.7  编辑字节：常用ASCII字符的输入选择框；按字节数、或按一组字节值来换行；查找与替换，本页或整个文件，计数功能；
行过滤，“包含任一”、“不含所有”、“包含所有”、“不含任一”，累加过滤，保存过滤结果，是否包含行号；
选择字符集来解码，同步显示、同步滚动、同步选择；
分页，可用于查看和编辑非常大的文件，如几十G的二进制文件，设置页尺寸，对于跨页字节组，确保查找、替换、过滤的正确性。
批量改变文件的换行符。
合并“文件重命名”和“目录文件重命名”。
图像模糊改为“平均模糊”算法，它足够好且更快。

2018-12-31 版本4.6  编辑文本：自动检测换行符；转换换行符；支持LF（Unix/Linux）、CR（iOS）、CRLF（Windows）。
查找与替换，可只本页查找、或整个文件查找。
行过滤，匹配类型：“包含字串之一”、“不包含所有字串”，可累加过滤，可保存过滤结果。
分页：可用于查看和编辑非常大的文件，如几十G的运行日志；可设置页尺寸；对于跨页字符串确保查找、替换、过滤的正确性。
先加载显示首页，同时后端扫描文件以统计字符数和行数；统计期间部分功能不可用；统计完毕自动刷新界面。
进度等待界面添加按钮“MyBox”和“取消”，以使用户可使用其它功能、或取消当前进程。

2018-12-15 版本4.5 文字编码：自动检测或手动设置文件编码；设置目标文件编码以实现转码；支持BOM设置；
十六进制同步显示、同步选择；显示行号。批量文字转码。
图像分割支持按尺寸的方式。
可将图像或图像的选中部分复制到系统粘贴板（Ctrl-c）。
在查看图像的界面可裁剪保存。

2018-12-03 版本4.4 多帧图像文件的查看、提取、创建、编辑。支持多帧Tiff文件。
对于所有以图像文件为输入的操作，处理多帧图像文件的情形。
对于所有以图像文件为输入的操作，处理极大图像（加载所需内存超出可用内存）的情形。自动评估、判断、给出提示信息和下一步处理的选择。
对于极大图像，支持局部读取、边读边写的图像分割，可保存为多个图像文件、多帧Tiff、或者PDF。
对于极大图像，支持降采样。可设置采样区域和采样比率。

2018-11-22 版本4.3 支持动画Gif。查看动画Gif：设置间隔、暂停/继续、显示指定帧并导览上下帧。
提取动画Gif：可选择起止帧、文件类型。
创建/编辑动画Gif：增删图片、调整顺序、设置间隔、是否循环、选择保持图片尺寸、或统一设置图片尺寸、另存，所见即所得。
更简洁更强力的图像处理“范围”：全部、矩形、圆形、抠图、颜色匹配、矩形中颜色匹配、圆形中颜色匹配；
颜色匹配可针对：红/蓝/绿通道、饱和度、明暗、色相；可方便地增减抠图的点集和颜色列表；均可反选。
归并图像处理的“颜色”、“滤镜”、“效果”、“换色”，以减少界面选择和用户输入。
多图查看界面：可调整每屏文件数；更均匀地显示图片。

2018-11-13 版本4.2 图像处理的“范围”：全部、抠图、矩形、圆形、色彩匹配、色相匹配、矩形/圆形累加色彩/色相匹配。
“抠图”如PhotoShop的魔术棒或者windows画图的油漆桶。
“范围”可应用于：色彩增减、滤镜、效果、卷积、换色。可简单通过左右键点击来确定范围。
卷积管理器：可自动填写高斯分布值；添加处理边缘像素的选项。
目录重命名：可设置关键字列表来过滤要处理的文件。
调整和优化图像处理的代码。
更多的快捷键。

2018-11-08 版本4.1 图像的“覆盖”处理。可在图像上覆盖：矩形马赛克、圆形马赛克、矩形磨砂玻璃、圆形磨砂玻璃、或者图片。
可设置马赛克或磨砂玻璃的范围和粒度；可选内部图片或用户自己的图片；可设置图片的大小和透明度。
图像的“卷积”处理。可选择卷积核来加工图像。可批量处理。
卷积核管理器。自定义（增/删/改/复制）图像处理的卷积核，可自动归一化，可测试。提供示例数据。
图像滤镜：新增黄/青/紫通道。

2018-11-04 版本4.0  图像色彩调整：新增黄/青/紫通道。尤其黄色通道方便生成“暖色”调图片。
图像滤镜：新增“褐色”。可以生成怀旧风格的图片。
图像效果：新增“浮雕”，可以设置方向、半径、是否转换为灰色。
图像的混合：可设置图像交叉位置、可选择多种常用混合模式。
在线帮助：新增一些关键信息。

2018-10-26 版本3.9  内嵌Derby数据库以保存程序数据；确保数据正确从配置文件迁移到数据库。
图像处理：保存修改历史，以便退回到前面的修改；用户可以设置历史个数。
用户手册的英文版。

2018-10-15 版本3.8 优化代码：拆分图像处理的大类为各功能的子类。
优化界面控件，使工具更易使用。设置快捷键。
图像处理添加三个滤镜：红/蓝/绿的单通道反色。水印文字可以设置为“轮廓”。

2018-10-09 版本3.7 微博截图工具：利用Javascript事件来依次加载图片，确保最小间隔以免被服务器判定为不善访问，
同时监视最大加载间隔以免因图片挂了或者加载太快未触发事件而造成迭代中断。
图像处理“效果”：模糊、锐化、边沿检测、海报（减色）、阈值化。

2018-10-04 版本3.6 微博截图工具：继续调优程序逻辑以确保界面图片全部加载；整理代码以避免内存泄露。
降低界面皮肤背景的明亮度和饱和度。
在文档中添加关于界面分辨率的介绍。

2018-10-01 版本3.5 微博截图工具：调优程序逻辑，以确保界面图片全部加载。
提供多种界面皮肤。

2018-09-30 版本3.4 修正问题：1）微博截图工具，调整页面加载完成的判断条件，以保证页面信息被完整保存。
2）关闭/切换窗口时若任务正在执行，用户选择“取消”时应留在当前窗口。
新增功能：1）可以设置PDF处理的最大主内存和临时文件的目录；2）可以清除个人设置。

2018-09-30 版本3.3 最终解决微博网站认证的问题。已在Windows、CentOS、Mac上验证。

2018-09-29 版本3.2 微博截图功能：1）在Linux和Windows上自动导入微博证书而用户无需登录可直接使用工具。
但在Mac上没有找到导入证书的途径，因此苹果用户只好登录以后才能使用。
2）可以展开页面上所有评论和所有图片然后截图。
3）可以将页面中所有图片的原图保存下来。（感觉好酷）

2018-09-26 版本3.1 所有图像操作都可以批量处理了。修正颜色处理算法。
设置缺省字体大小以适应屏幕分辨率的变化。用户手册拆分成各个工具的分册了。
提示用户：在使用微博截图功能之前需要在MyBox浏览器里成功登录一次以安装微博证书、
（正在寻求突破这一限制的办法。Mybox没有兴趣接触用户个人信息）。

2018-09-18 版本3.0 微博截图工具：可以只截取有效内容（速度提高一倍并且文件大小减小一半）、
可以展开评论（好得意这个功能！）、可以设置合并PDF的最大尺寸。
修正html编辑器的错误并增强功能。

2018-09-17 版本2.14 微博截图工具：设置失败时重试次数、以应对网络状况很糟的情况；
当某个月的微博页数很多时，不合并当月的PDF文件，以避免无法生成非常大的PDF文件的情况（有位博主一个月发了36页微博~）。。

2018-09-15 版本2.13 分开参照图和范围图。确保程序退出时不残留线程。批量PDF压缩图片。
微博截图工具：自动保存任意微博账户的所有微博内容，可以设置起止月份，可以截图为PDF、也可以保存html文件
（由于微博是动态加载内容，本地网页无法正常打开，仅供获取其中的文本内容）。
如果微博修改网页访问方式，此工具将可能失效。

2018-09-11 版本2.12 合并多个图片为PDF文件、压缩PDF文件的图片、合并PDF、分割PDF。 
支持PDF写中文，程序自动定位系统中的字体文件，用户也可以输入ttf字体文件路径。 
提示信息的显示更平滑友好。网页浏览器：字体缩放，设置截图延迟、截图可保存为PDF。

2018-09-06 版本2.11 图片的合并，支持排列选项、背景颜色、间隔、边沿、和尺寸选项。
网页浏览器，同步网页编辑器，把网页完整内容保存为一张图片。图片处理：阴影、圆角、加边。
确保大图片处理的正确性和性能。

2018-08-11 版本2.10 图像的分割，支持均等分割个和定制分割。使图像处理的“范围”更易用。
同屏查看多图不限制文件个数了。

2018-08-07 版本2.9 图像的裁剪。图像处理的“范围”：依据区域（矩形或圆形）和颜色匹配，可用于局部处理图像。

2018-07-31 版本2.8 图像的切边、水印、撤销、重做。Html编辑器、文本编辑器。

2018-07-30 版本2.7 图像的变形：旋转、斜拉、镜像。

2018-07-26 版本2.6 增强图像的换色：可以选择多个原色，可以按色彩距离或者色相距离来匹配。支持透明度处理。

2018-07-25 版本2.5 调色盘。图像的换色：可以精确匹配颜色、或者设置色距，此功能可以替换图像背景色、或者清除色彩噪声。

2018-07-24 版本2.4 完善图像处理和多图查看：平滑切换、对照图、像素调整。

2018-07-18 版本2.3 闹钟，包括时间选项和音乐选项，支持wav铃音和MP3铃音，可以在后端运行。感谢我家乖乖贡献了“喵”。

2018-07-11 版本2.2 修正线程处理逻辑的漏洞。整理文件，将文件按修改时间或者生成时间重新归类在新目录下。
此功能可用于处理照片、游戏截图、和系统日志等需要按时间归档的批量文件。

2018-07-09 版本2.1 完善图片处理的界面，支持导览。
目录同步，包含复制子目录、新文件、特定时间以后已修改文件、原文件属性，以及删除源目录不存在文件和目录，等选项。

2018-07-06 版本2.0 批量提取PDF文字、批量转换图片。
目录文件重命名，包含文件名和排序的选项，被重命名的文件可以全部恢复或者指定恢复原来的名字。

2018-07-03 版本1.9 修正问题。提取PDF文字时可以定制页分割行。
完善图像处理：参数化调整饱和度、明暗、色相；滤镜：灰色、反色、黑白色。

2018-07-01 版本1.8 将PDF文件中的文字提取出来。处理图片：调整饱和度、明暗，或者转换为灰色、反色。

2018-06-30 版本1.7 完善像素计算器。支持同屏查看最多十张图，可以分别或者同步旋转和缩放。

2018-06-27 版本1.6 将图片转换为其它格式，支持色彩、长宽、压缩、质量等选项。
提供像素计算器。新增图像格式：gif, wbmp, pnm, pcx。

2018-06-24 版本1.5 提取PDF中的图片保存为原格式。
支持批量转换和批量提取。感谢 “https://shuge.org/” 的帮助：书格提出提取PDF中图片的需求。

2018-06-21 版本1.4 读写图像的元数据,目前支持图像格式：png, jpg, bmp, tif。
感谢 “https://shuge.org/” 的帮助：书格提出图像元数据读写的需求。

2018-06-15 版本1.3 修正OTSU算法的灰度计算；优化代码：提取共享部件；支持PDF密码；使界面操作更友好。

2018-06-14 版本1.2 针对黑白色添加色彩转换的选项；自动保存用户的选择；优化帮助文件的读取。
感谢 “https://shuge.org/” 的帮助：书格提出二值化转换阈值的需求。

2018-06-13 版本1.1 添加：转换格式tiff和raw，压缩和质量选项，以及帮助信息。
感谢 “https://shuge.org/” 的帮助：书格提出tiff转换的需求。

2018-06-12 版本1.0 实现功能：将PDF文件的每页转换为一张图片，包含图像密度、类型、格式等选项，并且可以暂停/继续转换过程。
```

## 实现基础
MyBox使用NetBeans 8.2和JavaFX Scene Builder 2.0开发：

[https://netbeans.org/](https://netbeans.org/)

[https://www.oracle.com/technetwork/java/javafxscenebuilder-1x-archive-2199384.html](https://www.oracle.com/technetwork/java/javafxscenebuilder-1x-archive-2199384.html)


基于以下开源软件/开源库：

[JavaFx  https://docs.oracle.com/javafx/2/](https://docs.oracle.com/javafx/2/)
	
[PDFBox  https://pdfbox.apache.org/](https://pdfbox.apache.org/)
	
[jai-imageio  https://github.com/jai-imageio/jai-imageio-core](https://github.com/jai-imageio/jai-imageio-core)
	
[javazoom  http://www.javazoom.net/index.shtml](http://www.javazoom.net/index.shtml)
	
[log4j   https://logging.apache.org/log4j/2.x/](https://logging.apache.org/log4j/2.x/)
	
[Derby   http://db.apache.org/derby/](http://db.apache.org/derby/)

[GifDecoder   https://github.com/DhyanB/Open-Imaging/](https://github.com/DhyanB/Open-Imaging/)

[EncodingDetect  https://www.cnblogs.com/ChurchYim/p/8427373.html](https://www.cnblogs.com/ChurchYim/p/8427373.html)


## 主界面
![About](https://mararsh.github.io/MyBox/0.png)


