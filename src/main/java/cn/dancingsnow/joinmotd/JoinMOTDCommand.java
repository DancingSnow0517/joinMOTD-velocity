package cn.dancingsnow.joinmotd;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.velocitypowered.api.command.BrigadierCommand;
import com.velocitypowered.api.command.CommandSource;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public class JoinMOTDCommand {
    public static void init(JoinMOTD joinMOTD) {
        joinMOTD.server.getEventManager().register(joinMOTD, joinMOTD.injector.getInstance(JoinMOTDCommand.class));
        joinMOTD.commandManager.register(joinMOTD.injector.getInstance(JoinMOTDCommand.class).createBrigadierCommand());
    }

    public BrigadierCommand createBrigadierCommand() {
        LiteralCommandNode<CommandSource> node = LiteralArgumentBuilder
                .<CommandSource>literal("joinMOTD")
                .then(LiteralArgumentBuilder.<CommandSource>literal("reload")
                        .executes(context -> {
                            if (JoinMOTD.config.load()) {
                                context.getSource().sendMessage(
                                        Component.text("reload joinMOTD config successes.").color(NamedTextColor.GREEN)
                                );
                            } else {
                                context.getSource().sendMessage(
                                        Component.text("reload joinMOTD config failed.").color(NamedTextColor.RED)
                                );
                            }
                            return 1;
                        }))
                .build();
        return new BrigadierCommand(node);
    }
}
