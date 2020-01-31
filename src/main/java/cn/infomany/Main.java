package cn.infomany;

import cn.infomany.model.CommanderArgs;
import cn.infomany.service.YapiService;
import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import groovy.lang.GroovyClassLoader;
import groovy.lang.GroovyObject;
import groovy.lang.MetaMethod;
import jdk.nashorn.internal.parser.JSONParser;
import org.apache.commons.io.IOUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.*;
import java.nio.charset.Charset;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;


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
        JCommander jCommander = JCommander.newBuilder().addObject(commanderArgs).build();
        try {
            jCommander.parse(args);
        } catch (ParameterException e) {
            System.out.println(e.getMessage());
            jCommander.usage();
            return;
        }
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
                if ("Swagger".equalsIgnoreCase(type)) {
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
