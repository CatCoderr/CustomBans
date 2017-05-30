package me.catcoder.custombans.storage;

import lombok.Getter;
import me.catcoder.custombans.CustomBans;
import me.catcoder.custombans.database.Database;
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
    private final Map<UUID, Ban> bans = new HashMap<>();
    @Getter
    private final Map<UUID, Mute> mutes = new HashMap<>();
    private final Database database;
    private final CustomBans plugin;

    public PunishmentStorage(Database database, CustomBans plugin) {
        this.database = database;
        this.plugin = plugin;
    }

    public void load() {
        database.close();
        database.getCore().flush();
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = database.getConnection().prepareStatement("SELECT * FROM `bans`");
            rs = ps.executeQuery();

            while (rs.next()) {
                UUID uuid = UUID.fromString(rs.getString("uuid"));
                String banner = rs.getString("banner");
                String reason = rs.getString("reason");
                String params = rs.getString("params");
                long time = rs.getLong("time");
                Ban ban;
                if (time > 0) {
                    ban = new TempBan(reason, uuid, banner, time, (params.isEmpty() ? null : params));
                } else {
                    ban = new Ban(reason, uuid, banner, (params.isEmpty() ? null : params));
                }
                bans.put(ban.getUniqueId(), ban);
            }
            rs.close();
            ps.close();
            plugin.getLogger().log(Level.INFO, "Bans loaded (%s)", bans.size());
        } catch (SQLException ex) {
            plugin.getLogger().log(Level.SEVERE, "Cannot load bans.", ex);
        }
        try {
            ps = database.getConnection().prepareStatement("SELECT * FROM `mutes`");
            rs = ps.executeQuery();

            while (rs.next()) {
                UUID uuid = UUID.fromString(rs.getString("uuid"));
                String banner = rs.getString("banner");
                String reason = rs.getString("reason");
                String params = rs.getString("params");
                long time = rs.getLong("time");
                Mute mute;
                if (time > 0) {
                    mute = new TempMute(reason, uuid, banner, time, (params.isEmpty() ? null : params));
                } else {
                    mute = new Mute(reason, uuid, banner, (params.isEmpty() ? null : params));
                }
                mutes.put(mute.getUniqueId(), mute);
            }
            rs.close();
            ps.close();
            plugin.getLogger().log(Level.INFO, "Mutes loaded (%s)", mutes.size());
        } catch (SQLException ex) {
            plugin.getLogger().log(Level.SEVERE, "Cannot load mutes.", ex);
        }
    }

    public void add(Mute mute) {
        mutes.put(mute.getUniqueId(), mute);
        mute.insertInto(database);
    }

    public void add(Ban ban) {
        bans.put(ban.getUniqueId(), ban);
        ban.insertInto(database);
    }

    public void removeBan(UUID uuid) {
        bans.remove(uuid);
        database.execute("DELETE FROM `bans` WHERE uuid=?", uuid.toString());
    }

    public void removeMute(UUID uuid) {
        mutes.remove(uuid);
        database.execute("DELETE FROM `mutes` WHERE uuid=?", uuid.toString());
    }
}
