package Fepbox.FepEconomy.Utils;

import Fepbox.FepEconomy.FepEconomy;

import java.sql.Connection;
import java.sql.SQLException;

public final class Database {

    private Database() {
    }

    public static void run(SqlConsumer action) throws SQLException {
        synchronized (Database.class) {
            action.accept(FepEconomy.getPlugin().getConnection());
        }
    }

    public static <T> T call(SqlFunction<T> action) throws SQLException {
        synchronized (Database.class) {
            return action.apply(FepEconomy.getPlugin().getConnection());
        }
    }

    @FunctionalInterface
    public interface SqlConsumer {
        void accept(Connection connection) throws SQLException;
    }

    @FunctionalInterface
    public interface SqlFunction<T> {
        T apply(Connection connection) throws SQLException;
    }
}
