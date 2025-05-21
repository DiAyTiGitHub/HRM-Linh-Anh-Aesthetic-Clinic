import { Grid } from "@material-ui/core";
import { useFormikContext } from "formik";
import { useEffect, useState } from "react";
import ImageDisplay from "./ImageDisplay";
import ImageUploader from "./ImageUploader";

const SignatureUpload = () => {
    const [imageBase64, setImageBase64] = useState(null);
    const { values, setFieldValue } = useFormikContext();
    // Hàm nhận base64 từ component ImageUploader
    const handleImageUpload = (base64) => {
        setImageBase64(base64);
        setFieldValue("signature", base64);
        console.log(base64);
    };

    useEffect(() => {
        // Set imageBase64 from the form's signature value
        setImageBase64(values?.signature);
    }, [values?.signature]);  // Make sure to listen for changes to signature

    return (
        <Grid container spacing={2}>
            <Grid item xs={6}>
                <ImageUploader
                    onUpload={handleImageUpload}
                    buttonText='Tải chữ ký'
                    acceptType='image/*'
                    showPreview={false}
                />
            </Grid>
            <Grid item xs={6}>
                <ImageDisplay base64Image={imageBase64} />
            </Grid>
        </Grid>
    );
};

export default SignatureUpload;
