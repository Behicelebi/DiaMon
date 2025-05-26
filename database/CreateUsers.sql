-- 1. LOGIN OLUÞTURMA
IF NOT EXISTS (SELECT * FROM sys.sql_logins WHERE name = N'doktor_login')
BEGIN
    CREATE LOGIN doktor_login WITH PASSWORD = 'doktor123';
END
GO

IF NOT EXISTS (SELECT * FROM sys.sql_logins WHERE name = N'hasta_login')
BEGIN
    CREATE LOGIN hasta_login WITH PASSWORD = 'hasta123';
END
GO

-- 2. VERÝTABANINA GEÇ
USE DiyabetSistem;
GO

-- 3. DATABASE USER OLUÞTURMA
IF NOT EXISTS (SELECT * FROM sys.database_principals WHERE name = N'doktor_user')
BEGIN
    CREATE USER doktor_user FOR LOGIN doktor_login WITH DEFAULT_SCHEMA = dbo;
END
GO

IF NOT EXISTS (SELECT * FROM sys.database_principals WHERE name = N'hasta_user')
BEGIN
    CREATE USER hasta_user FOR LOGIN hasta_login WITH DEFAULT_SCHEMA = dbo;
END
GO

-- 4. ROLE OLUÞTURMA
IF NOT EXISTS (SELECT * FROM sys.database_principals WHERE name = N'doktor_role' AND type = 'R')
BEGIN
    CREATE ROLE doktor_role;
END
GO

IF NOT EXISTS (SELECT * FROM sys.database_principals WHERE name = N'hasta_role' AND type = 'R')
BEGIN
    CREATE ROLE hasta_role;
END
GO

-- 5. ROLE ÜYELÝKLERÝ
IF NOT EXISTS (
    SELECT 1 FROM sys.database_role_members
    WHERE role_principal_id = USER_ID('doktor_role') AND member_principal_id = USER_ID('doktor_user')
)
BEGIN
    EXEC sp_addrolemember N'doktor_role', N'doktor_user';
END
GO

IF NOT EXISTS (
    SELECT 1 FROM sys.database_role_members
    WHERE role_principal_id = USER_ID('hasta_role') AND member_principal_id = USER_ID('hasta_user')
)
BEGIN
    EXEC sp_addrolemember N'hasta_role', N'hasta_user';
END
GO

-- 6. DOKTOR ROLE YETKÝLERÝ
GRANT SELECT, INSERT ON KULLANICI TO doktor_role;
GRANT SELECT, INSERT ON HASTA_DOKTOR TO doktor_role;
GRANT SELECT, INSERT ON HASTA_OLCUM TO doktor_role;
GRANT SELECT, INSERT ON HASTA_BELIRTI TO doktor_role;
GRANT SELECT, INSERT ON HASTA_DIYET TO doktor_role;
GRANT SELECT, INSERT ON HASTA_DIYET_CHECK TO doktor_role;
GRANT SELECT, INSERT ON HASTA_EGZERSIZ TO doktor_role;
GRANT SELECT, INSERT ON HASTA_EGZERSIZ_CHECK TO doktor_role;
GRANT SELECT, INSERT ON HASTA_UYARI TO doktor_role;
GRANT SELECT, INSERT ON HASTA_INSULIN TO doktor_role;
GRANT SELECT ON BELIRTI_TURU TO doktor_role;
GRANT SELECT ON DIYET_TURU TO doktor_role;
GRANT SELECT ON EGZERSIZ_TURU TO doktor_role;
GRANT SELECT ON UYARI_TURU TO doktor_role;
GO

-- 7. HASTA ROLE YETKÝLERÝ
GRANT SELECT, UPDATE ON KULLANICI TO hasta_role;
GRANT SELECT ON HASTA_DOKTOR TO hasta_role;
GRANT SELECT ON HASTA_DIYET TO hasta_role;
GRANT SELECT ON HASTA_EGZERSIZ TO hasta_role;
GRANT SELECT, INSERT ON HASTA_UYARI TO hasta_role;
GRANT SELECT, INSERT ON HASTA_DIYET_CHECK TO hasta_role;
GRANT SELECT, INSERT ON HASTA_EGZERSIZ_CHECK TO hasta_role;
GRANT SELECT, INSERT ON HASTA_BELIRTI TO hasta_role;
GRANT SELECT, INSERT ON HASTA_OLCUM TO hasta_role;
GRANT SELECT, INSERT ON HASTA_INSULIN TO hasta_role;
GRANT SELECT ON BELIRTI_TURU TO hasta_role;
GRANT SELECT ON DIYET_TURU TO hasta_role;
GRANT SELECT ON EGZERSIZ_TURU TO hasta_role;
GRANT SELECT ON UYARI_TURU TO hasta_role;
GO
