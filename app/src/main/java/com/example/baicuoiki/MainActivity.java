package com.example.baicuoiki;

import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.qlkhuyenmai.KhuyenMaiMainActivity;
import com.example.qlkhuyenmai.cskh.CSKHActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private LinearLayout btnSupplier, btnSchedule, btnCustomer, btnPromotion, btnCSKH;
    private BottomNavigationView bottomNavigation;
    private RecyclerView rvProducts;
    private ProductAdapter productAdapter;
    private List<Product> productList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            initViews();
            setupRecyclerView();
            setupMenuClickEvents();
            setupBottomNavigation();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Lỗi khởi tạo giao diện!", Toast.LENGTH_LONG).show();
        }
    }

    private void initViews() {
        btnSupplier    = findViewById(R.id.btnSupplier);
        btnSchedule    = findViewById(R.id.btnSchedule);
        btnCustomer    = findViewById(R.id.btnCustomer);
        btnPromotion   = findViewById(R.id.btnPromotion);
        btnCSKH        = findViewById(R.id.btnCSKH);
        bottomNavigation = findViewById(R.id.bottomNavigation);
        rvProducts = findViewById(R.id.rvProducts);
    }

    private void setupRecyclerView() {
        productList = new ArrayList<>();
        // Dữ liệu Đặc sản 3 miền - Lấy link từ Pexels
        productList.add(new Product("DS01", "Phở Thìn Lò Đúc (Hà Nội)", 65000, "https://images.pexels.com/photos/6420444/pexels-photo-6420444.jpeg", "Phở bò gia truyền nổi tiếng Hà Nội với thịt bò tái lăn đặc trưng.", 100));
        productList.add(new Product("DS02", "Bánh Mì Phượng (Hội An)", 35000, "https://images.pexels.com/photos/10350125/pexels-photo-10350125.jpeg", "Bánh mì ngon nhất thế giới với nước sốt đặc biệt.", 150));
        productList.add(new Product("DS03", "Bún Bò Huế Cố Đô", 55000, "https://images.pexels.com/photos/11467512/pexels-photo-11467512.jpeg", "Hương vị đậm đà đặc trưng của miền Trung nắng gió.", 80));
        productList.add(new Product("DS04", "Cơm Tấm Sài Gòn", 45000, "https://images.pexels.com/photos/17650116/pexels-photo-17650116.jpeg", "Sườn nướng mật ong, bì chả thơm ngon đúng điệu miền Nam.", 120));
        productList.add(new Product("DS05", "Bánh Xèo Miền Tây", 40000, "https://images.pexels.com/photos/12392817/pexels-photo-12392817.jpeg", "Bánh xèo giòn rụm với nhân tôm thịt, ăn kèm rau rừng.", 90));
        productList.add(new Product("DS06", "Cao Lầu Hội An", 50000, "https://images.pexels.com/photos/20341147/pexels-photo-20341147.jpeg", "Sợi mì vàng ươm ăn kèm xá xíu và rau sống Trà Quế.", 60));
        productList.add(new Product("DS07", "Nem Chua Rán Hà Nội", 45000, "https://images.pexels.com/photos/14730467/pexels-photo-14730467.jpeg", "Món ăn vặt đường phố không thể thiếu của giới trẻ Thủ đô.", 200));
        productList.add(new Product("DS08", "Mì Quảng Tôm Thịt", 45000, "https://images.pexels.com/photos/12392831/pexels-photo-12392831.jpeg", "Món ăn đặc sản Quảng Nam với sợi mì dày và nước dùng cô đặc.", 85));
        productList.add(new Product("DS09", "Chả Cá Lã Vọng", 150000, "https://images.pexels.com/photos/11467511/pexels-photo-11467511.jpeg", "Cá lăng nướng vàng ươm ăn kèm mắm tôm và rau thì là.", 40));
        productList.add(new Product("DS10", "Gỏi Cuốn Tôm Thịt", 15000, "https://images.pexels.com/photos/615467/pexels-photo-615467.jpeg", "Món ăn thanh mát, tốt cho sức khỏe với nhiều rau xanh.", 300));
        productList.add(new Product("DS11", "Bún Đậu Mắm Tôm", 55000, "https://images.pexels.com/photos/20120612/pexels-photo-20120612.jpeg", "Mẹt bún đầy đủ với đậu hũ chiên, chả cốm và mắm tôm.", 110));
        productList.add(new Product("DS12", "Cà Phê Muối Huế", 30000, "https://images.pexels.com/photos/312418/pexels-photo-312418.jpeg", "Hương vị cà phê độc đáo kết hợp vị mặn nhẹ của kem muối.", 250));
        productList.add(new Product("DS13", "Bún Chả Hà Nội", 50000, "https://images.pexels.com/photos/954637/pexels-photo-954637.jpeg", "Thịt nướng than hoa ăn kèm nước mắm chua ngọt và bún.", 95));
        productList.add(new Product("DS14", "Chè Cung Đình Huế", 25000, "https://images.pexels.com/photos/5946631/pexels-photo-5946631.jpeg", "Món tráng miệng thanh tao với nhiều loại hạt và củ.", 180));
        productList.add(new Product("DS15", "Cơm Hến Sông Hương", 30000, "https://images.pexels.com/photos/12392825/pexels-photo-12392825.jpeg", "Đặc sản dân dã của Huế với hến xào, tóp mỡ và mắm ruốc.", 140));
        productList.add(new Product("DS16", "Bún Quậy Phú Quốc", 65000, "https://images.pexels.com/photos/20120614/pexels-photo-20120614.jpeg", "Trải nghiệm tự pha nước chấm và thưởng thức hải sản tươi.", 75));
        productList.add(new Product("DS17", "Bánh Căn Đà Lạt", 35000, "https://images.pexels.com/photos/12392830/pexels-photo-12392830.jpeg", "Bánh căn nóng hổi ăn kèm xíu mại trong tiết trời se lạnh.", 160));
        productList.add(new Product("DS18", "Cá Kho Vũ Đại", 250000, "https://images.pexels.com/photos/11467514/pexels-photo-11467514.jpeg", "Cá trắm đen kho tộ kỳ công trong niêu đất suốt 12 tiếng.", 30));
        productList.add(new Product("DS19", "Lẩu Mắm Miền Tây", 180000, "https://images.pexels.com/photos/12392819/pexels-photo-12392819.jpeg", "Hương vị mắm cá linh đặc trưng cùng đủ loại rau đồng nội.", 50));
        productList.add(new Product("DS20", "Gỏi Cá Mai Ninh Thuận", 95000, "https://images.pexels.com/photos/14730465/pexels-photo-14730465.jpeg", "Cá mai tươi rói trộn thính, ăn kèm nước chấm đậu phộng.", 65));

        productAdapter = new ProductAdapter(this, productList);
        rvProducts.setLayoutManager(new GridLayoutManager(this, 2));
        rvProducts.setAdapter(productAdapter);
        rvProducts.setNestedScrollingEnabled(false);
    }

    private void setupMenuClickEvents() {
        if (btnSupplier != null) {
            btnSupplier.setOnClickListener(v -> {
                startActivity(new Intent(MainActivity.this, SupplierActivity.class));
            });
        }

        if (btnSchedule != null) {
            btnSchedule.setOnClickListener(v -> {
                startActivity(new Intent(MainActivity.this, LichLamActivity.class));
            });
        }

        if (btnCustomer != null) {
            btnCustomer.setOnClickListener(v -> {
                startActivity(new Intent(MainActivity.this, CustomerActivity.class));
            });
        }

        if (btnPromotion != null) {
            btnPromotion.setOnClickListener(v -> {
                startActivity(new Intent(MainActivity.this, KhuyenMaiMainActivity.class));
            });
        }

        if (btnCSKH != null) {
            btnCSKH.setOnClickListener(v -> {
                startActivity(new Intent(MainActivity.this, CSKHActivity.class));
            });
        }
    }

    private void setupBottomNavigation() {
        if (bottomNavigation != null) {
            bottomNavigation.setOnItemSelectedListener(item -> {
                int id = item.getItemId();
                if (id == R.id.nav_home) {
                    return true;
                } else if (id == R.id.nav_cart) {
                    startActivity(new Intent(this, CartActivity.class));
                    return true;
                } else if (id == R.id.nav_order) {
                    startActivity(new Intent(this, OrderActivity.class));
                    return true;
                } else if (id == R.id.nav_profile) {
                    Toast.makeText(this, "Tài khoản", Toast.LENGTH_SHORT).show();
                    return true;
                }
                return false;
            });
        }
    }
}
