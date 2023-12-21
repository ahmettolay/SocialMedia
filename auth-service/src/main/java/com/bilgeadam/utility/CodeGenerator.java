package com.bilgeadam.utility;

import java.util.UUID;

//Kayıt olan kullanıcıya bir aktivasyon kodu dönecektir.
//Bu kod ile kullanıcı giriş yapabilir. Bu kod olmadan giriş hatası alacaktır.
//CodeGenerator sınıfı da yalnızca bu kodun üretilmesinden sorumludur.
public class CodeGenerator {
    public static String generateCode(){
        String code = UUID.randomUUID().toString(); //örnek UUID --> 1b9318d9-e123-4744-8ef2-39603e3218aa
        String[] data = code.split("-");
        String newCode="";
        for (String str : data){
            newCode += str.charAt(0); //1e483
        }
        return newCode;
    }
}
