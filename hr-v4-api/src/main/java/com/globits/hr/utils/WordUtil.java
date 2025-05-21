package com.globits.hr.utils;

import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;

import java.util.List;

public class WordUtil {
    public static String REPLACE_EMPTY_STRING = ".....";
    // thay thế đoạn văn bản
    public static void replaceParagraph(List<XWPFParagraph> paragraphList, String[] targetText,String[] replacements){
        for (XWPFParagraph paragraph : paragraphList) {
            String paragraphText = paragraph.getText();
            // Duyệt qua từng phần tử trong targetText
            for (int j = 0; j < targetText.length; j++) {
                if (paragraphText.equals(targetText[j])) { // Kiểm tra từng đoạn văn bản
                    // Xóa tất cả các run hiện tại trong đoạn (tránh lỗi UnsupportedOperationException)
                    for (int i = paragraph.getRuns().size() - 1; i >= 0; i--) {
                        paragraph.removeRun(i);
                    }
                    // Tạo nội dung mới
                    XWPFRun run = paragraph.createRun();
                    run.setText(replacements[j]); // Thay thế bằng nội dung mới
                }
            }
        }
    }
    // Phương thức set style cho cell
    public static void setCellTextAndStyle(XWPFTableCell cell, String text) {
        // Thêm đoạn văn vào cell
        XWPFParagraph paragraph = cell.addParagraph();
        XWPFRun run = paragraph.createRun();
        // Đặt nội dung cho ô
        run.setText(text);
        // Set style cho văn bản trong ô
        run.setFontFamily("Times New Roman");  // Đặt font chữ là Times New Roman
        run.setFontSize(13);         // Đặt cỡ chữ
        run.setColor("000000");      // Đặt màu chữ (black)
        run.setBold(true);           // Đặt chữ in đậm
        run.setItalic(false);        // Đặt chữ in nghiêng (false nếu không cần)
        // Căn chỉnh văn bản trong ô (có thể là trái, giữa hoặc phải)
        paragraph.setAlignment(ParagraphAlignment.CENTER); // Căn giữa
    }
}
