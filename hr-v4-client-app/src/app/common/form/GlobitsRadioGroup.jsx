import React from "react";
import {
  RadioGroup,
  Radio,
  FormControl,
  FormControlLabel,
  FormLabel,
  FormHelperText,
} from "@material-ui/core";
import { useField } from "formik";
import { FastField } from "formik";

const GlobitsRadioGroup = (props) => {
  return (
    <FastField
      {...props}
      name={props.name}
    >
      {({ field, meta, form }) => {
        return (
          <MyRadioGroup
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

const MyRadioGroup = ({
  name,
  label,
  options,
  inARow,
  disabled,
  setFieldValue,
  readOnly = false,  // Thêm prop readOnly
  ...otherProps
}) => {
  const [field, meta] = useField(name);

  const configRadioGroup = {
    ...field,
    ...otherProps,
    row: otherProps?.row ? otherProps.row : true,
  };

  const handleChange = (value) => {
    if (readOnly) return;  // Nếu là readOnly thì không thay đổi giá trị
    setFieldValue(name, value);
  };

  return (
    <FormControl
      component="divider"
      error={meta && meta.touched && meta.error}
      className={readOnly ? "read-only" : ""}  // Thêm class read-only nếu readOnly là true
    >
      {!inARow && <FormLabel className="mr-12">{label}</FormLabel>}
      <RadioGroup aria-label={name} {...configRadioGroup}>
        {inARow && <FormLabel className="mr-12">{label}</FormLabel>}
        {options?.map((option) => {
          return (
            <FormControlLabel
              disabled={disabled}  // Disable khi readOnly là true
              value={option?.value}
              label={option?.name}
              control={<Radio />}
              checked={option?.value == field.value}
              onClick={() => handleChange(option?.value)}  // Cập nhật giá trị chỉ khi không phải readOnly
            />
          );
        })}
      </RadioGroup>
      <FormHelperText>{meta?.error}</FormHelperText>
    </FormControl>
  );
};

export default GlobitsRadioGroup;
