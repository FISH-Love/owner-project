package com.zyg.takeaway.util;

/**
 * 常用格式验证工具类
 */
public class ValidationOrdinaryUtils {

    /**
     * 用户名格式
     * 由字母数字下划线组成且开头必须是字母，不能超过16位；
     * @param user 数据字符串
     * @return false/true
     */
    public static boolean validateUser(String user) {
        if (user == null || user.length() > 16) {
            return false;
        }
        if (user.charAt(0) >= 'a' && user.charAt(0) <= 'z') {
            return true;
        }
        for (int index = 1; index < user.length(); index++) {
            char temp = user.charAt(index);
            if (!(temp == '_')) {
                return false;
            }
        }
        return letter(user) && number(user);
    }

    /**
     * 密码格式
     * 字母和数字构成，不能超过16位
     * @param userPwd 数据字符串
     * @return false/true
     */
    public static boolean validatePwd(String userPwd) {
        if (userPwd == null || userPwd.length() > 16) {
            return false;
        }
        return letter(userPwd) && number(userPwd);
    }

    /**
     * 昵称
     * 字母和数字构成长度大于6小于10
     * @param petName 数据字符串
     * @return false/true
     */
    public static boolean validatePetName(String petName) {
        if (petName == null || petName.length() < 6 || petName.length() > 10) {
            return false;
        }
        return letter(petName) && number(petName);
    }

    /**
     * 手机号
     * 全数字，长度为11位
     * @param phoneNumber 数据字符串
     * @return false/true
     */
    public static boolean isValidPhoneNumber(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.length() != 11) {
            return false;
        }
        for (int i = 0; i < phoneNumber.length(); i++) {
            if (!Character.isDigit(phoneNumber.charAt(i))) {
                return false;
            }
        }
        String prefix = phoneNumber.substring(1, 2);
        String[] validPrefixes = {"3", "4", "5", "6", "7", "8", "9"};
        for (String validPrefix : validPrefixes) {
            if (prefix.equals(validPrefix)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 邮箱
     * 必须包含@符号；必须包含点；点和@之间必须有字符；
     * @param email 数据字符串
     * @return false/true
     */
    public static boolean validateEmail(String email) {
        if (email == null)
            return false;
        int emailFlag = email.indexOf('@');
        int pointFlag = email.lastIndexOf('.');
        if (emailFlag == -1 || pointFlag == -1 || email.length() < 7)
            return false;
        String subName = email.substring(pointFlag);
        if (!subName.equals(".com"))
            return false;
        return pointFlag - emailFlag > 1;
    }

    /**
     * 验证数字格式
     * @param number 数据字符串
     * @return false/true
     */
    private static boolean number(String number) {
        for (int index = 0; index < number.length(); index++) {
            char temp = number.charAt(index);
            if (!(temp >= '0' && temp <= '9')) {
                return false;
            }
        }
        return true;
    }

    /**
     * 验证字母
     * @param letter 数据字符串
     * @return false/true
     */
    private static boolean letter(String letter) {
        for (int index = 0; index < letter.length(); index++) {
            char temp = letter.charAt(index);
            if (!((temp >= 'a' && temp <= 'z') || (temp >= 'A' && temp <= 'Z') || (temp >= '0' && temp <= '9'))) {
                return false;
            }
        }
        return true;
    }
}