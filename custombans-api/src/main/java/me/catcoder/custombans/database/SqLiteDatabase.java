package me.catcoder.custombans.database;

import lombok.NonNull;
import lombok.experimental.Builder;
import org.sqlite.SQLiteDataSource;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by Ruslan on 31.03.2017.
 */
public class SqLiteDatabase implements AbstractDatabase {

    private final SQLiteDataSource dataSource = new SQLiteDataSource();

    private Connection connection = null;

    @Builder(builderMethodName = "newBuilder", builderClassName = "SqLiteBuilder", buildMethodName = "create")
    public SqLiteDatabase(@NonNull File file) {
        this.dataSource.setUrl("jdbc:sqlite:".concat(file.getAbsolutePath()));
    }

    @Override
    public int execute(String query, Object... objects) {
        StatementWrapper wrapper = StatementWrapper.create(this, query);

        int result = wrapper.execute(PreparedStatement.RETURN_GENERATED_KEYS, objects);
        this.commitQuery();
        return result;
    }

    private void commitQuery() {
        try {
            this.connection.commit();
        } catch (SQLException e) {
            throw new IllegalStateException("Unable to commit the last query.", e);
        }
    }

    @Override
    public int execute(StatementWrapper wrapper, Object... objects) {
        int result = wrapper.execute(PreparedStatement.RETURN_GENERATED_KEYS, objects);
        this.commitQuery();
        return result;
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

    @Override
    public void close() {
        try {
            this.connection.close();
        } catch (SQLException ignored) {
        }
    }

    protected void refreshConnection() {
        try {
            this.connection = dataSource.getConnection();
            this.connection.setAutoCommit(false);
        } catch (SQLException ignored) {
        }
    }
}
