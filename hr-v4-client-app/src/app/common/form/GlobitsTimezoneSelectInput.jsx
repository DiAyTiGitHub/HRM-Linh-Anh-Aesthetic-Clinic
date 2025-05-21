import React from "react";
import { TextField, MenuItem } from "@material-ui/core";
import { useField, useFormikContext } from "formik";

const GlobitsTimezoneSelectInput = ({
    name,
    keyValue = "value",
    displayvalue,
    size,
    variant,
    label,
    hideNullOption,
    readOnly = false, // Thêm prop readOnly
    ...otherProps
}) => {
    const { setFieldValue } = useFormikContext();
    const [field, meta] = useField(name);

    const handleChange = (evt) => {
        if (readOnly) return; // Nếu readOnly, không thay đổi giá trị
        const { value } = evt.target;
        setFieldValue(name, value);
    };

    const configSelectInput = {
        ...field,
        ...otherProps,
        select: true,
        variant: variant ? variant : "outlined",
        size: size ? size : "small",
        fullWidth: true,
        onChange: otherProps?.handleChange ? otherProps.handleChange : handleChange,
        InputLabelProps: {
            htmlFor: name,
            shrink: true,
        },
        className: `${readOnly ? "read-only" : ""}`, // Thêm class read-only khi readOnly là true
    };

    if (meta && meta.touched && meta.error) {
        configSelectInput.error = true;
        configSelectInput.helperText = meta.error;
    }

    const options = []; // Options có thể thay đổi tùy theo dữ liệu của bạn

    return (
        <>
            <label htmlFor={name} style={{ fontSize: "1rem" }}>
                {label}
            </label>
            <TextField {...configSelectInput}>
                {!hideNullOption && (
                    <MenuItem value={null}>
                        <em>---</em>
                    </MenuItem>
                )}
                {options?.map((item, pos) => {
                    return (
                        <MenuItem key={pos} value={item[keyValue]}>
                            {item[displayvalue ? displayvalue : "label"]}
                        </MenuItem>
                    );
                })}
            </TextField>
        </>
    );
};

export default GlobitsTimezoneSelectInput;
