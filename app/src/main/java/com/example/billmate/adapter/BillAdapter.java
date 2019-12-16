package com.example.billmate.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.billmate.R;
import com.example.billmate.itemsBean.Bill;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class BillAdapter extends RecyclerView.Adapter<BillAdapter.ViewHolder> {
    private ArrayList<Bill> mList;
    private OnItemClickListener mListener;
    private FirebaseUser user_google_information = FirebaseAuth.getInstance().getCurrentUser();

    public void setOnItemClickListener(BookOfBillAdapter.OnItemClickListener new_activity) {
    }

    public interface OnItemClickListener {
        void onItemClick(int position);

        void onPayClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView mBillTitle;
        public TextView mBillDescription;
        public TextView mBillTime;
        public TextView mBillOwner;
        public TextView mBillTotal;
        public TextView mBillOwes;
        public View mBillPayButton;

        public ViewHolder(@NonNull View itemView, final BillAdapter.OnItemClickListener listener) {
            super(itemView);
            mBillTitle = itemView.findViewById(R.id.mBillTitle);
            mBillDescription = itemView.findViewById(R.id.mBillDescription);
            mBillTime = itemView.findViewById(R.id.mBillTime);
            mBillOwner = itemView.findViewById(R.id.mBillOwner);
            mBillTotal = itemView.findViewById(R.id.mBillTotal);
            mBillOwes = itemView.findViewById(R.id.mBillOwes);
            mBillPayButton = itemView.findViewById(R.id.mBillPayButton);

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
            mBillPayButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onPayClick(position);
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
        if (currentItem.getBillOwner().equals(user_google_information.getEmail()) == true
                || currentItem.getBillPayers().get(user_google_information.getEmail())) {
            holder.mBillPayButton.setVisibility(View.GONE);
            holder.mBillOwes.setVisibility(View.GONE);
        } else {
            holder.mBillPayButton.setVisibility(View.VISIBLE);
            holder.mBillOwes.setVisibility(View.VISIBLE);
        }
        long result = milliseconds() - currentItem.getTime();
        holder.mBillTime.setText("" + convertMillisecondsToHours(result) + " hours ago");
        holder.mBillDescription.setText("Description: " + currentItem.getBillDescription());
        holder.mBillTitle.setText("Product: " + currentItem.getBillTitle());
        holder.mBillOwner.setText("" + currentItem.getBillOwner());
        holder.mBillTotal.setText("Cost of purchase: " + currentItem.getBillTotal());
        holder.mBillOwes.setText("To pay: " + currentItem.getBillOwes());
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
