package com.wyattteeter.Legacy;

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
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

public class Commands implements CommandExecutor {

	@SuppressWarnings("deprecation")
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
				if (!sender.hasPermission("legacy.check")) {
					sender.sendMessage(ChatColor.DARK_RED + "You do not have permission to perform this action");
					return true;
				} else {
					Bukkit.getPlayer(uuid)
							.sendMessage(ChatColor.GREEN + "Time played: " + ChatColor.GOLD + Legacy.getPlaytime(uuid));
					return true;
				}
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
			          tempTracker.put(eachUUID, Long.valueOf(Legacy.logConfiguration.getLong(each.toString())));
			        }
			        sender.sendMessage(ChatColor.GREEN + "-= Legacy Leaderboard =-");
			        
			        for (int i = 1; i < Legacy.top + 1; i++)
			        {
			          highTime = 0L;
			          for (Entry<UUID, Long> entry : tempTracker.entrySet()) {
			            if ((((Long)entry.getValue()).longValue() > highTime) && (!sortTracker.containsKey(entry.getKey())))
			            {
			              highPlayer = entry.getKey();
			              highTime = ((Long)entry.getValue()).longValue();
			            }
			          }
			          sortTracker.put(highPlayer, Long.valueOf(highTime));
			          sender.sendMessage(ChatColor.RED + String.valueOf(i) + ". " + ChatColor.GREEN + Bukkit.getOfflinePlayer(highPlayer).getName() + ": " + ChatColor.GOLD + timePlayed(Long.valueOf(highTime)));
			        }
			        return true;
			      }

			if ((args.length == 1) && (args[0].equals("reload"))) {
				if (!sender.hasPermission("legacy.reload")) {
					sender.sendMessage(ChatColor.DARK_RED + "You do not have permission to perform this action");
					return true;
				} else {
					Legacy.configConfiguration = YamlConfiguration.loadConfiguration(Legacy.configFile);
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
				
				if (Bukkit.getOfflinePlayer(playerName) == null){
					sender.sendMessage(ChatColor.DARK_RED + playerName + " has never played on this server!");
					return true;
				}
				
				UUID playerUUID = Bukkit.getOfflinePlayer(playerName).getUniqueId();
				
				long totalTime = 0L;

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
