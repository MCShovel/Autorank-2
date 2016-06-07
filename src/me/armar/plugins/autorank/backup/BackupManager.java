package me.armar.plugins.autorank.backup;

import java.io.File;
import java.io.IOException;

import org.bukkit.ChatColor;

import com.google.common.io.Files;

import me.armar.plugins.autorank.Autorank;

/**
 * Class that allows to backup files before overwriting them.
 * Stores functions to backup.
 * <p>
 * Date created: 15:25:43 12 dec. 2014
 * 
 * @author Staartvin
 * 
 */
public class BackupManager {

	private final Autorank plugin;

	public BackupManager(final Autorank plugin) {
		this.plugin = plugin;
	}

	/**
	 * Backup a file to a folder.
	 * 
	 * @param sourceFileName Path of file to backup
	 * @param storePath Path to backup the file to, can be null.
	 */
	public void backupFile(final String sourceFileName, final String storePath) {
	}

	/**
	 * Starts internal backup system of Autorank.
	 * <br>
	 * This will make a backup of each file every day.
	 */
	public void startBackupSystem() {
	}

}
