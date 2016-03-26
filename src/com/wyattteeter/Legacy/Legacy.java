package com.wyattteeter.Legacy;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;

import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

public class Legacy extends JavaPlugin implements Listener {

	public static Map<UUID, Long> timeTracker = new HashMap<UUID, Long>(100);
	public static Map<UUID, Long> timeAway = new HashMap<UUID, Long>(100);
	public static final Logger log = Logger.getLogger("Legacy");
	static FileConfiguration configConfiguration = null;
	static File configFile = null;
	static FileConfiguration logConfiguration = null;
	static File logFile = null;

	@Override
	public void onDisable() {
		savePlayerTime();
		log.info(this + " is now disabled.");
	}

	@Override
	public void onEnable() {
		getServer().getPluginManager().registerEvents((Listener) new EventListener(), this);

		this.saveDefaultConfig();
		loadConfig();
		loadLog();
		saveLog();

		int delay = configConfiguration.getInt("auto-save.frequency") * 20 * 60;
		getServer().getScheduler().runTaskTimerAsynchronously(this, new Runnable() {
			public void run() {
				Legacy.this.savePlayerTime();
			}
		}, delay, delay);

		delay = configConfiguration.getInt("idle.frequency") * 20 * 60;
		getServer().getScheduler().runTaskTimerAsynchronously(this, new Runnable() {
			public void run() {
				Legacy.this.idleTime();
			}
		}, delay, delay);

	}

	protected void idleTime() {
		Date date = new Date();
		for (Map.Entry<UUID, Long> entry : timeAway.entrySet()) {
			if (((Long) entry.getValue()).longValue() + configConfiguration.getInt("idle.timeout") * 1000 * 60 < date
					.getTime()) {
				Legacy.pausePlayerLegacy(entry.getKey());
			}
		}
	}

	@SuppressWarnings("static-access")
	static void pausePlayerLegacy(UUID key) {
		if (!timeTracker.containsKey(key)) {
			return;
		}
		Date now = new Date();
		long playerSession = (now.getTime() - ((Long) timeTracker.get(key)).longValue()) / 1000L;
		if (logConfiguration.contains(key.toString())) {
			logConfiguration.set(key.toString(),
					Long.valueOf(logConfiguration.getLong(key.toString()) + playerSession));
		} else {
			logConfiguration.set(key.toString(), Long.valueOf(playerSession));
		}
		saveLog();

		timeTracker.remove(key);

		Bukkit.getPlayer(key).sendMessage(ChatColor.ITALIC.DARK_GRAY + "[Legacy] Paused due to idle.");
	}

	protected void savePlayerTime() {
		Date now = new Date();
		for (Player each : getServer().getOnlinePlayers()) {
			if (timeTracker.containsKey(each)) {
				long playerSession = (now.getTime() - ((Long) timeTracker.get(each)).longValue()) / 1000L;
				if (logConfiguration.contains(each.getName())) {
					logConfiguration.set(each.getName(),
							Long.valueOf(logConfiguration.getLong(each.getName()) + playerSession));
				} else {
					logConfiguration.set(each.getName(), Long.valueOf(playerSession));
				}
				saveLog();

				timeTracker.remove(each);
			}
		}
		now = new Date();
		for (Player each : getServer().getOnlinePlayers()) {
			timeTracker.put(each.getUniqueId(), Long.valueOf(now.getTime()));
		}
		log.info("[Legacy] Auto-saved player time");
	}

	@SuppressWarnings("static-access")
	public static void resumePlayerLegacy(UUID key) {
		if (timeTracker.containsKey(key)) {
			return;
		}
		timeTracker.put(key, Long.valueOf(new Date().getTime()));

		Bukkit.getPlayer(key).sendMessage(ChatColor.ITALIC.DARK_GRAY + "[Legacy] Resumed due to activity.");
	}

	public void loadConfig() {
		if (configConfiguration == null) {
			if (configFile == null) {
				configFile = new File(getDataFolder(), "config.yml");
			}
			configConfiguration = YamlConfiguration.loadConfiguration(configFile);
		}
	}

	public void saveConfig() {
		try {
			configConfiguration.save(configFile);
		} catch (IOException e) {
			log.severe("[Legacy] Unable to save config.yml.");
		}
	}

	public void loadLog() {
		if (logConfiguration == null) {
			if (logFile == null) {
				logFile = new File(getDataFolder(), "log.yml");
			}
			logConfiguration = YamlConfiguration.loadConfiguration(logFile);
		}
	}

	public static void saveLog() {
		try {
			logConfiguration.save(logFile);
		} catch (IOException e) {
			log.severe("[Legacy] Unable to save log.yml");
		}
	}

	public static long getPlaytime(UUID uuid) {
		
		Date now = new Date();

		long totalTime = 0L;
		for (Map.Entry<UUID, Long> entry : Legacy.timeTracker.entrySet()) {
			if ((entry.getKey()).equals(uuid)) {
				totalTime += (now.getTime() - ((Long) entry.getValue()).longValue()) / 1000L;
			}
		}

		if (Legacy.logConfiguration.contains(Bukkit.getPlayer(uuid).getName())) {
			totalTime += Legacy.logConfiguration.getLong(Bukkit.getPlayer(uuid).getName());
		}

		return totalTime;
	}
}
