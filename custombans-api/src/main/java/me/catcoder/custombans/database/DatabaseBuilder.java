package me.catcoder.custombans.database;

/**
 * Created by Ruslan on 31.03.2017.
 */

public class DatabaseBuilder {

    public static MySqlDatabase.MySqlBuilder useMySql() {
        return MySqlDatabase.newBuilder();
    }

    public static SqLiteDatabase.SqLiteBuilder useSqLite() {
        return SqLiteDatabase.newBuilder();
    }
}
