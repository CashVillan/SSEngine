package com.swingsword.ssengine.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

import com.swingsword.ssengine.MasterPlugin;

public class SQL {

	protected String sqlName;
	protected String host;
	protected String databaseName;
	protected String tableName;
	protected String user;
	protected String pass;

	public Connection connection;

	public boolean reconnecting = true;

	public SQL(String sqlName) {
		this.sqlName = sqlName;
		
		databaseName = sqlName;
		host = MasterPlugin.getMasterPlugin().getConfig().getString("sql." + sqlName + ".host");
		tableName = MasterPlugin.getMasterPlugin().getConfig().getString("sql." + sqlName + ".table");
		user = MasterPlugin.getMasterPlugin().getConfig().getString("sql." + sqlName + ".user");
		pass = MasterPlugin.getMasterPlugin().getConfig().getString("sql." + sqlName + ".pass");

		connect();
	}

	public void connect() {
		new BukkitRunnable() {
			public void run() {
				openConnection(host, "3306", databaseName, user, pass);

				if (isConnected()) {
					new BukkitRunnable() {
						public void run() {
							reconnecting = false;
						}
					}.runTask(MasterPlugin.getMasterPlugin());
				}
			}
		}.runTask(MasterPlugin.getMasterPlugin());
	}

	public void reconnect() {
		reconnecting = true;
		System.out.println("[" + sqlName + " SQL] Trying to reconnect in 10 seconds...");
		
		new BukkitRunnable() {
			public void run() {
				connect();
			}
		}.runTaskLaterAsynchronously(MasterPlugin.getMasterPlugin(), 200);
	}

	public void openConnection(String ip, String port, String databaseName, String name, String pass) {
		try {
			connection = DriverManager.getConnection("jdbc:mysql://" + ip + ":" + port + "/" + databaseName, name,
					pass);
			System.out.println("[" + sqlName + " SQL] Database connected successfully.");
			SQLManager.databases.put(this.sqlName, this);

		} catch (Exception e) {
			System.out.println("[" + sqlName + " SQL] Database failed to connect. (" + e.getCause() + ")");

			e.printStackTrace();
			
			reconnect();
		}
	}

	public void closeConnection() {
		try {
			if (connection != null) {
				if (!connection.isClosed()) {
					connection.close();
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public boolean isConnected() {
		try {
			if (connection != null && !connection.isClosed()) {
				return true;
			}
		} catch (SQLException e) {
			System.out.println("[" + sqlName + " SQL] The database was unreachable, so some code could not execute.");
		}

		connection = null;
		if (!reconnecting) {
			reconnect();
		}
		return false;
	}

	public void createAccount(UUID uuid, String name, HashMap<String, Object> entries) {
		if (isConnected()) {
			String data = "";
			String questionmarks = "";
			HashMap<Integer, String> numberValues = new HashMap<Integer, String>();
			int n = 3;

			for (String all : entries.keySet()) {
				data = data + ", `" + all + "`";
				questionmarks = questionmarks + ", ?";

				numberValues.put(n, (String) entries.get(all));

				n++;
			}

			try {
				PreparedStatement sql = connection.prepareStatement("INSERT INTO `" + databaseName + "`.`" + tableName
						+ "` (`uuid`, `name`" + data + ") VALUES (?, ?" + questionmarks + ");");
				sql.setString(1, uuid.toString());
				sql.setString(2, name);

				for (int x = 3; x < data.split(", ").length + 2; x++) {
					sql.setString(x, numberValues.get(x));
				}

				sql.execute();
				sql.close();

				System.out.println("[" + sqlName + " SQL] Account created for " + name + " (" + uuid.toString() + ")");

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void removeAccount(String name) {
		if(isConnected()) {
			try {
				PreparedStatement sql = connection.prepareStatement("DELETE FROM `" + databaseName + "`.`" + tableName + "` WHERE `name`=?");
				sql.setString(1, name);
	
				sql.execute();
				sql.close();
			
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	@SuppressWarnings("deprecation")
	public UUID getUUID(String playerName) {
		if (Bukkit.getOfflinePlayer(playerName).isOnline()) {
			return Bukkit.getPlayer(playerName).getUniqueId();

		} else if (isConnected()) {
			UUID dataValue = null;

			try {
				PreparedStatement sql = connection
						.prepareStatement("SELECT uuid FROM `" + tableName + "` WHERE name=?;");
				sql.setString(1, playerName);
				ResultSet resultSet = sql.executeQuery();
				boolean exists = resultSet.next();

				if (exists == true) {
					dataValue = UUID.fromString(resultSet.getString("uuid"));
				}

				sql.close();
				resultSet.close();

			} catch (Exception e) {
				e.printStackTrace();
			}

			return dataValue;
		}
		return null;
	}

	public boolean accountExists(UUID uuid) {
		if (isConnected()) {
			try {
				PreparedStatement sql = connection
						.prepareStatement("SELECT * FROM `" + databaseName + "`.`" + tableName + "` WHERE uuid=?;");
				sql.setString(1, uuid.toString());
				ResultSet resultSet = sql.executeQuery();
				boolean containsPlayer = resultSet.next();

				sql.close();
				resultSet.close();

				if (containsPlayer == true) {
					if (Bukkit.getOfflinePlayer(uuid).isOnline()) {
						if (!getValue(uuid, "name").equals(Bukkit.getPlayer(uuid).getName())) {
							setValue(uuid, "name", Bukkit.getPlayer(uuid).getName());
						}
					}
				}

				return containsPlayer;

			} catch (Exception e) {
				e.printStackTrace();

				return false;
			}
		}
		return false;
	}

	public boolean accountExists(String name) {
		if (isConnected()) {
			try {
				PreparedStatement sql = connection
						.prepareStatement("SELECT * FROM `" + databaseName + "`.`" + tableName + "` WHERE name=?;");
				sql.setString(1, name);
				ResultSet resultSet = sql.executeQuery();
				boolean containsPlayer = resultSet.next();

				sql.close();
				resultSet.close();

				return containsPlayer;

			} catch (Exception e) {
				e.printStackTrace();

				return false;
			}
		}
		return false;
	}

	public HashMap<String, Object> getValues(UUID uuid) {
		if (isConnected()) {
			HashMap<String, Object> dataValues = new HashMap<String, Object>();

			try {
				PreparedStatement sql = connection.prepareStatement("SELECT * FROM `" + tableName + "` WHERE uuid=?;");
				sql.setString(1, uuid.toString());
				ResultSet resultSet = sql.executeQuery();

				if (resultSet.next()) {
					ResultSetMetaData resultSetMetaData = resultSet.getMetaData();

					for (int x = 1; x <= resultSetMetaData.getColumnCount(); x++) {
						dataValues.put(resultSetMetaData.getColumnName(x),
								resultSet.getObject(resultSetMetaData.getColumnName(x)));
					}
				}

				sql.close();
				resultSet.close();

			} catch (Exception e) {
			}

			return dataValues;
		}
		return null;
	}

	public Object getValue(UUID uuid, String key) {
		if (isConnected()) {
			Object dataValue = null;

			try {
				PreparedStatement sql = connection.prepareStatement("SELECT " + key + " FROM `" + tableName + "` WHERE uuid=?;");
				sql.setString(1, uuid.toString());
				ResultSet resultSet = sql.executeQuery();
				boolean exists = resultSet.next();

				if (exists == true) {
					dataValue = resultSet.getObject(key);
				}

				sql.close();
				resultSet.close();

			} catch (Exception e) {
			}

			return dataValue;
		}
		return null;
	}

	public void setValue(UUID uuid, String key, String value) {
		if (isConnected()) {
			try {
				PreparedStatement updateData = connection.prepareStatement("UPDATE `" + tableName + "` SET " + key + "=? WHERE uuid=?;");
				updateData.setString(1, value.replace("null", ""));
				updateData.setString(2, uuid.toString());
				updateData.executeUpdate();

				updateData.close();

			} catch (Exception e) { }
		}
	}
	
	public void setValues(UUID uuid, HashMap<String, Object> entries) {
		if (isConnected() && entries != null) {
			try {
				String data = "";
				for(String key : entries.keySet()) {
					if(data.equals("")) {
						data = key + "=?";
					} else {
						data += ", " + key + "=?";
					}
				}
								
				PreparedStatement updateData = connection.prepareStatement("UPDATE `" + tableName + "` SET " + data + " WHERE uuid=?;");
				for(int x = 0; x < entries.size(); x++) {
					updateData.setString(x + 1, ((String) entries.values().toArray()[x]).replace("null", ""));
				}
				updateData.setString(entries.size() + 1, uuid.toString());
				updateData.executeUpdate();
				
				updateData.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}