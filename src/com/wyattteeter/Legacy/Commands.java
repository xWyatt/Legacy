package com.wyattteeter.Legacy;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Commands implements CommandExecutor {

	public boolean onCommand(CommandSender sender, Command command, String alias, String[] args) {
		Boolean isPlayer = sender instanceof Player;
		if (!isPlayer.booleanValue()) {
			sender.sendMessage(ChatColor.DARK_RED + "You must be a player to perform this action!");
			return true;
		}

		Date now = new Date();
		
		String cmd = command.getName().toLowerCase();
		if (cmd.equals("legacy")) {
			if (args.length > 1) {
				return false;
			}
			if ((args.length == 0) && (isPlayer.booleanValue())) {
				if (!sender.hasPermission("legacy.check")) {
					sender.sendMessage(ChatColor.DARK_RED + "You do not have permission to perform this action");
					return true;
				}
				
				UUID playerUUID = Bukkit.getPlayer(sender.getName()).getUniqueId();
				Long totalTime = 0L;
				
				// Check file
				if (Legacy.logConfiguration.contains(playerUUID.toString())) {
					totalTime += Legacy.logConfiguration.getLong(playerUUID.toString());
				}
				
				// Check memory
				if (Legacy.timeTracker.containsKey(playerUUID)) {
					totalTime += (now.getTime() - Legacy.timeTracker.get(playerUUID)) / 1000L;
				}
				
				sender.sendMessage(ChatColor.GREEN + "You have played: " + ChatColor.GOLD
						+ timePlayed(Long.valueOf(totalTime)));
				return true;
			}

			if ((args.length == 1) && (args[0].equals("top"))) {
				if (!sender.hasPermission("legacy.top")) {
					sender.sendMessage(ChatColor.DARK_RED + "You do not have permission to perform this action");
					return true;
				}
				Map<UUID, Long> tempTracker = new HashMap<UUID, Long>(500);
				Map<UUID, Long> sortTracker = new HashMap<UUID, Long>(5);
				UUID highPlayer = null;
				long highTime = 0L;
				for (String each : Legacy.logConfiguration.getConfigurationSection("").getKeys(false)) {
					UUID eachUUID = UUID.fromString(each);
					
					// Sum time from file and memory (assuming player is online and currently tracking time)
					long time = Long.valueOf(Legacy.logConfiguration.getLong(each));
					if (Legacy.timeTracker.containsKey(eachUUID)) {
						time += (now.getTime() - Legacy.timeTracker.get(eachUUID)) / 1000L;
					}
					
					tempTracker.put(eachUUID, time);
				}
				sender.sendMessage(ChatColor.GREEN + "-= Legacy Leaderboard =-");

				for (int i = 1; i < Legacy.top + 1; i++) {
					highTime = 0L;
					for (Entry<UUID, Long> entry : tempTracker.entrySet()) {
						if ((((Long) entry.getValue()).longValue() > highTime)
								&& (!sortTracker.containsKey(entry.getKey()))) {
							highPlayer = entry.getKey();
							highTime = ((Long) entry.getValue()).longValue();
						}
					}
					sortTracker.put(highPlayer, Long.valueOf(highTime));
					sender.sendMessage(ChatColor.RED + String.valueOf(i) + ". " + ChatColor.GREEN
							+ Bukkit.getOfflinePlayer(highPlayer).getName() + ": " + ChatColor.GOLD
							+ timePlayed(Long.valueOf(highTime)));
				}
				return true;
			}

			if ((args.length == 1) && (args[0].equals("reload"))) {
				if (!sender.hasPermission("legacy.reload")) {
					sender.sendMessage(ChatColor.DARK_RED + "You do not have permission to perform this action");
					return true;
				} else {
					Legacy.configConfiguration = Legacy.plugin.getConfig();
					Legacy.plugin.loadConfig();
					sender.sendMessage(ChatColor.GREEN + "Configuration files have been reloaded!");
					return true;
				}
			}

			if (args.length == 1) {
				if (!sender.hasPermission("legacy.others")) {
					sender.sendMessage(ChatColor.DARK_RED + "You do not have permission to perform this action");
					return true;
				}
				
				String playerName = args[0];
				UUID playerUUID = null;
				
				// Try to find the UUID of this player - possible the username is offline
				for (OfflinePlayer offlinePlayer : Bukkit.getOfflinePlayers()) {
					if (offlinePlayer.getName().equals(playerName)) {
						playerUUID = offlinePlayer.getUniqueId();
						break; // Exit for loop for *performance*
					}
				}
				
				// Player not found
				if (playerUUID == null) {
					sender.sendMessage(ChatColor.DARK_RED + playerName + " has never played on this server!");
					return true;
				}

				long totalTime = 0L;

				// Pull time from file
				if (Legacy.logConfiguration.contains(playerUUID.toString())) {
					totalTime += Legacy.logConfiguration.getLong(playerUUID.toString());
				}
				
				// Pull time from memory
				if (Legacy.timeTracker.containsKey(playerUUID)) {
					totalTime += (now.getTime() - Legacy.timeTracker.get(playerUUID)) / 1000L;
				}
				
				if (totalTime == 0L) {
					sender.sendMessage(ChatColor.DARK_RED + playerName + " has no time recorded.");
					return true;
				}
				sender.sendMessage(ChatColor.GREEN + playerName + " played: " + ChatColor.GOLD
						+ timePlayed(Long.valueOf(totalTime)));
				return true;
			}
		}
		return false;
	}

	public String timePlayed(Long totalTime) {
		long days = totalTime.longValue() / 86400L;
		String _days;
		if (days == 1L) {
			_days = "day";
		} else {
			_days = "days";
		}
		long hours = totalTime.longValue() / 3600L - days * 24L;
		String _hours;
		if (hours == 1L) {
			_hours = "hour";
		} else {
			_hours = "hours";
		}
		long minutes = totalTime.longValue() / 60L - hours * 60L - days * 1440L;
		String _minutes;
		if (minutes == 1L) {
			_minutes = "minute";
		} else {
			_minutes = "minutes";
		}
		return days + " " + _days + ", " + hours + " " + _hours + ", " + minutes + " " + _minutes;
	}
}
