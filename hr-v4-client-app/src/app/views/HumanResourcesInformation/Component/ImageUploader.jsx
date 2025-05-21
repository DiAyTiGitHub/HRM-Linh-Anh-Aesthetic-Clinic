import React, { useState } from "react";
import { Button, Box, Typography } from "@material-ui/core";
import { Grid } from "@material-ui/core";

const ImageUploader = ({ onUpload, acceptType = "image/*", buttonText = "Upload Image", showPreview = true }) => {
    const [file, setFile] = useState(null);
    const [base64, setBase64] = useState("");

    // Hàm chuyển đổi file thành base64
    const handleFileChange = (event) => {
        const file = event.target.files[0];
        if (file) {
            const reader = new FileReader();
            reader.onloadend = () => {
                setBase64(reader.result);
                onUpload(reader.result); // Callback để trả base64 lên parent component
            };
            reader.readAsDataURL(file);
            setFile(file);
        }
    };

    return (
        <Grid container spacing={2}>
            {/* Cột 1 - Button Upload */}
            <Grid item xs={12} sm={showPreview ? 6 : 12}>
                <input
                    type='file'
                    accept={acceptType}
                    onChange={handleFileChange}
                    style={{ display: "none" }}
                    id='file-upload'
                />
                <label htmlFor='file-upload'>
                    <Button
                        className='btn bgc-lighter-dark-blue d-inline-flex text-white'
                        variant='contained'
                        component='span'
                        fullWidth>
                        {buttonText}
                    </Button>
                </label>
            </Grid>

            {/* Cột 2 - Hiển thị ảnh nếu có */}
            {showPreview && (
                <Grid item xs={12} sm={6} style={{ display: "flex", justifyContent: "center", alignItems: "center" }}>
                    <Box
                        border={1} // Thêm border quanh ảnh
                        borderColor={file ? "transparent" : "#ccc"} // Nếu không có ảnh, hiển thị border màu xám
                        borderRadius={4} // Góc bo tròn của border
                        padding={2} // Khoảng cách giữa border và ảnh
                        display='flex'
                        justifyContent='center'
                        alignItems='center'
                        width='100%'
                        height='auto'>
                        {file ? (
                            <img
                                src={base64}
                                alt='preview'
                                style={{ width: "100%", maxWidth: "200px", height: "auto" }}
                            />
                        ) : (
                            <Typography variant='body2' color='textSecondary'>
                                No image selected
                            </Typography>
                        )}
                    </Box>
                </Grid>
            )}
        </Grid>
    );
};

export default ImageUploader;
