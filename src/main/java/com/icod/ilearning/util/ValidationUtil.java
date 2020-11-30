package com.icod.ilearning.util;

import org.apache.commons.validator.routines.EmailValidator;

public class ValidationUtil {

    public static boolean isNullOrEmpty(String input){
        if(input==null || input.trim().length()==0){
            return true;
        }
        return false;
    }

    public static boolean isValidEmail(String email){
        return EmailValidator.getInstance().isValid(email);
    }

    public static boolean isValidPassword(String password){
        if(isNullOrEmpty(password) || password.trim().length()<8){
            return false;
        }
        return true;
    }
}
