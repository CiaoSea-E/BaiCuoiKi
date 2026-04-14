package com.example.baicuoiki;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import database.DatabaseHelper;

// Nếu ExcelHelper nằm ở package khác (ví dụ: com.example.baicuoiki.utils),
// Android Studio sẽ tự động import nó vào đây.

public class LichLamActivity extends AppCompatActivity {

    private ImageView btnTimKiem, btnBack;
    private EditText edtTimKiem;
    private ListView lvLichLam;
    private TextView tvKhongCoDuLieu;
    private Button btnThemLich;
    private Button btnExport;

    private List<LichLamViec> listLichLam;
    private LichLamViecAdapter adapter;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lichlam);

        initViews();

        dbHelper = new DatabaseHelper(this);
        listLichLam = new ArrayList<>();
        adapter = new LichLamViecAdapter(this, R.layout.item_lichlam, listLichLam);
        lvLichLam.setAdapter(adapter);

        setupEvents();
        loadDataFromDatabase();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        btnTimKiem = findViewById(R.id.btnTimKiem);
        edtTimKiem = findViewById(R.id.edtTimKiem);
        lvLichLam = findViewById(R.id.lvLichLam);
        tvKhongCoDuLieu = findViewById(R.id.tvKhongCoDuLieu);
        btnThemLich = findViewById(R.id.btnThemLich);
        btnExport = findViewById(R.id.btnExport);
    }

    private void loadDataFromDatabase() {
        try {
            listLichLam.clear();
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            Cursor cursor = db.rawQuery("SELECT * FROM LICH_LAM_VIEC", null);

            if (cursor != null && cursor.moveToFirst()) {
                int idxMa = cursor.getColumnIndex("maLich");
                int idxMaNV = cursor.getColumnIndex("maNhanVien");
                int idxNgay = cursor.getColumnIndex("ngayLamViec");
                int idxCa = cursor.getColumnIndex("caLam");
                int idxNhiemVu = cursor.getColumnIndex("nhiemVu");

                do {
                    listLichLam.add(new LichLamViec(
                            idxMa != -1 ? cursor.getString(idxMa) : "",
                            idxMaNV != -1 ? cursor.getString(idxMaNV) : "",
                            idxNgay != -1 ? cursor.getString(idxNgay) : "",
                            idxCa != -1 ? cursor.getString(idxCa) : "",
                            idxNhiemVu != -1 ? cursor.getString(idxNhiemVu) : ""
                    ));
                } while (cursor.moveToNext());
                cursor.close();
            }

            if (listLichLam.size() == 0) {
                tvKhongCoDuLieu.setVisibility(View.VISIBLE);
                lvLichLam.setVisibility(View.GONE);
            } else {
                tvKhongCoDuLieu.setVisibility(View.GONE);
                lvLichLam.setVisibility(View.VISIBLE);
            }
            adapter.notifyDataSetChanged();

        } catch (Exception e) {
            Toast.makeText(this, "Lỗi nạp dữ liệu: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void setupEvents() {
        // Sự kiện nút quay lại
        btnBack.setOnClickListener(v -> finish());

        // ====================================================================
        // ĐÃ SỬA: SỰ KIỆN XUẤT FILE EXCEL
        // ====================================================================
        btnExport.setOnClickListener(v -> {
            // 1. Định nghĩa mảng Header cho Excel
            String[] headers = {"Mã Lịch", "Mã NV", "Ngày Làm Việc", "Ca Làm", "Nhiệm Vụ"};

            // 2. Gọi hàm tiện ích từ ExcelHelper
            ExcelHelper.exportToExcel(
                    LichLamActivity.this,
                    "LichLamViec_Export",
                    "Danh_Sach_Lich",
                    headers,
                    listLichLam,
                    (row, lich) -> {
                        // Ánh xạ dữ liệu vào từng cột của dòng hiện tại
                        row.createCell(0).setCellValue(lich.getMaLich());
                        row.createCell(1).setCellValue(lich.getMaNhanVien());
                        row.createCell(2).setCellValue(lich.getNgayLamViec());
                        row.createCell(3).setCellValue(lich.getCaLam());
                        row.createCell(4).setCellValue(lich.getNhiemVu());
                    }
            );
        });

        btnThemLich.setOnClickListener(v -> showDialogLichLam(null));

        lvLichLam.setOnItemClickListener((parent, view, position, id) -> {
            if (position < listLichLam.size()) {
                LichLamViec selectedItem = listLichLam.get(position);
                showDialogLichLam(selectedItem);
            }
        });

        lvLichLam.setOnItemLongClickListener((parent, view, position, id) -> {
            LichLamViec selectedItem = listLichLam.get(position);
            showDeleteConfirmDialog(selectedItem);
            return true;
        });

        btnTimKiem.setOnClickListener(v -> {
            String keyword = edtTimKiem.getText().toString().trim();
            if (keyword.isEmpty()) {
                loadDataFromDatabase();
                return;
            }

            try {
                listLichLam.clear();
                SQLiteDatabase db = dbHelper.getReadableDatabase();
                String param = "%" + keyword + "%";
                String sql = "SELECT * FROM LICH_LAM_VIEC WHERE maNhanVien LIKE ? OR ngayLamViec LIKE ? OR maLich LIKE ?";
                Cursor cursor = db.rawQuery(sql, new String[]{param, param, param});

                if (cursor != null && cursor.moveToFirst()) {
                    int idxMa = cursor.getColumnIndex("maLich");
                    int idxMaNV = cursor.getColumnIndex("maNhanVien");
                    int idxNgay = cursor.getColumnIndex("ngayLamViec");
                    int idxCa = cursor.getColumnIndex("caLam");
                    int idxNhiemVu = cursor.getColumnIndex("nhiemVu");

                    do {
                        listLichLam.add(new LichLamViec(
                                idxMa != -1 ? cursor.getString(idxMa) : "",
                                idxMaNV != -1 ? cursor.getString(idxMaNV) : "",
                                idxNgay != -1 ? cursor.getString(idxNgay) : "",
                                idxCa != -1 ? cursor.getString(idxCa) : "",
                                idxNhiemVu != -1 ? cursor.getString(idxNhiemVu) : ""
                        ));
                    } while (cursor.moveToNext());
                    cursor.close();
                }

                if (listLichLam.size() == 0) {
                    lvLichLam.setVisibility(View.GONE);
                    tvKhongCoDuLieu.setVisibility(View.VISIBLE);
                    tvKhongCoDuLieu.setText("Không tìm thấy lịch làm việc cho: '" + keyword + "'");
                } else {
                    lvLichLam.setVisibility(View.VISIBLE);
                    tvKhongCoDuLieu.setVisibility(View.GONE);
                }
                adapter.notifyDataSetChanged();

            } catch (Exception e) {
                Toast.makeText(this, "Đã xảy ra lỗi hệ thống khi tìm kiếm!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // --- HÀM HIỂN THỊ HỘP THOẠI XÁC NHẬN XÓA ---
    private void showDeleteConfirmDialog(LichLamViec item) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Xác nhận xóa");
        builder.setMessage("Bạn có chắc chắn muốn xóa lịch làm việc của nhân viên '" + item.getMaNhanVien() + "' vào ngày " + item.getNgayLamViec() + " không? Dữ liệu không thể khôi phục.");

        builder.setPositiveButton("XÓA", (dialog, which) -> {
            try {
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                int result = db.delete("LICH_LAM_VIEC", "maLich=?", new String[]{item.getMaLich()});

                if (result > 0) {
                    Toast.makeText(this, "Đã xóa lịch làm việc thành công", Toast.LENGTH_SHORT).show();
                    loadDataFromDatabase();
                } else {
                    Toast.makeText(this, "Lỗi: Không tìm thấy lịch làm việc để xóa!", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                Toast.makeText(this, "Lỗi hệ thống khi xóa!", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("HỦY", null);
        builder.create().show();
    }

    // --- HÀM HIỂN THỊ DIALOG THÊM / SỬA ---
    private void showDialogLichLam(LichLamViec item) {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_lichlam);

        Window window = dialog.getWindow();
        if (window != null) {
            window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        final TextView tvTieuDeDialog = dialog.findViewById(R.id.tvTieuDeDialog);
        final TextInputEditText edtMaLich = dialog.findViewById(R.id.edtMaLich);
        final TextInputEditText edtMaNhanVien = dialog.findViewById(R.id.edtMaNhanVien);
        final TextView tvNgayLam = dialog.findViewById(R.id.tvNgayLam);
        final Button btnChonNgay = dialog.findViewById(R.id.btnChonNgay);
        final Spinner spCaLam = dialog.findViewById(R.id.spCaLam);
        final TextInputEditText edtNhiemVu = dialog.findViewById(R.id.edtNhiemVu);
        Button btnHuy = dialog.findViewById(R.id.btnHuy);
        Button btnLuu = dialog.findViewById(R.id.btnLuu);

        String[] mangCaLam = {"Ca Sáng", "Ca Chiều", "Ca Tối"};
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, mangCaLam);
        spCaLam.setAdapter(spinnerAdapter);

        btnChonNgay.setOnClickListener(v -> {
            Calendar c = Calendar.getInstance();
            DatePickerDialog dateDialog = new DatePickerDialog(LichLamActivity.this,
                    (view, year, month, dayOfMonth) -> {
                        String date = dayOfMonth + "/" + (month + 1) + "/" + year;
                        tvNgayLam.setText(date);
                    }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
            dateDialog.show();
        });

        btnHuy.setOnClickListener(v -> dialog.dismiss());

        boolean isEditMode = (item != null);

        if (isEditMode) {
            tvTieuDeDialog.setText("CẬP NHẬT LỊCH LÀM VIỆC");
            edtMaLich.setText(item.getMaLich());
            edtMaLich.setEnabled(false); // Không cho sửa khóa chính
            edtMaNhanVien.setText(item.getMaNhanVien());
            tvNgayLam.setText(item.getNgayLamViec());
            edtNhiemVu.setText(item.getNhiemVu());

            for (int i = 0; i < mangCaLam.length; i++) {
                if (item.getCaLam() != null && mangCaLam[i].equals(item.getCaLam())) {
                    spCaLam.setSelection(i);
                    break;
                }
            }
        }

        btnLuu.setOnClickListener(v -> {
            String maLich = edtMaLich.getText().toString().trim();
            String maNV = edtMaNhanVien.getText().toString().trim();
            String ngay = tvNgayLam.getText().toString().trim();
            String nhiemVu = edtNhiemVu.getText().toString().trim();
            String caLam = spCaLam.getSelectedItem() != null ? spCaLam.getSelectedItem().toString() : "";

            if (maLich.isEmpty() || maNV.isEmpty() || ngay.equals("Chọn ngày...")) {
                Toast.makeText(this, "Vui lòng nhập đủ các trường bắt buộc!", Toast.LENGTH_SHORT).show();
                return;
            }

            ContentValues values = new ContentValues();
            values.put("maNhanVien", maNV);
            values.put("ngayLamViec", ngay);
            values.put("caLam", caLam);
            values.put("nhiemVu", nhiemVu);

            try {
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                if (!isEditMode) {
                    values.put("maLich", maLich); // Chỉ thêm mã lịch khi tạo mới
                    long result = db.insert("LICH_LAM_VIEC", null, values);
                    if (result != -1) {
                        Toast.makeText(this, "Thêm thành công", Toast.LENGTH_SHORT).show();
                        loadDataFromDatabase();
                        dialog.dismiss();
                    } else {
                        Toast.makeText(this, "Mã lịch đã tồn tại!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    int rows = db.update("LICH_LAM_VIEC", values, "maLich=?", new String[]{maLich});
                    if (rows > 0) {
                        Toast.makeText(this, "Cập nhật thành công", Toast.LENGTH_SHORT).show();
                        loadDataFromDatabase();
                        dialog.dismiss();
                    } else {
                        Toast.makeText(this, "Cập nhật thất bại!", Toast.LENGTH_SHORT).show();
                    }
                }
            } catch (Exception e) {
                Toast.makeText(this, "Lỗi hệ thống: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        dialog.show();
    }
}
