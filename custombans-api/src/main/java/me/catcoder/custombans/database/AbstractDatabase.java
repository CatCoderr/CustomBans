package me.catcoder.custombans.database;


import java.sql.Connection;
import java.sql.ResultSet;

/**
 * Created by Ruslan on 12.03.2017.
 */
public interface AbstractDatabase {

    final String SQLITE = "sqlite";
    final String MYSQL = "mysql";


    int execute(String query, Object... objects);

    int execute(StatementWrapper wrapper, Object... objects);

    <T> T executeQuery(String query, ResponseHandler<ResultSet, T> handler, Object... objects);

    <T> T executeQuery(StatementWrapper wrapper, ResponseHandler<ResultSet, T> handler, Object... objects);

    void close();

    Connection getConnection();
}
