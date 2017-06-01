package me.catcoder.custombans.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.*;

/**
 * Created by Ruslan on 12.03.2017.
 */
public class StatementWrapper {

    private final Connection connection;
    private String query;

    private boolean sync = false;

    public StatementWrapper(Connection connection) {
        this.connection = connection;
    }

    public StatementWrapper(AbstractDatabase database) {
        this(database.getConnection());
    }

    public StatementWrapper setQuery(String query) {
        this.query = query;
        return this;
    }

    public StatementWrapper sync() {
        sync = true;
        return this;
    }

    private PreparedStatement createStatement(int generatedKeys, Object... objects) throws SQLException {
        PreparedStatement ps = connection.prepareStatement(query, generatedKeys);

        if (objects != null) {
            for (int i = 0; i < objects.length; i++) {
                ps.setObject(i + 1, objects[i]);
            }
        }
        return ps;
    }

    public int execute(int generatedKeys, Object... objects) {
        validateQuery();
        Callable<Integer> callable = () -> {
            try (PreparedStatement ps = createStatement(generatedKeys, objects)) {

                ps.execute();

                ResultSet rs = ps.getGeneratedKeys();
                return (rs.next() ? rs.getInt(1) : -1);
            }

        };
        int id = -1;
        if (!sync) {
            Future<Integer> future = asyncTask(callable);
            try {
                id = future.get();
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException("Failed to execute async query.", e);
            }

        } else try {
            id = callable.call();
        } catch (Exception e) {
            throw new RuntimeException("Failed to execute sync query.", e);
        }

        return id;
    }

    private void validateQuery() {
        if (query == null || query.isEmpty()) throw new IllegalStateException("Query must be not null.");
    }

    public <T> T executeQuery(ResponseHandler<ResultSet, T> handler, Object... objects) {
        validateQuery();

        Callable<T> callable = () -> {
            try (PreparedStatement ps = createStatement(PreparedStatement.NO_GENERATED_KEYS, objects)) {

                ResultSet rs = ps.executeQuery();

                return handler.handleResponse(rs);
            }
        };

        if (!sync) {
            Future<T> future = asyncTask(callable);
            try {
                return future.get();
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException("Failed to execute async query.", e);
            }

        } else try {
            return callable.call();
        } catch (Exception e) {
            throw new RuntimeException("Failed to execute sync query.", e);
        }
    }

    private <T> Future<T> asyncTask(Callable<T> callable) {
        return createQueryExecutor().submit(callable);
    }


    private ExecutorService createQueryExecutor() {
        return Executors.newSingleThreadExecutor();
    }

    public static StatementWrapper create(Connection connection, String query) {
        return new StatementWrapper(connection).setQuery(query);
    }

    public static StatementWrapper create(AbstractDatabase database, String query) {
        return new StatementWrapper(database).setQuery(query);
    }
}
