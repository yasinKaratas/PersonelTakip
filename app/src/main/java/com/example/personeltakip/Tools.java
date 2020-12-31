package com.example.personeltakip;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Tools {
    public Boolean isLoggedIn;

    public static class PersonelData {
        public static String CitizenNumber;
        public static String Name;
        public static String Surname;
        public static String FirmCode;
        public static String FirmTitle;
        public static String FirmCity;
    }


    public boolean TCConfirm(String kimlikNo) {
        if (kimlikNo.length() != 11) return false;
        if (kimlikNo.substring(0, 1).equals("0")) return false;
        int[] hane = new int[11];
        int toplam = 0;
        for (int i = 0; i < 11; i++) {
            hane[i] = Integer.parseInt(String.valueOf(kimlikNo.charAt(i)));
            toplam += hane[i];
        }
        toplam -= hane[10];
        if ((toplam % 10) != hane[10]) return false;
        if (((hane[0] + hane[2] + hane[4] + hane[6] + hane[8]) * 7 + (hane[1] + hane[3] + hane[5] + hane[7]) * 9) % 10 != hane[9])
            return false;
        if (((hane[0] + hane[2] + hane[4] + hane[6] + hane[8]) * 8) % 10 != hane[10]) return false;
        return true;
    }

    public String MD5Generator(String text) {
        MessageDigest m = null;
        try {
            m = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        m.update(text.getBytes(), 0, text.length());
        String hash = new BigInteger(1, m.digest()).toString(16);
        return hash;
    }

    public String RightStr(String _string, int _CharCount) {
        try {
            int _len = _string.length();
            return _string.substring(_len - _CharCount, _len);
        } catch (Exception err) {
            return _string;
        }
    }

    public boolean isInternetAvailable() {
        boolean _result = false;
        try {
            String command = "ping -c 1 google.com";
            _result = Runtime.getRuntime().exec(command).waitFor() == 0;
        } finally {
            return _result;
        }
    }


}

