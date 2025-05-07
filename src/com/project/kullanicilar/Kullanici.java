package com.project.kullanicilar;

import java.awt.image.BufferedImage;

public class Kullanici {
    public String ad, soyad, email, dogum_tarihi, cinsiyet, rol;
    public long tc_no;
    public BufferedImage profil_resmi;
    public Kullanici(long tc_no, String ad, String soyad, String email, String dogum_tarihi, String cinsiyet, BufferedImage profil_resmi, String rol){
        this.tc_no = tc_no;
        this.ad = ad;
        this.soyad = soyad;
        this.email = email;
        this.dogum_tarihi = dogum_tarihi;
        this.cinsiyet = cinsiyet;
        this.profil_resmi = profil_resmi;
        this.rol = rol;
    }
}
