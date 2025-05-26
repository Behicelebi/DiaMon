-- 1. DOKTOR EKLE
IF NOT EXISTS (SELECT * FROM KULLANICI WHERE tc_no = 10000000000)
BEGIN
    INSERT INTO KULLANICI (
        tc_no, ad, soyad, sifre, email, dogum_tarihi, cinsiyet, profil_resmi, rol
    )
    VALUES (
        10000000000,
        N'Murat Emre',
        N'Bi�ici',
        HASHBYTES('SHA2_256', N'admin123'),
        N'murat@example.com',
        '2005-08-30',
        'E',
        (SELECT * FROM OPENROWSET(
            BULK N'D:\PROLAB\BAHAR_PROJE_3\textures\murat_profil_resmi.png',
            SINGLE_BLOB
        ) AS Resim),
        'DOKTOR'
    )
END
GO

-- 2. BELIRTI_TURU EKLE
IF NOT EXISTS (SELECT * FROM BELIRTI_TURU WHERE tur_adi = N'Poli�ri')
    INSERT INTO BELIRTI_TURU (tur_adi) VALUES (N'Poli�ri');
IF NOT EXISTS (SELECT * FROM BELIRTI_TURU WHERE tur_adi = N'Polifaji')
    INSERT INTO BELIRTI_TURU (tur_adi) VALUES (N'Polifaji');
IF NOT EXISTS (SELECT * FROM BELIRTI_TURU WHERE tur_adi = N'Polidipsi')
    INSERT INTO BELIRTI_TURU (tur_adi) VALUES (N'Polidipsi');
IF NOT EXISTS (SELECT * FROM BELIRTI_TURU WHERE tur_adi = N'N�ropati')
    INSERT INTO BELIRTI_TURU (tur_adi) VALUES (N'N�ropati');
IF NOT EXISTS (SELECT * FROM BELIRTI_TURU WHERE tur_adi = N'Kilo Kayb�')
    INSERT INTO BELIRTI_TURU (tur_adi) VALUES (N'Kilo Kayb�');
IF NOT EXISTS (SELECT * FROM BELIRTI_TURU WHERE tur_adi = N'Yorgunluk')
    INSERT INTO BELIRTI_TURU (tur_adi) VALUES (N'Yorgunluk');
IF NOT EXISTS (SELECT * FROM BELIRTI_TURU WHERE tur_adi = N'Yaralar�n Yava� �yile�mesi')
    INSERT INTO BELIRTI_TURU (tur_adi) VALUES (N'Yaralar�n Yava� �yile�mesi');
IF NOT EXISTS (SELECT * FROM BELIRTI_TURU WHERE tur_adi = N'Bulan�k G�rme')
    INSERT INTO BELIRTI_TURU (tur_adi) VALUES (N'Bulan�k G�rme');
GO

-- 3. DIYET_TURU EKLE
IF NOT EXISTS (SELECT * FROM DIYET_TURU WHERE tur_adi = N'Az �ekerli Diyet')
    INSERT INTO DIYET_TURU (tur_adi) VALUES (N'Az �ekerli Diyet');
IF NOT EXISTS (SELECT * FROM DIYET_TURU WHERE tur_adi = N'Dengeli Beslenme')
    INSERT INTO DIYET_TURU (tur_adi) VALUES (N'Dengeli Beslenme');
IF NOT EXISTS (SELECT * FROM DIYET_TURU WHERE tur_adi = N'�ekersiz Diyet')
    INSERT INTO DIYET_TURU (tur_adi) VALUES (N'�ekersiz Diyet');
GO

-- 4. EGZERSIZ_TURU EKLE
IF NOT EXISTS (SELECT * FROM EGZERSIZ_TURU WHERE tur_adi = N'Bisiklet')
    INSERT INTO EGZERSIZ_TURU (tur_adi) VALUES (N'Bisiklet');
IF NOT EXISTS (SELECT * FROM EGZERSIZ_TURU WHERE tur_adi = N'Klinik Egzersiz')
    INSERT INTO EGZERSIZ_TURU (tur_adi) VALUES (N'Klinik Egzersiz');
IF NOT EXISTS (SELECT * FROM EGZERSIZ_TURU WHERE tur_adi = N'Y�r�y��')
    INSERT INTO EGZERSIZ_TURU (tur_adi) VALUES (N'Y�r�y��');
GO

-- 5. UYARI_TURU EKLE
IF NOT EXISTS (SELECT * FROM UYARI_TURU WHERE tur_adi = N'Acil Uyar�')
    INSERT INTO UYARI_TURU (tur_adi, tur_mesaji) VALUES (N'Acil Uyar�', N'Hastan�n kan �ekeri seviyesi 70 mg/dL''nin alt�na d��t�. Hipoglisemi riski! H�zl� m�dahale gerekebilir.');
IF NOT EXISTS (SELECT * FROM UYARI_TURU WHERE tur_adi = N'Uyar� Yok')
    INSERT INTO UYARI_TURU (tur_adi, tur_mesaji) VALUES (N'Uyar� Yok', N'Kan �ekeri seviyesi normal aral�kta. Hi�bir i�lem gerekmez.');
IF NOT EXISTS (SELECT * FROM UYARI_TURU WHERE tur_adi = N'Takip Uyar�s�')
    INSERT INTO UYARI_TURU (tur_adi, tur_mesaji) VALUES (N'Takip Uyar�s�', N'Hastan�n kan �ekeri 111-150 mg/dL aras�nda. Durum izlenmeli.');
IF NOT EXISTS (SELECT * FROM UYARI_TURU WHERE tur_adi = N'�zleme Uyar�s�')
    INSERT INTO UYARI_TURU (tur_adi, tur_mesaji) VALUES (N'�zleme Uyar�s�', N'Hastan�n kan �ekeri 151-200 mg/dL aras�nda. Diyabet kontrol� gereklidir.');
IF NOT EXISTS (SELECT * FROM UYARI_TURU WHERE tur_adi = N'Acil M�dahale Uyar�s�')
    INSERT INTO UYARI_TURU (tur_adi, tur_mesaji) VALUES (N'Acil M�dahale Uyar�s�', N'Hastan�n kan �ekeri 200 mg/dL''nin �zerinde. Hiperglisemi durumu. Acil m�dahale gerekebilir.');
IF NOT EXISTS (SELECT * FROM UYARI_TURU WHERE tur_adi = N'�l��m Eksik Uyar�s�')
    INSERT INTO UYARI_TURU (tur_adi, tur_mesaji) VALUES (N'�l��m Eksik Uyar�s�', N'Hasta g�n boyunca kan �ekeri �l��m� yapmam��t�r. Acil takip �nerilir');
IF NOT EXISTS (SELECT * FROM UYARI_TURU WHERE tur_adi = N'�l��m Yetersiz Uyar�s�')
    INSERT INTO UYARI_TURU (tur_adi, tur_mesaji) VALUES (N'�l��m Yetersiz Uyar�s�', N'Hastan�n g�nl�k kan �ekeri �l��m say�s� yetersiz (�l��m < 3). Durum izlenmelidir.');
IF NOT EXISTS (SELECT * FROM UYARI_TURU WHERE tur_adi = N'Veri Yok Uyar�s�')
    INSERT INTO UYARI_TURU (tur_adi, tur_mesaji) VALUES (N'Veri Yok Uyar�s�', N'�neri verisi bulunmamakta! �l��mlerinizi zaman�nda yap�n�z.');
IF NOT EXISTS (SELECT * FROM UYARI_TURU WHERE tur_adi = N'Eksik Veri Uyar�s�')
    INSERT INTO UYARI_TURU (tur_adi, tur_mesaji) VALUES (N'Eksik Veri Uyar�s�', N'Eksik �l��m(ler) bulunmakta! Ortalama hesaplan�rken bu de�er(ler) kullan�lamad�.');
IF NOT EXISTS (SELECT * FROM UYARI_TURU WHERE tur_adi = N'Yetersiz Veri Uyar�s�')
    INSERT INTO UYARI_TURU (tur_adi, tur_mesaji) VALUES (N'Yetersiz Veri Uyar�s�', N'Yetersiz veri! Ortalama hesaplamas� g�venilir de�ildir.');
GO
