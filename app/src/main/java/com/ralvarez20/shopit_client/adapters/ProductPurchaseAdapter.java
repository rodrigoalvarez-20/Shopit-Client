package com.ralvarez20.shopit_client.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ralvarez20.shopit_client.R;
import com.ralvarez20.shopit_client.models.Product;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import io.paperdb.Paper;

public class ProductPurchaseAdapter extends RecyclerView.Adapter<ProductPurchaseAdapter.ViewHolder>{

    private ArrayList<Product> productsList;
    private Context ctx;

    public ProductPurchaseAdapter(ArrayList<Product> pList, Context ctx) {
        this.productsList = pList;
        this.ctx = ctx;
    }

    @NonNull
    @Override
    public ProductPurchaseAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        Paper.init(context);

        View contactView = inflater.inflate(R.layout.product_purchase_info, parent, false);

        return new ProductPurchaseAdapter.ViewHolder(contactView);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductPurchaseAdapter.ViewHolder holder, int position) {
        Product p = this.productsList.get(position);

        holder.lblPPName.setText(p.getName());
        holder.lblPPSku.setText(this.ctx.getString(R.string.lblSku, p.getSku()));
        holder.lblPPCategory.setText(this.ctx.getString(R.string.lblCat, p.getCategory()));
        holder.lblPPQuantity.setText(this.ctx.getString(R.string.lblQuantityPurchase, p.getQuantity()));
        holder.lblPPPrice.setText(this.ctx.getString(R.string.lblPrice, p.getPrice()));
        Picasso.get().load(p.getImage()).into(holder.imgPPImage);
    }

    @Override
    public int getItemCount() {
        return this.productsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView imgPPImage;
        private TextView lblPPName, lblPPPrice, lblPPSku, lblPPQuantity, lblPPCategory;

        public ViewHolder(View itemView) {
            super(itemView);

            imgPPImage = itemView.findViewById(R.id.imgPPImage);
            lblPPName = itemView.findViewById(R.id.lblPPName);
            lblPPPrice = itemView.findViewById(R.id.lblPPPrice);
            lblPPSku = itemView.findViewById(R.id.lblPPSku);
            lblPPQuantity = itemView.findViewById(R.id.lblPPQuantity);
            lblPPCategory = itemView.findViewById(R.id.lblPPCategory);

        }
    }


}
