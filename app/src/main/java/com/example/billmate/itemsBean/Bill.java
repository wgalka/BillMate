package com.example.billmate.itemsBean;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class Bill {
    private int mBillImage;
    private String BillTitle;
    private String BillCreator;
    private String BillDescription;
    private String BillTotal;
    private String BillOwes;
    private Long BillTime;
    private String BillStatus;

    private HashMap<String, Boolean> BillPayers;

    public Bill() {
    }

    public Bill(int mBillImage, String billTitle, String billOwner, String billTotal, String billOwes) {
        this.mBillImage = mBillImage;
        this.BillTitle = billTitle;
        this.BillCreator = billOwner;
        this.BillTotal = billTotal;
        this.BillOwes = billOwes;
    }

    public Bill(String billTitle, String billCreator, String billDescription, String billTotal, HashMap<String, Boolean> billPayers,Long billTime, String billStatus) {
        this.BillTitle = billTitle;
        this.BillCreator = billCreator;
        this.BillDescription = billDescription;
        this.BillTotal = billTotal;
        double billowes = Double.parseDouble(billTotal);
        billowes = billowes/billPayers.size();
        billowes = round(billowes,2);
        this.BillOwes = String.valueOf(billowes);
        this.BillPayers = billPayers;
        this.BillTime = billTime;
        this.BillStatus = billStatus;
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

    public Long getTime() {
        return BillTime;
    }

    public String getTimeString(){
        if(this.BillTime == null){
            return "NULL TIME !!! ?? ?   ?";
        } else{
            Date currentDate = new Date(this.BillTime);
            return currentDate.toString();
        }
    }

    public void setTime(Long time) {
        this.BillTime = time;
    }

    public String getBillStatus() {
        return BillStatus;
    }

    public void setBillStatus(String billStatus) {
        BillStatus = billStatus;
    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = BigDecimal.valueOf(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
}
