# DiaMon: Akıllı Diyabet Monitör ve Takip Sistemi

![Java](https://img.shields.io/badge/language-Java-orange.svg)
![Database](https://img.shields.io/badge/database-SQL%20Server-blue.svg)
![UI](https://img.shields.io/badge/UI-Java%20Swing-red)
![Lisans](https://img.shields.io/badge/license-MIT-green.svg)

**DiaMon**, diyabet hastalarının günlük sağlık verilerini (kan şekeri, diyet, egzersiz) kolayca takip etmelerini, bu verileri doktorlarıyla güvenli bir şekilde paylaşmalarını ve sağlık durumlarındaki kritik değişikliklere göre otomatik uyarılar almalarını sağlayan bir masaüstü sağlık yönetim sistemidir.

---

## 📋 İçindekiler

- [Proje Hakkında](#-proje-hakkında)
- [Ana Özellikler](#-ana-özellikler)
- [Mimari ve Yöntem](#-mimari-ve-yöntem)
- [Kullanılan Teknolojiler ve Kütüphaneler](#-kullanılan-teknolojiler-ve-kütüphaneler)
- [Veritabanı Şeması ve ER Diyagramı](#-veritabanı-şeması-ve-er-diyagramı)
- [Ekran Görüntüleri](#-ekran-görüntüleri)
- [Kurulum ve Yapılandırma](#-kurulum-ve-yapılandırma)
- [Geliştiriciler](#-geliştiriciler)
- [Lisans](#-lisans)

## 📖 Proje Hakkında

Diyabet yönetiminin karmaşıklığını ve hasta-doktor iletişimindeki zorlukları hedef alan bu proje, modern bir dijital çözüm sunar. Geleneksel takip yöntemlerinin aksine **DiaMon**, verileri sistematik olarak toplar, analiz eder ve anlamlı bilgilere dönüştürür. Hastalar günlük ölçümlerini, diyet ve egzersiz uyumlarını sisteme girerken, doktorlar bu verileri anlık olarak izleyebilir, grafiksel analizlerle hastalarının durumunu değerlendirebilir ve proaktif önerilerde bulunabilir.

Projenin temel amacı, diyabet yönetimini kolaylaştırmak ve kritik durumlarda erken müdahale imkanı sağlayarak hastaların yaşam kalitesini artırmaktır.

## ✨ Ana Özellikler

### Hastalar İçin
- **Günlük Veri Girişi:** Kan şekeri, alınan belirtiler, diyet ve egzersiz uyum durumlarını kolayca kaydetme.
- **Otomatik Öneri Motoru:** Girilen verilere göre kişiselleştirilmiş diyet, egzersiz ve insülin dozu önerileri alma.
- **Görsel Takip:** Kan şekeri seviyelerindeki değişimi zaman bazlı grafikler üzerinden izleme.
- **Anlık Uyarılar:** Hipoglisemi (<70 mg/dL) ve Hiperglisemi (>200 mg/dL) gibi kritik durumlarda anında uyarı alma.

### Doktorlar İçin
- **Hasta Yönetim Paneli:** Kendilerine atanan hastaları listeleme ve hasta bazlı detayları görüntüleme.
- **Grafiksel Analiz:** Hastaların kan şekeri trendlerini, diyet/egzersiz uyum oranlarını renkli ve anlaşılır grafiklerle analiz etme.
- **Veri Filtreleme:** Tarih aralığına veya belirtilere göre hasta verilerini filtreleyerek spesifik durumları inceleme.
- **Otomatik Uyarı Sistemi:** Hastalarının kritik sağlık değerlerine ulaşması veya yetersiz veri girmesi durumunda anlık bildirimler alma.

### Sistem Genel Özellikleri
- **Rol Tabanlı Erişim (RBAC):** Doktor ve Hasta rolleri için ayrı arayüzler ve yetkilendirme.
- **Güvenli Kimlik Doğrulama:** T.C. Kimlik Numarası ve SHA-256 ile şifrelenmiş parola ile güvenli giriş.
- **Normalize Veritabanı:** 3. Normal Forma uygun tasarlanmış, veri bütünlüğü ve tutarlılığı sağlayan SQL Server veritabanı.

## 📐 Mimari ve Yöntem

Proje, üç ana bileşen üzerine kurulmuştur:
1.  **Veritabanı Katmanı:** SQL Server üzerinde 3NF'ye göre normalleştirilmiş, ilişkisel bir veritabanı tasarlanmıştır. Veri tutarlılığı, yabancı anahtarlar ve kısıtlamalar ile sağlanmıştır.
2.  **Masaüstü Uygulama (Java Swing):** Model-View-Controller (MVC) mimarisine benzer bir yaklaşımla geliştirilmiştir. Arayüz (View), iş mantığı (Controller) ve veri modelleri (Model) birbirinden ayrıştırılarak modüler bir yapı elde edilmiştir.
3.  **Kural Tabanlı Motor:** Sistemin "beyni" olarak çalışan bu yapı, `if-else` blokları ile kan şekeri değerlerine ve belirtilere göre otomatik olarak öneri, uyarı ve insülin dozu hesaplamaları yapar.

## 🛠️ Kullanılan Teknolojiler ve Kütüphaneler

- **Programlama Dili:** Java
- **Veritabanı Yönetim Sistemi:** Microsoft SQL Server (SSMS ile yönetildi)
- **Kullanıcı Arayüzü (GUI):**
  - **Java Swing:** Temel arayüz bileşenleri için.
  - **[FlatLaf](https://www.formdev.com/flatlaf/):** Modern ve şık bir "Look and Feel" sağlamak için.
  - **[JCalendar](https://toedter.com/jcalendar/):** Tarih seçimi bileşenleri için.
- **Grafik Kütüphanesi:**
  - **[JFreeChart](https://www.jfree.org/jfreechart/):** Hasta verilerini görselleştirmek için dinamik grafikler oluşturmada kullanıldı.
- **E-posta Entegrasyonu:**
  - **[JavaMail API](https://javaee.github.io/javamail/):** (Potansiyel) uyarıların e-posta ile gönderilmesi için.

## 🗄️ Veritabanı Şeması ve ER Diyagramı

Projenin temelini oluşturan normalleştirilmiş SQL veritabanı şeması aşağıdadır.

<details>
<summary>SQL CREATE TABLE Scriptlerini Görüntülemek İçin Tıklayın</summary>

```sql
CREATE TABLE KULLANICI (
    tc_no        BIGINT         PRIMARY KEY,
    ad           NVARCHAR(50)   NOT NULL,
    soyad        NVARCHAR(50)   NOT NULL,
    sifre        VARBINARY(MAX) NOT NULL,
    email        NVARCHAR(100)  NOT NULL,
    dogum_tarihi DATE           NOT NULL,
    cinsiyet     CHAR(1)        NOT NULL,
    profil_resmi VARBINARY(MAX) NULL,
    rol          NVARCHAR(10)   NOT NULL
);

CREATE TABLE HASTA_DOKTOR (
    doktor_tc BIGINT NOT NULL,
    hasta_tc  BIGINT NOT NULL,
    FOREIGN KEY (doktor_tc) REFERENCES KULLANICI(tc_no),
    FOREIGN KEY (hasta_tc)  REFERENCES KULLANICI(tc_no)
);

CREATE TABLE BELIRTI_TURU (
    belirti_turu_id INT IDENTITY(1,1) PRIMARY KEY,
    tur_adi         NVARCHAR(50) NOT NULL
);

CREATE TABLE DIYET_TURU (
    diyet_turu_id INT IDENTITY(1,1) PRIMARY KEY,
    tur_adi       NVARCHAR(50) NOT NULL
);

CREATE TABLE EGZERSIZ_TURU (
    egzersiz_turu_id INT IDENTITY(1,1) PRIMARY KEY,
    tur_adi          NVARCHAR(50) NOT NULL
);

CREATE TABLE UYARI_TURU (
    uyari_turu_id INT IDENTITY(1,1) PRIMARY KEY,
    tur_adi       NVARCHAR(50)  NOT NULL,
    tur_mesaji    NVARCHAR(255) NOT NULL
);

CREATE TABLE HASTA_OLCUM (
    hasta_tc      BIGINT   NOT NULL,
    olcum_tarihi  DATETIME NOT NULL,
    uyari_turu_id INT      NOT NULL,
    olcum_degeri  INT      NOT NULL,
    FOREIGN KEY (hasta_tc)      REFERENCES KULLANICI(tc_no),
    FOREIGN KEY (uyari_turu_id) REFERENCES UYARI_TURU(uyari_turu_id)
);

-- Diğer HASTA_* tabloları...
```
</details>

### ER Diyagramı
<p align="center">
  <img src="path/to/er_diagram.png" alt="ER Diyagramı">
</p>


## 🖼️ Ekran Görüntüleri

<p align="center">
  <strong>Giriş Ekranı</strong><br>
  <img src="path/to/login_screen.png" width="400" alt="Giriş Ekranı">
</p>
<p align="center">
  <strong>Hasta Paneli</strong><br>
  <img src="path/to/patient_dashboard.png" width="600" alt="Hasta Paneli">
</p>
<p align="center">
  <strong>Doktor Paneli - Grafik Analiz</strong><br>
  <img src="path/to/doctor_dashboard.png" width="600" alt="Doktor Paneli">
</p>


## 🚀 Kurulum ve Yapılandırma

Bu projeyi yerel makinenizde çalıştırmak için aşağıdaki adımları izleyin:

1.  **Veritabanını Kurun:**
    -   Microsoft SQL Server Management Studio (SSMS) açın.
    -   `PROJETEST` adında yeni bir veritabanı oluşturun.
    -   Yukarıda verilen [Veritabanı Şeması](#-veritabanı-şeması-ve-er-diyagramı) bölümündeki SQL script'ini bu veritabanı üzerinde çalıştırarak tabloları oluşturun.
    -   Gerekli başlangıç verilerini (örneğin, `BELIRTI_TURU`, `DIYET_TURU` vb.) ekleyin.

2.  **Projeyi Klonlayın:**
    ```bash
    git clone [https://github.com/kullanici-adiniz/DiaMon.git](https://github.com/kullanici-adiniz/DiaMon.git)
    ```

3.  **Projeyi Açın:**
    -   Projeyi IntelliJ IDEA veya tercih ettiğiniz bir Java IDE'si ile açın.
    -   Projenin kütüphane bağımlılıklarını (JFreeChart, FlatLaf, JCalendar, SQL Server JDBC Driver) yükleyin.

4.  **Veritabanı Bağlantısını Yapılandırın:**
    -   Proje içerisindeki veritabanı bağlantı ayarlarını içeren sınıfı veya dosyayı bulun.
    -   Bağlantı dizesini (connection string), kendi SQL Server kullanıcı adı ve şifrenizle güncelleyin.

5.  **Uygulamayı Çalıştırın:**
    -   Projenin ana (`Main`) sınıfını bulun ve çalıştırın.
    -   Giriş ekranı açılacaktır. (Test için veritabanına manuel olarak bir doktor ve hasta eklemeniz gerekebilir.)

## 👥 Geliştiriciler

-   **Murat Emre Biçici**
    -   Email: `muratemrebicici@gmail.com`
-   **Behiç Çelebi**
    -   Email: `celebibehic@gmail.com`

## 📜 Lisans

Bu proje, MIT Lisansı ile lisanslanmıştır. Detaylar için `LICENSE` dosyasına göz atabilirsiniz.
