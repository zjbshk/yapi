package cn.infomany.model;


import cn.infomany.convert.ParamScriptFileConvert;
import com.beust.jcommander.Parameter;

import java.io.File;
import java.util.Arrays;
import java.util.List;

public class CommanderArgs {

    @Parameter(names = {"-h", "--host"}, description = "yapi地址", arity = 1, order = 1, required = true)
    private String host;

    @Parameter(names = {"-e", "--email"}, description = "yapi登录邮箱", arity = 1, order = 2, required = true)
    private String email;

    @Parameter(names = {"-p", "--password"}, description = "yapi登录密码", arity = 1, order = 3, password = true, required = true)
    private String password;

    @Parameter(names = {"-t", "--type"}, description = "导出的类型数组，包含json,markdown,html,Swagger", order = 4)
    private List<String> types = Arrays.asList("html", "json");

    @Parameter(names = {"-pid"}, description = "项目pid", arity = 1, order = 5, required = true)
    private Integer pid;

    @Parameter(names = {"--isWiki"}, description = "是否添加wiki", arity = 1, order = 6)
    private boolean isWiki = true;

    @Parameter(names = {"--override"}, description = "是否覆盖已有文件", arity = 1, order = 7)
    private boolean override = true;

    @Parameter(names = {"-o", "--output-file-name"}, description = "导出文件名称，默认yapi（不带后缀）", arity = 1, order = 8)
    private String outputFileName = "yapi";

    @Parameter(names = {"-O", "--output-log-file"}, description = "log日志输出路径", arity = 1, order = 9)
    private String outputLogFile = "yapi-log.log";

    @Parameter(names = {"-s", "--script-file"}, description = "处理导出文本的脚本文件路径", converter = ParamScriptFileConvert.class, arity = 1, order = 10)
    private File scriptFile;


    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<String> getTypes() {
        return types;
    }

    public void setTypes(List<String> types) {
        this.types = types;
    }

    public Integer getPid() {
        return pid;
    }

    public void setPid(Integer pid) {
        this.pid = pid;
    }

    public boolean isWiki() {
        return isWiki;
    }

    public void setWiki(boolean wiki) {
        isWiki = wiki;
    }

    public String getOutputFileName() {
        return outputFileName;
    }

    public void setOutputFileName(String outputFileName) {
        this.outputFileName = outputFileName;
    }

    public String getOutputLogFile() {
        return outputLogFile;
    }

    public void setOutputLogFile(String outputLogFile) {
        this.outputLogFile = outputLogFile;
    }

    public File getScriptFile() {
        return scriptFile;
    }

    public void setScriptFile(File scriptFile) {
        this.scriptFile = scriptFile;
    }

    public boolean isOverride() {
        return override;
    }

    public void setOverride(boolean override) {
        this.override = override;
    }
}
