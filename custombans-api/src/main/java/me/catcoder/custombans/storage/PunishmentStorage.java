package me.catcoder.custombans.storage;

import lombok.Getter;
import me.catcoder.custombans.CustomBans;
import me.catcoder.custombans.database.AbstractDatabase;
import me.catcoder.custombans.database.ResponseHandler;
import me.catcoder.custombans.punishment.Ban;
import me.catcoder.custombans.punishment.Mute;
import me.catcoder.custombans.punishment.TempBan;
import me.catcoder.custombans.punishment.TempMute;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

/**
 * @author CatCoder
 */
public class PunishmentStorage {


    @Getter
    private final Map<String, Ban> bans = new HashMap<>();
    @Getter
    private final Map<String, Mute> mutes = new HashMap<>();
    private final AbstractDatabase database;
    private final CustomBans plugin;

    public PunishmentStorage(AbstractDatabase database, CustomBans plugin) {
        this.database = database;
        this.plugin = plugin;
    }

    public void load() {
        database.executeQuery("SELECT * FROM `bans`", getBansLoader());
        database.executeQuery("SELECT * FROM `mutes`", getMutesLoader());
    }

    public void add(Mute mute) {
        mutes.put(mute.getName().toLowerCase(), mute);
        mute.insertInto(database);
    }

    public void add(Ban ban) {
        bans.put(ban.getName().toLowerCase(), ban);
        ban.insertInto(database);
    }

    public void removeBan(String name) {
        bans.remove(name.toLowerCase());
        database.execute("DELETE FROM `bans` WHERE name=?", name.toLowerCase());
    }

    public void removeMute(String name) {
        mutes.remove(name.toLowerCase());
        database.execute("DELETE FROM `mutes` WHERE name=?", name.toLowerCase());
    }

    /////////////////////////////////////////////////////

    private ResponseHandler<ResultSet, ?> getBansLoader() {
        return (rs) -> {
            while (rs.next()) {
                String name = rs.getString("name");
                String banner = rs.getString("banner");
                String reason = rs.getString("reason");
                String params = rs.getString("params");
                long time = rs.getLong("time");
                Ban ban;
                if (time > 0) {
                    ban = new TempBan(reason, name, banner, time, (params.isEmpty() ? null : params));
                } else {
                    ban = new Ban(reason, name, banner, (params.isEmpty() ? null : params));
                }
                bans.put(name.toLowerCase(), ban);
            }
            plugin.getLogger().log(Level.INFO, "Bans loaded ({0})", bans.size());
            return Void.TYPE;
        };
    }

    private ResponseHandler<ResultSet, ?> getMutesLoader() {
        return (rs) -> {
            while (rs.next()) {
                String name = rs.getString("name");
                String banner = rs.getString("banner");
                String reason = rs.getString("reason");
                String params = rs.getString("params");
                long time = rs.getLong("time");
                Mute mute;
                if (time > 0) {
                    mute = new TempMute(reason, name, banner, time, (params.isEmpty() ? null : params));
                } else {
                    mute = new Mute(reason, name, banner, (params.isEmpty() ? null : params));
                }
                mutes.put(mute.getName().toLowerCase(), mute);
            }
            plugin.getLogger().log(Level.INFO, "Mutes loaded ({0})", mutes.size());
            return Void.TYPE;
        };
    }
}
