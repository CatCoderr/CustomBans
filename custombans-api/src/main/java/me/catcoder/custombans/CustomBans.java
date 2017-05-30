package me.catcoder.custombans;

import com.sk89q.CommandLocals;
import com.sk89q.CommandsManager;
import com.sk89q.SimpleInjector;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Builder;
import me.catcoder.custombans.actor.Actor;
import me.catcoder.custombans.config.Configuration;
import me.catcoder.custombans.config.ConfigurationLoader;
import me.catcoder.custombans.database.*;
import me.catcoder.custombans.language.Language;
import me.catcoder.custombans.limit.Limiter;
import me.catcoder.custombans.punishment.ActionType;
import me.catcoder.custombans.storage.PunishmentStorage;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * CustomBans API class.
 *
 * @author CatCoder
 */
public class CustomBans {

    /**
     * Plugin instance.
     */
    @Getter
    private static CustomBans instance;
    /**
     * Manager for bans/mutes manipulation.
     */
    @Getter
    private final BanManager banManager;
    /**
     * YAML configuration loader.
     */
    @Getter
    private final ConfigurationLoader configurationLoader = new ConfigurationLoader();

    /**
     * Manager for commands manipulation.
     */
    @Getter
    private final CommandsManager commandExecutor;
    /**
     * Limits checker (tempban, ban, etc.).
     */
    @Getter
    private final Limiter limiter;
    /**
     * Plugin folder.
     */
    @Getter
    private final File workingDirectory;
    /**
     * Plugin logger.
     */
    @Getter
    private final Logger logger;
    /**
     * Plugin platform.
     * Supports: BungeeCord, Bukkit (Spigot).
     */
    @Getter
    private final Platform platform;
    /**
     * Language file.
     */
    @Getter
    private final Language language;
    /**
     * The main plugin configuration (config.yml).
     */
    @Getter
    private final Configuration pluginConfiguration;
    /**
     * Database store (bans, mutes)
     */
    @Getter
    private final Database database;
    /**
     * Punisments storage.
     */
    @Getter
    private final PunishmentStorage storage;
    /**
     * Plugin version.
     */
    @Getter
    private final String version;

    //Other fields
    private final Function<String, Actor> actorFunction;
    private final Consumer<ReloadIntent> reloader;


    /**
     * Constructs plugin instance.
     *
     * @param limiter          - {@link Limiter}
     * @param workingDirectory - plugin folder
     * @param logger           - logger
     * @param platform         - platform (BUNGEE, BUKKIT)
     * @throws IOException                   - if configurations ({@link Language, plugin config} has load error.
     * @throws UnsupportedOperationException - platform exceptions.
     * @throws Database.ConnectionException  - if database connection failed.
     * @throws SQLException                  - if queries to database is not valid.
     */
    @Builder
    public CustomBans(
            Limiter limiter,
            File workingDirectory,
            Logger logger,
            Platform platform,
            BanManager banManager,
            Function<String, Actor> actorFunction,
            Consumer<ReloadIntent> reloader,
            String version)
            throws
            IOException,
            UnsupportedOperationException,
            Database.ConnectionException,
            SQLException {

        checkArgument(instance == null, "Instance already set.");
        checkArgument(workingDirectory.isDirectory(), "File %s is not a directory.", workingDirectory);
        //Set plugin instance
        instance = this;

        this.logger = logger;
        this.limiter = limiter;
        this.platform = platform;
        this.version = version;
        this.actorFunction = actorFunction;
        this.reloader = reloader;

        this.workingDirectory = workingDirectory;
        this.banManager = banManager;
        //Load 'config.yml file
        this.pluginConfiguration = configurationLoader.load(new File(workingDirectory, "config.yml"));
        //Load language
        this.language = new Language(new File(workingDirectory, "language.yml"), this);
        this.database = setupDatabase();

        this.storage = new PunishmentStorage(database, this);

        //Implementing CommandsManager
        this.commandExecutor = new CommandsManager<Actor>() {

            @Override
            public boolean hasPermission(Actor player, String permission) {
                //Check for player permission.
                return player.hasPermission(permission);
            }

            //CustomBans edit.
            @Override
            public void checkLimit(Actor player, ActionType type, CommandLocals locals) {
                //Limit checking.
                limiter.checkLimit(player, type, locals);
            }
        };
        this.registerCommands();

        //Inform user.
        logger.log(Level.INFO, "CustomBans (ALPHA) [{0}] enabled.", platform);
    }

    /**
     * Setting up {@link Database}
     *
     * @return - configured {@link Database}
     * @throws UnsupportedOperationException - platform exceptions.
     * @throws Database.ConnectionException  - if database connection failed.
     * @throws SQLException                  - if queries to database is not valid.
     */
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

    /**
     * Registering commands.
     */
    private void registerCommands() {
        //Setting our injector arguments.
        commandExecutor.setInjector(new SimpleInjector(this));

        //Registering commands.
    }


    /**
     * Utility method for getting Actors.
     *
     * @param name - name of actor.
     * @return Actor
     */
    public Actor getActor(String name) {
        return actorFunction.apply(name);
    }

    /**
     * Reload plugin.
     */
    public void reload(ReloadIntent intent) {
        reloader.accept(intent);
    }

    /**
     * Nulling plugin instance.
     * Internal access.
     */
    protected static void unregister() {
        instance = null;
    }
}
