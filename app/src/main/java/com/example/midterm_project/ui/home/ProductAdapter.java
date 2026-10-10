package com.example.midterm_project.ui.home;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.midterm_project.R;
import com.example.midterm_project.data.model.Product;


import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    // Interface callback để xử lý "Đặt hàng" nhanh từ list
    public interface OnOrderClickListener {
        void onOrderClick(Product product);
    }

    private List<Product> products = new ArrayList<>();
    private final OnOrderClickListener listener;

    public ProductAdapter(OnOrderClickListener listener) {
        this.listener = listener;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_product, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        // Animate each item as it appears
        holder.itemView.startAnimation(
                AnimationUtils.loadAnimation(holder.itemView.getContext(), R.anim.fade_in)
        );
        holder.bind(products.get(position));
    }

    @Override
    public int getItemCount() { return products.size(); }

    class ProductViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvName;
        private final TextView tvDescription;
        private final TextView tvPrice;
        private final Button btnOrder;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName        = itemView.findViewById(R.id.tvProductName);
            tvDescription = itemView.findViewById(R.id.tvProductDescription);
            tvPrice       = itemView.findViewById(R.id.tvProductPrice);
            btnOrder      = itemView.findViewById(R.id.btnOrder);
        }

        public void bind(Product product) {
            tvName.setText(product.getName());

            if (product.getDescription() != null && !product.getDescription().isEmpty()) {
                tvDescription.setVisibility(View.VISIBLE);
                tvDescription.setText(product.getDescription());
            } else {
                tvDescription.setVisibility(View.GONE);
            }

            NumberFormat fmt = NumberFormat.getNumberInstance(new Locale("vi", "VN"));
            tvPrice.setText(fmt.format(product.getPrice()) + " đ");

            // Click toàn bộ card -> mở màn hình chi tiết
            itemView.setOnClickListener(v -> {
                Intent intent = new Intent(v.getContext(), com.example.midterm_project.ui.cart.CartActivity.class);
                intent.putExtra(com.example.midterm_project.ui.cart.CartActivity.EXTRA_PRODUCT_ID,    product.getId());
                intent.putExtra(com.example.midterm_project.ui.cart.CartActivity.EXTRA_PRODUCT_NAME,  product.getName());
                intent.putExtra(com.example.midterm_project.ui.cart.CartActivity.EXTRA_PRODUCT_PRICE, product.getPrice());
                intent.putExtra(com.example.midterm_project.ui.cart.CartActivity.EXTRA_PRODUCT_DESC,  product.getDescription());
                v.getContext().startActivity(intent);
            });

            // Click nút "Đặt hàng" -> đặt hàng nhanh không cần vào chi tiết
            btnOrder.setOnClickListener(v -> listener.onOrderClick(product));
        }
    }
}
