package me.armar.plugins.autorank.util.uuid.storage;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.bukkit.ChatColor;
import me.armar.plugins.autorank.data.SQLDataStorage;

import me.armar.plugins.autorank.Autorank;

public class UUIDStorage {

	private final Autorank plugin;
	private final String tableName;

	// Expiration date in hours
	private final int expirationDate = 24;

	public UUIDStorage(final Autorank instance) {
		this.plugin = instance;
		this.tableName = plugin.getSettingsConfig().getString("sql.uuidlookup");
	}

	private SQLDataStorage sqlConnection() {
		if (this.plugin != null && this.plugin.getMySQLWrapper() != null && this.plugin.getMySQLWrapper().isMySQLEnabled()) {
			SQLDataStorage sql = this.plugin.getMySQLWrapper().getConnection();
			if (sql != null && sql.isClosed()) {
				this.plugin.getMySQLWrapper().sqlSetup();
				sql = this.plugin.getMySQLWrapper().getConnection();
			}
			return sql;
		}
		return null;
	}

	public void storeUUID(String playerName, final UUID uuid, final String realName) {
	}

	private String LookupByField(String field, String value, String lookupField) {

		SQLDataStorage sql = this.sqlConnection();
		if (sql == null || sql.isClosed()) {
			return null;
		}

		String result = null;
		final String statement = "SELECT " + lookupField + " FROM " + this.tableName + " WHERE " + field + "='" + value + "'";
		try {
			final ResultSet rs = sql.executeQuery(statement);

			if (rs != null) {
			 	if (rs.next()) {
					result = rs.getString(1);
				}
				rs.close();
			}
		} catch (final SQLException e) {
			System.out.println("SQLException: " + e.getMessage());
			System.out.println("SQLState: " + e.getSQLState());
			System.out.println("VendorError: " + e.getErrorCode());
		}

		return result;
	}

	public UUID getStoredUUID(String playerName) {
		String result = LookupByField("player", playerName, "uuid");
		if (result != null) {
			return UUID.fromString(result);
		}
		return null;
	}

	public String getCachedPlayerName(final UUID uuid) {
		return LookupByField("uuid", uuid.toString(), "player");
	}

	public String getRealName(final UUID uuid) {
		return LookupByField("uuid", uuid.toString(), "player");
	}

	public boolean hasRealName(final UUID uuid) {
		return getRealName(uuid) != null;
	}

	public boolean isOutdated(String playerName) {
		return false;
	}
}
