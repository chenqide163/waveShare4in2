# waveShare4in2
The java driver of waveShare4in2's e-paper
@[TOC](树莓派 java 驱动 微雪 墨水屏)
作为一个java程序员，习惯了idea的使用，习惯了代码编辑工具的纠错，而且java有很多框架可以使用，实在香，加上我本身也懒，虽然知道python或其他编程语言方便好用，但还是喜欢使用java，简直就是傻瓜式编程，好high，至少我写的代码比较傻瓜。。
# 驱动结果显示
先展示成品吧，感兴趣则继续往后看，本人承诺：我技术不咋地，相信懂java的同学都能看懂我的代码：）
1. 墨水屏官方网站给出的示例，可以看出有灰阶（白，浅黑，深黑，全黑）
![在这里插入图片描述](https://img-blog.csdnimg.cn/20200701001451252.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2NoZW5xaWRlMTYz,size_16,color_FFFFFF,t_70#pic_center =390x240)
2. 傻瓜式二值图像抖动算法，模拟灰阶进行展示，效果能看
![在这里插入图片描述](https://img-blog.csdnimg.cn/20200701001831455.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2NoZW5xaWRlMTYz,size_16,color_FFFFFF,t_70#pic_center =390x240)
3. 又，程序员的示爱
![在这里插入图片描述](https://img-blog.csdnimg.cn/20200701013338677.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2NoZW5xaWRlMTYz,size_16,color_FFFFFF,t_70#pic_center =420x240)
# 官方驱动测试
## 接线
1. 上微雪官网，找到我买的[4.2寸黑白墨水屏的主页](http://www.waveshare.net/wiki/4.2inch_e-Paper_Module)，进入树莓派教程页签：**RPI使用教程**，重点接线图见下，关注红框内的引脚号码，这里写的是Board物理引脚序号：
![在这里插入图片描述](https://img-blog.csdnimg.cn/20200701195748880.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2NoZW5xaWRlMTYz,size_16,color_FFFFFF,t_70#pic_center =600x230)
2. 对应树莓派的引脚见下图，一定要按Board的引脚接线，上图也明确写的Board物理引脚：
![在这里插入图片描述](https://img-blog.csdnimg.cn/2020070120021519.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2NoZW5xaWRlMTYz,size_16,color_FFFFFF,t_70#pic_center =200x400)
## 初步测试
1. 继续在树莓派教程页签（[微雪4.2寸黑白墨水屏的主页](http://www.waveshare.net/wiki/4.2inch_e-Paper_Module)，进入树莓派教程页签：**RPI使用教程**），按指导，打开spi，安装一堆python库，下载测试程序，中间如果遇到什么问题，简单百度一下就可以解决，然后跑python示例
2. **RPI使用教程**中，涉及c语言的跳过就行，**API详解**部分，不感兴趣的同学不看也没大关系，瞄一眼看懂看不懂都行，有个概念最好
3. python跑示例，树莓派控制台输出见：
```shell
pi@raspberrypi:$ python3 epd_4in2_test.py
INFO:root:epd4in2 Demo
INFO:root:init and Clear
INFO:root:1.Drawing on the Horizontal image...
DEBUG:root:Horizontal
......
```
4. 墨水屏跑示例见下，使用3倍速度：
![在这里插入图片描述](https://img-blog.csdnimg.cn/20200701230432747.gif#pic_center =450x)
# java驱动（仅二值图片展示，非灰阶部分）
1. 作为java的忠实拥趸，我还是参考python示例，使用java实现这个4.2寸墨水屏的驱动吧
2. 几个关键方法见下，实际上代码里都有，可以自己下代码详看，我列出来，主要是对部分关键处做总结
3. 代码稍后上传，代码见{此处为代码地址占位符}
4. 对了，介绍下二值图片，也就是像素由0和1组成的图片，0黑1白。本文先讲解二值图片的驱动。灰阶的另开文章讲解。
## 引脚初始化
1. 只要按前述方式接好了引脚，就可以直接使用以下引脚初始化方法
2. BUSY是读入引脚，留意一下
3. spi初始化的时候，需要选择spi模式，这里填的是DEFAULT_SPI_MODE，也就是spi mode 0，为什么是模式0，下边进行解释
	1. 墨水屏是**上升沿触发**，见4.2寸屏幕的数据手册，[这是地址](http://www.waveshare.net/wiki/4.2inch_e-Paper_Module)，进入**资料页签**，下载数据手册
	2. 数据手册中明确提到：	![在这里插入图片描述](https://img-blog.csdnimg.cn/20200701204500152.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2NoZW5xaWRlMTYz,size_16,color_FFFFFF,t_70)
	3. 那为啥是模式0，不是模式1/2/3呢？模式的区分参考**木木总裁**的文章:《[SPI4种模式](https://blog.csdn.net/ll148305879/article/details/91433089)》。也可参考这篇文章：《[Linux下树莓派spi编程](https://blog.csdn.net/TAlice/article/details/83868713)》
```java
final static GpioPinDigitalOutput CS;
final static GpioPinDigitalOutput DC;
final static GpioPinDigitalOutput RST;
final static GpioPinDigitalInput BUSY;
// SPI device
public static SpiDevice spi;

static {
	// in order to use the Broadcom GPIO pin numbering scheme, we need to configure the
	// GPIO factory to use a custom configured Raspberry Pi GPIO provider
	RaspiGpioProvider raspiGpioProvider = new RaspiGpioProvider(RaspiPinNumberingScheme.BROADCOM_PIN_NUMBERING);
	GpioFactory.setDefaultProvider(raspiGpioProvider);

	// create gpio controller
	final GpioController gpio = GpioFactory.getInstance();

	CS = gpio.provisionDigitalOutputPin(RaspiBcmPin.GPIO_08, "CS", PinState.HIGH);
	DC = gpio.provisionDigitalOutputPin(RaspiBcmPin.GPIO_25, "DC", PinState.HIGH);
	RST = gpio.provisionDigitalOutputPin(RaspiBcmPin.GPIO_17, "RST", PinState.HIGH);
	BUSY = gpio.provisionDigitalInputPin(raspiGpioProvider, RaspiBcmPin.GPIO_24, "BUSY");

	try {
		spi = SpiFactory.getInstance(SpiChannel.CS1, //这里我试了，使用CS0/CS1都行，不明白这个到底是什么用处
				SpiDevice.DEFAULT_SPI_SPEED, // default spi speed 1 MHz
				SpiDevice.DEFAULT_SPI_MODE); // default spi mode 0
	} catch (IOException e) {
		e.printStackTrace();
	}
}
```
## 初始化墨水屏
1. 首先，java代码是我参考官方的python示例写的。
2. python示例代码从这里下载（[官网](http://www.waveshare.net/wiki/4.2inch_e-Paper_Module)中的**资料**页签，示例程序下），解压后重点看这两个关键代码文件：驱动代码为RaspberryPi&JetsonNano\python\lib\waveshare_epd\epd4in2.py，主流程代码为RaspberryPi&JetsonNano/python/examples/epd_4in2_test.py
3. 这也符合4.2寸屏幕的数据手册（[这是地址](http://www.waveshare.net/wiki/4.2inch_e-Paper_Module)，进入**资料页签**，下载数据手册），数据手册中对应初始化的部分截图见：
![在这里插入图片描述](https://img-blog.csdnimg.cn/20200701210214437.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2NoZW5xaWRlMTYz,size_16,color_FFFFFF,t_70)
4. 对比以上数据手册截图，以及以下代码，其实就发现相当相近了，我这里展示这些东西出来，是让大家知道我写这个java驱动，参考的是哪些材料，了解整个过程。虽然我看整个数据手册也云里雾里
5. sendCommand这个方法设置的指令，也可以从数据手册中得知，大家可以看下，虽然好几个参数我也没明白啥意思
```java
public static void init() throws IOException, InterruptedException {
	System.out.println("init spi");
	reset();

	sendCommand(0x01); //Power Setting
	sendData(0x03);
	sendData(0x00);
	sendData(0x2b);
	sendData(0x2b);
	sendData(0x09);//python驱动中未写入该值，但是数据手册中写入了该值

	sendCommand(0x06); //Booster Soft Start
	sendData(0x17);
	sendData(0x17);
	sendData(0x17);

	sendCommand(0x04); //Power ON
	readBusy();

	sendCommand(0x00); //Panel Setting
	sendData(0xbf);
	//sendData(0x0d); //python驱动中多写入了0d,不知道是何用意

	sendCommand(0x30); //PLL control
	sendData(0x3c);

	sendCommand(0x61); //Resolution setting
	sendData(0x01);
	sendData(0x90);
	sendData(0x01);
	sendData(0x2c);

	sendCommand(0x82); //VCM_DC Setting
	sendData(0x28);

	sendCommand(0X50); //Vcom and data interval setting
	sendData(0x97);

	setLut(); //设置LUT
}
```
## 点阵排列
1. 我在这个屏幕的数据手册中，没有找到屏幕对应的数据排列方式，但是从驱动的过程中，屏幕中像素与字节的关系应该如下，见截图
2. 这里如果说的不对，请各位大佬指正一下，谢谢
![在这里插入图片描述](https://img-blog.csdnimg.cn/20200701213924951.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2NoZW5xaWRlMTYz,size_16,color_FFFFFF,t_70#pic_center)
## 显示方法
1. 结合以上数据手册LUT后的流程图，就可以理解设置的sendCommand是为什么
2. 结合以上**点阵排列**章节，就能理解为什么可以直接写入的像素字节
```java
public static void display(byte[] pixels) throws IOException, InterruptedException {
	//sendCommand(0x92); //Partial Out,python驱动中有设置这个，奇怪，设置局部刷新？这个指令不设置也是可以的
	setLut();
	sendCommand(0x10);
	for (int i = 0; i < 300 * 400 / 8; i++) {
		sendData(0xFF);
	}
	sendCommand(0x13);
	for (int i = 0; i < 300 * 400 / 8; i++) {
		sendData(pixels[i]);
	}
	sendCommand(0x12);
	readBusy();
}
```
## 清空屏幕
1. 清空屏幕方法同显示方法非常像似，也就是将屏幕全部写1（白），这样屏幕就干净了，不会有墨水屏残影
```java
public static void clear() throws IOException, InterruptedException {
	//sendCommand(0x92);
	setLut();
	sendCommand(0x10);
	for (int i = 0; i < 300 * 400; i++) {
		sendData(0xFF);
	}
	sendCommand(0x13);
	for (int i = 0; i < 300 * 400; i++) {
		sendData(0xFF);
	}
	sendCommand(0x12);
	readBusy();
}
```
## main方法概览
1. 就3条语句，且都已经介绍过
2. getFontImg()这个方法只是插入了一个图像的字节数组，就可以驱动墨水屏输出了
```java
init(); //初始化
clear(); //清空屏幕
display(getBinImg()); //展示图片
```
## PS
以上
1. 代码中关键处已经列出
2. 数据手册的关键流程图已经给出
3. 关键知识点，也就是点阵排列，也给出

后续只要输入二值图片的字节数组，就可以驱动墨水屏显示图片。
后边讲下我瞎写的二值图片抖动算法，其实就是利用随机数啦。
# 获取图像字节数组
## 二值图像模拟灰阶
1. 这个方法，相信你蹲在马桶上都能看懂，方法的主要流程如下
	1. 读取自定义图片，这个图片最好是大于400*300像素的图片
	2. 为了符合4.2寸的墨水屏进行展示，将这个图片缩放为400*300像素。参考的这篇文章：[使用ImageIO和BufferedImage缩放图片](https://blog.csdn.net/linghuainian/article/details/82689201)
	3. 将缩放的图片，转为二值图片，转的过程中，使用随机数，模拟进行灰阶展示
2. 如果有台电脑你可以试下，便宜好用
```java
public static void getBinImg() throws IOException {
	int width = 400;
	int height = 300 ;
	//定义一个BufferedImage对象，用于保存缩小后的位图
	BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
	Graphics graphics = bufferedImage.getGraphics();
	//读取原始位图
	Image srcImage = ImageIO.read(new File("D:\\test.jpg"));
	//将原始位图按墨水屏幕大小缩小后绘制到bufferedImage对象中
	graphics.drawImage(srcImage, 0, 0, width, height, null);
	//将bufferedImage对象输出到磁盘上
	ImageIO.write(bufferedImage, "jpg", new File("D:\\test2.jpg"));
	BufferedImage binImage = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_BINARY);
	for (int i = 0; i < width; i++) {
		for (int j = 0; j < height; j++) {
			int rgb = bufferedImage.getRGB(i, j);
			int oneGate = rgb & 0xffffff;
			int randomNum = new Random().nextInt(0xffffff);
			int binValue = 0;
			//0是黑  1是白 ，或者说数值小就靠近黑色，数值大就靠近白色
			if(oneGate > 0xf2ffff)//大于一定数值，直接用白点，这个值自己调
			{
				binValue = 0xffffff;
			}
			else if(oneGate < 0x900000)//小于一定数值直接用黑点，这个值自己调
			{
				binValue = 0;
			}
			else if(oneGate > randomNum)//关键if,模拟灰阶使用随机数画白点
			{
				binValue = 0xffffff;
			}
			else
			{
				binValue = 0;
			}
			binImage.setRGB(i,j,binValue);
		}
		ImageIO.write(binImage, "jpg", new File("D:\\test3.jpg"));
	}
}
```
3. 效果如下，图片若侵权，请告知，我马上删除。
	1.原图：
	![在这里插入图片描述](https://img-blog.csdnimg.cn/20200701222416854.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2NoZW5xaWRlMTYz,size_16,color_FFFFFF,t_70#pic_center =300x230)
	2.模拟灰阶的二值图片：
	![在这里插入图片描述](https://img-blog.csdnimg.cn/2020070122264287.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2NoZW5xaWRlMTYz,size_16,color_FFFFFF,t_70#pic_center =300x230)
	3. 树莓派展示见下，墨水屏这个屏幕看着非常舒服，暖暖的，对眼睛很讨喜：
![在这里插入图片描述](https://img-blog.csdnimg.cn/20200701223057620.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2NoZW5xaWRlMTYz,size_16,color_FFFFFF,t_70#pic_center =300x230)
## 输出文字内容的图片
1. 其实这个很容易，任意字体，任意文字，想怎么展示怎么展示，见我之前的一篇内容《[树莓派使用java控制ssd1306驱动12864oled显示任意字体任意文字](https://blog.csdn.net/chenqide163/article/details/106933858)》
2. 这里还是给出一个示例，就拿本文最初的那首小诗来说吧：
```java
public static void getFontImage() throws IOException {
	int width = 400;
	int height = 300;
	BufferedImage image = new BufferedImage(400, 300,
			BufferedImage.TYPE_BYTE_BINARY);
	for(int i=0;i<width;i++){
		for(int j=0;j<height;j++){
			//0黑1白，将画布置为白底色
			image.setRGB(i,j,0xffffff);
		}
	}
	Graphics2D g = image.createGraphics();
	g.setFont(new java.awt.Font("微软雅黑", Font.PLAIN, 30));
	g.setColor(new Color(0)); //0黑1白，设置字体为黑色
	g.drawString("WYY,你好哇:", 10, 50);
	g.drawString("待 得 花 信 年 ，", 80,100);
	g.drawString("意 欲 离 阁 否 ？", 80,150);
	g.drawString("明 媚 鲜 妍 时 ，", 80,200);
	g.drawString("粉 黛 嫁  C Q  ！", 80,250);

	File newFile = new File("D:/verse.jpg");
	ImageIO.write(image, "jpg", newFile);
}
```
3. 输出的图像见下，在电脑的彩色显示器上，纯白色很刺眼，但是到了墨水屏上，感觉就很平易近人。墨水屏的效果可以翻到上边去看下。
![在这里插入图片描述](https://img-blog.csdnimg.cn/20200701224107177.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2NoZW5xaWRlMTYz,size_16,color_FFFFFF,t_70#pic_center =180x120)
# 参考资料
代码稍后上传到github，请稍等
# 致谢
谢几百公里外的爱人和老父母，我因外地工作不能陪伴在旁，他们带孩子很辛苦。做个图以表感谢。
![在这里插入图片描述](https://img-blog.csdnimg.cn/20200701001648264.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2NoZW5xaWRlMTYz,size_16,color_FFFFFF,t_70#pic_center =240x190)
