package com.example.baicuoiki;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;

import database.DatabaseHelper;

/**
 * Senior Android Developer - Refactored Product Management
 * Tối ưu hiển thị chi tiết sản phẩm và JOIN Tên nhà cung cấp.
 * Hỗ trợ chọn ảnh từ thư viện và lưu vào bộ nhớ trong.
 */
public class ProductActivity extends AppCompatActivity {

    private ListView lvProducts;
    private EditText edtSearch;
    private Button btnSearch, btnAddNew;
    private ImageView btnBack;
    
    private DatabaseHelper dbHelper;
    private SQLiteDatabase db;
    private ArrayList<Product> productList;
    private ProductAdapter adapter;

    // Biến tạm để lưu đường dẫn ảnh đang chọn trong dialog
    private String currentSelectedImageUri = "";
    private ImageView imgPreviewInDialog;

    private static final int PICK_IMAGE_REQUEST = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_management);

        btnBack = findViewById(R.id.btnBack);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Quản lý sản phẩm");
        }

        dbHelper = new DatabaseHelper(this);
        db = dbHelper.getWritableDatabase();

        lvProducts = findViewById(R.id.lvProducts);
        edtSearch = findViewById(R.id.edtSearch);
        btnSearch = findViewById(R.id.btnSearch);
        btnAddNew = findViewById(R.id.btnAddNew);

        productList = new ArrayList<>();
        loadDataFromDatabase("");

        btnSearch.setOnClickListener(v -> {
            String keyword = edtSearch.getText().toString().trim();
            loadDataFromDatabase(keyword);
        });

        btnAddNew.setOnClickListener(v -> showProductDialog(null));

        lvProducts.setOnItemClickListener((parent, view, position, id) -> {
            Product selected = productList.get(position);
            showProductDialog(selected);
        });

        lvProducts.setOnItemLongClickListener((parent, view, position, id) -> {
            confirmDelete(productList.get(position));
            return true;
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void loadDataFromDatabase(String keyword) {
        productList.clear();
        Cursor cursor = null;
        try {
            String sql = "SELECT sp.*, ncc.tenNCC FROM SAN_PHAM sp " +
                         "LEFT JOIN NHA_CUNG_CAP ncc ON sp.maNCC = ncc.maNCC";
            
            if (keyword.isEmpty()) {
                cursor = db.rawQuery(sql, null);
            } else {
                sql += " WHERE sp.tenSanpham LIKE ? OR sp.maSanpham LIKE ?";
                cursor = db.rawQuery(sql, new String[]{"%" + keyword + "%", "%" + keyword + "%"});
            }

            if (cursor != null) {
                while (cursor.moveToNext()) {
                    Product p = new Product();
                    p.setId(cursor.getString(0));
                    p.setName(cursor.getString(1));
                    p.setImage(cursor.getString(2));
                    p.setDescription(cursor.getString(3));
                    p.setUnit(cursor.getString(4));
                    p.setPrice(cursor.getDouble(5));
                    p.setStock(cursor.getInt(6));
                    p.setExpiryDate(cursor.getString(7));
                    p.setSupplierId(cursor.getString(9));
                    p.setSupplierName(cursor.getString(10));
                    productList.add(p);
                }
                cursor.close();
            }

            if (adapter == null) {
                adapter = new ProductAdapter(this, productList);
                lvProducts.setAdapter(adapter);
            } else {
                adapter.notifyDataSetChanged();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Lỗi truy vấn: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void showProductDialog(Product product) {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.activity_add_edit_product);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        TextView tvTitle = dialog.findViewById(R.id.tvTitle);
        EditText edtId = dialog.findViewById(R.id.edtId);
        EditText edtName = dialog.findViewById(R.id.edtName);
        EditText edtDesc = dialog.findViewById(R.id.edtDescription);
        EditText edtUnit = dialog.findViewById(R.id.edtUnit);
        EditText edtPrice = dialog.findViewById(R.id.edtPrice);
        EditText edtStock = dialog.findViewById(R.id.edtStock);
        EditText edtExpiry = dialog.findViewById(R.id.edtExpiry);
        
        imgPreviewInDialog = dialog.findViewById(R.id.imgPreview);
        Button btnChooseImage = dialog.findViewById(R.id.btnChooseImage);
        
        Button btnSave = dialog.findViewById(R.id.btnSave);
        Button btnDelete = dialog.findViewById(R.id.btnDelete);

        currentSelectedImageUri = ""; // Reset khi mở dialog

        edtExpiry.setFocusable(false);
        edtExpiry.setClickable(true);
        edtExpiry.setOnClickListener(v -> {
            java.util.Calendar calendar = java.util.Calendar.getInstance();
            int year = calendar.get(java.util.Calendar.YEAR);
            int month = calendar.get(java.util.Calendar.MONTH);
            int day = calendar.get(java.util.Calendar.DAY_OF_MONTH);

            new android.app.DatePickerDialog(ProductActivity.this,
                    (view, selectedYear, selectedMonth, selectedDay) -> {
                        calendar.set(selectedYear, selectedMonth, selectedDay);
                        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault());
                        edtExpiry.setText(sdf.format(calendar.getTime()));
                    }, year, month, day).show();
        });

        android.widget.Spinner spnSupplier = dialog.findViewById(R.id.spnSupplier);
        ArrayList<String> supplierNames = new ArrayList<>();
        ArrayList<String> supplierIds = new ArrayList<>();

        try {
            Cursor cursorNcc = db.rawQuery("SELECT maNCC, tenNCC FROM NHA_CUNG_CAP", null);
            if (cursorNcc != null) {
                while (cursorNcc.moveToNext()) {
                    supplierIds.add(cursorNcc.getString(0));
                    supplierNames.add(cursorNcc.getString(1));
                }
                cursorNcc.close();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Lỗi lấy dữ liệu NCC: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        android.widget.ArrayAdapter<String> spinnerAdapter = new android.widget.ArrayAdapter<>(
                this, android.R.layout.simple_spinner_dropdown_item, supplierNames);
        spnSupplier.setAdapter(spinnerAdapter);

        boolean isEdit = (product != null);
        btnDelete.setVisibility(View.GONE);

        if (isEdit) {
            tvTitle.setText("SỬA SẢN PHẨM");
            edtId.setText(product.getId());
            edtId.setEnabled(false);
            edtName.setText(product.getName());
            edtDesc.setText(product.getDescription());
            edtUnit.setText(product.getUnit());
            edtPrice.setText(String.valueOf(product.getPrice()));
            edtStock.setText(String.valueOf(product.getStock()));
            edtExpiry.setText(product.getExpiryDate());
            
            currentSelectedImageUri = product.getImage();
            if (currentSelectedImageUri != null && !currentSelectedImageUri.isEmpty()) {
                Glide.with(this).load(currentSelectedImageUri).into(imgPreviewInDialog);
            }

            if (product.getSupplierId() != null) {
                int position = supplierIds.indexOf(product.getSupplierId());
                if (position >= 0) spnSupplier.setSelection(position);
            }
        } else {
            tvTitle.setText("THÊM SẢN PHẨM MỚI");
        }

        btnChooseImage.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(intent, PICK_IMAGE_REQUEST);
        });

        btnSave.setOnClickListener(v -> {
            try {
                String id = edtId.getText().toString().trim();
                String name = edtName.getText().toString().trim();

                if (id.isEmpty() || name.isEmpty()) {
                    Toast.makeText(this, "Mã và Tên không được để trống!", Toast.LENGTH_SHORT).show();
                    return;
                }

                ContentValues values = new ContentValues();
                values.put("maSanpham", id);
                values.put("tenSanpham", name);
                values.put("hinhAnh", currentSelectedImageUri); // Dùng path đã chọn
                values.put("motaSanpham", edtDesc.getText().toString().trim());
                values.put("donViTinh", edtUnit.getText().toString().trim());
                values.put("giaDon", Double.parseDouble(edtPrice.getText().toString().isEmpty() ? "0" : edtPrice.getText().toString()));
                values.put("soLuongTon", Integer.parseInt(edtStock.getText().toString().isEmpty() ? "0" : edtStock.getText().toString()));
                values.put("hanSuDung", edtExpiry.getText().toString().trim());

                int selectedPosition = spnSupplier.getSelectedItemPosition();
                if (selectedPosition >= 0) {
                    values.put("maNCC", supplierIds.get(selectedPosition));
                }

                if (isEdit) {
                    db.update("SAN_PHAM", values, "maSanpham=?", new String[]{id});
                    Toast.makeText(this, "Cập nhật thành công!", Toast.LENGTH_SHORT).show();
                } else {
                    long res = db.insert("SAN_PHAM", null, values);
                    if (res != -1) Toast.makeText(this, "Thêm mới thành công!", Toast.LENGTH_SHORT).show();
                    else Toast.makeText(this, "Thêm thất bại!", Toast.LENGTH_SHORT).show();
                }

                dialog.dismiss();
                loadDataFromDatabase("");
            } catch (Exception e) {
                Toast.makeText(this, "Lỗi lưu dữ liệu: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        dialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            if (imageUri != null) {
                String savedPath = saveImageToInternalStorage(imageUri);
                if (savedPath != null) {
                    currentSelectedImageUri = savedPath;
                    if (imgPreviewInDialog != null) {
                        Glide.with(this).load(savedPath).into(imgPreviewInDialog);
                    }
                }
            }
        }
    }

    private String saveImageToInternalStorage(Uri uri) {
        try {
            String fileName = "prod_" + System.currentTimeMillis() + ".jpg";
            InputStream is = getContentResolver().openInputStream(uri);
            FileOutputStream fos = openFileOutput(fileName, Context.MODE_PRIVATE);
            
            byte[] buffer = new byte[1024];
            int read;
            while ((read = is.read(buffer)) != -1) {
                fos.write(buffer, 0, read);
            }
            is.close();
            fos.close();
            return getFilesDir() + "/" + fileName;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void confirmDelete(Product p) {
        new AlertDialog.Builder(this)
                .setTitle("Xác nhận xóa")
                .setMessage("Bạn có chắc chắn muốn xóa sản phẩm [" + p.getName() + "] không?")
                .setPositiveButton("XÓA", (dialog, which) -> {
                    try {
                        db.delete("SAN_PHAM", "maSanpham=?", new String[]{p.getId()});
                        Toast.makeText(this, "Đã xóa sản phẩm", Toast.LENGTH_SHORT).show();
                        loadDataFromDatabase("");
                    } catch (Exception e) {
                        Toast.makeText(this, "Lỗi khi xóa: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("HỦY", null)
                .show();
    }

    private class ProductAdapter extends BaseAdapter {
        private Context context;
        private ArrayList<Product> list;
        private DecimalFormat formatter = new DecimalFormat("###,###,###");

        public ProductAdapter(Context context, ArrayList<Product> list) {
            this.context = context;
            this.list = list;
        }

        @Override public int getCount() { return list.size(); }
        @Override public Object getItem(int position) { return list.get(position); }
        @Override public long getItemId(int position) { return position; }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;

            if (convertView == null) {
                convertView = LayoutInflater.from(context).inflate(R.layout.item_product_list, parent, false);
                holder = new ViewHolder();
                holder.img = convertView.findViewById(R.id.imgProduct);
                holder.txtName = convertView.findViewById(R.id.txtName);
                holder.txtSupplier = convertView.findViewById(R.id.txtSupplierName);
                holder.txtPrice = convertView.findViewById(R.id.txtPrice);
                holder.txtInfo = convertView.findViewById(R.id.txtInfo);
                holder.txtDesc = convertView.findViewById(R.id.txtDesc);
                holder.btnEditItem = convertView.findViewById(R.id.btnEdit);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            Product p = list.get(position);

            holder.txtName.setText(p.getName());
            holder.txtSupplier.setText("NCC: " + (p.getSupplierName() != null ? p.getSupplierName() : "N/A"));
            holder.txtPrice.setText(formatter.format(p.getPrice()) + "đ");
            holder.txtInfo.setText("ĐVT: " + p.getUnit() + " | HSD: " + p.getExpiryDate() + " | Kho: " + p.getStock());
            holder.txtDesc.setText(p.getDescription());

            Glide.with(context)
                    .load(p.getImage())
                    .placeholder(android.R.drawable.ic_menu_gallery)
                    .error(android.R.drawable.ic_menu_report_image)
                    .into(holder.img);

            holder.btnEditItem.setOnClickListener(v -> showProductDialog(p));

            return convertView;
        }

        private class ViewHolder {
            ImageView img, btnEditItem;
            TextView txtName, txtSupplier, txtPrice, txtInfo, txtDesc;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (db != null && db.isOpen()) db.close();
    }
}
