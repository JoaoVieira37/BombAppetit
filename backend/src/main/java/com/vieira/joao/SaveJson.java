package com.vieira.joao;

import com.vieira.joao.model.MealVoucher;

import java.io.*;
import java.util.List;
public class SaveJson {

    public static void saveMealVoucherListAsJson(List<MealVoucher> mealVoucherList) {
        String filePath = "voucher.json";

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            writer.write("{\"mealVouchers\":{\"list\":[");
            for (int i = 0; i < mealVoucherList.size(); i++) {
                writer.write("{\"id\":\"" + mealVoucherList.get(i).getId() + "\",\"code\":\"" + mealVoucherList.get(i).getCode() + "\"}");
                if (i < mealVoucherList.size() - 1) writer.write(',');
            }
            writer.write("]}}");


        } catch (IOException e) {
            System.err.println("Error writing to file: " + e.getMessage());
        }

    }



}
