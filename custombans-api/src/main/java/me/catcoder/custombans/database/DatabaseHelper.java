package me.catcoder.custombans.database;

import java.sql.SQLException;

public class DatabaseHelper {
	public static void setup(Database db) throws SQLException {
		createTables(db);
	}

	/**
	 * Creates the database tables.
	 */
	public static void createTables(Database db) throws SQLException {
		// Creates the database tables
		if (!db.hasTable("bans")) {
			createBanTable(db);
		}
		if (!db.hasTable("mutes")) {
			createMuteTable(db);
		}
	}

	private static void createBanTable(Database db) {
		db.execute(
				"CREATE TABLE bans (name TEXT(30) NOT NULL, banner TEXT(30) NOT NULL, reason TEXT(30) NOT NULL, admin int(2), expires long(2) DEFAULT '0', date TEXT);");
	}

	private static void createMuteTable(Database db) {
		db.execute(
				"CREATE TABLE mutes (name TEXT(30) NOT NULL, banner TEXT(30) NOT NULL, reason TEXT(30) NOT NULL, admin int(2),  expires long(2) DEFAULT '0', date TEXT);");
	}

}