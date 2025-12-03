package org.clinicapaciente.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    // Detalhes da sua conexão PostgreSQL
    private static final String URL = "jdbc:postgresql://localhost:5432/db-java";
    private static final String USER = "postgres";
    private static final String PASS = "postgres";

    public static Connection getConnection() throws SQLException {
        // DriverManager gerencia as conexões JDBC
        // Não é mais necessário usar Class.forName("org.postgresql.Driver") no JDK 17
        return DriverManager.getConnection(URL, USER, PASS);
    }
}
