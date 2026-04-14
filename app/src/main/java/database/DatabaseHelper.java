package database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "QuanLyHeThong.db";
    private static final int DATABASE_VERSION = 1;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // 1. Phần của Thùy [cite: 7]
        db.execSQL("CREATE TABLE TAI_KHOAN (tenDangnhap TEXT PRIMARY KEY, matKhau TEXT NOT NULL, trangThai INTEGER, soLanDangNhapSai INTEGER DEFAULT 0, maOTP TEXT, thoiGianHetHanOTP TEXT)");
        db.execSQL("CREATE TABLE NHAN_VIEN (maNhanVien TEXT PRIMARY KEY, hoTen TEXT NOT NULL, sdt TEXT UNIQUE, email TEXT UNIQUE, cccd TEXT UNIQUE, ngaySinh TEXT, vaiTro TEXT, tenDangnhap TEXT REFERENCES TAI_KHOAN(tenDangnhap))");
        db.execSQL("CREATE TABLE DANH_MUC_SP (maDanhMuc TEXT PRIMARY KEY, tenDanhMuc TEXT, trangThai INTEGER)");
        db.execSQL("CREATE TABLE SAN_PHAM (maSanpham TEXT PRIMARY KEY, tenSanpham TEXT NOT NULL, maDanhMuc TEXT REFERENCES DANH_MUC_SP(maDanhMuc), hinhAnh TEXT, motaSanpham TEXT, donViTinh TEXT, giaDon REAL, soLuongTon INTEGER DEFAULT 0, hanSuDung TEXT, trangThai TEXT, maNCC TEXT REFERENCES NHA_CUNG_CAP(maNCC))");
        // 2. Phần của Đức Anh [cite: 36]
        db.execSQL("CREATE TABLE NHA_CUNG_CAP (maNCC TEXT PRIMARY KEY, tenNCC TEXT NOT NULL, sdt TEXT UNIQUE, email TEXT UNIQUE, diaChi TEXT, trangThai TEXT)");
        db.execSQL("CREATE TABLE LICH_LAM_VIEC (maLich TEXT PRIMARY KEY, maNhanVien TEXT REFERENCES NHAN_VIEN(maNhanVien), ngayLamViec TEXT, caLam TEXT, nhiemVu TEXT NOT NULL)");

        // 3. Phần của My [cite: 49]
        db.execSQL("CREATE TABLE KHUYEN_MAI (maKhuyenMai TEXT PRIMARY KEY, loaiMa TEXT, giaTriGiam REAL, donToiThieu REAL, ngayKetThuc TEXT)");
        db.execSQL("CREATE TABLE YEU_CAU_HO_TRO (maYeuCau TEXT PRIMARY KEY, loaiYeuCau TEXT, maKhachHang TEXT REFERENCES KHACH_HANG(maKhachHang), noiDungKH TEXT, noiDungPhanHoi TEXT, trangThai TEXT, maNVCSKH TEXT)");

        // 4. Phần của Duy [cite: 62]
        db.execSQL("CREATE TABLE PHIEU_KHO (maPhieu TEXT PRIMARY KEY, loaiPhieu TEXT, ngayLapPhieu TEXT, tongTien REAL, maNVKho TEXT REFERENCES NHAN_VIEN(maNhanVien), maNCC TEXT REFERENCES NHA_CUNG_CAP(maNCC), trangThaiTT TEXT)");
        db.execSQL("CREATE TABLE CHI_TIET_PHIEU_KHO (maPhieu TEXT REFERENCES PHIEU_KHO(maPhieu), maSanpham TEXT REFERENCES SAN_PHAM(maSanpham), soLuong INTEGER, donGia REAL, PRIMARY KEY(maPhieu, maSanpham))");
        db.execSQL("CREATE TABLE BANG_LUONG (maBangLuong TEXT PRIMARY KEY, thang TEXT, luongCoBan REAL, tongPhuCap REAL, tongKhauTru REAL, tongLuong REAL, maNhanVien TEXT REFERENCES NHAN_VIEN(maNhanVien), maNVKeToan TEXT REFERENCES NHAN_VIEN(maNhanVien))");

        // 5. Phần của Tân (Đã gộp địa chỉ) [cite: 81, 82]
        db.execSQL("CREATE TABLE KHACH_HANG (maKhachHang TEXT PRIMARY KEY, hoTen TEXT NOT NULL, sdt TEXT UNIQUE, email TEXT UNIQUE, diaChi TEXT, trangThai INTEGER, tenDangnhap TEXT REFERENCES TAI_KHOAN(tenDangnhap))");
        db.execSQL("CREATE TABLE HOA_DON (maHoadon TEXT PRIMARY KEY, maKhachHang TEXT REFERENCES KHACH_HANG(maKhachHang), ngayTaohoadon TEXT, pThucThanhToan TEXT, tongTienTT REAL, trangThaiDH TEXT, lyDoHuy TEXT)");
        db.execSQL("CREATE TABLE CHI_TIET_HOA_DON (maHoadon TEXT REFERENCES HOA_DON(maHoadon), maSanpham TEXT REFERENCES SAN_PHAM(maSanpham), soLuong INTEGER, giaBan REAL, PRIMARY KEY(maHoadon, maSanpham))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop các bảng nếu tồn tại và tạo lại (Giữ nguyên thứ tự drop cẩn thận khóa ngoại)
        db.execSQL("DROP TABLE IF EXISTS CHI_TIET_HOA_DON");
        db.execSQL("DROP TABLE IF EXISTS HOA_DON");
        db.execSQL("DROP TABLE IF EXISTS KHACH_HANG");
        db.execSQL("DROP TABLE IF EXISTS BANG_LUONG");
        db.execSQL("DROP TABLE IF EXISTS CHI_TIET_PHIEU_KHO");
        db.execSQL("DROP TABLE IF EXISTS PHIEU_KHO");
        db.execSQL("DROP TABLE IF EXISTS YEU_CAU_HO_TRO");
        db.execSQL("DROP TABLE IF EXISTS KHUYEN_MAI");
        db.execSQL("DROP TABLE IF EXISTS LICH_LAM_VIEC");
        db.execSQL("DROP TABLE IF EXISTS NHA_CUNG_CAP");
        db.execSQL("DROP TABLE IF EXISTS SAN_PHAM");
        db.execSQL("DROP TABLE IF EXISTS DANH_MUC_SP");
        db.execSQL("DROP TABLE IF EXISTS NHAN_VIEN");
        db.execSQL("DROP TABLE IF EXISTS TAI_KHOAN");
        onCreate(db);
    }
}