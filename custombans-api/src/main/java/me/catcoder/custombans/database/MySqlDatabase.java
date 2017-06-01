package me.catcoder.custombans.database;


import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import lombok.experimental.Builder;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by Ruslan on 12.03.2017.
 */
public class MySqlDatabase implements AbstractDatabase {

    //Основные параметры подключения
    private String host, password, user, data;

    //MySQL
    private MysqlDataSource dataSource;

    //Подключение
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

        this.dataSource = configureDataSource(new MysqlDataSource());
    }


    private MysqlDataSource configureDataSource(MysqlDataSource source) {
        source.setDatabaseName(this.data); //Имя БД
        source.setPassword(this.password); //Пароль
        source.setUser(this.user); //Пользователь
        source.setServerName(this.host); //Имя хоста
        source.setPort(3306); //Порт
        source.setAutoReconnect(true); //Авто-подключение
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
            ex.printStackTrace(); // ):
            this.connection = null;
        }
    }

    @Override
    public void close() {
        //Dont close
    }
}
