package com.globits.timesheet.dto.api;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TimeSheetRecordDto {

	@JsonProperty("MaChamCong")
    private int maChamCong;
    
    @JsonProperty("MaNhanVien")
    private String maNhanVien;
    
    @JsonProperty("TenNhanVien")
    private String tenNhanVien;
    
    @JsonProperty("NgayCham")
    private String ngayCham;
    
    @JsonProperty("GioCham")
    private String gioCham;
    
    @JsonProperty("TenPhongBan")
    private String tenPhongBan;

    private Date ngayGioChamCongDate;
    
    // Getters và Setters
    public int getMaChamCong() {
        return maChamCong;
    }

    public void setMaChamCong(int maChamCong) {
        this.maChamCong = maChamCong;
    }

    public String getMaNhanVien() {
        return maNhanVien;
    }

    public void setMaNhanVien(String maNhanVien) {
        this.maNhanVien = maNhanVien;
    }

    public String getTenNhanVien() {
        return tenNhanVien;
    }

    public void setTenNhanVien(String tenNhanVien) {
        this.tenNhanVien = tenNhanVien;
    }

    public String getNgayCham() {
        return ngayCham;
    }

    public void setNgayCham(String ngayCham) {
        this.ngayCham = ngayCham;
    }

    public String getGioCham() {
        return gioCham;
    }

    public void setGioCham(String gioCham) {
        this.gioCham = gioCham;
    }

    public String getTenPhongBan() {
        return tenPhongBan;
    }

    public void setTenPhongBan(String tenPhongBan) {
        this.tenPhongBan = tenPhongBan;
    }


    public Date getNgayGioChamCongDate() throws ParseException {
        if (ngayGioChamCongDate == null) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            ngayGioChamCongDate = sdf.parse(this.ngayCham + " " + this.gioCham);
        }
        return ngayGioChamCongDate;
    }

 // ➡️ Hàm convert ngày + giờ thành Date
    public  Date getNgayGioChamCongAsDate() {
        return parseNgayGioChamCong(this.ngayCham, this.gioCham);
    }

    // ➡️ Hàm tiện ích để parse ngayCham + gioCham thành Date
    private static Date parseNgayGioChamCong(String ngayCham, String gioCham)  {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        try {
			return sdf.parse(ngayCham + " " + gioCham);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return null;
    }
}
