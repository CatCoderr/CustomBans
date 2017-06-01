package me.catcoder.custombans.database;

import java.sql.SQLException;

public class DatabaseHelper {
    public static void setup(AbstractDatabase db) throws SQLException {
        createTables(db);
    }

    /**
     * Creates the database tables.
     */
    public static void createTables(AbstractDatabase db) throws SQLException {
        createBanTable(db);
        createMuteTable(db);
    }

    private static void createBanTable(AbstractDatabase db) {
        db.execute(
                "CREATE TABLE IF NOT EXISTS `bans` (uuid TEXT NOT NULL UNIQUE, banner TEXT(30) NOT NULL, reason TEXT(30) NOT NULL, params TEXT NOT NULL, time BIGINT NOT NULL DEFAULT 0)");
    }

    private static void createMuteTable(AbstractDatabase db) {
        db.execute(
                "CREATE TABLE IF NOT EXISTS `mutes` (uuid TEXT NOT NULL UNIQUE, banner TEXT(30) NOT NULL, reason TEXT(30) NOT NULL, params TEXT NOT NULL, time BIGINT NOT NULL DEFAULT 0)");
    }

}