package com.project.model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class CreateUsers {
    public CreateUsers(){
        String connectionUrl = "jdbc:sqlserver://localhost:1433;user=SA;password=diyabet1234;encrypt=true;trustServerCertificate=true";

        try(Connection conn = DriverManager.getConnection(connectionUrl); Statement stmt = conn.createStatement()){

            // doktor_login girişi
            String createLoginDoktor =
                    "IF NOT EXISTS (SELECT * FROM sys.sql_logins WHERE name = 'doktor_login') " +
                            "BEGIN CREATE LOGIN doktor_login WITH PASSWORD = 'doktor123' " +
                            "END";
            stmt.executeUpdate(createLoginDoktor);

            // hasta_login girişi
            String createLoginHasta =
                    "IF NOT EXISTS (SELECT * FROM sys.sql_logins WHERE name = 'hasta_login') " +
                            "BEGIN CREATE LOGIN hasta_login WITH PASSWORD = 'hasta123' " +
                            "END";
            stmt.executeUpdate(createLoginHasta);

            stmt.execute("USE PROJETEST");

            // doktor_user kullanıcısı
            String createUserDoktor =
                    "IF NOT EXISTS (SELECT * FROM sys.database_principals WHERE name = 'doktor_user') " +
                            "BEGIN CREATE USER doktor_user FOR LOGIN doktor_login WITH DEFAULT_SCHEMA = dbo " +
                            "END";
            stmt.executeUpdate(createUserDoktor);

            // hasta_user kullanıcısı
            String createUserHasta =
                    "IF NOT EXISTS (SELECT * FROM sys.database_principals WHERE name = 'hasta_user') " +
                            "BEGIN CREATE USER hasta_user FOR LOGIN hasta_login WITH DEFAULT_SCHEMA = dbo " +
                            "END";
            stmt.executeUpdate(createUserHasta);

            // doktor_role rolü
            String createRoleDoktor =
                    "IF NOT EXISTS (SELECT * FROM sys.database_principals WHERE name = 'doktor_role') " +
                            "BEGIN CREATE ROLE doktor_role " +
                            "END";
            stmt.executeUpdate(createRoleDoktor);

            // hasta_role rolü
            String createRoleHasta =
                    "IF NOT EXISTS (SELECT * FROM sys.database_principals WHERE name = 'hasta_role') " +
                            "BEGIN CREATE ROLE hasta_role " +
                            "END";
            stmt.executeUpdate(createRoleHasta);

            String addDoktorToRole =
                    "IF NOT EXISTS ( SELECT * FROM sys.database_role_members " +
                            "WHERE role_principal_id = USER_ID('doktor_role') " +
                            "AND member_principal_id = USER_ID('doktor_user')) " +
                            "BEGIN EXEC sp_addrolemember 'doktor_role', 'doktor_user' END";
            stmt.executeUpdate(addDoktorToRole);

            String addHastaToRole =
                    "IF NOT EXISTS ( SELECT * FROM sys.database_role_members " +
                            "WHERE role_principal_id = USER_ID('hasta_role') " +
                            "AND member_principal_id = USER_ID('hasta_user')) " +
                            "BEGIN EXEC sp_addrolemember 'hasta_role', 'hasta_user' END";
            stmt.executeUpdate(addHastaToRole);

            // Doktor yetkileri
            stmt.executeUpdate("GRANT SELECT, INSERT ON KULLANICI TO doktor_role");
            stmt.executeUpdate("GRANT SELECT, INSERT ON HASTA_DOKTOR TO doktor_role");
            stmt.executeUpdate("GRANT SELECT, INSERT ON HASTA_OLCUM TO doktor_role");
            stmt.executeUpdate("GRANT SELECT, INSERT ON HASTA_BELIRTI TO doktor_role");
            stmt.executeUpdate("GRANT SELECT, INSERT ON HASTA_DIYET TO doktor_role");
            stmt.executeUpdate("GRANT SELECT, INSERT ON HASTA_DIYET_CHECK TO doktor_role");
            stmt.executeUpdate("GRANT SELECT, INSERT ON HASTA_EGZERSIZ TO doktor_role");
            stmt.executeUpdate("GRANT SELECT, INSERT ON HASTA_EGZERSIZ_CHECK TO doktor_role");
            stmt.executeUpdate("GRANT SELECT, INSERT ON HASTA_UYARI TO doktor_role");
            stmt.executeUpdate("GRANT SELECT ON BELIRTI_TURU TO doktor_role");
            stmt.executeUpdate("GRANT SELECT ON DIYET_TURU TO doktor_role");
            stmt.executeUpdate("GRANT SELECT ON EGZERSIZ_TURU TO doktor_role");
            stmt.executeUpdate("GRANT SELECT ON UYARI_TURU TO doktor_role");

            // Hasta yetkileri
            stmt.executeUpdate("GRANT SELECT ON KULLANICI TO hasta_role");
            stmt.executeUpdate("GRANT SELECT ON HASTA_DOKTOR TO hasta_role");
            stmt.executeUpdate("GRANT SELECT ON HASTA_DIYET TO hasta_role");
            stmt.executeUpdate("GRANT SELECT ON HASTA_EGZERSIZ TO hasta_role");
            stmt.executeUpdate("GRANT SELECT ON HASTA_UYARI TO hasta_role");
            stmt.executeUpdate("GRANT SELECT, INSERT ON HASTA_DIYET_CHECK TO hasta_role");
            stmt.executeUpdate("GRANT SELECT, INSERT ON HASTA_EGZERSIZ_CHECK TO hasta_role");
            stmt.executeUpdate("GRANT SELECT, INSERT ON HASTA_BELIRTI TO hasta_role");
            stmt.executeUpdate("GRANT SELECT, INSERT ON HASTA_OLCUM TO hasta_role");
            stmt.executeUpdate("GRANT SELECT ON BELIRTI_TURU TO hasta_role");
            stmt.executeUpdate("GRANT SELECT ON DIYET_TURU TO hasta_role");
            stmt.executeUpdate("GRANT SELECT ON EGZERSIZ_TURU TO hasta_role");
            stmt.executeUpdate("GRANT SELECT ON UYARI_TURU TO hasta_role");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
