package me.catcoder.custombans;

import static com.google.common.base.Preconditions.*;

import lombok.Getter;
import lombok.experimental.Builder;
import me.catcoder.custombans.command.utility.CommandExecutor;
import me.catcoder.custombans.config.Configuration;
import me.catcoder.custombans.config.ConfigurationLoader;
import me.catcoder.custombans.database.*;
import me.catcoder.custombans.language.Language;
import me.catcoder.custombans.limit.Limiter;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by Ruslan on 25.04.2017.
 */
@Getter
public class CustomBans {

    @Getter
    private static CustomBans instance;
    private final ConfigurationLoader configurationLoader = new ConfigurationLoader();

    private final CommandExecutor commandExecutor;
    private final Limiter limiter;
    private final File workingDirectory;
    private final Logger logger;
    private final Platform platform;

    private final Language language;
    private final Configuration pluginConfiguration;
    private final Database database;

    @Builder
    public CustomBans(CommandExecutor commandExecutor,
                      Limiter limiter,
                      File workingDirectory,
                      Logger logger,
                      Platform platform)
            throws
            IOException,
            UnsupportedOperationException,
            Database.ConnectionException,
            SQLException {

        checkArgument(instance == null, "Instance already set.");
        checkArgument(workingDirectory.isDirectory(), "File %s is not a directory.", workingDirectory);
        instance = this;
        this.commandExecutor = commandExecutor;
        this.logger = logger;
        this.limiter = limiter;
        this.platform = platform;

        this.workingDirectory = workingDirectory;
        this.pluginConfiguration = configurationLoader.load(new File(workingDirectory, "config.yml"));
        this.language = new Language(new File(workingDirectory, "language.yml"), this);
        this.database = setupDatabase();

        logger.log(Level.INFO, "CustomBans [{0}] enabled.", platform);
    }

    private Database setupDatabase() throws Database.ConnectionException, UnsupportedOperationException, SQLException {
        boolean mysql = pluginConfiguration.getBoolean("mysql.enabled");
        DatabaseCore core;
        if (!mysql && platform == Platform.BUNGEE)
            throw new UnsupportedOperationException("SQLite not supported for BungeeCord platform.");
        if (mysql) {
            Configuration mysqlSection = pluginConfiguration.getSection("mysql");
            core = new MySQLCore(
                    mysqlSection.getString("host"),
                    mysqlSection.getString("user"),
                    mysqlSection.getString("password"),
                    mysqlSection.getString("database"),
                    mysqlSection.getString("port"));
        } else {
            core = new SQLiteCore(new File(workingDirectory, "bans.db"));
        }
        Database data = new Database(core);
        DatabaseHelper.setup(data);
        return data;
    }

    protected static void unregister() {
        instance = null;
    }
}
