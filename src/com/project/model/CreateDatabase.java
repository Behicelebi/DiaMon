package com.project.model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class CreateDatabase {
    public CreateDatabase(){
        String connectionUrl = "jdbc:sqlserver://localhost:1433;user=SA;password=diyabet1234;encrypt=true;trustServerCertificate=true";

        try (Connection conn = DriverManager.getConnection(connectionUrl); Statement stmt = conn.createStatement()) {

            String createDB = "IF NOT EXISTS (SELECT * FROM sys.databases WHERE name = 'PROJETEST') " +
                    "BEGIN CREATE DATABASE PROJETEST COLLATE Turkish_CI_AS END";
            stmt.executeUpdate(createDB);
            stmt.execute("USE PROJETEST");

            // KULLANICI tablosu
            String createKullaniciTable =
                    "IF NOT EXISTS (SELECT * FROM sysobjects WHERE name='KULLANICI' AND xtype='U') " +
                            "BEGIN CREATE TABLE KULLANICI (" +
                            "tc_no BIGINT PRIMARY KEY, " +
                            "ad NVARCHAR(50) NOT NULL, " +
                            "soyad NVARCHAR(50) NOT NULL, " +
                            "sifre VARBINARY(MAX) NOT NULL, " +
                            "email NVARCHAR(100) NOT NULL, " +
                            "dogum_tarihi DATE NOT NULL, " +
                            "cinsiyet char(1) NOT NULL, " +
                            "profil_resmi VARBINARY(MAX) NULL, " +
                            "rol NVARCHAR(10) NOT NULL) " +
                            "END";
            stmt.executeUpdate(createKullaniciTable);

            // HASTA_DOKTOR tablosu
            String createHastaDoktorTable =
                    "IF NOT EXISTS (SELECT * FROM sysobjects WHERE name='HASTA_DOKTOR' AND xtype='U') " +
                            "BEGIN CREATE TABLE HASTA_DOKTOR (" +
                            "doktor_tc BIGINT NOT NULL, " +
                            "hasta_tc BIGINT NOT NULL, " +
                            "FOREIGN KEY (doktor_tc) REFERENCES KULLANICI(tc_no), " +
                            "FOREIGN KEY (hasta_tc) REFERENCES KULLANICI(tc_no)) " +
                            "END";
            stmt.executeUpdate(createHastaDoktorTable);

            // BELIRTI_TURU tablosu
            String createBelirtiTuruTable =
                    "IF NOT EXISTS (SELECT * FROM sysobjects WHERE name='BELIRTI_TURU' AND xtype='U') " +
                            "BEGIN CREATE TABLE BELIRTI_TURU (" +
                            "belirti_turu_id INT IDENTITY(1,1) PRIMARY KEY, " +
                            "tur_adi NVARCHAR(50) NOT NULL)" +
                            "END";
            stmt.executeUpdate(createBelirtiTuruTable);

            // DIYET_TURU tablosu
            String createDiyetTuruTable =
                    "IF NOT EXISTS (SELECT * FROM sysobjects WHERE name='DIYET_TURU' AND xtype='U') " +
                            "BEGIN CREATE TABLE DIYET_TURU (" +
                            "diyet_turu_id INT IDENTITY(1,1) PRIMARY KEY, " +
                            "tur_adi NVARCHAR(50) NOT NULL)" +
                            "END";
            stmt.executeUpdate(createDiyetTuruTable);

            // EGZERSIZ_TURU tablosu
            String createEgzersizTuruTable =
                    "IF NOT EXISTS (SELECT * FROM sysobjects WHERE name='EGZERSIZ_TURU' AND xtype='U') " +
                            "BEGIN CREATE TABLE EGZERSIZ_TURU (" +
                            "egzersiz_turu_id INT IDENTITY(1,1) PRIMARY KEY, " +
                            "tur_adi NVARCHAR(50) NOT NULL)" +
                            "END";
            stmt.executeUpdate(createEgzersizTuruTable);

            // UYARI_TURU tablosu
            String createUyariTuruTable =
                    "IF NOT EXISTS (SELECT * FROM sysobjects WHERE name='UYARI_TURU' AND xtype='U') " +
                            "BEGIN CREATE TABLE UYARI_TURU (" +
                            "uyari_turu_id INT IDENTITY(1,1) PRIMARY KEY, " +
                            "tur_adi NVARCHAR(50) NOT NULL, " +
                            "tur_mesaji NVARCHAR(255) NOT NULL)" +
                            "END";
            stmt.executeUpdate(createUyariTuruTable);

            // HASTA_OLCUM tablosu
            String createHastaOlcumTable =
                    "IF NOT EXISTS (SELECT * FROM sysobjects WHERE name='HASTA_OLCUM' AND xtype='U') " +
                            "BEGIN CREATE TABLE HASTA_OLCUM (" +
                            "hasta_tc BIGINT NOT NULL, " +
                            "olcum_tarihi DATETIME NOT NULL, " +
                            "uyari_turu_id INT NOT NULL, " +
                            "olcum_degeri INT NOT NULL, " +
                            "FOREIGN KEY (hasta_tc) REFERENCES KULLANICI(tc_no), " +
                            "FOREIGN KEY (uyari_turu_id) REFERENCES UYARI_TURU(uyari_turu_id)) " +
                            "END";
            stmt.executeUpdate(createHastaOlcumTable);

            // HASTA_BELIRTI tablosu
            String createHastaBelirtiTable =
                    "IF NOT EXISTS (SELECT * FROM sysobjects WHERE name='HASTA_BELIRTI' AND xtype='U') " +
                            "BEGIN CREATE TABLE HASTA_BELIRTI (" +
                            "hasta_tc BIGINT NOT NULL, " +
                            "belirti_turu_id INT NOT NULL, " +
                            "FOREIGN KEY (hasta_tc) REFERENCES KULLANICI(tc_no), " +
                            "FOREIGN KEY (belirti_turu_id) REFERENCES BELIRTI_TURU(belirti_turu_id))" +
                            "END";
            stmt.executeUpdate(createHastaBelirtiTable);

            // HASTA_DIYET tablosu
            String createHastaDiyetTable =
                    "IF NOT EXISTS (SELECT * FROM sysobjects WHERE name='HASTA_DIYET' AND xtype='U') " +
                            "BEGIN CREATE TABLE HASTA_DIYET (" +
                            "hasta_tc BIGINT NOT NULL, " +
                            "diyet_turu_id INT NOT NULL, " +
                            "FOREIGN KEY (hasta_tc) REFERENCES KULLANICI(tc_no), " +
                            "FOREIGN KEY (diyet_turu_id) REFERENCES DIYET_TURU(diyet_turu_id))" +
                            "END";
            stmt.executeUpdate(createHastaDiyetTable);

            // HASTA_DIYET_CHECK tablosu
            String createHastaDiyetCheckTable =
                    "IF NOT EXISTS (SELECT * FROM sysobjects WHERE name='HASTA_DIYET_CHECK' AND xtype='U') " +
                            "BEGIN CREATE TABLE HASTA_DIYET_CHECK (" +
                            "hasta_tc BIGINT NOT NULL, " +
                            "tarih DATE NOT NULL, " +
                            "yapildi_mi BIT NOT NULL, " +
                            "FOREIGN KEY (hasta_tc) REFERENCES KULLANICI(tc_no))" +
                            "END";
            stmt.executeUpdate(createHastaDiyetCheckTable);

            // HASTA_EGZERSIZ tablosu
            String createHastaEgzersizTable =
                    "IF NOT EXISTS (SELECT * FROM sysobjects WHERE name='HASTA_EGZERSIZ' AND xtype='U') " +
                            "BEGIN CREATE TABLE HASTA_EGZERSIZ (" +
                            "hasta_tc BIGINT NOT NULL, " +
                            "egzersiz_turu_id INT NOT NULL, " +
                            "FOREIGN KEY (hasta_tc) REFERENCES KULLANICI(tc_no), " +
                            "FOREIGN KEY (egzersiz_turu_id) REFERENCES EGZERSIZ_TURU(egzersiz_turu_id))" +
                            "END";
            stmt.executeUpdate(createHastaEgzersizTable);

            // HASTA_EGZERSIZ_CHECK tablosu
            String createHastaEgzersizCheckTable =
                    "IF NOT EXISTS (SELECT * FROM sysobjects WHERE name='HASTA_EGZERSIZ_CHECK' AND xtype='U') " +
                            "BEGIN CREATE TABLE HASTA_EGZERSIZ_CHECK (" +
                            "hasta_tc BIGINT NOT NULL, " +
                            "tarih DATE NOT NULL, " +
                            "yapildi_mi BIT NOT NULL, " +
                            "FOREIGN KEY (hasta_tc) REFERENCES KULLANICI(tc_no))" +
                            "END";
            stmt.executeUpdate(createHastaEgzersizCheckTable);

            // HASTA_UYARI tablosu
            String createHastaUyariTable =
                    "IF NOT EXISTS (SELECT * FROM sysobjects WHERE name='HASTA_EGZERSIZ_CHECK' AND xtype='U') " +
                            "BEGIN CREATE TABLE HASTA_UYARI (" +
                            "hasta_tc BIGINT NOT NULL, " +
                            "tarih DATE NOT NULL, " +
                            "uyari_turu_id INT NOT NULL, " +
                            "FOREIGN KEY (hasta_tc) REFERENCES KULLANICI(tc_no)) " +
                            "END";
            stmt.executeUpdate(createHastaUyariTable);

            System.out.println("✅ Veritabanı ve tablolar başarıyla kuruldu.");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
