import React, { memo, useEffect, useMemo, useRef, useState } from "react";
import { FastField, getIn } from "formik";
import { DropzoneArea } from "material-ui-dropzone";

function GlobitsDropzoneArea(props) {
    const { label } = props;

    return (
        <FastField
            {...props}
            name={props?.name}
            shouldUpdate={shouldComponentUpdate}
        >
            {({ field, form }) => (
                <>
                    {label && (
                        <label htmlFor={props?.name} style={{ fontSize: "1rem" }}>
                            {label} {props?.validate ? <span style={{ color: "red" }}> * </span> : <></>}
                        </label>
                    )}
                    <MyComponent
                        {...props}
                        field={field}
                        setFieldValue={form.setFieldValue}
                    />
                </>
            )}
        </FastField>
    );
}

function MyComponent({
    disabled,
    field,
    name,
    setFieldValue,
    placeholder,
    readOnly,
}) {
    const dropZoneRef = useRef();
    const [data, setData] = useState(field.value);

    function handleChange(value) {
        setData(value);
        setFieldValue(name, value);
    }

    useEffect(() => {
        setData(field?.value ?? null);
    }, [field?.value]);

    return (
        <DropzoneArea
            onChange={handleChange}
            ref={dropZoneRef}
        />
    );
}

export default memo(GlobitsDropzoneArea);

const shouldComponentUpdate = (nextProps, currentProps) =>
    nextProps?.value !== currentProps?.value ||
    nextProps?.disabled !== currentProps?.disabled ||
    Object.keys(nextProps).length !== Object.keys(currentProps).length ||
    getIn(nextProps.formik.values, currentProps.name) !==
    getIn(currentProps.formik.values, currentProps.name) ||
    getIn(nextProps.formik.errors, currentProps.name) !==
    getIn(currentProps.formik.errors, currentProps.name) ||
    getIn(nextProps.formik.touched, currentProps.name) !==
    getIn(currentProps.formik.touched, currentProps.name);
