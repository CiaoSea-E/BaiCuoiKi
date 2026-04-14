package com.example.baicuoiki;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import database.DatabaseHelper;

public class SupplierActivity extends AppCompatActivity {

    private ImageView btnBack, btnSearch;
    private EditText edtSearch;
    private ListView lvSupplier;
    private TextView tvEmpty;
    private FloatingActionButton fabAdd;
    private List<Supplier> supplierList;
    private SupplierAdapter adapter;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_supplier);

        initViews();
        dbHelper = new DatabaseHelper(this);
        supplierList = new ArrayList<>();
        adapter = new SupplierAdapter(this, R.layout.item_supplier, supplierList);
        lvSupplier.setAdapter(adapter);

        setupEvents();
        loadDataFromDatabase();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        btnSearch = findViewById(R.id.btnSearch);
        edtSearch = findViewById(R.id.edtSearch);
        lvSupplier = findViewById(R.id.lvSupplier);
        tvEmpty = findViewById(R.id.tvEmpty);
        fabAdd = findViewById(R.id.fabAdd);
    }

    private void loadDataFromDatabase() {
        try {
            supplierList.clear();
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            Cursor cursor = db.rawQuery("SELECT * FROM NHA_CUNG_CAP", null);
            
            if (cursor != null && cursor.moveToFirst()) {
                int idxMa = cursor.getColumnIndex("maNCC");
                int idxTen = cursor.getColumnIndex("tenNCC");
                int idxSdt = cursor.getColumnIndex("sdt");
                int idxEmail = cursor.getColumnIndex("email");
                int idxDiaChi = cursor.getColumnIndex("diaChi");
                int idxTrangThai = cursor.getColumnIndex("trangThai");

                do {
                    supplierList.add(new Supplier(
                            idxMa != -1 ? cursor.getString(idxMa) : "",
                            idxTen != -1 ? cursor.getString(idxTen) : "",
                            idxSdt != -1 ? cursor.getString(idxSdt) : "",
                            idxEmail != -1 ? cursor.getString(idxEmail) : "",
                            idxDiaChi != -1 ? cursor.getString(idxDiaChi) : "",
                            idxTrangThai != -1 ? cursor.getString(idxTrangThai) : ""
                    ));
                } while (cursor.moveToNext());
                cursor.close();
            }
            
            if (supplierList.size() == 0) {
                tvEmpty.setVisibility(View.VISIBLE);
                lvSupplier.setVisibility(View.GONE);
            } else {
                tvEmpty.setVisibility(View.GONE);
                lvSupplier.setVisibility(View.VISIBLE);
            }
            adapter.notifyDataSetChanged();
        } catch (Exception e) {
            Toast.makeText(this, "Lỗi nạp dữ liệu: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void setupEvents() {
        btnBack.setOnClickListener(v -> finish());
        fabAdd.setOnClickListener(v -> showAddSupplierDialog());
        
        // Sự kiện Click bình thường: Chỉnh sửa
        lvSupplier.setOnItemClickListener((parent, view, position, id) -> {
            if (position < supplierList.size()) {
                Supplier selectedSupplier = supplierList.get(position);
                showEditSupplierDialog(selectedSupplier);
            }
        });

        // --- XỬ LÝ SỰ KIỆN NHẤN GIỮ LÂU (LONG CLICK) ĐỂ XÓA ---
        lvSupplier.setOnItemLongClickListener((parent, view, position, id) -> {
            Supplier selectedSupplier = supplierList.get(position);
            
            // KIỂM TRA RÀNG BUỘC: Chỉ xóa nếu trạng thái là "Ngừng hợp tác"
            if (selectedSupplier.getTrangThai() != null && 
                selectedSupplier.getTrangThai().equalsIgnoreCase("Ngừng hợp tác")) {
                showDeleteConfirmDialog(selectedSupplier);
            } else {
                Toast.makeText(this, "Chỉ có thể xóa nhà cung cấp đã Ngừng hợp tác!", Toast.LENGTH_SHORT).show();
            }
            return true; // Trả về true để không kích hoạt OnItemClick
        });

        btnSearch.setOnClickListener(v -> {
            String keyword = edtSearch.getText().toString().trim();
            if (keyword.isEmpty()) {
                loadDataFromDatabase();
                return;
            }
            try {
                supplierList.clear();
                SQLiteDatabase db = dbHelper.getReadableDatabase();
                String param = "%" + keyword + "%";
                String sql = "SELECT * FROM NHA_CUNG_CAP WHERE tenNCC LIKE ? OR maNCC LIKE ? OR sdt LIKE ? OR email LIKE ?";
                Cursor cursor = db.rawQuery(sql, new String[]{param, param, param, param});

                if (cursor != null && cursor.moveToFirst()) {
                    int idxMa = cursor.getColumnIndex("maNCC");
                    int idxTen = cursor.getColumnIndex("tenNCC");
                    int idxSdt = cursor.getColumnIndex("sdt");
                    int idxEmail = cursor.getColumnIndex("email");
                    int idxDiaChi = cursor.getColumnIndex("diaChi");
                    int idxTrangThai = cursor.getColumnIndex("trangThai");

                    do {
                        supplierList.add(new Supplier(
                                idxMa != -1 ? cursor.getString(idxMa) : "",
                                idxTen != -1 ? cursor.getString(idxTen) : "",
                                idxSdt != -1 ? cursor.getString(idxSdt) : "",
                                idxEmail != -1 ? cursor.getString(idxEmail) : "",
                                idxDiaChi != -1 ? cursor.getString(idxDiaChi) : "",
                                idxTrangThai != -1 ? cursor.getString(idxTrangThai) : ""
                        ));
                    } while (cursor.moveToNext());
                    cursor.close();
                }

                if (supplierList.size() == 0) {
                    lvSupplier.setVisibility(View.GONE);
                    tvEmpty.setVisibility(View.VISIBLE);
                    tvEmpty.setText("Không tìm thấy kết quả phù hợp với từ khóa: '" + keyword + "'");
                } else {
                    lvSupplier.setVisibility(View.VISIBLE);
                    tvEmpty.setVisibility(View.GONE);
                }
                adapter.notifyDataSetChanged();

            } catch (Exception e) {
                Toast.makeText(this, "Đã xảy ra lỗi hệ thống khi tìm kiếm!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // --- HÀM HIỂN THỊ HỘP THOẠI XÁC NHẬN XÓA ---
    private void showDeleteConfirmDialog(Supplier supplier) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Xác nhận xóa");
        builder.setMessage("Bạn có chắc chắn muốn xóa nhà cung cấp '" + supplier.getTenNCC() + "' không? Dữ liệu không thể khôi phục.");
        
        // Nút XÓA (Khẳng định)
        builder.setPositiveButton("XÓA", (dialog, which) -> {
            try {
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                // Thực thi xóa theo mã nhà cung cấp (Khóa chính)
                int result = db.delete("NHA_CUNG_CAP", "maNCC=?", new String[]{supplier.getMaNCC()});
                
                if (result > 0) {
                    Toast.makeText(this, "Đã xóa nhà cung cấp thành công", Toast.LENGTH_SHORT).show();
                    loadDataFromDatabase(); // Làm mới danh sách ListView
                } else {
                    Toast.makeText(this, "Lỗi: Không tìm thấy nhà cung cấp để xóa!", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                Toast.makeText(this, "Lỗi hệ thống khi xóa!", Toast.LENGTH_SHORT).show();
            }
        });

        // Nút HỦY (Phủ định)
        builder.setNegativeButton("HỦY", null);
        
        builder.create().show();
    }

    private void showAddSupplierDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_add_supplier);

        Window window = dialog.getWindow();
        if (window != null) {
            window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        final EditText edtMa = dialog.findViewById(R.id.edtMaNCC);
        final EditText edtTen = dialog.findViewById(R.id.edtTenNCC);
        final EditText edtSdt = dialog.findViewById(R.id.edtSdt);
        final EditText edtEmail = dialog.findViewById(R.id.edtEmail);
        final EditText edtDiaChi = dialog.findViewById(R.id.edtDiaChi);
        Button btnHuy = dialog.findViewById(R.id.btnHuy);
        Button btnLuu = dialog.findViewById(R.id.btnLuu);

        btnHuy.setOnClickListener(v -> dialog.dismiss());

        btnLuu.setOnClickListener(v -> {
            String ma = edtMa.getText().toString().trim();
            String ten = edtTen.getText().toString().trim();
            String sdt = edtSdt.getText().toString().trim();
            String email = edtEmail.getText().toString().trim();
            String diaChi = edtDiaChi.getText().toString().trim();

            if (ma.isEmpty() || ten.isEmpty() || sdt.isEmpty() || email.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đủ các trường bắt buộc (*) !", Toast.LENGTH_SHORT).show();
                return;
            }

            ContentValues values = new ContentValues();
            values.put("maNCC", ma);
            values.put("tenNCC", ten);
            values.put("sdt", sdt);
            values.put("email", email);
            values.put("diaChi", diaChi);
            values.put("trangThai", "Đang hợp tác");

            try {
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                long result = db.insert("NHA_CUNG_CAP", null, values);
                if (result != -1) {
                    Toast.makeText(this, "Thêm thành công", Toast.LENGTH_SHORT).show();
                    loadDataFromDatabase();
                    dialog.dismiss();
                } else {
                    Toast.makeText(this, "Mã NCC hoặc SĐT đã tồn tại!", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                Toast.makeText(this, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        dialog.show();
    }

    private void showEditSupplierDialog(Supplier supplier) {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_edit_supplier);

        Window window = dialog.getWindow();
        if (window != null) {
            window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        final EditText edtMa = dialog.findViewById(R.id.edtEditMa);
        final EditText edtTen = dialog.findViewById(R.id.edtEditTen);
        final EditText edtSdt = dialog.findViewById(R.id.edtEditSdt);
        final EditText edtEmail = dialog.findViewById(R.id.edtEditEmail);
        final EditText edtDiaChi = dialog.findViewById(R.id.edtEditDiaChi);
        final RadioGroup rgTrangThai = dialog.findViewById(R.id.rgTrangThai);
        final RadioButton rbDang = dialog.findViewById(R.id.rbDang);
        final RadioButton rbNgung = dialog.findViewById(R.id.rbNgung);
        Button btnHuy = dialog.findViewById(R.id.btnHuyEdit);
        Button btnCapNhat = dialog.findViewById(R.id.btnCapNhat);

        edtMa.setText(supplier.getMaNCC());
        edtMa.setEnabled(false);
        edtTen.setText(supplier.getTenNCC());
        edtSdt.setText(supplier.getSdt());
        edtEmail.setText(supplier.getEmail());
        edtDiaChi.setText(supplier.getDiaChi());

        if (supplier.getTrangThai() != null && supplier.getTrangThai().equals("Đang hợp tác")) {
            rbDang.setChecked(true);
        } else {
            rbNgung.setChecked(true);
        }

        btnHuy.setOnClickListener(v -> dialog.dismiss());

        btnCapNhat.setOnClickListener(v -> {
            String ten = edtTen.getText().toString().trim();
            String sdt = edtSdt.getText().toString().trim();
            String email = edtEmail.getText().toString().trim();
            String diaChi = edtDiaChi.getText().toString().trim();

            if (ten.isEmpty() || sdt.isEmpty() || email.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đủ thông tin bắt buộc!", Toast.LENGTH_SHORT).show();
                return;
            }

            String trangThaiMoi = (rgTrangThai.getCheckedRadioButtonId() == R.id.rbDang) ? "Đang hợp tác" : "Ngừng hợp tác";

            ContentValues values = new ContentValues();
            values.put("tenNCC", ten);
            values.put("sdt", sdt);
            values.put("email", email);
            values.put("diaChi", diaChi);
            values.put("trangThai", trangThaiMoi);

            try {
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                int rows = db.update("NHA_CUNG_CAP", values, "maNCC=?", new String[]{supplier.getMaNCC()});
                if (rows > 0) {
                    Toast.makeText(this, "Cập nhật thành công", Toast.LENGTH_SHORT).show();
                    loadDataFromDatabase();
                    dialog.dismiss();
                }
            } catch (Exception e) {
                Toast.makeText(this, "Lỗi: Số điện thoại hoặc Email đã bị trùng!", Toast.LENGTH_LONG).show();
            }
        });

        dialog.show();
    }
}