package com.example.billmate.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.billmate.R;
import com.example.billmate.itemsBean.ItemBill;

import java.util.ArrayList;

public class BillAdapter extends RecyclerView.Adapter<BillAdapter.BillViewHolder> {
    private ArrayList<ItemBill> mBillList;
    public static class BillViewHolder extends RecyclerView.ViewHolder {
        public ImageView mBillImage;
        public TextView mBillTitle;
        public TextView mBillOwner;
        public TextView mBillTotal;
        public TextView mBillOwes;

        public BillViewHolder(@NonNull View itemView) {
            super(itemView);
            mBillImage = itemView.findViewById(R.id.mBillImage);
            mBillTitle = itemView.findViewById(R.id.mBillTitle);
            mBillOwner = itemView.findViewById(R.id.mBillOwner);
            mBillTotal = itemView.findViewById(R.id.mBillTotal);
            mBillOwes = itemView.findViewById(R.id.mBillOwes);
        }
    }

    public BillAdapter(ArrayList<ItemBill> BillList) {
        mBillList = BillList;
    }

    ;

    @NonNull
    @Override
    public BillViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_bill, parent, false);
        BillViewHolder bvh = new BillViewHolder(v);
        return bvh;
    }

    @Override
    public void onBindViewHolder(@NonNull BillViewHolder holder, int position) {
        ItemBill currentItem = mBillList.get(position);

        holder.mBillImage.setImageResource(currentItem.getmBillImage());
        holder.mBillTitle.setText(currentItem.getmBillTitle());
        holder.mBillOwner.setText(currentItem.getmBillOwner());
        holder.mBillTotal.setText(currentItem.getmBillTotal());
        holder.mBillOwes.setText(currentItem.getmBillOwes());
    }

    @Override
    public int getItemCount() {
        return mBillList.size();
    }
}
