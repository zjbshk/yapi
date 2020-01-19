package cn.infomany;

import kotlin.text.Charsets;
import okhttp3.*;
import org.apache.commons.io.IOUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.List;


/**
 * @description: yapi导出
 * @author: zhanjinbing
 * @data: 2020-01-19 09:42
 */
public class Main {

    public static void main(String[] args) throws IOException {
        OkHttpClient client = new OkHttpClient();

        // 执行登录操作
        String url = "http://yapi.infomany.cn:3000/api/user/login";
        String json = "{\"email\":\"592466695@qq.com\",\"password\":\"zjb123456\"}";
        final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder().method("POST", body)
                .url(url)
                .build();
        Response response = client.newCall(request).execute();

        if (!response.isSuccessful()) {
            String msg = String.format("登录失败:code=[%s],body=[%s]", response.code(), response.body());
            throw new RuntimeException(msg);
        }

        // 执行下载操作
        url = "http://yapi.infomany.cn:3000/api/plugin/export?type=html&pid=17&status=all&isWiki=true";

        List<String> setCookieList = response.headers("Set-Cookie");
        StringBuilder cookie = new StringBuilder();
        for (String setCookie : setCookieList) {
            cookie.append(setCookie).append(";");
        }
        request = new Request.Builder()
                .addHeader("cookie", cookie.toString())
                .url(url)
                .build();
        response = client.newCall(request).execute();

        if (!response.isSuccessful()) {
            String msg = String.format("下载失败:code=[%s],body=[%s]", response.code(), response.body());
            throw new RuntimeException(msg);
        }

        String inputStream = response.body().string();

//        String rootPath = Main.class.getResource("./").getPath();
//        System.out.println("rootPath = " + rootPath);

        String outputFilePath = System.getProperty("user.dir") + File.separator + "yapi.html";
        File outFile = new File(outputFilePath);
//        try (OutputStream outputStream = new FileOutputStream(outFile);
//             InputStream inputStream = response.body().byteStream()) {
//            IOUtils.copy(inputStream, outputStream);
//        } catch (Exception e) {
//            throw e;
//        }

        String scriptHtml = "<link rel=\"stylesheet\" type=\"text/css\" href=\"js/layui/dist/css/layui.css\"/>\n" +
                "\t\t<script src=\"js/layui/dist/layui.js\" type=\"text/javascript\" charset=\"utf-8\"></script>\n" +
                "\t\t<script src=\"js/index.js\" type=\"text/javascript\" charset=\"utf-8\"></script><link rel=\"stylesheet\" href=\"css/default.css\">\n" +
                "\t\t<script src=\"js/highlight.min.js\"></script>" +
                "<script>hljs.initHighlightingOnLoad();</script>" +
                "<link rel=\"stylesheet\" href=\"css/index.css\">";

        final String DEFAULT_CHARSET = Charsets.UTF_8.toString();
        Document doc = Jsoup.parse(inputStream, DEFAULT_CHARSET);
        Element titleEle = doc.getElementsByTag("title").first();
        titleEle.after(scriptHtml);

        String generateHtml = doc.toString();
        System.out.println("更新成功:" + generateHtml);

        IOUtils.write(generateHtml.getBytes(DEFAULT_CHARSET), new FileOutputStream(outFile));

        System.out.println("文件更新位置:" + outFile.getAbsolutePath());
    }
}
