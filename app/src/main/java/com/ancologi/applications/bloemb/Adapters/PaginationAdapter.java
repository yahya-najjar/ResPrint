package com.ancologi.applications.bloemb.Adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ancologi.applications.bloemb.Activities.ShowOrderActivity;
import com.ancologi.applications.bloemb.Masters.MasterActivity;
import com.ancologi.applications.bloemb.Models.Order;
import com.ancologi.applications.bloemb.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class PaginationAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int ITEM = 0;
    private static final int LOADING = 1;

    private List<Order> orderResults;
    private Context context;
    private ItemClickListener mClickListener;

    private boolean isLoadingAdded = false;
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

    public PaginationAdapter(Context context) {
        this.context = context;
        orderResults = new ArrayList<>();
    }

    public List<Order> getOrders() {
        return orderResults;
    }

    public void setOrders(List<Order> orderResults) {
        this.orderResults = orderResults;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        switch (viewType) {
            case ITEM:
                viewHolder = getViewHolder(parent, inflater);
                break;
            case LOADING:
                View v2 = inflater.inflate(R.layout.item_progress, parent, false);
                viewHolder = new LoadingVH(v2);
                break;
        }
        return viewHolder;
    }

    @NonNull
    private RecyclerView.ViewHolder getViewHolder(ViewGroup parent, LayoutInflater inflater) {
        RecyclerView.ViewHolder viewHolder;
        View v1 = inflater.inflate(R.layout.activity_item_order, parent, false);
        viewHolder = new OrderVH(v1);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        Order mOrder = orderResults.get(position);



        switch (getItemViewType(position)) {
            case ITEM:
                final OrderVH orderVH = (OrderVH) holder;

                String order_title = mOrder.getCustomer_name();
                String order_subtitle = mOrder.getDatedelver() + "  -  " + mOrder.getTimedelver();

                orderVH.order_title.setText(order_title);
                orderVH.order_subtitle.setText(order_subtitle);

                Log.d("myRd",String.valueOf(mOrder.getOrder_id()));
                Log.d("myRd",String.valueOf(MasterActivity.mUnread_orders));

                if (MasterActivity.mUnread_orders != null)
                if (MasterActivity.mUnread_orders.contains(String.valueOf(mOrder.getOrder_id()))){
                    orderVH.constraintLayout.setBackground(context.getResources().getDrawable(R.drawable.item_order_back)); // should be item_order_back_unread
                    Log.d("myRd","true");
                }else
                    orderVH.constraintLayout.setBackground(context.getResources().getDrawable(R.drawable.item_order_back));


                orderVH.itemView.setOnClickListener(v ->{
                    Log.d("mDebug","1");
                    if (MasterActivity.mUnread_orders != null)
                    MasterActivity.mUnread_orders.remove(String.valueOf(mOrder.getOrder_id()));
                    orderVH.constraintLayout.setBackground(context.getResources().getDrawable(R.drawable.item_order_back));

                    Intent intent = new Intent(context, ShowOrderActivity.class);
                    intent.putExtra("order",mOrder);
                    context.startActivity(intent);

                });


                // customized colored dot

                Date death_date = new Date();
                Date today = new Date();
                try {
                    death_date =(sdf.parse(mOrder.getDatedelver()));
                } catch (ParseException e) {
                    e.printStackTrace();
                }



                break;

            case LOADING:
//                Do nothing
                break;
        }

    }

    @Override
    public int getItemCount() {
        return orderResults == null ? 0 : orderResults.size();
    }

    // allows clicks events to be caught
    public void setClickListener(PaginationAdapter.ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    @Override
    public int getItemViewType(int position) {
        return (position == orderResults.size() - 1 && isLoadingAdded) ? LOADING : ITEM;
    }


    /*
   Helpers
   _________________________________________________________________________________________________
    */

    public void add(Order r) {
        orderResults.add(r);
        notifyItemInserted(orderResults.size() - 1);
    }

    public void addAll(List<Order> moveResults) {
        for (Order result : moveResults) {
            add(result);
        }
    }

    public void remove(Order r) {
        int position = orderResults.indexOf(r);
        if (position > -1) {
            orderResults.remove(position);
            notifyItemRemoved(position);
        }
    }

    public void clear() {
        isLoadingAdded = false;
        while (getItemCount() > 0) {
            remove(getItem(0));
        }
    }

    public boolean isEmpty() {
        return getItemCount() == 0;
    }


    public void addLoadingFooter() {
        isLoadingAdded = true;
        add(new Order());
    }

    public void removeLoadingFooter() {
        isLoadingAdded = false;

        int position = orderResults.size() - 1;
        Order result = getItem(position);

        if (result != null) {
            orderResults.remove(position);
            notifyItemRemoved(position);
        }
    }

    public Order getItem(int position) {
        return orderResults.get(position);
    }


   /*
   View Holders
   _________________________________________________________________________________________________
    */

    /**
     * Main list's content ViewHolder
     */
    protected class OrderVH extends RecyclerView.ViewHolder {
        TextView order_title,order_subtitle;
        ImageView red_dot;
        ConstraintLayout constraintLayout;

        public OrderVH(View itemView) {
            super(itemView);

            order_title = itemView.findViewById(R.id.record_title);
            order_subtitle = itemView.findViewById(R.id.record_subtitle);
            red_dot = itemView.findViewById(R.id.red_dot);
            constraintLayout = itemView.findViewById(R.id.constraintLayout);

        }
    }


    protected class LoadingVH extends RecyclerView.ViewHolder {

        public LoadingVH(View itemView) {
            super(itemView);
        }
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }


}