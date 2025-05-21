import { TextField } from "@material-ui/core";
import Autocomplete from "@material-ui/lab/Autocomplete";
import { useField, useFormikContext } from "formik";
import React, { Fragment, useEffect } from "react";
import CircularProgress from "@material-ui/core/CircularProgress";

const GlobitsAsyncAutocomplete = ({
  name,
  api,
  displayData,
  variant,
  size,
  searchObject,
  label,
  shrink = false,
  required = false,
  placeholder = "",
  getOptionDisabled = (option) => false, // Default to not disabling any options
  oldStyle = false,
  readOnly = false, // Thêm prop readOnly với giá trị mặc định là false
  ...otherProps
}) => {
  const { setFieldValue } = useFormikContext();
  const [field, meta] = useField(name);
  const [open, setOpen] = React.useState(false);
  const [options, setOptions] = React.useState([]);
  const loading = open && options?.length === 0;

  useEffect(() => {
    let active = true;

    if (!loading) {
      return undefined;
    }

    (async () => {
      let response;
      try {
        if (searchObject != null) {
          response = await api(searchObject);
        } else {
          response = await api();
        }

        if (active && response.data) {
          const data = response.data.content || response.data;
          if (Array.isArray(data)) {
            setOptions(data);
          } else {
            setOptions([]);
          }
        }
      } catch (error) {
        console.error("Error fetching data: ", error);
        setOptions([]);
      }
    })();

    return () => {
      active = false;
    };
  }, [api, loading, searchObject]);

  useEffect(() => {
    if (!open) {
      setOptions([]);
    }
  }, [open]);

  const handleChange = (_, value) => {
    if (readOnly) return; // Ngăn thay đổi giá trị nếu readOnly
    setFieldValue(name, value ? value : null);
  };

  const defaultGetOptionLabel = (option) =>
    option[otherProps?.displayName ? otherProps?.displayName : "name"] || "";

  const configSyncAutocomplete = {
    ...field,
    ...otherProps,
    id: name,
    open: readOnly ? false : open, // Ngăn mở dropdown nếu readOnly
    size: size ? size : "small",
    className: `${oldStyle ? "" : "input-container"} ${readOnly ? "read-only" : ""}`, // Thêm class read-only
    onOpen: () => {
      if (readOnly) return; // Ngăn mở nếu readOnly
      setOpen(true);
    },
    onClose: () => {
      setOpen(false);
    },
    onChange: handleChange,
    getOptionSelected: (option, value) => option.id === value.id,
    getOptionLabel: otherProps?.getOptionLabel
      ? otherProps.getOptionLabel
      : defaultGetOptionLabel,
    getOptionDisabled: readOnly ? () => true : getOptionDisabled, // Vô hiệu hóa tất cả tùy chọn khi readOnly
    options: options,
    loading: loading && !readOnly, // Chỉ hiển thị loading khi không readOnly
    renderInput: (params) => (
      <>
        {label && (
          <label
            htmlFor={name}
            className={`${oldStyle ? "old-label" : "label-container"}`}
          >
            {label} {required ? <span style={{ color: "red" }}> * </span> : <></>}
          </label>
        )}
        <TextField
          {...params}
          variant={variant ? variant : "outlined"}
          InputProps={{
            ...params.InputProps,
            readOnly: readOnly, // Đặt TextField thành readOnly
            style: readOnly
              ? {
                  ...params.InputProps.style,
                  color: "rgba(0, 0, 0, 0.87)", // Màu chữ tối
                  backgroundColor: "rgba(0, 0, 0, 0.02)", // Nền nhạt để biểu thị readOnly
                  opacity: 1, // Độ trong suốt đầy đủ
                }
              : params.InputProps.style,
            endAdornment: (
              <Fragment>
                {loading && !readOnly ? (
                  <CircularProgress color="inherit" size={20} />
                ) : null}
                {params.InputProps.endAdornment}
              </Fragment>
            ),
          }}
          inputProps={{
            ...params.inputProps,
            readOnly: readOnly, // Đảm bảo input cũng là readOnly
            style: readOnly
              ? {
                  ...params.inputProps.style,
                  color: "rgba(0, 0, 0, 0.87)", // Màu chữ tối
                  cursor: "text", // Con trỏ dạng text
                  opacity: 1, // Độ trong suốt đầy đủ
                }
              : params.inputProps.style,
          }}
        />
      </>
    ),
  };

  if (meta && meta.touched && meta.error) {
    configSyncAutocomplete.error = true;
    configSyncAutocomplete.helperText = meta.error;
  }

  return <Autocomplete {...configSyncAutocomplete} />;
};

export default GlobitsAsyncAutocomplete;