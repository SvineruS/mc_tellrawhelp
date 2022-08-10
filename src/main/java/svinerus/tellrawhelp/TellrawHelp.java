package svinerus.tellrawhelp;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public final class TellrawHelp extends JavaPlugin {

    private static TellrawHelp instance;

    @Override
    public void onEnable() {
        instance = this;
        this.saveDefaultConfig();

        var helpCmd = new HelpCmd();
        this.getCommand("help").setTabCompleter(helpCmd);
        this.getCommand("help").setExecutor(helpCmd);
        this.getCommand("tellrawhelpreload").setExecutor(new ReloadCmd());
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    boolean sendHelp(CommandSender from, String senderName, String cmd) {
        return Bukkit.dispatchCommand(from, "tellraw " + senderName + " " + cmd);
    }


    private static class HelpCmd implements CommandExecutor, TabCompleter {
        @Override
        public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
            String cmd = TellrawHelp.instance.getConfig().getString(
              (args.length >= 1) ? args[0] : "default"
            );
            return TellrawHelp.instance.sendHelp(Bukkit.getServer().getConsoleSender(), sender.getName(), cmd);
        }

        @Override
        public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
            return TellrawHelp.instance.getConfig().getKeys(true).stream()
              .filter(e -> !"default".equals(e)).toList();
        }
    }

    private static class ReloadCmd implements CommandExecutor {
        @Override
        public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
            TellrawHelp.instance.reloadConfig();
            sender.sendMessage("Reloaded config; New help:");
            var cfg = TellrawHelp.instance.getConfig();
            for (String key : cfg.getKeys(true)) {
                sender.sendMessage(" ===== " + key + " =====");
                TellrawHelp.instance.sendHelp(sender, sender.getName(), cfg.getString(key));
            }
            return true;
        }
    }

}
