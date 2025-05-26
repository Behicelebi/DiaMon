package com.project.model;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class Kullanici {
    public String ad, soyad, email, dogum_tarihi, cinsiyet, rol;
    public long tc_no;
    public BufferedImage profil_resmi;

    public ArrayList<String> belirtiler = new ArrayList<String>();
    public ArrayList<Integer> olcumler = new ArrayList<Integer>();
    public ArrayList<String> olcumTarihleri = new ArrayList<String>();
    public ArrayList<String> olcumUyarilar = new ArrayList<String>();
    public ArrayList<String> olcumUyariAciklamalar = new ArrayList<String>();
    public ArrayList<Integer> insulinDegerleri = new ArrayList<Integer>();
    public ArrayList<String> insulinTarihleri = new ArrayList<String>();
    public ArrayList<String> insulinUyarilar = new ArrayList<String>();
    public ArrayList<String> insulinUyariAciklamalar = new ArrayList<String>();
    public String egzersizOneri = "Yok", diyetOneri = "Yok";
    public boolean oneriGirdiMi = false;
    public int diyetYuzdesi = 0, egzersizYuzdesi = 0;

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
