package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class DataSource {
    private static final String URL =
            "jdbc:mysql://192.168.20.5:3306/balanca_mas_nao_cai?useSSL=false&serverTimezone=UTC";
    private static final String USUARIO = "root";
    private static final String SENHA = "12345"; // use a senha real do Workbench

    private DataSource() { }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USUARIO, SENHA);
    }
}
