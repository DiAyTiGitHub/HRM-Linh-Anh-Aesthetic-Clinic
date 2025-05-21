import React from "react";
import { Checkbox, FormControlLabel, FormGroup, makeStyles } from "@material-ui/core";
import { useField } from "formik";

const useStyles = makeStyles((theme) => ({
    checkBoxLabel: {
        margin: "0px !important",
        "& .MuiIconButton-root": {
            padding: "8px !important",
        },
    },
    alignCenter: {
        display: "flex",
        alignItems: "flex-start",
        justifyContent: "center",
        height: "100%",
    },
}));

const GlobitsCheckBox = ({
    name,
    label,
    style,
    alignCenter = true,
    readOnly = false, // Thêm prop readOnly với giá trị mặc định là false
    handleChange: externalHandleChange,
    ...otherProps
}) => {
    const classes = useStyles();
    const [field, meta] = useField(name);
    // Tạo wrapper cho handleChange từ bên ngoài
    const handleExternalChange = (evt) => {
        if (readOnly) return; // Không thực hiện khi readOnly
        if (externalHandleChange) {
            externalHandleChange(evt, evt.target.checked); // Truyền thêm giá trị checked
        }
    };

    // Default handleChange
    const handleInternalChange = (evt) => {
        if (readOnly) return; // Ngăn thay đổi giá trị nếu readOnly
        field.onChange(evt); // Gọi hàm onChange mặc định của Formik
    };
    // const handleChange = (event) => {
    //     if (readOnly) return; // Ngăn thay đổi giá trị nếu readOnly
    //     field.onChange(event); // Gọi hàm onChange mặc định của Formik
    // };

    const configCheckBox = {
        ...field,
        ...otherProps,
        checked: field.value || false, // Đảm bảo giá trị mặc định là false nếu không có giá trị
        onChange: externalHandleChange ? handleExternalChange : handleInternalChange,
        className: readOnly ? "read-only" : "", // Thêm class read-only khi readOnly là true
    };

    if (meta && meta.touched && meta.error) {
        configCheckBox.error = true;
        configCheckBox.helperText = meta.error;
    }

    return (
        <FormGroup className={alignCenter ? classes.alignCenter : ""}>
            <FormControlLabel
                className={`${classes.checkBoxLabel} ${readOnly ? "read-only" : ""}`} // Thêm class read-only cho FormControlLabel
                style={style}
                control={<Checkbox {...configCheckBox} />}
                label={label}
            />
        </FormGroup>
    );
};

export default GlobitsCheckBox;
