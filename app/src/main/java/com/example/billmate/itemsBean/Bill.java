package com.example.billmate.itemsBean;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class Bill {
    private int mBillImage;
    private String mBillTitle;
    private String mBillOwner;
    private String mBillTotal;
    private String mBillOwes;

    public Bill(int mBillImage, String mBillTitle, String mBillOwner, String mBillTotal, String mBillOwes) {
        this.mBillImage = mBillImage;
        this.mBillTitle = mBillTitle;
        this.mBillOwner = mBillOwner;
        this.mBillTotal = mBillTotal;
        this.mBillOwes = mBillOwes;
    }

    public int getmBillImage() {
        return mBillImage;
    }

    public void setmBillImage(int mBillImage) {
        this.mBillImage = mBillImage;
    }

    public String getmBillTitle() {
        return mBillTitle;
    }

    public void setmBillTitle(String mBillTitle) {
        this.mBillTitle = mBillTitle;
    }

    public String getmBillOwner() {
        return mBillOwner;
    }

    public void setmBillOwner(String mBillOwner) {
        this.mBillOwner = mBillOwner;
    }

    public String getmBillTotal() {
        return mBillTotal;
    }

    public void setmBillTotal(String mBillTotal) {
        this.mBillTotal = mBillTotal;
    }

    public String getmBillOwes() {
        return mBillOwes;
    }

    public void setmBillOwes(String mBillOwes) {
        this.mBillOwes = mBillOwes;
    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = BigDecimal.valueOf(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
}

class Product {

}