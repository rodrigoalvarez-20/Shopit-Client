package com.ralvarez20.shopit_client.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.badge.BadgeDrawable;
import com.ralvarez20.shopit_client.R;
import com.ralvarez20.shopit_client.models.Product;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import es.dmoral.toasty.Toasty;
import io.paperdb.Paper;

public class CartProductAdapter extends RecyclerView.Adapter<CartProductAdapter.ViewHolder> {
    private ArrayList<Product> prodList;
    private Context ctx;
    private BadgeDrawable badgeRef;
    private Button btnCheckoutRef;

    public CartProductAdapter(ArrayList<Product> prodList, Context ctx, BadgeDrawable badgeRef, Button btn) {
        this.prodList = prodList;
        this.ctx = ctx;
        this.badgeRef = badgeRef;
        this.btnCheckoutRef = btn;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView lblProdName, lblProdPrice, lblProdQuantity;
        public ImageButton btnAddOne, btnDecOne, btnDelete;
        public ImageView imgProduct;

        public ViewHolder(View itemView) {
            super(itemView);

            lblProdName = itemView.findViewById(R.id.lblCartProdName);
            lblProdPrice = itemView.findViewById(R.id.lblCartProdPrice);
            lblProdQuantity = itemView.findViewById(R.id.lblCartQuantity);
            btnAddOne = itemView.findViewById(R.id.btnCartAddOne);
            btnDecOne = itemView.findViewById(R.id.btnCartDecOne);
            btnDelete = itemView.findViewById(R.id.btnCartDeleteProd);
            imgProduct = itemView.findViewById(R.id.imgCartProd);
        }
    }

    @NonNull
    @Override
    public CartProductAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        Paper.init(context);
        View contactView = inflater.inflate(R.layout.cart_prod_view, parent, false);
        return new CartProductAdapter.ViewHolder(contactView);
    }

    @Override
    public void onBindViewHolder(@NonNull CartProductAdapter.ViewHolder holder, int position) {
        Product p = this.prodList.get(position);
        holder.lblProdName.setText(p.getName());
        holder.lblProdPrice.setText(this.ctx.getString(R.string.lblPrice, p.getPrice()));
        holder.lblProdQuantity.setText(String.valueOf(p.getQuantity()));

        Picasso.get().load("http://20.225.97.73/" + p.getImage()).into(holder.imgProduct);

        //Añadir los listeners para los botones y notificar al badge


        holder.btnDecOne.setOnClickListener(dec -> {
            if (p.getQuantity() - 1 == 0){
                Toasty.warning(this.ctx, "Para eliminar del carrtito ocupe el boton correspondiente", Toasty.LENGTH_SHORT).show();
            }else {
                p.setQuantity( p.getQuantity() - 1 );
                saveChanges(position);
            }
        });

        holder.btnAddOne.setOnClickListener(add -> {
            if(p.getQuantity() + 1 > p.getStock()){
                Toasty.warning(this.ctx, "La cantidad no puede exceder el stock del producto", Toasty.LENGTH_SHORT).show();
            }else {
                p.setQuantity( p.getQuantity() + 1 );
                saveChanges(position);
            }
        });

        holder.btnDelete.setOnClickListener(btnDel -> {
            // Eliminar el producto del adaptador
            AlertDialog.Builder dgConfirm = new AlertDialog.Builder(this.ctx);

            dgConfirm.setPositiveButton(ctx.getString(R.string.btnDeleteProdInCart), (dialog, id) -> {
                prodList.remove(p);
                saveChanges(position);
            });
            dgConfirm.setNegativeButton(ctx.getString(R.string.btnDissmisDialog), (dialog, id) -> { });
            dgConfirm.setMessage(ctx.getString(R.string.lblDialogRemoveProduct));
            dgConfirm.create().show();
        });

    }

    private void saveChanges(int positionAffected){

        // Hacer el recalculo
        double total = 0;
        for (Product pL : prodList)
            total += (pL.getPrice() * pL.getQuantity());

        btnCheckoutRef.setText(this.ctx.getString(R.string.btnCheckout, total));

        //Guardar los cambios en el local
        Paper.book().write("cart", prodList);
        //Actualizar el badge
        if(prodList.size() > 0)
            this.badgeRef.setNumber(prodList.size());
        else {
            this.badgeRef.setVisible(false);
            btnCheckoutRef.setClickable(false);
        }
        notifyItemChanged(positionAffected);
    }

    @Override
    public int getItemCount() {
        return this.prodList.size();
    }

}
