package com.ralvarez20.shopit_client.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
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

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ViewHolder> {
    private ArrayList<Product> productsList;
    private Context ctx;
    BadgeDrawable badgeRef;

    public ProductAdapter(ArrayList<Product> pL, Context ctx, BadgeDrawable b){
        this.productsList = pL;
        this.ctx = ctx;
        this.badgeRef = b;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView lblName, lblDesc, lblPrice, lblStock;
        public EditText txtQuantity;
        public Button btnAddToCart;
        public ImageView imgProduct;

        public ViewHolder(View itemView) {
            super(itemView);

            lblName = itemView.findViewById(R.id.lblProductName);
            lblDesc = itemView.findViewById(R.id.lblProductDesc);
            lblPrice = itemView.findViewById(R.id.lblProductPrice);
            lblStock = itemView.findViewById(R.id.lblStock);
            txtQuantity = itemView.findViewById(R.id.txtQuantity);
            btnAddToCart = itemView.findViewById(R.id.btnAddToCart);
            imgProduct = itemView.findViewById(R.id.imgProduct);

        }
    }

    @NonNull
    @Override
    public ProductAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        Paper.init(context);

        View contactView = inflater.inflate(R.layout.product_view, parent, false);

        return new ViewHolder(contactView);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductAdapter.ViewHolder holder, int position) {
        Product p = this.productsList.get(position);
        holder.lblName.setText(p.getName());
        holder.lblDesc.setText(p.getDescription());
        holder.lblStock.setText(ctx.getString(R.string.lblStock, p.getStock()));
        holder.lblPrice.setText(ctx.getString(R.string.lblPrice, p.getPrice()));

        holder.btnAddToCart.setClickable(p.getStock() > 0);

        Picasso.get().load(p.getImage()).into(holder.imgProduct);

        holder.btnAddToCart.setOnClickListener(v -> {

            String quantity = holder.txtQuantity.getText().toString().trim();

            if (quantity.isEmpty() || Integer.parseInt(quantity) <= 0){
                Toasty.warning(this.ctx, "La cantidad debe de ser mayor a 0", Toasty.LENGTH_SHORT).show();
            }else if(Integer.parseInt(quantity) > p.getStock()){
                Toasty.warning(this.ctx, "La cantidad no puede exceder el stock", Toasty.LENGTH_SHORT).show();
            } else {
                ArrayList<Product> savedProds = Paper.book().read("cart", new ArrayList<>());
                if(savedProds != null && savedProds.size() > 0){
                    // Ya hay items, validar la suma de cantidad contra el stock
                    Product pInList = null;
                    int quantityToAdd = Integer.parseInt(quantity);
                    for (Product item : savedProds){
                        if(item.getSku().equals(p.getSku())){
                            pInList = item;
                            break;
                        }
                    }
                    if(pInList != null){
                        if (pInList.getQuantity() + quantityToAdd > p.getStock()){
                            Toasty.warning(this.ctx, "La cantidad no puede exceder el stock", Toasty.LENGTH_SHORT).show();

                        }else {
                            savedProds.get( savedProds.indexOf(pInList)).setQuantity( pInList.getQuantity() + quantityToAdd );
                            Paper.book().write("cart", savedProds);
                            changeStateOnSave(savedProds.size(), holder.txtQuantity);
                        }
                    } else {
                        // No esta el item en el arreglo, agregar
                        p.setQuantity(quantityToAdd);
                        savedProds.add(p);
                        Paper.book().write("cart", savedProds);
                        changeStateOnSave(savedProds.size(), holder.txtQuantity);
                    }
                }else {
                    //No hay items, validar
                    savedProds = new ArrayList<>();
                    p.setQuantity(Integer.parseInt(quantity));
                    savedProds.add(p);
                    Paper.book().write("cart", savedProds);
                    changeStateOnSave(savedProds.size(), holder.txtQuantity);
                }
            }
        });

    }

    private void changeStateOnSave(int newSize, EditText quantity){
        Toasty.success(this.ctx, "Se ha añadido al carrito", Toasty.LENGTH_SHORT).show();
        this.badgeRef.setNumber(newSize);
        this.badgeRef.setVisible(true);
        quantity.setText("");
        quantity.clearFocus();
    }

    @Override
    public int getItemCount() {
        return this.productsList.size();
    }
}
