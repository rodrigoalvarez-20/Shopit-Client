package com.ralvarez20.shopit_client.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ralvarez20.shopit_client.R;
import com.ralvarez20.shopit_client.models.Product;
import com.ralvarez20.shopit_client.models.Purchase;
import com.ralvarez20.shopit_client.utilities.ApiAdapter;
import com.ralvarez20.shopit_client.utilities.Common;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import es.dmoral.toasty.Toasty;
import io.paperdb.Paper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PurchasesAdapter extends RecyclerView.Adapter<PurchasesAdapter.ViewHolder> {
    private ArrayList<Purchase> purchasesList;
    private Context ctx;

    public PurchasesAdapter(ArrayList<Purchase> purchasesList, Context ctx) {
        this.purchasesList = purchasesList;
        this.ctx = ctx;
    }

    @NonNull
    @Override
    public PurchasesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        Paper.init(context);

        View contactView = inflater.inflate(R.layout.purchase_view, parent, false);

        return new PurchasesAdapter.ViewHolder(contactView);
    }

    @Override
    public int getItemCount() {
        return this.purchasesList.size();
    }

    @Override
    public void onBindViewHolder(@NonNull PurchasesAdapter.ViewHolder holder, int position) {
        Purchase purchase = purchasesList.get(position);

        holder.btnShowPurchaseInfo.setText(this.ctx.getString(R.string.btnPurchaseInfo, purchase.getDate(), purchase.getTotal()));
        // rcv Cambiar visibilidad
        holder.btnShowPurchaseInfo.setOnClickListener(v -> {

            if (holder.rcvPurchaseItems.getVisibility() == View.VISIBLE){
                holder.rcvPurchaseItems.setVisibility(View.GONE);
            }else {
                //Aqui
                ArrayList<Product> prods = purchase.getProducts();
                ProductPurchaseAdapter ppAdapter = new ProductPurchaseAdapter(prods, ctx);
                holder.rcvPurchaseItems.setAdapter(ppAdapter);
                holder.rcvPurchaseItems.setLayoutManager(new LinearLayoutManager(ctx));
                holder.rcvPurchaseItems.setVisibility(View.VISIBLE);
            }
        });

    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        private Button btnShowPurchaseInfo;
        private RecyclerView rcvPurchaseItems;

        public ViewHolder(View itemView) {
            super(itemView);

            btnShowPurchaseInfo = itemView.findViewById(R.id.btnPPInfo);
            rcvPurchaseItems = itemView.findViewById(R.id.rcvPPInfo);

        }
    }

}
