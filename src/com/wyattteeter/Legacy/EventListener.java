package com.wyattteeter.Legacy;

import java.util.Date;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class EventListener
  implements Listener
{
  @EventHandler
  public void onPlayerJoin(PlayerJoinEvent event)
  {
    Legacy.timeTracker.put(event.getPlayer().getUniqueId(), Long.valueOf(new Date().getTime()));
    Legacy.timeAway.put(event.getPlayer().getUniqueId(), Long.valueOf(new Date().getTime()));
  }
  
  @EventHandler
  public void onPlayerQuit(PlayerQuitEvent event)
  {
    Legacy.timeAway.remove(event.getPlayer().getUniqueId());
    Legacy.pausePlayerLegacy(event.getPlayer().getUniqueId());
  }
  
  @EventHandler
  public void onPlayerMove(PlayerMoveEvent event)
  {
    Legacy.timeAway.put(event.getPlayer().getUniqueId(), Long.valueOf(new Date().getTime()));
    Legacy.resumePlayerLegacy(event.getPlayer().getUniqueId());
  }
  
  @EventHandler
  public void onAsyncPlayerChat(AsyncPlayerChatEvent event)
  {
    Legacy.timeAway.put(event.getPlayer().getUniqueId(), Long.valueOf(new Date().getTime()));
    Legacy.resumePlayerLegacy(event.getPlayer().getUniqueId());
  }
  
  @EventHandler
  public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event)
  {
    Legacy.timeAway.put(event.getPlayer().getUniqueId(), Long.valueOf(new Date().getTime()));
    Legacy.resumePlayerLegacy(event.getPlayer().getUniqueId());
  }
  
  @EventHandler
  public void onPlayerInteract(PlayerInteractEvent event)
  {
    Legacy.timeAway.put(event.getPlayer().getUniqueId(), Long.valueOf(new Date().getTime()));
    Legacy.resumePlayerLegacy(event.getPlayer().getUniqueId());
  }
  
  @EventHandler
  public void onInventoryClickEvent(InventoryClickEvent event)
  {
    for (HumanEntity each : event.getInventory().getViewers())
    {
      Legacy.timeAway.put(each.getUniqueId(), Long.valueOf(new Date().getTime()));
      Legacy.resumePlayerLegacy(each.getUniqueId());
    }
  }
  
  @EventHandler
  public void onBlockBreak(BlockBreakEvent event)
  {
    Legacy.timeAway.put(event.getPlayer().getUniqueId(), Long.valueOf(new Date().getTime()));
    Legacy.resumePlayerLegacy(event.getPlayer().getUniqueId());
  }
  
  @EventHandler
  public void onBlockPlace(BlockPlaceEvent event)
  {
    Legacy.timeAway.put(event.getPlayer().getUniqueId(), Long.valueOf(new Date().getTime()));
    Legacy.resumePlayerLegacy(event.getPlayer().getUniqueId());
  }
}

