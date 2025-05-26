-- Veritabanýný oluþtur
IF NOT EXISTS (SELECT * FROM sys.databases WHERE name = N'DiyabetSistem')
BEGIN
    CREATE DATABASE DiyabetSistem COLLATE Turkish_CI_AS;
END
GO

USE DiyabetSistem;
GO

-- KULLANICI tablosu
IF NOT EXISTS (SELECT * FROM sysobjects WHERE name='KULLANICI' AND xtype='U')
BEGIN
    CREATE TABLE KULLANICI (
        tc_no BIGINT PRIMARY KEY,
        ad NVARCHAR(50) NOT NULL,
        soyad NVARCHAR(50) NOT NULL,
        sifre VARBINARY(MAX) NOT NULL,
        email NVARCHAR(100) NOT NULL,
        dogum_tarihi DATE NOT NULL,
        cinsiyet CHAR(1) NOT NULL,
        profil_resmi VARBINARY(MAX) NULL,
        rol NVARCHAR(10) NOT NULL
    );
END
GO

-- HASTA_DOKTOR tablosu
IF NOT EXISTS (SELECT * FROM sysobjects WHERE name='HASTA_DOKTOR' AND xtype='U')
BEGIN
    CREATE TABLE HASTA_DOKTOR (
        doktor_tc BIGINT NOT NULL,
        hasta_tc BIGINT NOT NULL,
        FOREIGN KEY (doktor_tc) REFERENCES KULLANICI(tc_no),
        FOREIGN KEY (hasta_tc) REFERENCES KULLANICI(tc_no)
    );
END
GO

-- BELIRTI_TURU tablosu
IF NOT EXISTS (SELECT * FROM sysobjects WHERE name='BELIRTI_TURU' AND xtype='U')
BEGIN
    CREATE TABLE BELIRTI_TURU (
        belirti_turu_id INT IDENTITY(1,1) PRIMARY KEY,
        tur_adi NVARCHAR(50) NOT NULL
    );
END
GO

-- DIYET_TURU tablosu
IF NOT EXISTS (SELECT * FROM sysobjects WHERE name='DIYET_TURU' AND xtype='U')
BEGIN
    CREATE TABLE DIYET_TURU (
        diyet_turu_id INT IDENTITY(1,1) PRIMARY KEY,
        tur_adi NVARCHAR(50) NOT NULL
    );
END
GO

-- EGZERSIZ_TURU tablosu
IF NOT EXISTS (SELECT * FROM sysobjects WHERE name='EGZERSIZ_TURU' AND xtype='U')
BEGIN
    CREATE TABLE EGZERSIZ_TURU (
        egzersiz_turu_id INT IDENTITY(1,1) PRIMARY KEY,
        tur_adi NVARCHAR(50) NOT NULL
    );
END
GO

-- UYARI_TURU tablosu
IF NOT EXISTS (SELECT * FROM sysobjects WHERE name='UYARI_TURU' AND xtype='U')
BEGIN
    CREATE TABLE UYARI_TURU (
        uyari_turu_id INT IDENTITY(1,1) PRIMARY KEY,
        tur_adi NVARCHAR(50) NOT NULL,
        tur_mesaji NVARCHAR(255) NOT NULL
    );
END
GO

-- HASTA_OLCUM tablosu
IF NOT EXISTS (SELECT * FROM sysobjects WHERE name='HASTA_OLCUM' AND xtype='U')
BEGIN
    CREATE TABLE HASTA_OLCUM (
        hasta_tc BIGINT NOT NULL,
        olcum_tarihi DATETIME NOT NULL,
        uyari_turu_id INT NOT NULL,
        olcum_degeri INT NOT NULL,
        FOREIGN KEY (hasta_tc) REFERENCES KULLANICI(tc_no),
        FOREIGN KEY (uyari_turu_id) REFERENCES UYARI_TURU(uyari_turu_id)
    );
END
GO

-- HASTA_BELIRTI tablosu
IF NOT EXISTS (SELECT * FROM sysobjects WHERE name='HASTA_BELIRTI' AND xtype='U')
BEGIN
    CREATE TABLE HASTA_BELIRTI (
        hasta_tc BIGINT NOT NULL,
        belirti_turu_id INT NOT NULL,
        FOREIGN KEY (hasta_tc) REFERENCES KULLANICI(tc_no),
        FOREIGN KEY (belirti_turu_id) REFERENCES BELIRTI_TURU(belirti_turu_id)
    );
END
GO

-- HASTA_DIYET tablosu
IF NOT EXISTS (SELECT * FROM sysobjects WHERE name='HASTA_DIYET' AND xtype='U')
BEGIN
    CREATE TABLE HASTA_DIYET (
        hasta_tc BIGINT NOT NULL,
        diyet_turu_id INT NOT NULL,
        FOREIGN KEY (hasta_tc) REFERENCES KULLANICI(tc_no),
        FOREIGN KEY (diyet_turu_id) REFERENCES DIYET_TURU(diyet_turu_id)
    );
END
GO

-- HASTA_DIYET_CHECK tablosu
IF NOT EXISTS (SELECT * FROM sysobjects WHERE name='HASTA_DIYET_CHECK' AND xtype='U')
BEGIN
    CREATE TABLE HASTA_DIYET_CHECK (
        hasta_tc BIGINT NOT NULL,
        tarih DATE NOT NULL,
        yapildi_mi BIT NOT NULL,
        FOREIGN KEY (hasta_tc) REFERENCES KULLANICI(tc_no)
    );
END
GO

-- HASTA_EGZERSIZ tablosu
IF NOT EXISTS (SELECT * FROM sysobjects WHERE name='HASTA_EGZERSIZ' AND xtype='U')
BEGIN
    CREATE TABLE HASTA_EGZERSIZ (
        hasta_tc BIGINT NOT NULL,
        egzersiz_turu_id INT NOT NULL,
        FOREIGN KEY (hasta_tc) REFERENCES KULLANICI(tc_no),
        FOREIGN KEY (egzersiz_turu_id) REFERENCES EGZERSIZ_TURU(egzersiz_turu_id)
    );
END
GO

-- HASTA_EGZERSIZ_CHECK tablosu
IF NOT EXISTS (SELECT * FROM sysobjects WHERE name='HASTA_EGZERSIZ_CHECK' AND xtype='U')
BEGIN
    CREATE TABLE HASTA_EGZERSIZ_CHECK (
        hasta_tc BIGINT NOT NULL,
        tarih DATE NOT NULL,
        yapildi_mi BIT NOT NULL,
        FOREIGN KEY (hasta_tc) REFERENCES KULLANICI(tc_no)
    );
END
GO

-- HASTA_UYARI tablosu
IF NOT EXISTS (SELECT * FROM sysobjects WHERE name='HASTA_UYARI' AND xtype='U')
BEGIN
    CREATE TABLE HASTA_UYARI (
        hasta_tc BIGINT NOT NULL,
        tarih DATE NOT NULL,
        uyari_turu_id INT NOT NULL,
        FOREIGN KEY (hasta_tc) REFERENCES KULLANICI(tc_no)
    );
END
GO

-- HASTA_INSULIN tablosu
IF NOT EXISTS (SELECT * FROM sysobjects WHERE name='HASTA_INSULIN' AND xtype='U')
BEGIN
    CREATE TABLE HASTA_INSULIN (
        hasta_tc BIGINT NOT NULL,
        insulin_tarihi DATETIME NOT NULL,
        uyari_turu_id INT NOT NULL,
        insulin_degeri INT NOT NULL,
        FOREIGN KEY (hasta_tc) REFERENCES KULLANICI(tc_no),
        FOREIGN KEY (uyari_turu_id) REFERENCES UYARI_TURU(uyari_turu_id)
    );
END
GO