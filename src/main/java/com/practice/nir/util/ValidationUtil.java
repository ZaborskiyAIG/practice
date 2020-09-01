package com.practice.nir.util;

import java.util.HashSet;
import java.util.Set;

public class ValidationUtil {

   public static String checkFormulaPropositional(String formula){

       if(!formula.contains("!") && !formula.contains("+") && !formula.contains("*") && !formula.contains("@")){
           return formula;
       }

        formula = formula.replaceAll("[a-zA-Z!+*@()]", "");  // ! - не  + - дизьюкция   * - коньюкция  @ - импликация


        return formula;



   }

   public static Set<String> getAllParameters(String formula){
       Set<String> set = new HashSet<>();
       for (int i = 0; i < formula.length(); ++i){
           char ch = formula.charAt(i);
           if ((ch >= 65 && ch <= 90) || (ch >= 97 && ch <= 122))
               set.add(String.valueOf(ch));
       }
       return set;
   }

   public static String[][] createArray(String[][] array, int countParam, int countColumns){
       int n = countParam;   //построение таблицы истинности

       String[][] str = new String[(int) Math.pow(2,countParam)+1][countColumns];

       for (int i = 0 ; i != (1<<n) ; i++) {

           String s = Integer.toBinaryString(i);
           while (s.length() != countParam) {
               s = '0'+s;

           }
           str[i] = s.split("");

       }

       for (int i = 1; i < array.length; i++) {
           for (int j = 0; j < countParam; j++) {
               array[i][j]=str[i-1][j];
           }
       }



       return array;
   }

}
