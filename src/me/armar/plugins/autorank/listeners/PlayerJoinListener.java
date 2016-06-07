package me.armar.plugins.autorank.listeners;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import me.armar.plugins.autorank.Autorank;

/**
 * This listener will listen to players joining and send them a message when an
 * update is available or an error has been found
 * 
 * @author Staartvin
 * 
 */
public class PlayerJoinListener implements Listener {

	private final Autorank plugin;

	public PlayerJoinListener(final Autorank instance) {
		plugin = instance;
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onPlayerJoin(final PlayerJoinEvent event) {
		final Player player = event.getPlayer();

		// Cannot check player at this moment. -> try at next automatic task
		if (plugin.getPlayerChecker() == null) {
			plugin.getLogger()
					.severe("Autorank lost its player checker, this is bad! Please report this to the developers!");
			return;
		}

		//plugin.debugMessage("PlayerChecker: " + plugin.getPlayerChecker());

		// Do leaderboard exemption check
		plugin.getPlayerChecker().doLeaderboardExemptCheck(player);

		// Perform check for player on login
		plugin.getPlayerChecker().checkPlayer(player);

		// If player has notice on warning permission
		if (player.hasPermission("autorank.warning.notice")) {

			if (plugin.getWarningManager().getHighestWarning() != null) {

				// Schedule it later so it will appear at the bottom
				plugin.getServer().getScheduler().runTaskLater(plugin, new Runnable() {

					@Override
					public void run() {
						player.sendMessage(ChatColor.BLUE + "<AUTORANK> " + ChatColor.RED + "Warning: "
								+ ChatColor.GREEN + plugin.getWarningManager().getHighestWarning());
					}

				}, 10L);
			}
		}
	}
}
