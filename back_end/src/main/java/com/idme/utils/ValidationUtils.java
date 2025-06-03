package com.idme.utils;

public class ValidationUtils {

    // 校验用户名
    public static boolean isValidUsername(String name) {
        String usernameRegex = "^[A-Za-z0-9]{6,32}$";
        return name.matches(usernameRegex);
    }

    // 校验密码
    public static boolean isValidPassword(String pwd) {
        boolean lengthOk = pwd.length() >= 8 && pwd.length() <= 32;
        boolean hasLetter = pwd.matches(".*[A-Za-z].*");
        boolean hasNumber = pwd.matches(".*\\d.*");
        boolean hasSpecial = pwd.matches(".*[^A-Za-z0-9].*");
        return lengthOk && hasLetter && hasNumber && hasSpecial;
    }
}