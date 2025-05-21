/* eslint-disable react-hooks/exhaustive-deps */
import React, { useState, useEffect, memo } from "react";
import { TextField } from "@material-ui/core";
import { FastField, getIn } from "formik";
import NumberFormat from 'react-number-format';
import PropTypes from 'prop-types';

const GlobitsVNDCurrencyInput = (props) => (
    <FastField {...props} name={props.name} shouldUpdate={shouldComponentUpdate}>
        {({ field, meta, form }) => <Component {...props} field={field} meta={meta} setFieldValue={form.setFieldValue} />}
    </FastField>
);

const NumericFormatCustom = React.forwardRef(({ onChange, ...other }, ref) => (
    <NumberFormat
        {...other}
        getInputRef={ref}
        onValueChange={(values) => {
            onChange({
                target: {
                    name: other.name,
                    value: values.value,
                },
            });
        }}
        thousandSeparator
        valueIsNumericString
    />
));

NumericFormatCustom.propTypes = {
    name: PropTypes.string.isRequired,
    onChange: PropTypes.func.isRequired,
};

const Component = ({
    name,
    label,
    type = "text",
    debounceTime = 400, //default 0ms
    notDelay,
    field,
    meta,
    disabled,
    placeholder,
    minRowArea,
    required,
    className = '',
    onChange,
    setFieldValue,
    oldStyle,
    readOnly,
    variant,
    suffix = "",
    textAlignRight, // Add this prop
}) => {
    const [value, setValue] = useState(field.value);
    const [t, setT] = useState(undefined);

    useEffect(() => {
        if (field.value !== value) setValue(field.value ?? "");
    }, [field.value]);

    const handleChange = (e) => {
        let value = e.target.value;

        setValue(value);
        if (!notDelay) {
            if (t) {
                clearTimeout(t);
            }
            if (onChange) {
                setT(setTimeout(() => onChange(e), debounceTime));
            } else {
                setT(setTimeout(() => setFieldValue(name, e.target.value ? e.target.value : null), debounceTime));
            }
        } else {
            if (onChange) {
                onChange(e);
            } else {
                setFieldValue(name, e.target.value ? e.target.value : null);
            }
        }
    };

    return (
        <>
            {label && (
                <label htmlFor={name} className={`${oldStyle ? 'old-label' : 'label-container'}`}>
                    {label} {required ? <span style={{ color: "red" }}> * </span> : <></>}
                </label>
            )}

            <TextField
                variant={variant ? variant : "outlined"}
                id={name}
                name={name}
                value={value}
                fullWidth
                onChange={readOnly ? null : handleChange}  // Ngừng xử lý onChange khi readOnly
                placeholder={placeholder}
                disabled={disabled || readOnly}  // Vô hiệu hóa khi readOnly
                type={type}
                error={Boolean(meta && meta.touched && meta.error)}
                helperText={meta && meta.touched && meta.error ? meta.error : ""}
                InputProps={{
                    readOnly: readOnly,
                    inputComponent: NumericFormatCustom,
                    style: textAlignRight ? { textAlign: "right" } : {},
                    endAdornment: suffix && (
                        <span style={{ marginRight: '8px', color: '#757575' }}>{suffix}</span>
                    ),
                }}
                InputLabelProps={{
                    htmlFor: name,
                    shrink: true,
                }}

                minRows={minRowArea}
                className={`${oldStyle ? '' : 'input-container'} ${readOnly ? 'read-only' : ''}`}  // Thêm class read-only khi readOnly là true
            />
        </>
    );
};

const shouldComponentUpdate = (nextProps, currentProps) => (
    nextProps?.readOnly !== currentProps?.readOnly ||
    nextProps?.value !== currentProps?.value ||
    nextProps?.onChange !== currentProps?.onChange ||
    nextProps?.disabled !== currentProps?.disabled ||
    nextProps?.name !== currentProps?.name ||
    Object.assign(nextProps).length !== Object.assign(currentProps).length ||
    getIn(nextProps.formik.values, currentProps.name) !== getIn(currentProps.formik.values, currentProps.name) ||
    getIn(nextProps.formik.errors, currentProps.name) !== getIn(currentProps.formik.errors, currentProps.name) ||
    getIn(nextProps.formik.touched, currentProps.name) !== getIn(currentProps.formik.touched, currentProps.name)
);

export default memo(GlobitsVNDCurrencyInput);
