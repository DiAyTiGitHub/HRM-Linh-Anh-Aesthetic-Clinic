import React, { memo, useEffect, useState } from "react";
import { TextField, MenuItem, FormControl } from "@material-ui/core";
import { FastField, getIn, useField, useFormikContext } from "formik";

const GlobitsSelectInputV2 = (props, ref) => {
    return (
        <FastField {...props} name={props.name} shouldUpdate={shouldComponentUpdate}>
            {({ field, meta }) => {
                return <MySelectInput {...props} field={field} meta={meta} ref={ref} />;
            }}
        </FastField>
    );
};

const shouldComponentUpdate = (nextProps, currentProps) => {
    return (
        nextProps.name !== currentProps.name ||
        nextProps.value !== currentProps.value ||
        nextProps.onChange !== currentProps.onChange ||
        nextProps.label !== currentProps.label ||
        nextProps.required !== currentProps.required ||
        nextProps.requiredLabel !== currentProps.requiredLabel ||
        nextProps.disabled !== currentProps.disabled ||
        nextProps.readOnly !== currentProps.readOnly ||
        nextProps.displayvalue !== currentProps.displayvalue ||
        nextProps.options !== currentProps.options ||
        nextProps.keyValue !== currentProps.keyValue ||
        nextProps.hideNullOption !== currentProps.hideNullOption ||
        nextProps.formik.isSubmitting !== currentProps.formik.isSubmitting ||
        Object.keys(nextProps).length !== Object.keys(currentProps).length ||
        getIn(nextProps.formik.values, currentProps.name) !== getIn(currentProps.formik.values, currentProps.name) ||
        getIn(nextProps.formik.errors, currentProps.name) !== getIn(currentProps.formik.errors, currentProps.name) ||
        getIn(nextProps.formik.touched, currentProps.name) !== getIn(currentProps.formik.touched, currentProps.name)
    );
};

const MySelectInput = ({
    name,
    keyValue = "value",
    displayvalue,
    options,
    size,
    variant,
    label,
    hideNullOption,
    required = false,
    oldStyle = false,
    readOnly = false,
    getOptionDisabled,
    handleChange: externalHandleChange,
    value: externalValue,
    field, // from FastField
    meta, // from FastField
    ...otherProps
}) => {
    const { setFieldValue } = useFormikContext();
    const [internalValue, setInternalValue] = useState(externalValue || field?.value || "");

    // Sync with Formik and external value changes
    useEffect(() => {
        if (externalValue !== undefined) {
            setInternalValue(externalValue);
        } else if (field?.value !== undefined) {
            setInternalValue(field.value);
        }
    }, [externalValue, field?.value]);

    const handleChange = (event) => {
        if (readOnly) return;

        const newValue = event.target.value;
        setInternalValue(newValue);

        // Update Formik
        if (name && setFieldValue) {
            setFieldValue(name, newValue);
        }

        // Call external handler if provided
        if (externalHandleChange) {
            externalHandleChange(event, newValue);
        }
    };

    const configSelectInput = {
        ...field,
        ...otherProps,
        select: true,
        variant: variant || "outlined",
        size: size || "small",
        fullWidth: true,
        value: internalValue,
        onChange: handleChange,
        InputLabelProps: {
            htmlFor: name,
            shrink: true,
        },
        disabled: readOnly || otherProps.disabled,
        className: `${oldStyle ? "" : "input-container"} ${readOnly ? "read-only" : ""}`,
    };

    if (meta && meta.touched && meta.error) {
        configSelectInput.error = true;
        configSelectInput.helperText = meta.error;
    }

    return (
        <>
            {label && (
                <label className={`${oldStyle ? "old-label" : "label-container"}`} htmlFor={name}>
                    {label} {required && <span style={{ color: "red" }}> * </span>}
                </label>
            )}

            <FormControl fullWidth>
                <TextField {...configSelectInput}>
                    {!hideNullOption && (
                        <MenuItem value=''>
                            <em>---</em>
                        </MenuItem>
                    )}
                    {options?.map((item, pos) => {
                        const isDisabled = getOptionDisabled ? getOptionDisabled(item) : false;
                        const itemValue = item[keyValue];
                        const itemLabel = item[displayvalue || "name"];

                        return (
                            <MenuItem key={pos} value={itemValue} disabled={isDisabled}>
                                {itemLabel}
                            </MenuItem>
                        );
                    })}
                </TextField>
            </FormControl>
        </>
    );
};

export default memo(GlobitsSelectInputV2);
