package cn.dancingsnow.joinmotd.data;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.nio.file.Path;

public class Config extends AbstractConfig<Config.ConfigData> {

    private ConfigData config = new ConfigData();
    public Config(Path dataFolderPath) {
        super(dataFolderPath.resolve("config.json"), ConfigData.class);
    }

    @Override
    protected ConfigData getData() {
        return config;
    }

    @Override
    protected void setData(ConfigData data) {
        config = data;
    }

    public String getServerName() {
        return config.serverName;
    }

    public String getStartDay() {
        return config.startDay;
    }

    public static class ConfigData {
        @SerializedName("server_name")
        private String serverName = "My Server";
        @SerializedName("start_day")
        private String startDay = "2019-1-1";
    }
}
