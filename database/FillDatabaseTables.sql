-- 1. DOKTOR EKLE
IF NOT EXISTS (SELECT * FROM KULLANICI WHERE tc_no = 10000000000)
BEGIN
    INSERT INTO KULLANICI (
        tc_no, ad, soyad, sifre, email, dogum_tarihi, cinsiyet, profil_resmi, rol
    )
    VALUES (
        10000000000,
        N'Murat Emre',
        N'Biçici',
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
IF NOT EXISTS (SELECT * FROM BELIRTI_TURU WHERE tur_adi = N'Poliüri')
    INSERT INTO BELIRTI_TURU (tur_adi) VALUES (N'Poliüri');
IF NOT EXISTS (SELECT * FROM BELIRTI_TURU WHERE tur_adi = N'Polifaji')
    INSERT INTO BELIRTI_TURU (tur_adi) VALUES (N'Polifaji');
IF NOT EXISTS (SELECT * FROM BELIRTI_TURU WHERE tur_adi = N'Polidipsi')
    INSERT INTO BELIRTI_TURU (tur_adi) VALUES (N'Polidipsi');
IF NOT EXISTS (SELECT * FROM BELIRTI_TURU WHERE tur_adi = N'Nöropati')
    INSERT INTO BELIRTI_TURU (tur_adi) VALUES (N'Nöropati');
IF NOT EXISTS (SELECT * FROM BELIRTI_TURU WHERE tur_adi = N'Kilo Kaybý')
    INSERT INTO BELIRTI_TURU (tur_adi) VALUES (N'Kilo Kaybý');
IF NOT EXISTS (SELECT * FROM BELIRTI_TURU WHERE tur_adi = N'Yorgunluk')
    INSERT INTO BELIRTI_TURU (tur_adi) VALUES (N'Yorgunluk');
IF NOT EXISTS (SELECT * FROM BELIRTI_TURU WHERE tur_adi = N'Yaralarýn Yavaþ Ýyileþmesi')
    INSERT INTO BELIRTI_TURU (tur_adi) VALUES (N'Yaralarýn Yavaþ Ýyileþmesi');
IF NOT EXISTS (SELECT * FROM BELIRTI_TURU WHERE tur_adi = N'Bulanýk Görme')
    INSERT INTO BELIRTI_TURU (tur_adi) VALUES (N'Bulanýk Görme');
GO

-- 3. DIYET_TURU EKLE
IF NOT EXISTS (SELECT * FROM DIYET_TURU WHERE tur_adi = N'Az Þekerli Diyet')
    INSERT INTO DIYET_TURU (tur_adi) VALUES (N'Az Þekerli Diyet');
IF NOT EXISTS (SELECT * FROM DIYET_TURU WHERE tur_adi = N'Dengeli Beslenme')
    INSERT INTO DIYET_TURU (tur_adi) VALUES (N'Dengeli Beslenme');
IF NOT EXISTS (SELECT * FROM DIYET_TURU WHERE tur_adi = N'Þekersiz Diyet')
    INSERT INTO DIYET_TURU (tur_adi) VALUES (N'Þekersiz Diyet');
GO

-- 4. EGZERSIZ_TURU EKLE
IF NOT EXISTS (SELECT * FROM EGZERSIZ_TURU WHERE tur_adi = N'Bisiklet')
    INSERT INTO EGZERSIZ_TURU (tur_adi) VALUES (N'Bisiklet');
IF NOT EXISTS (SELECT * FROM EGZERSIZ_TURU WHERE tur_adi = N'Klinik Egzersiz')
    INSERT INTO EGZERSIZ_TURU (tur_adi) VALUES (N'Klinik Egzersiz');
IF NOT EXISTS (SELECT * FROM EGZERSIZ_TURU WHERE tur_adi = N'Yürüyüþ')
    INSERT INTO EGZERSIZ_TURU (tur_adi) VALUES (N'Yürüyüþ');
GO

-- 5. UYARI_TURU EKLE
IF NOT EXISTS (SELECT * FROM UYARI_TURU WHERE tur_adi = N'Acil Uyarý')
    INSERT INTO UYARI_TURU (tur_adi, tur_mesaji) VALUES (N'Acil Uyarý', N'Hastanýn kan þekeri seviyesi 70 mg/dL''nin altýna düþtü. Hipoglisemi riski! Hýzlý müdahale gerekebilir.');
IF NOT EXISTS (SELECT * FROM UYARI_TURU WHERE tur_adi = N'Uyarý Yok')
    INSERT INTO UYARI_TURU (tur_adi, tur_mesaji) VALUES (N'Uyarý Yok', N'Kan þekeri seviyesi normal aralýkta. Hiçbir iþlem gerekmez.');
IF NOT EXISTS (SELECT * FROM UYARI_TURU WHERE tur_adi = N'Takip Uyarýsý')
    INSERT INTO UYARI_TURU (tur_adi, tur_mesaji) VALUES (N'Takip Uyarýsý', N'Hastanýn kan þekeri 111-150 mg/dL arasýnda. Durum izlenmeli.');
IF NOT EXISTS (SELECT * FROM UYARI_TURU WHERE tur_adi = N'Ýzleme Uyarýsý')
    INSERT INTO UYARI_TURU (tur_adi, tur_mesaji) VALUES (N'Ýzleme Uyarýsý', N'Hastanýn kan þekeri 151-200 mg/dL arasýnda. Diyabet kontrolü gereklidir.');
IF NOT EXISTS (SELECT * FROM UYARI_TURU WHERE tur_adi = N'Acil Müdahale Uyarýsý')
    INSERT INTO UYARI_TURU (tur_adi, tur_mesaji) VALUES (N'Acil Müdahale Uyarýsý', N'Hastanýn kan þekeri 200 mg/dL''nin üzerinde. Hiperglisemi durumu. Acil müdahale gerekebilir.');
IF NOT EXISTS (SELECT * FROM UYARI_TURU WHERE tur_adi = N'Ölçüm Eksik Uyarýsý')
    INSERT INTO UYARI_TURU (tur_adi, tur_mesaji) VALUES (N'Ölçüm Eksik Uyarýsý', N'Hasta gün boyunca kan þekeri ölçümü yapmamýþtýr. Acil takip önerilir');
IF NOT EXISTS (SELECT * FROM UYARI_TURU WHERE tur_adi = N'Ölçüm Yetersiz Uyarýsý')
    INSERT INTO UYARI_TURU (tur_adi, tur_mesaji) VALUES (N'Ölçüm Yetersiz Uyarýsý', N'Hastanýn günlük kan þekeri ölçüm sayýsý yetersiz (ölçüm < 3). Durum izlenmelidir.');
IF NOT EXISTS (SELECT * FROM UYARI_TURU WHERE tur_adi = N'Veri Yok Uyarýsý')
    INSERT INTO UYARI_TURU (tur_adi, tur_mesaji) VALUES (N'Veri Yok Uyarýsý', N'Öneri verisi bulunmamakta! Ölçümlerinizi zamanýnda yapýnýz.');
IF NOT EXISTS (SELECT * FROM UYARI_TURU WHERE tur_adi = N'Eksik Veri Uyarýsý')
    INSERT INTO UYARI_TURU (tur_adi, tur_mesaji) VALUES (N'Eksik Veri Uyarýsý', N'Eksik ölçüm(ler) bulunmakta! Ortalama hesaplanýrken bu deðer(ler) kullanýlamadý.');
IF NOT EXISTS (SELECT * FROM UYARI_TURU WHERE tur_adi = N'Yetersiz Veri Uyarýsý')
    INSERT INTO UYARI_TURU (tur_adi, tur_mesaji) VALUES (N'Yetersiz Veri Uyarýsý', N'Yetersiz veri! Ortalama hesaplamasý güvenilir deðildir.');
GO
