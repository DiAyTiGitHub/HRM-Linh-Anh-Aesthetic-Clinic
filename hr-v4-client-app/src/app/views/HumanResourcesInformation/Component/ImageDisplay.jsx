import React from "react";
import { Box, Grid, Typography } from "@material-ui/core";

const ImageDisplay = ({ base64Image, width = "200px", height = "auto", style = {} }) => {
    return (
        <Grid item xs={12} style={{ display: "flex", justifyContent: "center", alignItems: "center" }}>
            <Box
                border={1} // Thêm border quanh ảnh
                borderColor={base64Image ? "transparent" : "#ccc"} // Nếu không có ảnh, hiển thị border màu xám
                display='flex'
                justifyContent='center'
                alignItems='center'
                width='100%'
                minHeight={"150px"}>
                {base64Image ? (
                    <img src={base64Image} alt='preview' style={{ width: "100%", height: "auto" }} />
                ) : (
                    <Typography variant='body2' color='textSecondary'>
                        No image selected
                    </Typography>
                )}
            </Box>
        </Grid>
    );
};

export default ImageDisplay;
