package com.practice.nir.controller;

import com.practice.nir.util.RecursiveDescentParser;
import com.practice.nir.util.RecursiveDescentParserCalculator;
import com.practice.nir.util.ValidationUtil;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.*;

@Controller
public class MenuController {

    @GetMapping("")
    public String getMenu(Model model) {
        return "menu";
    }

    @GetMapping("/solution")
    public String getSolution(@RequestParam String F1, @RequestParam String F2, Model model) {
        //проверка на null

        System.out.println("F1:"+F1);
        System.out.println("F2:"+F2);

        String result = ValidationUtil.checkFormulaPropositional(F1);

        System.out.println(result+"fff");

        if(result.length()!=0){
            String message = "Неожиданный символ в формуле F1: " + result;
            model.addAttribute("message", message);
            return "menu";
        }

        result = ValidationUtil.checkFormulaPropositional(F2);

        if(result.length()!=0){
            String message = "Неожиданный символ в формуле F2: " + result;
            model.addAttribute("message", message);
            return "menu";
        }


        Set<String> params = ValidationUtil.getAllParameters(F1);
        params.addAll(ValidationUtil.getAllParameters(F2));

        List<String> tableTemplate = new ArrayList<>();
        tableTemplate.addAll(params);
        tableTemplate.addAll(RecursiveDescentParser.getFormulas(F1));
        RecursiveDescentParser.clear();
        tableTemplate.addAll(RecursiveDescentParser.getFormulas(F2));
        tableTemplate.add("");
        RecursiveDescentParser.clear();

        int countParam = params.size(); //количество параметров
        int countStr = (int) Math.pow(2, countParam); //количество строк

        String[][] tableIstinosty = new String[countStr+1][tableTemplate.size()];   //сначало строки потом столбцы
        tableIstinosty[0] = tableTemplate.toArray(new String[0]);



        tableIstinosty = ValidationUtil.createArray(tableIstinosty, countParam, tableTemplate.size());

        List<String> listF1 = new ArrayList<>();
        List<String> listF2 = new ArrayList<>();

        for (int i = 1; i < tableIstinosty.length; i++) {

            Map<String, String> map = new HashMap<>();
            for (int j = 0; j < countParam; j++) {
                map.put(tableIstinosty[0][j], tableIstinosty[i][j]);
            }

            for (int j = countParam; j < tableIstinosty[i].length-1; j++) {

                tableIstinosty[i][j] = String.valueOf(RecursiveDescentParserCalculator.getSolution(tableIstinosty[0][j],map ));

                if(tableIstinosty[0][j].equals(F1)){
                    listF1.add(tableIstinosty[i][j]);
                }

                if(tableIstinosty[0][j].equals(F2)){
                    listF2.add(tableIstinosty[i][j]);
                }

            }
        }

        List<String> array = new ArrayList<>();

        array.add(F1+"="+F2);
        int vl = 0;
        for(int i=0; i<listF1.size(); i++){

            if(listF1.get(i).equals(listF2.get(i))){
                array.add("1");
            } else {
                array.add("0");
                vl=1;

            }

        }

        for (int i = 0; i < tableIstinosty.length; i++) {
            for(int j = tableIstinosty[i].length-1; j <tableIstinosty[i].length; j++){
                tableIstinosty[i][j] = array.get(i);
            }
        }


        if(vl == 1){
            model.addAttribute("message", "Данные формулы не равносильны");
        } else {
            model.addAttribute("message", "Данные формулы равносильны");
        }

        model.addAttribute("table", tableIstinosty);

        return "solution";
    }

}
