package com.example.dangnhap.activities;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.baicuoiki.R;
import com.example.dangnhap.adapters.NhanVienAdapter;
import com.example.dangnhap.dao.NhanVienDAO;
import com.example.dangnhap.models.NhanVien;

import java.util.ArrayList;

public class NhanVienActivity extends AppCompatActivity {

    EditText edtMa, edtTen, edtVaiTro, edtUser;
    Button btnThem;
    ListView listView;

    NhanVienDAO dao;
    ArrayList<NhanVien> list;
    NhanVienAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nhanvien);

        edtMa = findViewById(R.id.edtMa);
        edtTen = findViewById(R.id.edtTen);
        edtVaiTro = findViewById(R.id.edtVaiTro);
        edtUser = findViewById(R.id.edtUserNV);
        btnThem = findViewById(R.id.btnThemNV);
        listView = findViewById(R.id.listNV);

        dao = new NhanVienDAO(this);

        loadData();

        btnThem.setOnClickListener(v -> {
            String ma = edtMa.getText().toString().trim();
            String ten = edtTen.getText().toString().trim();
            String vaiTro = edtVaiTro.getText().toString().trim();
            String user = edtUser.getText().toString().trim();

            if (ma.isEmpty() || ten.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập mã và tên", Toast.LENGTH_SHORT).show();
                return;
            }

            // Sửa lỗi: Sử dụng trực tiếp các biến public của NhanVien
            NhanVien nv = new NhanVien();
            nv.ma = ma;
            nv.ten = ten;
            nv.vaiTro = vaiTro;
            nv.username = user;

            if (dao.insert(nv)) {
                Toast.makeText(this, "Thêm thành công", Toast.LENGTH_SHORT).show();
                loadData();
                clearForm();
            } else {
                Toast.makeText(this, "Thêm thất bại", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void clearForm() {
        edtMa.setText("");
        edtTen.setText("");
        edtVaiTro.setText("");
        edtUser.setText("");
    }

    void loadData() {
        list = dao.getAll();
        // Sửa lỗi: Sử dụng NhanVienAdapter thay vì ArrayAdapter đơn giản
        if (list == null) {
            list = new ArrayList<>();
        }
        adapter = new NhanVienAdapter(this, list);
        listView.setAdapter(adapter);
    }
}
