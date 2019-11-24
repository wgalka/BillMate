package com.example.billmate.itemsBean;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;

public class Bill {
    private int mBillImage;
    private String BillTitle;
    private String BillCreator;
    private String BillDescription;
    private String BillTotal;
    private String BillOwes;



    private HashMap<String, Boolean> BillPayers;

    public Bill() {
    }

    public Bill(int mBillImage, String billTitle, String billOwner, String billTotal, String billOwes) {
        this.mBillImage = mBillImage;
        BillTitle = billTitle;
        BillCreator = billOwner;
        BillTotal = billTotal;
        BillOwes = billOwes;
    }

    public Bill(String billTitle, String billCreator, String billDescription, String billTotal, HashMap<String, Boolean> billPayers) {
        BillTitle = billTitle;
        BillCreator = billCreator;
        BillDescription = billDescription;
        BillTotal = billTotal;
        double billowes = Double.parseDouble(billTotal);
        billowes = billowes/billPayers.size();
        billowes = round(billowes,2);
        BillOwes = String.valueOf(billowes);
        BillPayers = billPayers;
    }

    public int getmBillImage() {
        return mBillImage;
    }

    public void setmBillImage(int mBillImage) {
        this.mBillImage = mBillImage;
    }

    public String getBillTitle() {
        return BillTitle;
    }

    public void setBillTitle(String billTitle) {
        BillTitle = billTitle;
    }

    public String getBillOwner() {
        return BillCreator;
    }

    public void setBillOwner(String billOwner) {
        BillCreator = billOwner;
    }


    public String getBillDescription() {
        return BillDescription;
    }

    public void setBillDescription(String billDescription) {
        BillDescription = billDescription;
    }


    public String getBillTotal() {
        return BillTotal;
    }

    public void setBillTotal(String billTotal) {
        BillTotal = billTotal;
    }

    public String getBillOwes() {
        return BillOwes;
    }

    public void setBillOwes(String billOwes) {
        BillOwes = billOwes;
    }

    public HashMap<String, Boolean> getBillPayers() {
        return BillPayers;
    }

    public void setBillPayers(HashMap<String, Boolean> billPayers) {
        BillPayers = billPayers;
    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = BigDecimal.valueOf(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
}
