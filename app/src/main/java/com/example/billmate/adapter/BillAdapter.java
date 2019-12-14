package com.example.billmate.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.billmate.R;
import com.example.billmate.itemsBean.Bill;

import java.util.ArrayList;

public class BillAdapter extends RecyclerView.Adapter<BillAdapter.ViewHolder> {
    private ArrayList<Bill> mList;
    private OnItemClickListener mListener;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView mBillTitle;
        public TextView mBillOwner;
        public TextView mBillTotal;
        public TextView mBillOwes;
        public TextView mBillDate;
        public TextView mBillDescription;

        public ViewHolder(@NonNull View itemView, final BillAdapter.OnItemClickListener listener) {
            super(itemView);
            mBillTitle = itemView.findViewById(R.id.mBillTitle);
            mBillOwner = itemView.findViewById(R.id.mBillOwner);
            mBillTotal = itemView.findViewById(R.id.mBillTotal);
            mBillOwes = itemView.findViewById(R.id.mBillOwes);
            mBillDate = itemView.findViewById(R.id.mBillDate);
            mBillDescription = itemView.findViewById(R.id.mBillDescription);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onItemClick(position);
                        }
                    }
                }
            });
        }
    }

    public BillAdapter(ArrayList<Bill> BillList) {
        mList = BillList;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_card_view_bills, parent, false);
        ViewHolder viewHolder = new ViewHolder(v, mListener);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Bill currentItem = mList.get(position);

        holder.mBillTitle.setText(currentItem.getBillTitle());
        holder.mBillOwner.setText(currentItem.getBillOwner());
        holder.mBillTotal.setText(currentItem.getBillTotal());
        holder.mBillOwes.setText(currentItem.getBillOwes());
        holder.mBillDate.setText(currentItem.getTimeString());
        holder.mBillDescription.setText(currentItem.getBillDescription());
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }
}
