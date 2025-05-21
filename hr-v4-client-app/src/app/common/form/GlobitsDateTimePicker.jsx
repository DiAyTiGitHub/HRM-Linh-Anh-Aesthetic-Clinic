import React, { useEffect, useMemo, useState } from "react";
import { FastField, getIn } from "formik";
import {
  MuiPickersUtilsProvider,
  KeyboardDatePicker,
  KeyboardDateTimePicker,
  KeyboardTimePicker,
} from "@material-ui/pickers";
import DateFnsUtils from "@date-io/date-fns";
import viLocale from "date-fns/locale/vi";
import moment from "moment";

const configDefaultForm = {
  size: null,
  variant: undefined,
  fullWidth: true,
  debounceTime: 200,
  notDefaultNumber: true,
  datePicker: {
    notValueMillisecond: false,
  },
};

const GlobitsDateTimePicker = (props) => (
  <FastField
    {...props}
    name={props.name}
    shouldUpdate={shouldComponentUpdate}
  >
    {({ field, meta, form }) => {
      return (
        <Component
          {...props}
          field={field}
          meta={meta}
          setFieldValue={form.setFieldValue}
        />
      );
    }}
  </FastField>
);

const Component = ({
  disabled,
  fullWidth = configDefaultForm.fullWidth,
  label,
  name,
  size = configDefaultForm.size,
  inputVariant,
  className = "",
  debounceTime = configDefaultForm.debounceTime,
  notDelay,
  field,
  meta,
  requiredLabel,
  required,
  onChange,
  readOnly = false, // Thêm prop readOnly với giá trị mặc định là false
  InputProps,
  InputLabelProps,
  disablePast = false,
  disableFuture = false,
  isDateTimePicker,
  isDateTimeSecondsPicker,
  isTimePicker,
  format = isTimePicker
    ? "HH:mm"
    : "dd/MM/yyyy" +
      (isDateTimeSecondsPicker ? " HH:mm:ss" : isDateTimePicker ? " HH:mm" : ""),
  minDate,
  maxDate,
  minDateMessage = "Ngày không hợp lệ",
  maxDateMessage = "Ngày không hợp lệ",
  okLabel = "CHỌN",
  cancelLabel = "HUỶ",
  setFieldValue,
  multiple,
  notValueMillisecond = configDefaultForm.datePicker.notValueMillisecond,
  tabIndex,
  ...otherProps
}) => {
  const [value, setValue] = useState(field.value);
  const [t, setT] = useState(undefined);

  useEffect(() => {
    setValue(field.value ?? null);
  }, [field.value]);

  const handleChange = (value) => {
    if (readOnly) return; // Ngăn thay đổi giá trị nếu readOnly
    setValue(value);
    let newDate = value;
    if (!notValueMillisecond && moment(newDate, "DD/MM/YYYY", true).isValid()) {
      newDate = new Date(newDate).getTime();
    }

    if (!notDelay) {
      if (t) {
        clearTimeout(t);
      }

      setT(
        setTimeout(() => {
          if (onChange) {
            onChange(newDate);
          } else {
            setFieldValue(name, newDate);
          }
        }, debounceTime)
      );
    } else {
      if (onChange) {
        onChange(newDate);
      } else {
        setFieldValue(name, newDate);
      }
    }
  };

  const isError = meta?.touched && meta?.error;

  const configDate = {
    ...otherProps,
    ...field,
    name: name,
    id: name,
    disabled: disabled, // Vô hiệu hóa hoàn toàn nếu readOnly
    fullWidth: fullWidth,
    size: size,
    inputVariant: inputVariant ? inputVariant : "outlined",
    value: value || null,
    onChange: handleChange,
    minDate,
    maxDate,
    format: format,
    error: isError,
    helperText: fullWidth && isError ? meta.error : "",
    InputProps: {
      ...InputProps,
      readOnly: readOnly, // Đặt TextField thành readOnly
      style: readOnly
        ? {
            color: "rgba(0, 0, 0, 0.87)", // Màu chữ tối
            backgroundColor: "rgba(0, 0, 0, 0.02)", // Nền nhạt để biểu thị readOnly
            opacity: 1, // Độ trong suốt đầy đủ
          }
        : undefined,
    },
    inputProps: {
      readOnly: readOnly, // Đảm bảo input cũng là readOnly
      tabIndex: tabIndex,
      style: readOnly
        ? {
            color: "rgba(0, 0, 0, 0.87)", // Màu chữ tối
            cursor: "not-allowed", // Con trỏ biểu thị không thể tương tác
            opacity: 1, // Độ trong suốt đầy đủ
          }
        : undefined,
    },
    KeyboardButtonProps: {
      tabIndex: tabIndex || 0,
      style: readOnly ? { display: "none" } : undefined, // Ẩn nút lịch khi readOnly
    },
    InputLabelProps: {
      htmlFor: name,
      shrink: false,
      ...InputLabelProps,
    },
    className: `input-container ${className} ${readOnly ? "read-only" : ""}`,
    disablePast: disablePast,
    disableFuture: disableFuture,
    invalidDateMessage: "Ngày không hợp lệ",
    minDateMessage: minDateMessage,
    maxDateMessage: maxDateMessage,
    okLabel: okLabel,
    cancelLabel: cancelLabel,
  };

  return (
    <div className="h-100 flex justify-right align-start flex-column">
      <MuiPickersUtilsProvider utils={DateFnsUtils} locale={viLocale}>
        {label && (
          <label htmlFor={name} className={`label-container`}>
            {label} {required ? <span style={{ color: "red" }}> * </span> : <></>}
          </label>
        )}

        {isDateTimePicker ? (
          <KeyboardDateTimePicker {...configDate} />
        ) : isTimePicker ? (
          <KeyboardTimePicker {...configDate} />
        ) : (
          <KeyboardDatePicker {...configDate} />
        )}
      </MuiPickersUtilsProvider>
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
    nextProps.readOnly !== currentProps.readOnly ||
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

export default React.memo(GlobitsDateTimePicker);