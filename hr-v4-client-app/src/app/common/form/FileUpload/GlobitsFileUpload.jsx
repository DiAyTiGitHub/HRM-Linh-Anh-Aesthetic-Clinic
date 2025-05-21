import React, { useState } from "react";
import PropTypes from "prop-types";
import { Button, Input, Typography } from "@material-ui/core";

const GlobitsFileUpload = ({ onFileChange, multiple = false, label = "Upload File" ,name}) => {
  const [selectedFiles, setSelectedFiles] = useState([]);

  // Hàm xử lý khi chọn file
  const handleChange = (event) => {
    const files = Array.from(event.target.files); // Chuyển FileList thành mảng

    setSelectedFiles(files); // Lưu danh sách file đã chọn vào state

    if (onFileChange) {
      onFileChange(files); // Gọi hàm onFileChange truyền vào với danh sách file
    }
  };

  return (
    <div>
      <Typography variant="h6">{label}</Typography>
      {/* Input để chọn file */}
      <Input
        type="file"
        name={name}
        inputProps={{ multiple: multiple }} // Cho phép chọn nhiều file nếu multiple = true
        onChange={handleChange}
      />

      {/* Hiển thị tên các file đã chọn */}
      {selectedFiles.length > 0 && (
        <div style={{ marginTop: "10px" }}>
          <Typography variant="body1">Selected Files:</Typography>
          <ul>
            {selectedFiles.map((file, index) => (
              <li key={index}>{file.name}</li>
            ))}
          </ul>
        </div>
      )}
    </div>
  );
};

// Định nghĩa kiểu dữ liệu cho prop
GlobitsFileUpload.propTypes = {
  onFileChange: PropTypes.func.isRequired, // Hàm xử lý file được chọn
  multiple: PropTypes.bool, // Chọn upload nhiều file hay không
  label: PropTypes.string, // Nhãn hiển thị
  name: PropTypes.string, // Nhãn hiển thị
};

export default GlobitsFileUpload;
