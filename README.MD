
> 之前公司使用`Yapi`(我的建议，哈哈)来管理`API`，但是公司要求提供`API`文档方便与其他项目组对接。好在`Yapi`提供到处`html`接口文档。但是问题来了，请看下面的`html`导出效果。

![](https://upload-images.jianshu.io/upload_images/7473008-75486647e8ca04b9.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
[演示链接](https://hellosean1025.github.io/yapi/openapi.html)

总体还是可以的，虽然不是特别好看，但是还是看的过去的。不过我们项目要求要有搜索功能。为此要编写一个导出工具，在导出时动态的改变`html`的到想要的效果了。本来这里应该开发一个`Yapi`插件的，但是基于其他考虑，最后决定使用`Java`开发。

**总体采用**
`jcommander`解析命令行输入参数。
`okhttp3`进行网络请求。
`jsoup`对`html`进行处理。
`gson`对`json`进行处理。
`groovy`自定义对导出的数据进行处理。


###### 项目结构如下图：
![](https://upload-images.jianshu.io/upload_images/7473008-010b6da0ff39a3a1.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

###### 下面还是展示源代码：
```java

/**
 * @description: yapi导出
 * @author: zhanjinbing
 * @data: 2020-01-19 09:42
 */
public class Main {

    private static CommanderArgs commanderArgs = new CommanderArgs();
    private static String scriptText;
    private static GroovyObject groovyObject;

    public static void main(String[] args) {

        // 显示版本信息
        Optional<String> versionOpt = Arrays.asList(args).stream()
                .filter(arg -> "-v".equals(arg) || "-version".equals(arg))
                .findFirst();

        if (versionOpt.isPresent()) {
            System.out.println("当前版本为：" + commanderArgs.getVersion());
            return;
        }

        JCommander jCommander = JCommander.newBuilder().addObject(commanderArgs).build();
        try {
            jCommander.parse(args);
        } catch (ParameterException e) {
            System.out.println(e.getMessage());
            jCommander.usage();
            return;
        }

        System.out.println(
                        "╭╮╭╮╭━━╮╭━━╮╭━━╮　┌──╮　　┐┐　┐　　┌──┐　╭─┌╯─╮\n" +
                        "┃╰╯┃┃╭╮┃┃╭╮┃╰╮╭╯┌┬──┬┐┌╯├─┼┘└╯╮　│　│││┌┐│\n" +
                        "╰╮╭╯┃╰╯┃┃╰╯┃　┃┃　　│　　│　│││　│　┌─┴─╯┐││││││\n" +
                        "　┃┃　┃╭╮┃┃╭━╯　┃┃　　│　　│　││╯　│　┌─┬──┐││││││\n" +
                        "　┃┃　┃┃┃┃┃┃　　╭╯╰╮　└─┌╯　　│──┼┘├─┼──┤││││││\n" +
                        "　╰╯　╰╯╰╯╰╯　　╰━━╯└──╯─┘╰┘　　┘　╰─┴──┘　┘─╯┘╯\n");
        // 开始运行导出方案
        run();


    }

    private static void run() {

        // 执行登录过程
        YapiService yapiService = new YapiService(commanderArgs.getHost());

        try {
            yapiService.login(commanderArgs.getEmail(), commanderArgs.getPassword());
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("登录失败");
            return;
        }

        YapiProjectInfo yapiProjectInfo;
        try {
            String projectInfo = yapiService.getProjectInfo(commanderArgs.getPid());
            JsonElement jsonElement = JsonParser.parseString(projectInfo);
            JsonObject data = jsonElement.getAsJsonObject().get("data").getAsJsonObject();
            Gson gson = new Gson();
            yapiProjectInfo = gson.fromJson(data, YapiProjectInfo.class);

            String msg = String.format("正在导出...《%s》中的%s", yapiProjectInfo.getName(), commanderArgs.getTypes());
            System.out.println(msg);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("获取项目信息失败");
            return;
        }

        if (commanderArgs.getScriptFile() != null) {
            try {
                scriptText = IOUtils.toString(new FileInputStream(commanderArgs.getScriptFile())
                        , Charset.defaultCharset());
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("脚本文件文本读取异常");
                return;
            }

            GroovyClassLoader classLoader = new GroovyClassLoader();
            Class groovyClass = classLoader.parseClass(scriptText);
            try {
                groovyObject = (GroovyObject) groovyClass.newInstance();
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
                System.out.println("生成java类发生异常");
            }
        }

        // 导出指定类型的文件
        for (String type : commanderArgs.getTypes()) {
            String outputFilePath = String.format("%s%s%s.%s", System.getProperty("user.dir"),
                    File.separator, commanderArgs.getOutputFileName(), type);
            File outFile = new File(outputFilePath);
            if (outFile.exists() && !commanderArgs.isOverride()) {
                System.out.println(String.format("[%s]文件已存在，跳过", outputFilePath));
                continue;
            }

            try {
                String exportStr;
                if ("swagger".equalsIgnoreCase(type)) {
                    exportStr = yapiService.exportSwagger(commanderArgs.getPid(), commanderArgs.isWiki());
                } else {
                    exportStr = yapiService.export(type, commanderArgs.getPid(), commanderArgs.isWiki());
                }

                // 判断是否有脚本要处理文件
                String outPutStr = (scriptText != null ? dealExportScript(type, exportStr) : exportStr);

                // 检查处理后的文本
                if (outPutStr == null) {
                    System.out.println(String.format("[%s]处理后的文本为空，跳过", outputFilePath));
                    continue;
                }

                try (OutputStream outputStream = new FileOutputStream(outFile)) {
                    outputStream.write(outPutStr.getBytes(Charset.defaultCharset()));
                    outputStream.flush();
                    System.out.println(String.format("[%s]文件下载成功", outputFilePath));
                } catch (IOException e) {
                    System.out.println(String.format("[%s]文件写入失败", outputFilePath));
                    throw e;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static String dealExportScript(String type, String exportStr) {
        String methodName = String.format("deal%s", type.toUpperCase());

        String dealStr;
        switch (type) {
            case "swagger":
            case "json":
                JsonElement jsonElement = JsonParser.parseString(exportStr);
                dealStr = invoke(methodName, exportStr, jsonElement);
                break;
            case "html":
                Document doc = Jsoup.parse(exportStr);
                dealStr = invoke(methodName, exportStr, doc);
                break;
            default:
                dealStr = invoke(methodName, exportStr);
                break;
        }
        return dealStr;
    }

    private static String invoke(String function, Object... objects) {
        Optional<MetaMethod> method =
                groovyObject.getMetaClass().getMethods().stream()
                        .filter(metaMethod -> metaMethod.getName().equals(function))
                        .findFirst();

        return (String) (method.isPresent() ?
                groovyObject.invokeMethod(function, objects) :
                objects[0]);
    }
}
```
这里我就只贴`Main`类中的代码吧，减少篇幅。下面是`github`地址
[https://github.com/zjbshk/yapi](https://github.com/zjbshk/yapi)

下面是演示效果：
![](https://upload-images.jianshu.io/upload_images/7473008-0dc6eb347518cc96.gif?imageMogr2/auto-orient/strip)
```
java -jar yapi-1.0-SNAPSHOT.jar -h [Yapi地址] -e [用户名] -p [密码]  -pid [项目id] -o [文件名] -t json -t html -s [script.groovy,脚本路径]
```
![](https://upload-images.jianshu.io/upload_images/7473008-c57a6758a998168a.gif?imageMogr2/auto-orient/strip)
###精美的html导出工具就做好啦。
