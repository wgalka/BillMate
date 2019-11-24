package com.example.billmate.itemsBean;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;

public class Bill {
    private int mBillImage;
    private String BillTitle;
    private String BillOwner;
    private String BillTotal;
    private String BillOwes;
    private ArrayList<String> BillPayers;

    public Bill(int mBillImage, String billTitle, String billOwner, String billTotal, String billOwes) {
        this.mBillImage = mBillImage;
        BillTitle = billTitle;
        BillOwner = billOwner;
        BillTotal = billTotal;
        BillOwes = billOwes;
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
        return BillOwner;
    }

    public void setBillOwner(String billOwner) {
        BillOwner = billOwner;
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

    public ArrayList<String> getBillPayers() {
        return BillPayers;
    }

    public void setBillPayers(ArrayList<String> billPayers) {
        BillPayers = billPayers;
    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = BigDecimal.valueOf(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
}
