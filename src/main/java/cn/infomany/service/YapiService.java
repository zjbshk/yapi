package cn.infomany.service;

import okhttp3.*;

import java.io.IOException;
import java.util.List;

public class YapiService {

    private String host;

    private String cookies;

    public YapiService(String host) {
        this.host = host;
    }

    private OkHttpClient client = new OkHttpClient();

    public void login(String email, String password) throws IOException {
        String loginInfoJsonStr = String.format("{\"email\":\"%s\",\"password\":\"%s\"}", email, password);
        final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(loginInfoJsonStr, JSON);
        String LOGIN_PATH = "/api/user/login";
        Request request = new Request.Builder().method("POST", body)
                .url(String.format("%s%s", host, LOGIN_PATH))
                .build();
        Response response = client.newCall(request).execute();

        if (!response.isSuccessful()) {
            String msg = String.format("登录失败:code=[%s],body=[%s]", response.code(), response.body());
            throw new RuntimeException(msg);
        }
        response.close();
        List<String> setCookieList = response.headers("Set-Cookie");
        StringBuilder cookieStringBuilder = new StringBuilder();
        for (String setCookie : setCookieList) {
            cookieStringBuilder.append(setCookie).append(";");
        }
        cookies = cookieStringBuilder.toString();
    }

    public String export(String type, Integer pid, Boolean isWiki) throws IOException {
        String exportParams = String.format("type=%s&pid=%d&status=all&isWiki=%b", type, pid, isWiki);

        String EXPORT_PATH = "/api/plugin/export";
        String urlPath = String.format("%s%s?%s", host, EXPORT_PATH, exportParams);
        return basicExport(urlPath);
    }

    public String exportSwagger(Integer pid, Boolean isWiki) throws IOException {
        String type = "OpenAPIV2";
        String exportParams = String.format("type=%s&pid=%d&status=all&isWiki=%b", type, pid, isWiki);

        String EXPORT_SWAGGER_PATH = "/api/plugin/exportSwagger";
        String urlPath = String.format("%s%s?%s", host, EXPORT_SWAGGER_PATH, exportParams);
        return basicExport(urlPath);
    }

    private String basicExport(String urlPath) throws IOException {
        if (cookies == null || cookies.isEmpty()) {
            throw new RuntimeException("请先登录执行此操作");
        }

        Request request = new Request.Builder()
                .addHeader("cookie", cookies)
                .url(urlPath)
                .build();
        Response response = client.newCall(request).execute();

        ResponseBody body = response.body();
        if (!response.isSuccessful() || body == null) {
            String msg = String.format("下载失败:code=[%s],body=[%s]", response.code(), body);
            throw new RuntimeException(msg);
        }
        String bodyStr = body.string();
        response.close();
        return bodyStr;
    }

}
