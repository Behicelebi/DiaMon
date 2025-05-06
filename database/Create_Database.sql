/*------------------------------------------------------------------
      1) TEMİZ BAŞLANGIÇ  (varsa önce sil)
------------------------------------------------------------------*/
IF OBJECT_ID('dbo.UYARI', 'U')             IS NOT NULL DROP TABLE dbo.UYARI;
IF OBJECT_ID('dbo.HASTA_DOKTOR', 'U')      IS NOT NULL DROP TABLE dbo.HASTA_DOKTOR;
IF OBJECT_ID('dbo.HASTA_EGZERSIZ_TAKIP', 'U') IS NOT NULL DROP TABLE dbo.HASTA_EGZERSIZ_TAKIP;
IF OBJECT_ID('dbo.HASTA_DIYET_TAKIP', 'U') IS NOT NULL DROP TABLE dbo.HASTA_DIYET_TAKIP;
IF OBJECT_ID('dbo.INSULIN_DOZ', 'U')       IS NOT NULL DROP TABLE dbo.INSULIN_DOZ;
IF OBJECT_ID('dbo.KAN_SEKERI_OLCUM', 'U')  IS NOT NULL DROP TABLE dbo.KAN_SEKERI_OLCUM;
IF OBJECT_ID('dbo.HASTA_BELIRTI', 'U')     IS NOT NULL DROP TABLE dbo.HASTA_BELIRTI;
IF OBJECT_ID('dbo.HASTA', 'U')             IS NOT NULL DROP TABLE dbo.HASTA;
IF OBJECT_ID('dbo.DOKTOR', 'U')            IS NOT NULL DROP TABLE dbo.DOKTOR;
IF OBJECT_ID('dbo.OLCUM_ZAMAN', 'U')       IS NOT NULL DROP TABLE dbo.OLCUM_ZAMAN;
IF OBJECT_ID('dbo.UYARI_TIPI', 'U')        IS NOT NULL DROP TABLE dbo.UYARI_TIPI;
IF OBJECT_ID('dbo.KAN_SEKERI_ARALIK', 'U') IS NOT NULL DROP TABLE dbo.KAN_SEKERI_ARALIK;
IF OBJECT_ID('dbo.BELIRTI', 'U')           IS NOT NULL DROP TABLE dbo.BELIRTI;
IF OBJECT_ID('dbo.EGZERSIZ_TURU', 'U')     IS NOT NULL DROP TABLE dbo.EGZERSIZ_TURU;
IF OBJECT_ID('dbo.DIYET_TURU', 'U')        IS NOT NULL DROP TABLE dbo.DIYET_TURU;
IF OBJECT_ID('dbo.KULLANICI', 'U')         IS NOT NULL DROP TABLE dbo.KULLANICI;

/*------------------------------------------------------------------
      2) REFERANS TABLOLARI  (sabit listeler)
------------------------------------------------------------------*/
CREATE TABLE dbo.DIYET_TURU (
    diyet_turu_id  INT          IDENTITY PRIMARY KEY,
    ad             NVARCHAR(30) COLLATE Turkish_CI_AS NOT NULL UNIQUE
);

CREATE TABLE dbo.EGZERSIZ_TURU (
    egzersiz_turu_id INT          IDENTITY PRIMARY KEY,
    ad               NVARCHAR(30) COLLATE Turkish_CI_AS NOT NULL UNIQUE
);

CREATE TABLE dbo.BELIRTI (
    belirti_id INT          IDENTITY PRIMARY KEY,
    ad         NVARCHAR(50) COLLATE Turkish_CI_AS NOT NULL UNIQUE
);

CREATE TABLE dbo.KAN_SEKERI_ARALIK (
    aralik_id  INT IDENTITY PRIMARY KEY,
    alt_deger  SMALLINT,
    ust_deger  SMALLINT,
    ad         NVARCHAR(30) COLLATE Turkish_CI_AS NOT NULL UNIQUE
);

CREATE TABLE dbo.UYARI_TIPI (
    uyari_tipi_id INT IDENTITY PRIMARY KEY,
    ad            NVARCHAR(30) COLLATE Turkish_CI_AS NOT NULL UNIQUE
);

CREATE TABLE dbo.OLCUM_ZAMAN (          -- Sabah / Öğle / İkindi …
    olcum_zaman_id INT IDENTITY PRIMARY KEY,
    ad             NVARCHAR(20) COLLATE Turkish_CI_AS NOT NULL UNIQUE,
    saat_baslangic TIME NOT NULL,
    saat_bitis     TIME NOT NULL
);

/*------------------------------------------------------------------
      3) ANA KİMLİK TABLOSU  (TC = PK)
------------------------------------------------------------------*/
CREATE TABLE dbo.KULLANICI (
    tc_no        BIGINT       NOT NULL PRIMARY KEY,       -- TC Kimlik No
    ad           NVARCHAR(50) COLLATE Turkish_CI_AS NOT NULL,
    soyad        NVARCHAR(50) COLLATE Turkish_CI_AS NOT NULL,
    sifre_hash   VARBINARY(MAX)  NOT NULL,                -- SHA‑256 + salt
    email        NVARCHAR(100) COLLATE Turkish_CI_AS NOT NULL UNIQUE,
    dogum_tarihi DATE         NOT NULL,
    cinsiyet     CHAR(1)      NOT NULL CHECK (cinsiyet IN ('E','K')),
    profil_resmi VARBINARY(MAX) NULL,
    rol          VARCHAR(10)  NOT NULL CHECK (rol IN ('DOKTOR','HASTA')) -- basit RBAC
);

/*------------------------------------------------------------------
      4) DOKTOR / HASTA ALT TABLOLARI
------------------------------------------------------------------*/
CREATE TABLE dbo.DOKTOR (
    tc_no BIGINT PRIMARY KEY                          -- FK → KULLANICI
        REFERENCES dbo.KULLANICI(tc_no)
        ON DELETE CASCADE
);

CREATE TABLE dbo.HASTA (
    tc_no     BIGINT PRIMARY KEY                      -- FK → KULLANICI
        REFERENCES dbo.KULLANICI(tc_no)
        ON DELETE CASCADE,
    doktor_tc BIGINT NOT NULL                         -- hangi doktora bağlı
        REFERENCES dbo.DOKTOR(tc_no)
        ON DELETE NO ACTION
);

/*------------------------------------------------------------------
      5) OPERASYONEL TABLOLAR
------------------------------------------------------------------*/
CREATE TABLE dbo.KAN_SEKERI_OLCUM (
    hasta_tc        BIGINT      NOT NULL
        REFERENCES dbo.HASTA(tc_no)
        ON DELETE CASCADE,
    tarih_saat      DATETIME2(0) NOT NULL,
    olcum_zaman_id  INT NOT NULL
        REFERENCES dbo.OLCUM_ZAMAN(olcum_zaman_id),
    deger_mgdl      SMALLINT    NOT NULL
        CHECK (deger_mgdl BETWEEN 20 AND 600),
    CONSTRAINT PK_KanSekeri PRIMARY KEY (hasta_tc, tarih_saat, olcum_zaman_id)
);

CREATE TABLE dbo.INSULIN_DOZ (
    hasta_tc   BIGINT       NOT NULL
        REFERENCES dbo.HASTA(tc_no)
        ON DELETE CASCADE,
    tarih_saat DATETIME2(0) NOT NULL,
    doz_ml     TINYINT      NOT NULL
        CHECK (doz_ml BETWEEN 0 AND 10),
    CONSTRAINT PK_Insulin PRIMARY KEY (hasta_tc, tarih_saat)
);

CREATE TABLE dbo.HASTA_DIYET_TAKIP (
    hasta_tc      BIGINT   NOT NULL REFERENCES dbo.HASTA(tc_no) ON DELETE CASCADE,
    tarih         DATE     NOT NULL,
    diyet_turu_id INT      NOT NULL REFERENCES dbo.DIYET_TURU(diyet_turu_id),
    uygulandi     BIT      NOT NULL,
    CONSTRAINT PK_DiyetTakip PRIMARY KEY (hasta_tc, tarih, diyet_turu_id)
);

CREATE TABLE dbo.HASTA_EGZERSIZ_TAKIP (
    hasta_tc        BIGINT  NOT NULL REFERENCES dbo.HASTA(tc_no) ON DELETE CASCADE,
    tarih           DATE    NOT NULL,
    egzersiz_turu_id INT     NOT NULL REFERENCES dbo.EGZERSIZ_TURU(egzersiz_turu_id),
    yapildi         BIT     NOT NULL,
    CONSTRAINT PK_EgzersizTakip PRIMARY KEY (hasta_tc, tarih, egzersiz_turu_id)
);

CREATE TABLE dbo.HASTA_BELIRTI (
    hasta_tc    BIGINT       NOT NULL REFERENCES dbo.HASTA(tc_no) ON DELETE CASCADE,
    tarih_saat  DATETIME2(0) NOT NULL,
    belirti_id  INT          NOT NULL REFERENCES dbo.BELIRTI(belirti_id),
    CONSTRAINT PK_HastaBelirti PRIMARY KEY (hasta_tc, tarih_saat, belirti_id)
);

CREATE TABLE dbo.HASTA_DOKTOR (          -- hasta <-> doktor ilişkisi
    doktor_tc BIGINT NOT NULL REFERENCES dbo.DOKTOR(tc_no) ON DELETE NO ACTION,
    hasta_tc  BIGINT NOT NULL REFERENCES dbo.HASTA(tc_no)  ON DELETE CASCADE,
    CONSTRAINT PK_HastaDoktor PRIMARY KEY (doktor_tc, hasta_tc)
);

CREATE TABLE dbo.UYARI (
    uyari_id       INT IDENTITY PRIMARY KEY,
    hasta_tc       BIGINT NOT NULL REFERENCES dbo.HASTA(tc_no)   ON DELETE CASCADE,
    doktor_tc      BIGINT NOT NULL REFERENCES dbo.DOKTOR(tc_no)  ON DELETE NO ACTION,
    uyari_tipi_id  INT    NOT NULL REFERENCES dbo.UYARI_TIPI(uyari_tipi_id),
    tarih_saat     DATETIME2(0) NOT NULL,
    mesaj          NVARCHAR(255) COLLATE Turkish_CI_AS NOT NULL
);
