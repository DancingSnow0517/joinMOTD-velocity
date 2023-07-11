package cn.dancingsnow.joinmotd;

import cn.dancingsnow.joinmotd.data.Config;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.player.ServerPostConnectEvent;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.io.File;
import java.nio.file.Path;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

@Plugin(
        id = "join_motd",
        name = "joinMOTD",
        version = "1.0.0"
)
public class JoinMOTD {

    @Inject
    private Logger logger;
    @Inject
    public ProxyServer server;
    @Inject
    public Injector injector;
    @Inject
    public CommandManager commandManager;


    @Inject
    @DataDirectory
    public Path dataFolderPath;

    @Nullable
    private static JoinMOTD instance;

    public static Config config;

    public static Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
    private static final DateFormat format = new SimpleDateFormat("yyyy-MM-dd");

    @NotNull
    public static JoinMOTD getInstance() {
        return Objects.requireNonNull(instance);
    }

    public static Logger logger() {
        return getInstance().logger;
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        instance = this;

        File dataFolder = dataFolderPath.toFile();
        if (!dataFolder.exists() && !dataFolder.isDirectory()) {
            if (dataFolder.mkdir()) {
                logger.info("Directory create success");
            }
        }
        config = new Config(dataFolderPath);
        if (!config.load()) {
            logger.error("joinMOTD load fail");
            throw new IllegalStateException("joinMOTD init fail");
        }
        config.save();

        JoinMOTDCommand.init(this);

        logger.info("init");
    }

    @Subscribe
    public void onServerPostConnectEvent(ServerPostConnectEvent event) {
        Date startDay;
        try {
            startDay = format.parse(config.getStartDay());
        } catch (ParseException e) {
            e.printStackTrace();
            logger.error("error format for startDay");
            startDay = new Date();
        }
        Date endDay = new Date();
        Long num = endDay.getTime() - startDay.getTime();

        var currentServer = event.getPlayer().getCurrentServer();
        String currentServerName = "UnKnow";
        if (currentServer.isPresent()) {
            currentServerName = currentServer.get().getServerInfo().getName();
        }
        var message = Component.empty()
                .append(Component.text("======= ").color(NamedTextColor.GRAY))
                .append(Component.text("Welcome back to "))
                .append(Component.text(String.format("%s Server", currentServerName)).color(NamedTextColor.YELLOW))
                .append(Component.text(" =======").color(NamedTextColor.GRAY))
                .appendNewline()
                .append(Component.text("今天是"))
                .append(Component.text(config.getServerName()).color(NamedTextColor.YELLOW))
                .append(Component.text("开服的第"))
                .append(Component.text(num / 24 / 60 / 60 / 1000).color(NamedTextColor.YELLOW))
                .append(Component.text("天"))
                .appendNewline()
                .append(Component.text("------- ").color(NamedTextColor.GRAY))
                .append(Component.text("Server List"))
                .append(Component.text(" -------").color(NamedTextColor.GRAY))
                .appendNewline();

        for (var serverName : server.getConfiguration().getServers().keySet()) {
            var command = "/server " + serverName;
            if (serverName.equalsIgnoreCase(currentServerName)) {
                message = message.append(Component.text(String.format("[%s] ", serverName)).color(NamedTextColor.AQUA));
            } else {
                message = message.append(Component.text(String.format("[%s] ", serverName))
                        .hoverEvent(HoverEvent.showText(Component.text(command)))
                        .clickEvent(ClickEvent.runCommand(command)));
            }
        }

        event.getPlayer().sendMessage(message);
    }
}
