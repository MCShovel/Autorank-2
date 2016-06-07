package me.armar.plugins.autorank.playerdatahandler;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.api.events.RequirementCompleteEvent;
import me.armar.plugins.autorank.playerchecker.result.Result;
import me.armar.plugins.autorank.rankbuilder.ChangeGroup;
import me.armar.plugins.autorank.rankbuilder.holders.RequirementsHolder;

/**
 * PlayerDataHandler will keep track of the latest known group and progress a
 * player made (via /ar complete)
 * When the last known group is not equal to the current group of a player, all
 * progress should be reset as a player is not longer in the same group.
 * 
 * PlayerDataHandler uses a file (/playerdata/playerdata.yml) which keeps
 * tracks of these things.
 * 
 * @author Staartvin
 * 
 */
public class PlayerDataHandler {

	private FileConfiguration config;
	private File configFile;

	private final Autorank plugin;

	public PlayerDataHandler(final Autorank instance) {
		this.plugin = instance;
	}

	public void addCompletedRanks(final UUID uuid, final String rank) {
		final List<String> completed = getCompletedRanks(uuid);

		completed.add(rank);

		setCompletedRanks(uuid, completed);
	}

	public void addPlayerProgress(final UUID uuid, final int reqID) {
		final List<Integer> progress = getProgress(uuid);

		if (hasCompletedRequirement(reqID, uuid))
			return;

		progress.add(reqID);

		setPlayerProgress(uuid, progress);
	}

	public void createNewFile() {
		reloadConfig();
		saveConfig();
		loadConfig();

		// Convert old format to new UUID storage format
		//convertNamesToUUIDs();

		plugin.getLogger().info("Loaded playerdata.");
	}

	private List<String> getCompletedRanks(final UUID uuid) {
		if (uuid == null || config == null) {
			return new ArrayList<String>();
		}
		final List<String> completed = config.getStringList(uuid.toString() + ".completed ranks");

		return completed;
	}

	public FileConfiguration getConfig() {
		if (config == null) {
			this.reloadConfig();
		}
		return config;
	}

	public String getLastKnownGroup(final UUID uuid) {
		//Validate.notNull(uuid, "UUID of a player is null!");

		//UUID uuid = UUIDManager.getUUIDFromPlayer(playerName);
		return config.getString(uuid.toString() + ".last group");
	}

	@SuppressWarnings("unchecked")
	public List<Integer> getProgress(final UUID uuid) {
		//UUID uuid = UUIDManager.getUUIDFromPlayer(playerName);
		//Validate.notNull(uuid, "UUID of a player is null!");
		if (uuid == null) {
			return new ArrayList<Integer>();
		}

		return (List<Integer>) config.getList(uuid.toString() + ".progress", new ArrayList<Integer>());
	}

	public boolean hasCompletedRank(final UUID uuid, final String rank) {
		// If player can rank up forever on the same rank, we will always return false.
		// Fixed issue #134
		if (plugin.getConfigHandler().allowInfiniteRanking()) {
			return false;
		}

		List<String> completed = getCompletedRanks(uuid);
		if (completed != null)
			return completed.contains(rank);
		return false;
	}

	public boolean hasCompletedRequirement(final int reqID, final UUID uuid) {
		final List<Integer> progress = getProgress(uuid);

		return progress.contains(reqID);
	}

	public void loadConfig() {
		reloadConfig();
	}

	@SuppressWarnings("deprecation")
	public void reloadConfig() {
		config = new YamlConfiguration();
	}

	public void runResults(final RequirementsHolder holder, final Player player) {

		// Fire event so it can be cancelled
		// Create the event here/
		// TODO Implement logic for events with RequirementHolder
		final RequirementCompleteEvent event = new RequirementCompleteEvent(player, holder);
		// Call the event
		Bukkit.getServer().getPluginManager().callEvent(event);

		// Check if event is cancelled.
		if (event.isCancelled())
			return;

		// Run results
		final List<Result> results = holder.getResults();

		// Apply result
		for (final Result realResult : results) {
			realResult.applyResult(player);
		}
	}

	public void saveConfig() {
	}

	public void setCompletedRanks(final UUID uuid, final List<String> completedRanks) {
		config.set(uuid.toString() + ".completed ranks", completedRanks);
	}

	public void setLastKnownGroup(final UUID uuid, final String group) {
		//UUID uuid = UUIDManager.getUUIDFromPlayer(playerName);
		config.set(uuid.toString() + ".last group", group);
	}

	public void setPlayerProgress(final UUID uuid, final List<Integer> progress) {
		//UUID uuid = UUIDManager.getUUIDFromPlayer(playerName);

		config.set(uuid.toString() + ".progress", progress);
	}

	public boolean hasLeaderboardExemption(final UUID uuid) {
		//Validate.notNull(uuid, "UUID of a player is null!");
		return config.getBoolean(uuid.toString() + ".exempt leaderboard", false);
	}

	public void hasLeaderboardExemption(final UUID uuid, final boolean value) {
		config.set(uuid.toString() + ".exempt leaderboard", value);
	}

	public void setChosenPath(final UUID uuid, final String path) {
		config.set(uuid.toString() + ".chosen path", path);
	}

	public String getChosenPath(final UUID uuid) {
		return config.getString(uuid.toString() + ".chosen path", "unknown");
	}

	public boolean checkValidChosenPath(final Player player) {

		final String groupName = plugin.getPermPlugHandler().getPrimaryGroup(player);
		final String chosenPath = this.getChosenPath(player.getUniqueId());

		final List<ChangeGroup> changeGroups = plugin.getPlayerChecker().getChangeGroupManager()
				.getChangeGroups(groupName);

		boolean validChosenPath = false;

		// Check whether the chosen path equals one of the change groups
		for (final ChangeGroup group : changeGroups) {
			if (group.getInternalGroup().equals(chosenPath)) {
				validChosenPath = true;
			}
		}

		if (!validChosenPath) {
			// Somehow there wrong chosen path was still left over. Remove it.
			plugin.getPlayerDataHandler().setChosenPath(player.getUniqueId(), null);
		}

		return validChosenPath;

	}
}
