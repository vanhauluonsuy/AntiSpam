package me.neyucity.project.neyuantispam;

import java.io.File;
import java.sql.*;
import java.util.UUID;

public class Database {
    private Connection connection;

    public Database(Neyuantispam plugin) {
        try {
            File file = new File(plugin.getDataFolder(), "database.db");
            connection = DriverManager.getConnection("jdbc:sqlite:" + file.getAbsolutePath());
            try (Statement s = connection.createStatement()) {
                s.execute("CREATE TABLE IF NOT EXISTS violations (uuid TEXT PRIMARY KEY, count INTEGER)");
            }
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public synchronized int getViolations(UUID uuid) {
        try (PreparedStatement p = connection.prepareStatement("SELECT count FROM violations WHERE uuid = ?")) {
            p.setString(1, uuid.toString());
            ResultSet r = p.executeQuery();
            return r.next() ? r.getInt("count") : 0;
        } catch (SQLException e) { return 0; }
    }

    public synchronized void increment(UUID uuid) {
        try (PreparedStatement p = connection.prepareStatement(
                "INSERT INTO violations(uuid, count) VALUES(?, 1) ON CONFLICT(uuid) DO UPDATE SET count = count + 1")) {
            p.setString(1, uuid.toString());
            p.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public void resetViolations(UUID uuid) {
        try (PreparedStatement p = connection.prepareStatement("DELETE FROM violations WHERE uuid = ?")) {
            p.setString(1, uuid.toString());
            p.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }
}