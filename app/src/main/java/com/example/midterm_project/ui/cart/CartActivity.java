package com.example.midterm_project.ui.cart;

import android.os.Bundle;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.midterm_project.R;
import com.example.midterm_project.data.api.ApiService;
import com.example.midterm_project.data.api.GatewayApiClient;
import com.example.midterm_project.data.local.TokenManager;
import com.example.midterm_project.data.model.Product;
import com.example.midterm_project.data.repository.AppRepository;
import com.google.android.material.textfield.TextInputEditText;

import java.text.NumberFormat;
import java.util.Locale;

public class CartActivity extends AppCompatActivity {

    public static final String EXTRA_PRODUCT_ID   = "product_id";
    public static final String EXTRA_PRODUCT_NAME = "product_name";
    public static final String EXTRA_PRODUCT_PRICE = "product_price";
    public static final String EXTRA_PRODUCT_DESC  = "product_description";

    private int quantity = 1;
    private long unitPrice = 0;

    private TextView tvDetailName, tvDetailPrice, tvDetailDescription, tvQuantity, tvTotalPrice;
    private ImageButton btnBack, btnDecrease, btnIncrease;
    private Button btnPlaceOrder;
    private ProgressBar progressBar;
    private TextInputEditText etAddress;

    private OrderViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        // Slide-in animation
        View root = findViewById(android.R.id.content);
        root.startAnimation(AnimationUtils.loadAnimation(this, R.anim.slide_in_bottom));

        // Setup ViewModel
        TokenManager tokenManager = new TokenManager(getApplicationContext());
        ApiService apiService = GatewayApiClient.getInstance(tokenManager).create(ApiService.class);
        AppRepository repository = new AppRepository(apiService);
        OrderViewModelFactory factory = new OrderViewModelFactory(repository);
        viewModel = new ViewModelProvider(this, factory).get(OrderViewModel.class);

        initViews();

        // Get product data from Intent
        String productId   = getIntent().getStringExtra(EXTRA_PRODUCT_ID);
        String productName = getIntent().getStringExtra(EXTRA_PRODUCT_NAME);
        unitPrice          = getIntent().getLongExtra(EXTRA_PRODUCT_PRICE, 0);
        String productDesc = getIntent().getStringExtra(EXTRA_PRODUCT_DESC);

        bindProductData(productName, productDesc, unitPrice);
        observeViewModel();

        btnBack.setOnClickListener(v -> finish());

        btnDecrease.setOnClickListener(v -> {
            if (quantity > 1) {
                quantity--;
                updateQuantityAndTotal();
            }
        });

        btnIncrease.setOnClickListener(v -> {
            if (quantity < 99) {
                quantity++;
                updateQuantityAndTotal();
            }
        });

        btnPlaceOrder.setOnClickListener(v -> {
            String address = etAddress.getText() != null ? etAddress.getText().toString().trim() : "";
            if (address.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập địa chỉ giao hàng", Toast.LENGTH_SHORT).show();
                return;
            }
            // Tạo product tạm để gọi placeOrder
            Product product = new Product();
            product.setId(productId);
            product.setName(productName);
            product.setPrice(unitPrice);
            product.setQuantityToOrder(quantity);
            product.setDeliveryAddress(address);
            viewModel.placeOrder(product);
        });
    }

    private void initViews() {
        tvDetailName        = findViewById(R.id.tvDetailName);
        tvDetailPrice       = findViewById(R.id.tvDetailPrice);
        tvDetailDescription = findViewById(R.id.tvDetailDescription);
        tvQuantity          = findViewById(R.id.tvQuantity);
        tvTotalPrice        = findViewById(R.id.tvTotalPrice);
        btnBack             = findViewById(R.id.btnBack);
        btnDecrease         = findViewById(R.id.btnDecrease);
        btnIncrease         = findViewById(R.id.btnIncrease);
        btnPlaceOrder       = findViewById(R.id.btnPlaceOrder);
        progressBar         = findViewById(R.id.progressBar);
        etAddress           = findViewById(R.id.etAddress);
    }

    private void bindProductData(String name, String desc, long price) {
        tvDetailName.setText(name);
        tvDetailDescription.setText(desc != null && !desc.isEmpty() ? desc : "Không có mô tả.");

        NumberFormat fmt = NumberFormat.getNumberInstance(new Locale("vi", "VN"));
        tvDetailPrice.setText(fmt.format(price) + " đ");
        updateQuantityAndTotal();
    }

    private void updateQuantityAndTotal() {
        tvQuantity.setText(String.valueOf(quantity));
        NumberFormat fmt = NumberFormat.getNumberInstance(new Locale("vi", "VN"));
        tvTotalPrice.setText(fmt.format(unitPrice * quantity) + " đ");
    }

    private void observeViewModel() {
        viewModel.getOrderResult().observe(this, resource -> {
            if (resource == null) return;
            switch (resource.status) {
                case LOADING:
                    progressBar.setVisibility(View.VISIBLE);
                    btnPlaceOrder.setEnabled(false);
                    break;
                case SUCCESS:
                    progressBar.setVisibility(View.GONE);
                    btnPlaceOrder.setEnabled(true);
                    Toast.makeText(this,
                            "✅ Đặt hàng thành công!\nMã đơn: " + resource.data.getOrderId(),
                            Toast.LENGTH_LONG).show();
                    finish();
                    break;
                case ERROR:
                    progressBar.setVisibility(View.GONE);
                    btnPlaceOrder.setEnabled(true);
                    Toast.makeText(this, "❌ " + resource.message, Toast.LENGTH_SHORT).show();
                    break;
            }
        });
    }
}
