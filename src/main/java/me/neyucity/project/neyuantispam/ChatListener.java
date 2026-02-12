package me.neyucity.project.neyuantispam;

import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Pattern;

public class ChatListener implements Listener {
    private final Neyuantispam plugin;
    private final Map<UUID, Long> cooldowns = new HashMap<>();
    private final Map<UUID, String> lastMsgs = new HashMap<>();

    public ChatListener(Neyuantispam plugin) { this.plugin = plugin; }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onChat(AsyncChatEvent event) {
        Player p = event.getPlayer();
        if (p.hasPermission("neyuantispam.bypass")) return;

        String msg = PlainTextComponentSerializer.plainText().serialize(event.message());
        String clean = msg.toLowerCase().replaceAll("[^a-z0-9]", "");
        UUID id = p.getUniqueId();
        long now = System.currentTimeMillis();

        // 1. Cooldown
        if (cooldowns.containsKey(id) && (now - cooldowns.get(id)) < plugin.getConfig().getInt("delay") * 1000L) {
            handleViolation(event, p, "messages.spam-cooldown"); return;
        }
        // 2. Similarity
        if (plugin.getConfig().getBoolean("block-repeated-messages") && lastMsgs.containsKey(id)) {
            if (getSimilarity(clean, lastMsgs.get(id)) > plugin.getConfig().getDouble("similarity-threshold")) {
                handleViolation(event, p, "messages.spam-repeated"); return;
            }
        }
        // 3. Characters
        if (plugin.getConfig().getBoolean("block-repeated-characters") &&
                Pattern.compile("(.)\\1{" + plugin.getConfig().getInt("max-repeating-characters") + ",}").matcher(clean).find()) {
            handleViolation(event, p, "messages.spam-characters"); return;
        }
        // 4. Blocked words
        for (String word : plugin.getConfig().getStringList("blocked-words")) {
            if (clean.contains(word.toLowerCase())) {
                handleViolation(event, p, "messages.spam-blocked-word"); return;
            }
        }

        cooldowns.put(id, now);
        lastMsgs.put(id, clean);
    }

    private void handleViolation(AsyncChatEvent event, Player p, String path) {
        event.setCancelled(true);
        p.sendMessage(plugin.getConfig().getString(path).replace("%seconds%", String.valueOf(plugin.getConfig().getInt("delay"))));

        if (plugin.getConfig().getBoolean("punishment.enabled")) {
            Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
                plugin.getChatDatabase().increment(p.getUniqueId());
                int count = plugin.getChatDatabase().getViolations(p.getUniqueId());
                String cmd = plugin.getConfig().getString("punishment.levels." + count);
                if (cmd == null) cmd = plugin.getConfig().getString("punishment.levels.4");

                if (cmd != null) {
                    String finalCmd = cmd.replace("%player%", p.getName());
                    Bukkit.getScheduler().runTask(plugin, () -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), finalCmd));
                }
            });
        }
    }

    private double getSimilarity(String s1, String s2) {
        if (s1.isEmpty() || s2.isEmpty()) return 0;
        int distance = editDistance(s1, s2);
        return 1.0 - ((double) distance / Math.max(s1.length(), s2.length()));
    }

    private int editDistance(String s1, String s2) {
        int[] costs = new int[s2.length() + 1];
        for (int i = 0; i <= s1.length(); i++) {
            int last = i;
            for (int j = 0; j <= s2.length(); j++) {
                if (i == 0) costs[j] = j;
                else if (j > 0) {
                    int next = costs[j - 1];
                    if (s1.charAt(i - 1) != s2.charAt(j - 1))
                        next = Math.min(Math.min(next, last), costs[j]) + 1;
                    costs[j - 1] = last; last = next;
                }
            }
            if (i > 0) costs[s2.length()] = last;
        }
        return costs[s2.length()];
    }
}