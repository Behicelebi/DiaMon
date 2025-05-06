UPDATE KULLANICI
SET profil_resmi = (
    SELECT *
    FROM OPENROWSET(
        BULK 'D:\Prolab\BAHAR_PROJE_3\textures\profil_resmi.png',
        SINGLE_BLOB
    ) AS Resim
)
WHERE tc_no = 10000000010;
