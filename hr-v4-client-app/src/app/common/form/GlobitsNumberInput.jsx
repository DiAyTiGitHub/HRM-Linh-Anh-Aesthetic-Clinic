import React, { useEffect, useState } from "react";
import { TextField, useMediaQuery } from "@material-ui/core";
import { FastField, getIn } from "formik";
import { useTheme } from "@material-ui/core/styles";
import clsx from "clsx";
import { containsOnlyNumbers, RequiredLabel } from "app/LocalFunction";

const GlobitsNumberInput = (props) => {
    return (
        <FastField {...props} name={props.name} shouldUpdate={shouldComponentUpdate}>
            {({ field, meta ,form}) => {
                return <MyNumberInput {...props} field={field} meta={meta} formik={form} />;
            }}
        </FastField>
    );
};

const MyNumberInput = ({
    name,
    variant = "outlined",
    size,
    type,
    regexInput = /^\d+$/, //default
    field,
    meta,
    notDelay,
    debounceTime = 100,
    decimal = false,
    inputProps,
    maxValue,
    reportComponent = false,
    fullWidth = true,
    label,
    requiredLabel = false,
    oldStyle = false,
    validate,
    required = false,
    readOnly = false, // Thêm prop readOnly với giá trị mặc định là false
    formik,
    ...otherProps
}) => {
    const theme = useTheme();
    const isMobile = useMediaQuery(theme.breakpoints.down("sm"));

    const [value, setValue] = useState(field.value);
    const [t, setT] = useState(undefined);

    const onChange = (e) => {
        e.persist();

        const eventValue = e.target.value;

        if (decimal) {
            let matches = eventValue?.match(/\./g);
            let value = eventValue?.replace(".", "");
            if (matches?.length > 1) {
                return;
            } else if (value?.length > 0 && !containsOnlyNumbers(value)) {
                return;
            }
        } else {
            if (eventValue !== "" && !containsOnlyNumbers(eventValue)) {
                setValue(field.value ?? "");
                e.preventDefault();
                return;
            }
        }

        if (maxValue) {
            if (eventValue > maxValue) {
                return;
            }
        }
        setValue(eventValue);
        if (t) clearTimeout(t);
        // @ts-ignore
        if (notDelay) {
            if (otherProps.onChange) {
                otherProps.onChange(e);
            } else {
                field.onChange(e);
            }
        } else {
            setT(
                setTimeout(() => {
                    if (otherProps.onChange) {
                        otherProps.onChange(e);
                    } else {
                        field.onChange(e);
                    }
                }, debounceTime)
            );
        }
    };

    useEffect(() => {
        setValue(field.value);
    }, [field.value]);

    useEffect(() => {
        setValue(otherProps.value);
    }, [otherProps.value]);

    // Effect để đồng bộ giá trị
    useEffect(() => {
        const newValue = field.value ?? "";
        if (value !== newValue) {
            setValue(newValue);
        }
    }, [field.value]);

    // Effect để xử lý reset
    useEffect(() => {
        if (formik && !formik.dirty) {
            setValue(getIn(formik.initialValues, name) ?? "")
        }
    }, [formik?.dirty, formik?.initialValues]);

    const handleKeyDown = (evt) => {
        var theEvent = evt || window.event;
        //Arrow key
        if (
            theEvent.key === "ArrowUp" ||
            theEvent.key === "ArrowRight" ||
            theEvent.key === "ArrowDown" ||
            theEvent.key === "ArrowLeft"
        ) {
            theEvent.returnValue = true;
            return;
        }
        //backspace, tab, enter
        if (theEvent.key === "Backspace" || theEvent.key === "Tab" || theEvent.key === "Enter") {
            theEvent.returnValue = true;
            return;
        }
        // Ctrl+A or Cmd+A pressed?
        if ((theEvent.ctrlKey || theEvent.metaKey) && (theEvent.key === "a" || theEvent.key === "A")) {
            theEvent.returnValue = true;
            return;
        }

        // Ctrl+C or Cmd+C pressed?
        if ((theEvent.ctrlKey || theEvent.metaKey) && (theEvent.key === "c" || theEvent.key === "C")) {
            theEvent.returnValue = true;
            return;
        }

        // Ctrl+V or Cmd+V pressed?
        if ((theEvent.ctrlKey || theEvent.metaKey) && (theEvent.key === "v" || theEvent.key === "V")) {
            theEvent.returnValue = true;
            return;
        }

        // Ctrl+X or Cmd+X pressed?
        if ((theEvent.ctrlKey || theEvent.metaKey) && (theEvent.key === "x" || theEvent.key === "X")) {
            theEvent.returnValue = true;
            return;
        }

        // Ctrl+Z or Cmd+Z pressed?
        if ((theEvent.ctrlKey || theEvent.metaKey) && (theEvent.key === "z" || theEvent.key === "Z")) {
            theEvent.returnValue = true;
            return;
        }

        //cho phép thập phân
        if (decimal && theEvent.key === ".") {
            theEvent.returnValue = true;
            return;
        }

        if (theEvent.key === "Delete") {
            theEvent.returnValue = true;
            return;
        }

        if (!regexInput.test(theEvent.key)) {
            theEvent.returnValue = false;
            if (theEvent.preventDefault) theEvent.preventDefault();
        }
    };

    const displayLabel = label ? (
        <>
            {label}
            {requiredLabel && <RequiredLabel />}
        </>
    ) : (
        ""
    );

    const configTextfield = {
        ...field,
        ...otherProps,
        value: value,
        id: name,
        onKeyDown: readOnly ? undefined : handleKeyDown, // Ngăn nhập dữ liệu nếu readOnly
        onChange: readOnly ? undefined : onChange, // Ngăn thay đổi giá trị nếu readOnly

        fullWidth: fullWidth,
        variant: variant,
        size: size ? size : "small",
        type: isMobile ? "number" : type ? type : "",
        className: clsx(
            oldStyle ? "" : "input-container",
            readOnly && "read-only" // Thêm class "read-only" nếu readOnly = true
        ),
        inputProps: {
            ...inputProps,
            inputmode: "numeric",
            readOnly: readOnly, // Thêm thuộc tính readOnly vào input
            className: clsx(reportComponent && "text-align-right", inputProps?.className, readOnly && "read-only"),
        },
    };

    if (meta && meta.touched && meta.error) {
        configTextfield.error = true;
        configTextfield.helperText = meta.error;
    }

    return (
        <>
            {label && (
                <label
                    htmlFor={name}
                    className={`${oldStyle ? "old-label" : "label-container"}`}
                    id={`label-for-${name}`} // Thêm ID để có thể focus vào label khi có lỗi
                >
                    {label}
                    {required && <span style={{ color: "red" }}> * </span>}
                </label>
            )}
            <TextField {...configTextfield} />
        </>
    );
};

const shouldComponentUpdate = (nextProps, currentProps) => {
    return (
        nextProps.name !== currentProps.name ||
        nextProps.value !== currentProps.value ||
        nextProps.onChange !== currentProps.onChange ||
        nextProps.label !== currentProps.label ||
        nextProps.required !== currentProps.required ||
        nextProps.disabled !== currentProps.disabled ||
        nextProps.readOnly !== currentProps.readOnly ||
        nextProps.className !== currentProps.className ||
        nextProps.formik.isSubmitting !== currentProps.formik.isSubmitting ||
        Object.keys(nextProps).length !== Object.keys(currentProps).length ||
        getIn(nextProps.formik.values, currentProps.name) !== getIn(currentProps.formik.values, currentProps.name) ||
        getIn(nextProps.formik.errors, currentProps.name) !== getIn(currentProps.formik.errors, currentProps.name) ||
        getIn(nextProps.formik.touched, currentProps.name) !== getIn(currentProps.formik.touched, currentProps.name) ||
        getIn(nextProps.formik.initialValues, currentProps.name) !==
            getIn(currentProps.formik.initialValues, currentProps.name)
    );
};

export default React.memo(GlobitsNumberInput);
