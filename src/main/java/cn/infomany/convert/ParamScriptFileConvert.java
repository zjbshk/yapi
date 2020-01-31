package cn.infomany.convert;

import com.beust.jcommander.IStringConverter;
import com.beust.jcommander.ParameterException;

import java.io.File;

public class ParamScriptFileConvert implements IStringConverter<File> {
    @Override
    public File convert(String value) {

        File file = new File(value);
        if (!file.exists()) {
            String msg = String.format("文件不存在[%s]", value);
            throw new ParameterException(msg);
        }

        return file;
    }
}
