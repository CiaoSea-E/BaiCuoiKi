package com.example.dangnhap.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.baicuoiki.MainActivity;
import com.example.baicuoiki.R;
import com.example.baicuoiki.RegisterActivity;
import com.example.dangnhap.dao.TaiKhoanDAO;
import com.example.dangnhap.models.TaiKhoan;

public class LoginActivity extends AppCompatActivity {

    private EditText edtUser, edtPass;
    private Button btnLogin;
    private TextView tvRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        edtUser = findViewById(R.id.edtUser);
        edtPass = findViewById(R.id.edtPass);
        btnLogin = findViewById(R.id.btnLogin);
        tvRegister = findViewById(R.id.tvRegister);

        if (btnLogin == null || edtUser == null || edtPass == null) {
            Toast.makeText(this, "Lỗi: Không tìm thấy giao diện Login!", Toast.LENGTH_LONG).show();
            return;
        }

        // Xử lý chuyển sang màn hình Đăng ký
        if (tvRegister != null) {
            tvRegister.setOnClickListener(v -> {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            });
        }

        btnLogin.setOnClickListener(v -> {
            String username = edtUser.getText().toString().trim();
            String password = edtPass.getText().toString().trim();

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                return;
            }

            TaiKhoanDAO tkDAO = new TaiKhoanDAO(this);
            TaiKhoan tk = tkDAO.checkLogin(username, password);

            if (tk != null) {
                // 1. Lưu quyền vào SharedPreferences
                SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                
                String role = username.equalsIgnoreCase("admin") ? "admin" : "customer";
                editor.putString("ROLE", role);
                editor.apply();

                // 2. Phân luồng chuyển màn hình
                Intent intent;
                if (role.equals("admin")) {
                    // Admin vào màn hình Quản lý
                    intent = new Intent(LoginActivity.this, MainActivity.class);
                } else {
                    // Khách hàng vào màn hình CustomerMainActivity (Giả định class đã tạo)
                    // Lưu ý: Nếu bạn chưa tạo CustomerMainActivity, hãy tạo file này trước để tránh lỗi compile
                    try {
                        Class<?> targetClass = Class.forName("com.example.baicuoiki.CustomerMainActivity");
                        intent = new Intent(LoginActivity.this, targetClass);
                    } catch (ClassNotFoundException e) {
                        Toast.makeText(this, "Màn hình Khách hàng đang phát triển!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }

                Toast.makeText(this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show();
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(this, "Sai tài khoản hoặc mật khẩu", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
