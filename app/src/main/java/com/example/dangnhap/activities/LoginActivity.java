package com.example.dangnhap.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.baicuoiki.MainActivity; // Import trang chủ của bạn
import com.example.baicuoiki.R;
import com.example.dangnhap.dao.NhanVienDAO;
import com.example.dangnhap.dao.TaiKhoanDAO;
import com.example.dangnhap.models.NhanVien;
import com.example.dangnhap.models.TaiKhoan;

public class LoginActivity extends AppCompatActivity {

    EditText edtUser, edtPass;
    Button btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        edtUser = findViewById(R.id.edtUser);
        edtPass = findViewById(R.id.edtPass);
        btnLogin = findViewById(R.id.btnLogin);

        if (btnLogin == null || edtUser == null || edtPass == null) {
            Toast.makeText(this, "Lỗi: Không tìm thấy giao diện Login!", Toast.LENGTH_LONG).show();
            return;
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
                // Đăng nhập thành công -> Chuyển thẳng vào Trang Chủ (MainActivity)
                Toast.makeText(this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show();
                
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                finish(); // Đóng màn hình Login
            } else {
                Toast.makeText(this, "Sai tài khoản hoặc mật khẩu", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
