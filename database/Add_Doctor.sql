INSERT INTO dbo.KULLANICI (
    tc_no,
    ad,
    soyad,
    sifre,
    email,
    dogum_tarihi,
    cinsiyet,
    rol,
    profil_resmi
)
VALUES (
    10000000000,                          -- TC
    N'Murat Emre',                        -- Ad
    N'Biçici',                            -- Soyad
    HASHBYTES('SHA2_256', N'admin123'),   -- Þifre
    N'murat@example.com',                 -- Email
    '2005-08-30',                         -- Doðum Tarihi
    'E',                                  -- Cinsiyet
    'DOKTOR',                             -- Rol
    (SELECT * FROM OPENROWSET(
        BULK N'D:\Prolab\BAHAR_PROJE_3\textures\murat_profil_resmi.png',
        SINGLE_BLOB
    ) AS Resim)                           -- Profil Resmi
);