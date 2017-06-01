package me.catcoder.custombans.database;


import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import lombok.experimental.Builder;
import me.catcoder.custombans.CustomBans;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

/**
 * Created by Ruslan on 12.03.2017.
 */
public class MySqlDatabase implements AbstractDatabase {

    private String host, password, user, data;

    private MysqlDataSource dataSource;

    private Connection connection = null;

    @Builder(builderMethodName = "newBuilder", builderClassName = "MySqlBuilder", buildMethodName = "create")
    private MySqlDatabase(String host,
                          String password,
                          String user,
                          String data) {

        this.host = host;
        this.password = password;
        this.user = user;
        this.data = data;

        try {
            this.dataSource = configureDataSource(new MysqlDataSource());
        } catch (SQLException e) {
            throw new RuntimeException("Cannot configure MySQL data source.");
        }
    }


    private MysqlDataSource configureDataSource(MysqlDataSource source) throws SQLException {
        source.setDatabaseName(this.data);
        source.setPassword(this.password);
        source.setUser(this.user);
        source.setServerName(this.host);
        source.setPort(3306);
        source.setConnectTimeout(5 * 1000);
        return source;
    }


    @Override
    public int execute(String query, Object... objects) {
        StatementWrapper wrapper = StatementWrapper.create(this, query);

        return wrapper.execute(PreparedStatement.RETURN_GENERATED_KEYS, objects);
    }

    @Override
    public int execute(StatementWrapper wrapper, Object... objects) {
        return wrapper.execute(PreparedStatement.RETURN_GENERATED_KEYS, objects);
    }

    @Override
    public <T> T executeQuery(String query, ResponseHandler<ResultSet, T> handler, Object... objects) {
        StatementWrapper wrapper = StatementWrapper.create(this, query);

        return wrapper.executeQuery(handler, objects);
    }

    @Override
    public <T> T executeQuery(StatementWrapper wrapper, ResponseHandler<ResultSet, T> handler, Object... objects) {
        return wrapper.executeQuery(handler, objects);
    }

    @Override
    public Connection getConnection() {
        refreshConnection();
        return connection;
    }

    protected void refreshConnection() {
        try {
            this.connection = dataSource.getConnection();
        } catch (SQLException ex) {
            CustomBans.getInstance().getLogger().log(Level.SEVERE, "Database connection failed!", ex);
            this.connection = null;
        }
    }

    @Override
    public void close() {
        //Dont close
    }
}
