package com.example.billmate.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.billmate.R;
import com.example.billmate.itemsBean.Bill;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class BookOfBillAdapter extends RecyclerView.Adapter<BookOfBillAdapter.ViewHolder> {
    private ArrayList<Bill> mList;
    private OnItemClickListener mListener;
    private FirebaseUser user_google_information = FirebaseAuth.getInstance().getCurrentUser();

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView mBillTitle;
        public TextView mBillDescription;
        public TextView mBillTime;
        public TextView mBillStatus;
        public TextView mBillTotal;

        public ViewHolder(@NonNull View itemView, final BookOfBillAdapter.OnItemClickListener listener) {
            super(itemView);
            mBillTitle = itemView.findViewById(R.id.mBillTitle);
            mBillDescription = itemView.findViewById(R.id.mBillDescription);
            mBillTime = itemView.findViewById(R.id.mBillTime);
            mBillStatus = itemView.findViewById(R.id.mBillStatus);
            mBillStatus.setVisibility(View.GONE);
            mBillTotal = itemView.findViewById(R.id.mBillTotal);

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

    public BookOfBillAdapter(ArrayList<Bill> BillList) {
        mList = BillList;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_card_view_book_of_bills, parent, false);
        ViewHolder viewHolder = new ViewHolder(v, mListener);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Bill currentItem = mList.get(position);
        holder.mBillTitle.setText("Product: " + currentItem.getBillTitle());
        holder.mBillDescription.setText("Description: " + currentItem.getBillDescription());
        holder.mBillTotal.setText("Cost of purchase: " + currentItem.getBillTotal());
        long result = milliseconds() - currentItem.getTime();
        holder.mBillTime.setText("Time: " + convertMillisecondsToHours(result) + " hours ago");
        holder.mBillStatus.setText("Status: " + currentItem.getBillStatus());
    }

    private Long convertMillisecondsToHours(Long milliseconds) {
        long minutes = TimeUnit.MILLISECONDS.toHours(milliseconds);
        return minutes;
    }

    protected Long milliseconds() {
        long date = System.currentTimeMillis();
        String milliseconds = String.valueOf(date);
        return date;
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }
}
