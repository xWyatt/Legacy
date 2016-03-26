package com.wyattteeter.Legacy;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Commands implements CommandExecutor {
	public Commands(Legacy plugin) {
	}

	public boolean onCommand(CommandSender sender, Command command, String alias, String[] args) {
		Boolean isPlayer = sender instanceof Player;
		UUID uuid = null;
		if (isPlayer.booleanValue()) {
			uuid = ((OfflinePlayer) sender).getUniqueId();
		}

		String cmd = command.getName().toLowerCase();
		if (cmd.equals("legacy")) {
			if (args.length > 1) {
				return false;
			}
			if ((args.length == 0) && (isPlayer.booleanValue())) {
				if (sender.hasPermission("legacy.check")) {
					Bukkit.getPlayer(uuid)
							.sendMessage(ChatColor.GREEN + "Time played: " + ChatColor.GOLD + Legacy.getPlaytime(uuid));
					return true;
				} else {
					sender.sendMessage(ChatColor.DARK_RED + "You do not have permission to perform this action");
					return true;
				}
			}
			// XXX /legacy top
			if ((args.length == 1) && (args[0].equals("top"))) {
				if (sender.hasPermission("legacy.others")) {
					sender.sendMessage(ChatColor.DARK_RED + "You do not have permission to perform this action");
					return true;
				}


				Map<String, Long> tempTracker = new HashMap<String, Long>(500);
				Map<String, Long> sortTracker = new HashMap<String, Long>(5);
				String highPlayer = "";
				long highTime = 0L;
				
				
				for (String each : Legacy.logConfiguration.getConfigurationSection("").getKeys(false)) {
					tempTracker.put(each, Long.valueOf(Legacy.logConfiguration.getLong(each)));
				}
				sender.sendMessage(ChatColor.GREEN + "-= Legacy Leaderboard =-");
				// XXX This is where you change /legacy top commands
				for (int i = 1; i < 21; i++) {
					highTime = 0L;
					for (Map.Entry<String, Long> entry : tempTracker.entrySet()) {
						if ((((Long) entry.getValue()).longValue() > highTime)
								&& (!sortTracker.containsKey(entry.getKey()))) {
							highPlayer = (String) entry.getKey();
							highTime = ((Long) entry.getValue()).longValue();
						}
					}
					sortTracker.put(highPlayer, Long.valueOf(highTime));
					sender.sendMessage(ChatColor.RED + String.valueOf(i) + ". " + ChatColor.GREEN + highPlayer + ": "
							+ ChatColor.GOLD + timePlayed(Long.valueOf(highTime)));
				}
				return true;
			}
			if ((args.length == 1) && (args[0].equals("total"))) {
				if (sender.hasPermission("legacy.others")) {
					sender.sendMessage(ChatColor.DARK_RED + "You do not have permission to perform this action");
					return true;
				}
				long totalTime = 0L;
				for (String each : Legacy.logConfiguration.getConfigurationSection("").getKeys(false)) {
					totalTime += Legacy.logConfiguration.getLong(each);
				}
				sender.sendMessage(
						ChatColor.GREEN + "Total Played: " + ChatColor.GOLD + timePlayed(Long.valueOf(totalTime)));
				return true;
			}

			if (args.length == 1) {
				if (sender.hasPermission("legacy.others")) {
					sender.sendMessage(ChatColor.DARK_RED + "You do not have permission to perform this action");
					return true;
				}

				String playerName = args[0];
				UUID playerUUID = Bukkit.getPlayer(playerName).getUniqueId();

				long totalTime = 0L;

				/*
				 * for (Entry<UUID, Long> entry : Legacy.timeTracker.entrySet())
				 * { if ((Bukkit.getPlayer(entry.getKey()).getName().contains(
				 * playerName))) { totalTime += (now.getTime() -
				 * ((Long)entry.getValue()).longValue()) / 1000L; } }
				 */

				if (Legacy.logConfiguration.contains(playerUUID.toString())) {
					totalTime += Legacy.logConfiguration.getLong(playerUUID.toString());
				}

				if (totalTime == 0L) {
					sender.sendMessage(ChatColor.DARK_RED + "Player not found.");
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
