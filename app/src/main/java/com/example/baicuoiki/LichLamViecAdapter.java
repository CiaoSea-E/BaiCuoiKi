package com.example.baicuoiki; // Đổi dòng này thành tên package của bạn

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class LichLamViecAdapter extends ArrayAdapter<LichLamViec> {

    // Các biến toàn cục để lưu trữ thông tin truyền vào
    private Context context;
    private int resource;
    private List<LichLamViec> objects;

    /**
     * Constructor khởi tạo Adapter
     * @param context: Context của Activity gọi đến (thường là màn hình chứa ListView)
     * @param resource: ID của file layout dòng (ví dụ: R.layout.item_lichlam)
     * @param objects: Danh sách dữ liệu truyền vào
     */
    public LichLamViecAdapter(@NonNull Context context, int resource, @NonNull List<LichLamViec> objects) {
        super(context, resource, objects);
        this.context = context;
        this.resource = resource;
        this.objects = objects;
    }

    /**
     * Hàm getView: Nơi diễn ra quá trình nạp layout và đổ dữ liệu cho từng dòng của ListView
     */
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        // 1. Kỹ thuật tối ưu hóa cơ bản: Tái sử dụng View
        // Chỉ nạp (inflate) layout mới nếu convertView bị null (khi dòng đó chưa từng được tạo)
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(this.context);
            // Nạp giao diện item_lichlam (biến resource) vào convertView
            convertView = inflater.inflate(this.resource, parent, false);
        }

        // 2. Lấy đối tượng LichLamViec tại vị trí (position) đang vẽ trên ListView
        LichLamViec lich = getItem(position);

        if (lich != null) {
            // 3. Ánh xạ các TextView dựa trên convertView vừa được nạp
            // Đã đổi ID từ tvMaLich thành tvIdLich cho khớp với item_lichlam.xml
            TextView tvIdLich = convertView.findViewById(R.id.tvIdLich);
            TextView tvNgayLam = convertView.findViewById(R.id.tvNgayLamViec);
            TextView tvMaNhanVien = convertView.findViewById(R.id.tvMaNhanVien);
            TextView tvCaLam = convertView.findViewById(R.id.tvCaLam);
            TextView tvNhiemVu = convertView.findViewById(R.id.tvNhiemVu);

            // 4. Sử dụng setText() để đổ dữ liệu từ đối tượng vào giao diện
            if (tvIdLich != null) {
                tvIdLich.setText("ID: " + lich.getId());
            }
            if (tvNgayLam != null) {
                tvNgayLam.setText(lich.getNgayLamViec());
            }

            // Có thể nối chuỗi thêm text phụ họa để hiển thị giống thiết kế Card đã làm
            if (tvMaNhanVien != null) {
                tvMaNhanVien.setText("Nhân viên: " + lich.getMaNhanVien());
            }
            if (tvCaLam != null) {
                tvCaLam.setText("Ca: " + lich.getCaLam());
            }
            if (tvNhiemVu != null) {
                tvNhiemVu.setText("Nhiệm vụ: " + lich.getNhiemVu());
            }
        }

        // 5. Trả về convertView đã mang đầy đủ dữ liệu để hiển thị lên màn hình
        return convertView;
    }
}