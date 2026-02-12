package me.neyucity.project.neyuantispam;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public class Neyuantispam extends JavaPlugin implements CommandExecutor {
    private Database database;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        this.database = new Database(this);
        getServer().getPluginManager().registerEvents(new ChatListener(this), this);
        getCommand("neyuantispam").setExecutor(this);
        getLogger().info("NeyuAntiSpam Enabled with SQLite!");
    }

    public Database getChatDatabase() { return database; }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (!sender.hasPermission("neyuantispam.admin")) {
            sender.sendMessage(getConfig().getString("messages.no-permission"));
            return true;
        }

        if (args.length > 0) {
            if (args[0].equalsIgnoreCase("reload")) {
                reloadConfig();
                sender.sendMessage(getConfig().getString("messages.reload-success"));
                return true;
            }
            if (args[0].equalsIgnoreCase("clear") && args.length > 1) {
                Player target = Bukkit.getPlayer(args[1]);
                if (target != null) {
                    database.resetViolations(target.getUniqueId());
                    sender.sendMessage("§aĐã xóa dữ liệu của " + target.getName());
                }
                return true;
            }
        }
        return false;
    }
}