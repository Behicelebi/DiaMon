package com.project.kullanicilar;

import java.awt.image.BufferedImage;

public class Kullanici {
    public String ad, soyad, email, dogum_tarihi, cinsiyet;
    public BufferedImage profil_resmi;
    public Kullanici(String ad, String soyad, String email, String dogum_tarihi, String cinsiyet, BufferedImage profil_resmi){
        this.ad = ad;
        this.soyad = soyad;
        this.email = email;
        this.dogum_tarihi = dogum_tarihi;
        this.cinsiyet = cinsiyet;
        this.profil_resmi = profil_resmi;
    }
}
