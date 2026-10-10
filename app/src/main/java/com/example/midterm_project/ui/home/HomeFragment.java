package com.example.midterm_project.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.midterm_project.R;
import com.example.midterm_project.data.api.ApiService;
import com.example.midterm_project.data.api.GatewayApiClient;
import com.example.midterm_project.data.local.TokenManager;
import com.example.midterm_project.data.model.Product;
import com.example.midterm_project.data.repository.AppRepository;
import com.example.midterm_project.ui.cart.OrderViewModel;
import com.example.midterm_project.ui.cart.OrderViewModelFactory;


/**
 * ──────────────────────────────────────────────────────────────────────────────
 * NGHIỆP VỤ 2: Xem danh sách sản phẩm & Đặt hàng (Catalog & Order Flow)
 * ──────────────────────────────────────────────────────────────────────────────
 *
 * Luồng hoạt động:
 *  [HomeFragment] ──GET /api/products──► [API Gateway]
 *                                              │
 *                                      Định tuyến sang
 *                                              ▼
 *                                      [Product Service]
 *                                              │
 *                                      Trả về danh sách JSON
 *                                              │
 *                                              ▼
 *                               ProductViewModel (LiveData)
 *                                              │
 *                                              ▼
 *                               RecyclerView + ProductAdapter
 *
 *  Khi đặt hàng:
 *  [Bấm "Đặt hàng"] ──POST /api/orders──► [API Gateway]
 *                                               │
 *                                       Định tuyến + Xác thực JWT
 *                                               ▼
 *                                       [Order Service]
 *                                         │        │
 *                               Check Token   Trừ tồn kho
 *                               (Auth Svc)  (Product Svc)
 *                                               │
 *                                       Lưu đơn hàng vào DB riêng
 *                                               │
 *                                       Trả về orderId + status
 * ──────────────────────────────────────────────────────────────────────────────
 */
public class HomeFragment extends Fragment {

    private ProductViewModel productViewModel;
    private OrderViewModel orderViewModel;
    private ProductAdapter   adapter;
    private RecyclerView     rvProducts;
    private ProgressBar      progressBar;
    private TextView         tvError;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // ─── Khởi tạo dependencies (Token, Retrofit, Repository) ───
        TokenManager tokenManager = new TokenManager(requireContext().getApplicationContext());
        ApiService apiService = GatewayApiClient.getInstance(tokenManager).create(ApiService.class);
        AppRepository repository = new AppRepository(apiService);

        // ─── Khởi tạo ProductViewModel và OrderViewModel ───
        ProductViewModelFactory productFactory = new ProductViewModelFactory(repository);
        productViewModel = new ViewModelProvider(this, productFactory).get(ProductViewModel.class);
        
        OrderViewModelFactory orderFactory = new OrderViewModelFactory(repository);
        orderViewModel = new ViewModelProvider(this, orderFactory).get(OrderViewModel.class);

        initViews(view);
        setupRecyclerView();
        observeViewModel();

        // ─── BƯỚC 1: Gọi API Gateway → Product Service (GET /api/v1/products) ───
        productViewModel.fetchProducts();
    }

    private void initViews(View view) {
        rvProducts  = view.findViewById(R.id.rvProducts);
        progressBar = view.findViewById(R.id.progressBar);
        tvError     = view.findViewById(R.id.tvError);
    }

    private void setupRecyclerView() {
        // Callback khi bấm nút "Đặt hàng" nhanh ngay trong list
        adapter = new ProductAdapter(this::showOrderConfirmDialog);
        rvProducts.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvProducts.setAdapter(adapter);
    }

    private void observeViewModel() {
        // Quan sát trạng thái danh sách sản phẩm (Loading / Success / Error)
        productViewModel.getProducts().observe(getViewLifecycleOwner(), resource -> {
            if (resource == null) return;
            switch (resource.status) {
                case LOADING:
                    progressBar.setVisibility(View.VISIBLE);
                    rvProducts.setVisibility(View.GONE);
                    tvError.setVisibility(View.GONE);
                    break;

                case SUCCESS:
                    progressBar.setVisibility(View.GONE);
                    rvProducts.setVisibility(View.VISIBLE);
                    tvError.setVisibility(View.GONE);
                    if (resource.data != null) {
                        // ─── BƯỚC 2: Cập nhật RecyclerView với danh sách sản phẩm ───
                        adapter.setProducts(resource.data);
                    }
                    break;

                case ERROR:
                    progressBar.setVisibility(View.GONE);
                    rvProducts.setVisibility(View.GONE);
                    tvError.setVisibility(View.VISIBLE);
                    tvError.setText(resource.message);
                    break;
            }
        });

        // Quan sát kết quả đặt hàng từ OrderViewModel
        orderViewModel.getOrderResult().observe(getViewLifecycleOwner(), resource -> {
            if (resource == null) return;
            switch (resource.status) {
                case LOADING:
                    Toast.makeText(requireContext(), "Đang xử lý đơn hàng...", Toast.LENGTH_SHORT).show();
                    break;

                case SUCCESS:
                    // ─── BƯỚC 4: Order Service xử lý xong → hiển thị kết quả ───
                    Toast.makeText(requireContext(),
                            "✅ Đặt hàng thành công!\nMã đơn: " + resource.data.getOrderId(),
                            Toast.LENGTH_LONG).show();
                    break;

                case ERROR:
                    Toast.makeText(requireContext(), "❌ " + resource.message, Toast.LENGTH_SHORT).show();
                    break;
            }
        });
    }

    /**
     * Dialog xác nhận đặt hàng nhanh (không cần vào chi tiết).
     * ─── BƯỚC 3: Gọi API Gateway → Order Service (POST /api/v1/orders) ───
     * Header "Authorization: Bearer <token>" được AuthInterceptor tự động đính kèm.
     */
    private void showOrderConfirmDialog(Product product) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Xác nhận đặt hàng")
                .setMessage("Đặt 1 sản phẩm \"" + product.getName() + "\"?")
                .setPositiveButton("Đặt ngay", (dialog, which) -> orderViewModel.placeOrder(product))
                .setNegativeButton("Xem chi tiết", (dialog, which) -> openCartDetail(product))
                .show();
    }

    /** Mở màn hình chi tiết để chọn số lượng và địa chỉ giao hàng */
    private void openCartDetail(Product product) {
        Intent intent = new Intent(requireContext(), com.example.midterm_project.ui.cart.CartActivity.class);
        intent.putExtra(com.example.midterm_project.ui.cart.CartActivity.EXTRA_PRODUCT_ID,    product.getId());
        intent.putExtra(com.example.midterm_project.ui.cart.CartActivity.EXTRA_PRODUCT_NAME,  product.getName());
        intent.putExtra(com.example.midterm_project.ui.cart.CartActivity.EXTRA_PRODUCT_PRICE, product.getPrice());
        intent.putExtra(com.example.midterm_project.ui.cart.CartActivity.EXTRA_PRODUCT_DESC,  product.getDescription());
        startActivity(intent);
    }
}
