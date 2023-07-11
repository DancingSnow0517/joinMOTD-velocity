package cn.dancingsnow.joinmotd.data;

import cn.dancingsnow.joinmotd.JoinMOTD;
import com.google.gson.JsonParseException;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public abstract class AbstractConfig<T> {
    protected final Path path;

    private final Class<T> dataType;

    public AbstractConfig(Path path, Class<T> dataType) {
        this.path = path;
        this.dataType = dataType;
    }

    protected abstract T getData();


    protected abstract void setData(T data);

    public boolean save() {
        if (!Files.exists(path)) {
            try {
                Files.createFile(path);
            } catch (IOException e) {
                e.printStackTrace();
                JoinMOTD.logger().error("Save {} error: createFile fail.", path);
                return false;
            }
        }
        BufferedWriter bfw;
        try {
            bfw = Files.newBufferedWriter(path, StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
            JoinMOTD.logger().error("Save {} error: newBufferedWriter fail.", path);
            return false;
        }

        try {
            bfw.write(JoinMOTD.GSON.toJson(getData()));
            bfw.close();
        } catch (IOException e) {
            e.printStackTrace();
            JoinMOTD.logger().error("Save {} error: bfw.write fail.", path);
            return false;
        }
        return true;
    }

    public boolean load() {
        if (!Files.exists(path)) {
            return save();
        }
        try {
            BufferedReader bfr = Files.newBufferedReader(path, StandardCharsets.UTF_8);
            setData(JoinMOTD.GSON.fromJson(bfr, dataType));
            bfr.close();
        } catch (IOException e) {
            e.printStackTrace();
            JoinMOTD.logger().error("Load {} error: newBufferedReader fail.", path);
            return false;
        } catch (JsonParseException e) {
            JoinMOTD.logger().error("Json {} parser fail!!", path);
            return false;
        }
        return true;
    }
}
