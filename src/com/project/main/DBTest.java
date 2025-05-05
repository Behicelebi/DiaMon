package com.project.main;

import com.project.view.Frame;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBTest {
    public static Frame frame;
    public static void main(String[] args) {
        // SQL Server bilgilerin burada olacak
        String url = "jdbc:sqlserver://localhost:1433;databaseName=ETRADE;encrypt=true;trustServerCertificate=true";
        String username = "sa"; // kendi kullanıcı adını yaz
        String password = "1234"; // kendi şifreni yaz

        try (Connection conn = DriverManager.getConnection(url, username, password)) {
            System.out.println("✅ Veritabanı bağlantısı başarılı!");
        } catch (SQLException e) {
            System.out.println("❌ Bağlantı kurulamadı:");
            e.printStackTrace();
        }

        frame = new Frame();
    }
}