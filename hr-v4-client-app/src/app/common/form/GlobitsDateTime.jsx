import React, { useEffect, useState } from "react";
import { FastField, getIn } from "formik";
import i18n from "i18n";
import { TextField } from "@material-ui/core";

const GlobitsDateTime = (props) => {
  return (
    <FastField
      {...props}
      name={props.name}
      shouldUpdate={shouldComponentUpdate}
    >
      {({ field, meta, form }) => {
        return (
          <MyDateTimePicker
            {...props}
            field={field}
            meta={meta}
            setFieldValue={form.setFieldValue}
          />
        );
      }}
    </FastField>
  );
};

const MyDateTimePicker = ({
  disablePast,
  disableFuture,
  name,
  size,
  format,
  inputVariant,
  defaultValue,
  isDateTimePicker,
  notDelay,
  field,
  meta,
  setFieldValue,
  readOnly = false, // Thêm prop readOnly với giá trị mặc định là false
  ...otherProps
}) => {
  const onChange = ({ target }) => {
    if (readOnly) return; // Ngăn thay đổi giá trị nếu readOnly
    const { value } = target;
    setValue(value);
    if (!notDelay) {
      if (t) clearTimeout(t);
      setT(
        setTimeout(() => {
          if (otherProps.onChange) {
            otherProps.onChange(value);
          } else {
            setFieldValue(name, value);
          }
        }, 400)
      );
    } else {
      if (otherProps.onChange) {
        otherProps.onChange(value);
      } else {
        setFieldValue(name, value);
      }
    }
  };

  const [value, setValue] = useState(field.value);
  const [t, setT] = useState(undefined);

  useEffect(() => {
    setValue(field.value);
  }, [field.value]);

  useEffect(() => {
    if (otherProps.value) {
      setValue(otherProps.value);
    }
  }, [otherProps.value]);

  const minDateMessageDefault = i18n.t("validation.invalidDate");
  const maxDateMessageDefault = i18n.t("validation.invalidDate");
  const okLabelDefault = "CHỌN";
  const cancelLabelDefault = "HUỶ";

  const configDateTimePicker = {
    ...field,
    ...otherProps,
    disablePast: disablePast ? disablePast : false,
    disableFuture: disableFuture ? disableFuture : false,
    inputVariant: inputVariant ? inputVariant : "outlined",
    size: size ? size : "small",
    fullWidth: true,
    value: value ? new Date(value) : null,
    id: name,
    label: false,
    type: "datetime-local",
    onChange: onChange,
    InputLabelProps: {
      htmlFor: name,
      shrink: true,
    },
    invalidDateMessage: i18n.t("validation.invalidDate"),
    minDateMessage: otherProps?.minDateMessage ? otherProps.minDateMessage : minDateMessageDefault,
    maxDateMessage: otherProps?.maxDateMessage ? otherProps.maxDateMessage : maxDateMessageDefault,
    okLabel: otherProps?.okLabel ? otherProps.okLabel : okLabelDefault,
    cancelLabel: otherProps?.cancelLabel ? otherProps.cancelLabel : cancelLabelDefault,
    className: readOnly ? "read-only" : "", // Thêm class read-only
    InputProps: {
      readOnly: readOnly, // Đặt TextField thành readOnly
    },
    inputProps: {
      readOnly: readOnly, // Đảm bảo input cũng là readOnly
    },
  };

  if (meta && meta.touched && meta.error) {
    configDateTimePicker.error = true;
    configDateTimePicker.helperText = meta.error;
  }

  return (
    <div>
      <label htmlFor={name} style={{ fontSize: "1rem" }}>
        {otherProps.label}{" "}
        {otherProps.required ? <span style={{ color: "red" }}> * </span> : <></>}
      </label>
      <TextField {...configDateTimePicker} />
    </div>
  );
};

const shouldComponentUpdate = (nextProps, currentProps) => {
  return (
    nextProps.name !== currentProps.name ||
    nextProps.value !== currentProps.value ||
    nextProps.onChange !== currentProps.onChange ||
    nextProps.disablePast !== currentProps.disablePast ||
    nextProps.disableFuture !== currentProps.disableFuture ||
    nextProps.label !== currentProps.label ||
    nextProps.required !== currentProps.required ||
    nextProps.disabled !== currentProps.disabled ||
    nextProps.readOnly !== currentProps.readOnly || // Đã có sẵn trong shouldComponentUpdate
    nextProps.formik.isSubmitting !== currentProps.formik.isSubmitting ||
    Object.keys(nextProps).length !== Object.keys(currentProps).length ||
    getIn(nextProps.formik.values, currentProps.name) !==
      getIn(currentProps.formik.values, currentProps.name) ||
    getIn(nextProps.formik.errors, currentProps.name) !==
      getIn(currentProps.formik.errors, currentProps.name) ||
    getIn(nextProps.formik.touched, currentProps.name) !==
      getIn(currentProps.formik.touched, currentProps.name)
  );
};

export default React.memo(GlobitsDateTime);