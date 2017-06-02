package me.catcoder.custombans;

import com.sk89q.CommandLocals;
import com.sk89q.CommandsManager;
import com.sk89q.SimpleInjector;
import lombok.Getter;
import lombok.experimental.Builder;
import me.catcoder.custombans.actor.Actor;
import me.catcoder.custombans.api.BanManager;
import me.catcoder.custombans.api.CustomAPI;
import me.catcoder.custombans.commands.BanCommands;
import me.catcoder.custombans.commands.KickCommand;
import me.catcoder.custombans.commands.MuteCommands;
import me.catcoder.custombans.commands.PluginCommands;
import me.catcoder.custombans.config.Configuration;
import me.catcoder.custombans.config.ConfigurationLoader;
import me.catcoder.custombans.database.*;
import me.catcoder.custombans.language.Language;
import me.catcoder.custombans.limit.Limiter;
import me.catcoder.custombans.punishment.ActionType;
import me.catcoder.custombans.storage.PunishmentStorage;
import me.catcoder.custombans.utility.FileUtility;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Arrays;
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
     * Supported languages.
     */
    public static final String[] SUPPORTED_LANGUAGES = new String[]{
            "ru_RU" //Russian
    };

    /**
     * Plugin instance.
     */
    @Getter
    private static CustomBans instance;

    /**
     * API
     */
    @Getter
    private static CustomAPI api;
    /**
     * Manager for bans/mutes manipulation.
     */
    @Getter
    private BanManager banManager;
    /**
     * YAML configuration loader.
     */
    @Getter
    private final ConfigurationLoader configurationLoader = new ConfigurationLoader();

    /**
     * Manager for commands manipulation.
     */
    @Getter
    private final CommandsManager<Actor> commandExecutor;
    /**
     * Limits checker (tempban, ban, etc.).
     */
    @Getter
    private Limiter limiter;
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
    private Language language;
    /**
     * The main plugin configuration (config.yml).
     */
    @Getter
    private Configuration pluginConfiguration;
    /**
     * Database store (bans, mutes)
     */
    @Getter
    private final AbstractDatabase database;
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
     * @param workingDirectory - plugin folder
     * @param logger           - logger
     * @param platform         - platform (BUNGEE, BUKKIT)
     * @throws IOException                   - if configurations ({@link Language, plugin config} has load error.
     * @throws UnsupportedOperationException - platform exceptions.
     * @throws SQLException                  - if queries to database is not valid.
     */
    @Builder
    public CustomBans(
            File workingDirectory,
            Logger logger,
            Platform platform,
            Function<String, Actor> actorFunction,
            Consumer<ReloadIntent> reloader,
            String version)
            throws
            IOException,
            UnsupportedOperationException,
            SQLException {

        checkArgument(instance == null, "Instance already set.");
        checkArgument(workingDirectory.isDirectory(), "File %s is not a directory.", workingDirectory);
        //Set plugin instance
        setInstance(this);

        this.logger = logger;
        this.platform = platform;
        this.version = version;
        this.actorFunction = actorFunction;
        this.reloader = reloader;

        this.workingDirectory = workingDirectory;
        //Load 'config.yml' file
        this.pluginConfiguration = FileUtility.get(configurationLoader, new File(workingDirectory, "config.yml"));
        //Load language
        this.reloadLanguage();
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

        //Initialize API
        api = new InternalAPI(this);
    }

    /**
     * Setting up {@link AbstractDatabase}
     *
     * @return - configured {@link AbstractDatabase}
     * @throws SQLException - if queries to database is not valid.
     */
    private AbstractDatabase setupDatabase() throws SQLException {
        boolean mysql = pluginConfiguration.getBoolean("mysql.enabled");
        AbstractDatabase data;
        if (!mysql && platform == Platform.BUNGEE) {
            logger.warning("SQLite database not supported for BungeeCord platform. Using mysql...");
            pluginConfiguration.set("mysql.enabled", true);
            FileUtility.save(configurationLoader, pluginConfiguration);
            mysql = true;
        }
        if (mysql) {
            Configuration mysqlSection = pluginConfiguration.getSection("mysql");
            data = DatabaseBuilder.useMySql()
                    .host(mysqlSection.getString("host"))
                    .data(mysqlSection.getString("database"))
                    .password(mysqlSection.getString("password"))
                    .user(mysqlSection.getString("user"))
                    .create();
        } else {
            data = DatabaseBuilder.useSqLite().file(new File(workingDirectory, "bans.db")).create();
        }
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
        commandExecutor.register(PluginCommands.class); /** /custombans, /cb */
        commandExecutor.register(BanCommands.class); /** /ban, /unban, /tempban */
        commandExecutor.register(MuteCommands.class); /** /mute, /unmute, /tempmute */
        commandExecutor.register(KickCommand.class); /** /kick */

    }

    /**
     * Copy all languages files and select specified language.
     *
     * @return specified {@link Language}
     */
    private Language getLanguageInput() throws IOException {
        File target = new File(workingDirectory, "languages");
        target.mkdir(); //Make sure is folder created.
        for (String language : SUPPORTED_LANGUAGES) {
            File file = new File(target, language.concat(".yml"));
            if (!file.exists())
                FileUtility.copy(getClass().getClassLoader().getResourceAsStream("languages/" + file.getName()), file.toPath());
        }
        //Language in config
        String language = pluginConfiguration.getString("language").concat(".yml");
        File[] contents = target.listFiles();
        assert contents != null;
        File localeFile = Arrays.stream(contents)
                .filter(file -> file.getName().equals(language))
                .findFirst()
                .orElse(null);
        if (localeFile == null) localeFile = new File(target, "en_US.yml");

        return new Language(localeFile, this);
    }

    /**
     * Public method to reload language.
     */
    public void reloadLanguage() throws IOException {
        this.language = getLanguageInput();
        this.logger.log(Level.INFO, "Using language: {0}", language);
    }

    /**
     * Reload the main plugin config.
     */
    public void reloadPluginConfiguration() {
        this.pluginConfiguration = FileUtility.get(configurationLoader, new File(workingDirectory, "config.yml"));
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
     * Setters
     */
    public void setBanManager(BanManager banManager) {
        checkArgument(this.banManager == null);
        this.banManager = banManager;
    }

    public void setLimiter(Limiter limiter) {
        checkArgument(this.limiter == null);
        this.limiter = limiter;
    }

    public static void setInstance(CustomBans instance) {
        CustomBans.instance = instance;
    }

}
