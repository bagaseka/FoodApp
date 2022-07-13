package com.bagaseka.foodapp.component.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodapp.R;
import com.bagaseka.foodapp.component.model.HomeMainList;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

public class ListCheckOutAdapter extends RecyclerView.Adapter<ListCheckOutAdapter.FoodViewHolder>  {

    private ArrayList<HomeMainList> checkoutData;
    int layout;
    private View rootView;
    private int subtotal = 0;
    private int ppn = 0;
    private int total = 0;
    private TextView textviewSubTotal,textviewFee,textviewTotal;
    private Context context;
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private String userID = auth.getUid();

    public ListCheckOutAdapter(ArrayList<HomeMainList> checkoutData) {
        this.checkoutData = checkoutData;
    }

    @NonNull
    @Override
    public FoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_checkout, parent, false);

        context = parent.getContext();
        rootView = ((Activity) context).getWindow().getDecorView().findViewById(android.R.id.content);

        textviewFee = (TextView) rootView.findViewById(R.id.ppn);
        textviewSubTotal = (TextView) rootView.findViewById(R.id.subPrice);
        textviewTotal = (TextView) rootView.findViewById(R.id.total);
        return new FoodViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ListCheckOutAdapter.FoodViewHolder holder, int position) {
        HomeMainList model = checkoutData.get(position);
        holder.setNameFood(model.getNama());
        holder.setPriceFood(model.getHarga());
        holder.setFoodID(model.getFoodID());
        holder.setItemCount(model.getItemCount());

        subtotal = subtotal + Integer.parseInt(model.getItemCount()) * Integer.parseInt(model.getHarga());
        ppn = (subtotal / 100) * 11;

        total = subtotal + ppn;

        textviewFee.setText(holder.formatRupiah(Double.parseDouble(String.valueOf(ppn))));
        textviewSubTotal.setText(holder.formatRupiah(Double.parseDouble(String.valueOf(subtotal))));
        textviewTotal.setText(holder.formatRupiah(Double.parseDouble(String.valueOf(total))));
    }

    @Override
    public int getItemCount() {
        return checkoutData.size();
    }

    public class FoodViewHolder extends RecyclerView.ViewHolder {
        TextView nameFood,priceFood,itemCount;
        String idFood;

        public FoodViewHolder(@NonNull View itemView) {
            super(itemView);
            nameFood = itemView.findViewById(R.id.nameFood);
            priceFood = itemView.findViewById(R.id.priceFood);
            itemCount = itemView.findViewById(R.id.count);

        }
        public void setNameFood(String name){
            nameFood.setText(name);
        }
        public void setPriceFood(String price){
            priceFood.setText(formatRupiah(Double.parseDouble(price)));


        }
        public void setFoodID(String id){
            idFood = id;
        }
        public void setItemCount(String count){
            itemCount.setText(count);
        }
        private String formatRupiah(Double number){
            Locale localeID = new Locale("in", "ID");
            NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(localeID);
            return formatRupiah.format(number);
        }
    }
}


