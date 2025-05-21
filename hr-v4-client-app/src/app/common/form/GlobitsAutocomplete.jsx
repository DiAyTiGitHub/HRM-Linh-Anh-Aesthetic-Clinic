import { TextField } from "@material-ui/core";
import Autocomplete from "@material-ui/lab/Autocomplete";
import { useFormikContext, useField } from "formik";
import React, { useEffect } from "react";
// import "./GlobitsAutocomplete.scss"; // Import file SCSS nếu bạn muốn tùy chỉnh style

const GlobitsAutocomplete = ({
  name,
  options,
  displayData,
  variant,
  size,
  isObject,
  properties,
  label = "",
  getOptionLabel,
  handleChange,
  defaultValue,
  validate,
  oldStyle,
  readOnly = false, // Thêm prop readOnly với giá trị mặc định là false
  ...otherProps
}) => {
  const { setFieldValue } = useFormikContext();
  const [field, meta] = useField(name);
  const [open, setOpen] = React.useState(false); // Thêm state để kiểm soát trạng thái mở dropdown

  useEffect(() => {
    if (defaultValue) handleChange(defaultValue);
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [defaultValue]);

  const defaultHandleChange = (_, value) => {
    if (readOnly) return; // Ngăn thay đổi giá trị nếu readOnly
    if (isObject != null && !isObject) {
      setFieldValue(name, value?.value ? value.value : null);
    } else {
      setFieldValue(name, value ? value : null);
    }
  };

  const defaultGetOptionLabel = (option) =>
    option[displayData ? displayData : "name"]
      ? option[displayData ? displayData : "name"]
      : "";

  const configAutocomplete = {
    ...field,
    ...otherProps,
    id: name,
    size: size ? size : "small",
    className: `${oldStyle ? "" : "input-container"} ${readOnly ? "read-only" : ""}`, // Thêm class read-only
    options: options,
    open: readOnly ? false : open, // Ngăn mở dropdown nếu readOnly
    onOpen: () => {
      if (readOnly) return; // Ngăn mở nếu readOnly
      setOpen(true);
    },
    onClose: () => {
      setOpen(false);
    },
    getOptionLabel: getOptionLabel || defaultGetOptionLabel,
    onChange: handleChange || defaultHandleChange,
    getOptionSelected: (option, value) => option?.id === value?.id,
    getOptionDisabled: readOnly ? () => true : undefined, // Vô hiệu hóa tất cả tùy chọn khi readOnly
    renderInput: (params) => {
      if (field?.value && !params?.inputProps?.value) {
        params.inputProps.value = (getOptionLabel || defaultGetOptionLabel)(field?.value);
      }
      return (
        <TextField
          {...params}
          variant={variant ? variant : "outlined"}
          className={readOnly ? "read-only" : ""} // Thêm class read-only cho TextField
          error={configAutocomplete.error}
          helperText={configAutocomplete.helperText}
          InputProps={{
            ...params.InputProps,
            readOnly: readOnly, // Đặt TextField thành readOnly
          }}
          inputProps={{
            ...params.inputProps,
            readOnly: readOnly, // Đảm bảo input cũng là readOnly
          }}
        />
      );
    },
  };

  if (meta && meta.touched && meta.error) {
    configAutocomplete.error = true;
    configAutocomplete.helperText = meta.error;
  }

  return (
    <>
      <label
        htmlFor={name}
        className={`${oldStyle ? "old-label" : "label-container"}`}
      >
        {label} {validate ? <span className="text-danger"> * </span> : <></>}
      </label>
      <Autocomplete {...configAutocomplete} />
    </>
  );
};

export default GlobitsAutocomplete;