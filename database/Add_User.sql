INSERT INTO dbo.KULLANICI (
    tc_no,
    ad,
    soyad,
    sifre_hash,
    email,
    dogum_tarihi,
    cinsiyet,
    rol
)
VALUES (
    10000000010,					   -- TC
    N'Murat',                          -- Ad
    N'Bi�ici',						   -- Soyad
    HASHBYTES('SHA2_256', N'abc123'),  -- �ifre (Hashed)
    N'murat@example.com',			   -- Email
    '2000-01-01',                      -- Do�um Tarihi
    'E',                               -- Cinsiyet
    'DOKTOR'                           -- Rol
);