package com.project.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.*;
import java.security.MessageDigest;

public class FillDatabaseTables {

    private final String connectionUrl = "jdbc:sqlserver://localhost:1433;databaseName=PROJETEST;user=SA;password=diyabet1234;encrypt=false";

    public FillDatabaseTables() {
        try (Connection conn = DriverManager.getConnection(connectionUrl)) {
            addDoctor(conn);
            addBelirtiTypes(conn);
            addDiyetTypes(conn);
            addEgzersizTypes(conn);
            addUyariTypes(conn);
            System.out.println("✅ Varsayılan veriler başarıyla yüklendi.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addDoctor(Connection conn) throws Exception {
        String sql =
                "IF NOT EXISTS (SELECT * FROM KULLANICI WHERE tc_no = ?) " +
                        "BEGIN " +
                        "INSERT INTO KULLANICI (tc_no, ad, soyad, sifre, email, dogum_tarihi, cinsiyet, profil_resmi, rol) " +
                        "VALUES (?, ?, ?, HASHBYTES('SHA2_256', CONVERT(NVARCHAR(MAX), 'admin123')), ?, ?, ?, ?, ?) " +
                        "END";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            long tcNo = 10000000000L;
            pstmt.setLong(1, tcNo);
            pstmt.setLong(2, tcNo);
            pstmt.setString(3, "Murat Emre");
            pstmt.setString(4, "Biçici");
            pstmt.setString(5, "muratemrebicici@gmail.com");
            pstmt.setDate(6, Date.valueOf("2005-08-30"));
            pstmt.setString(7, "E");
            byte[] resim = setResim("textures/profil_resmi.png"); 
            pstmt.setBytes(8, resim);
            pstmt.setString(9, "DOKTOR");

            pstmt.executeUpdate();
        }
    }

    private void addBelirtiTypes(Connection conn) throws SQLException {
        String[] belirtiTurleri = {"Poliüri", "Polifaji", "Polidipsi", "Nöropati", "Kilo Kaybı", "Yorgunluk", "Yaraların Yavaş İyileşmesi", "Bulanık Görme"};

        for (String tur : belirtiTurleri) {
            String sql = "IF NOT EXISTS (SELECT * FROM BELIRTI_TURU WHERE tur_adi = ?) " +
                    "INSERT INTO BELIRTI_TURU (tur_adi) VALUES (?)";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, tur);
                pstmt.setString(2, tur);
                pstmt.executeUpdate();
            }
        }
    }

    private void addDiyetTypes(Connection conn) throws SQLException {
        String[] diyetTurleri = {"Az Şekerli Diyet", "Şekersiz Diyet", "Dengeli Beslenme"};

        for (String tur : diyetTurleri) {
            String sql = "IF NOT EXISTS (SELECT * FROM DIYET_TURU WHERE tur_adi = ?) " +
                    "INSERT INTO DIYET_TURU (tur_adi) VALUES (?)";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, tur);
                pstmt.setString(2, tur);
                pstmt.executeUpdate();
            }
        }
    }

    private void addEgzersizTypes(Connection conn) throws SQLException {
        String[] egzersizTurleri = {"Yürüyüş", "Bisiklet", "Klinik Egzersiz"};

        for (String tur : egzersizTurleri) {
            String sql = "IF NOT EXISTS (SELECT * FROM EGZERSIZ_TURU WHERE tur_adi = ?) " +
                    "INSERT INTO EGZERSIZ_TURU (tur_adi) VALUES (?)";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, tur);
                pstmt.setString(2, tur);
                pstmt.executeUpdate();
            }
        }
    }

    private void addUyariTypes(Connection conn) throws SQLException {
        String[][] uyariTurleri = {
                {"Acil Uyarı", "Hastanın kan şekeri seviyesi 70 mg/dL'nin altına düştü. Hipoglisemi riski! Hızlı müdahale gerekebilir."},
                {"Uyarı Yok", "Kan şekeri seviyesi normal aralıkta. Hiçbir işlem gerekmez."},
                {"Takip Uyarısı", "Hastanın kan şekeri 111-150 mg/dL arasında. Durum izlenmeli."},
                {"İzleme Uyarısı", "Hastanın kan şekeri 151-200 mg/dL arasında. Diyabet kontrolü gereklidir."},
                {"Acil Müdahale Uyarısı","Hastanın kan şekeri 200 mg/dL'nin üzerinde. Hiperglisemi durumu. Acil müdahale gerekebilir."},
                {"Ölçüm Eksik Uyarısı","Hasta gün boyunca kan şekeri ölçümü yapmamıştır. Acil takip önerilir"},
                {"Ölçüm Yetersiz Uyarısı","Hastanın günlük kan şekeri ölçüm sayısı yetersiz (ölçüm < 3). Durum izlenmelidir."}
        };

        for (String[] uyari : uyariTurleri) {
            String sql = "IF NOT EXISTS (SELECT * FROM UYARI_TURU WHERE tur_adi = ?) " +
                    "INSERT INTO UYARI_TURU (tur_adi, tur_mesaji) VALUES (?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, uyari[0]);
                pstmt.setString(2, uyari[0]);
                pstmt.setString(3, uyari[1]);
                pstmt.executeUpdate();
            }
        }
    }

    private byte[] setResim(String dosyaYolu) {
        File file = new File(dosyaYolu);
        byte[] data = new byte[(int) file.length()];
        try (FileInputStream fis = new FileInputStream(file)) {
            fis.read(data);
        } catch (IOException e){
            System.out.println("Dosya yolu bulunamadı.");
            e.printStackTrace();
        }
        return data;
    }

}
