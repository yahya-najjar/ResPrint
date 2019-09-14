package com.ancologi.applications.bloemb.Adapters;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.ancologi.applications.bloemb.Models.Product;
import com.ancologi.applications.bloemb.R;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class ProductsAdapter extends RecyclerView.Adapter<ProductsAdapter.ViewHolder> {

    private List<Product> mData;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    private Context ctx;
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());


    // data is passed into the constructor
    public ProductsAdapter(Context context, List<Product> data) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
        this.ctx = context;
    }

    // inflates the row layout from xml when needed
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.activity_item_product, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        Product mProduct = mData.get(position);
        String record_title = mProduct.getItem_qty() + "  " +  mProduct.getItem_name();
        double record_subtitle = mProduct.getFinal_cost();


        holder.record_title.setText(record_title);
        holder.record_subtitle.setText(String.valueOf(record_subtitle));








        holder.itemView.setOnClickListener(v ->{
            // on click
        });
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return mData.size();
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView record_title,record_subtitle;
        ImageView red_dot;
        ConstraintLayout constraintLayout;


        ViewHolder(View itemView) {
            super(itemView);
            record_title = itemView.findViewById(R.id.record_title);
            record_subtitle = itemView.findViewById(R.id.record_subtitle);
            red_dot = itemView.findViewById(R.id.red_dot);
            constraintLayout = itemView.findViewById(R.id.constraintLayout);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }

    // convenience method for getting data at click position
    public Product getItem(int id) {
        return mData.get(id);
    }

    // allows clicks events to be caught
    public void setClickListener(ProductsAdapter.ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}
