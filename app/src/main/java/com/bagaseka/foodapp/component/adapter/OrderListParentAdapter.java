package com.bagaseka.foodapp.component.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bagaseka.foodapp.component.model.ParentItem;
import com.example.foodapp.R;

import java.util.ArrayList;
import java.util.List;

public class OrderListParentAdapter extends RecyclerView.Adapter<OrderListParentAdapter.ParentViewHolder> {

    private List<ParentItem> parentItemList;
    private final ArrayList<OrderListChildAdapter> childAdapters = new ArrayList<>();

    @SuppressLint("NotifyDataSetChanged")
    public void setParentItemList(List<ParentItem> parentItemList) {
        this.parentItemList = parentItemList;
        notifyDataSetChanged();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void addChildAdapter(OrderListChildAdapter childAdapter) {
        childAdapters.add(childAdapter);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ParentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_card_order_list, parent, false);
        return new ParentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ParentViewHolder holder, int position) {
//        ParentItem parentItem = parentItemList.get(position);
//        holder.setDate(parentItem.getDate());
        try {
            LinearLayoutManager hs_linearLayout = new LinearLayoutManager(holder.itemView.getContext(), LinearLayoutManager.VERTICAL, false);
            holder.historyOrderRv.setLayoutManager(hs_linearLayout);
            holder.historyOrderRv.setAdapter(childAdapters.get(position));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return parentItemList.size();
    }

    public static class ParentViewHolder extends RecyclerView.ViewHolder {

        public RecyclerView historyOrderRv;
        //public TextView date;

        public ParentViewHolder(@NonNull View itemView) {
            super(itemView);

            historyOrderRv = itemView.findViewById(R.id.historyOrderRv);
            //date = itemView.findViewById(R.id.date);

        }

        //public void setDate(String nDate){
            //date.setText(nDate);
        //}

    }
}
