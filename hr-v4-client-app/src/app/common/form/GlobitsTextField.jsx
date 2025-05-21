import React, { memo, useEffect, useState } from "react";
import { FormControl, TextField, makeStyles } from "@material-ui/core";
import { FastField, getIn, useField } from "formik";

const useStyles = makeStyles((theme) => ({
  endAdo: {
    position: "relative",
    left: 235,
    bottom: 29,
  },
  multiline: {
    "& > div": {
      height: 'unset !important',
      padding: '10px !important'
    }
  }
}));

const GlobitsTextField = (props, ref) => {
  return (
    <FastField
      {...props}
      name={props.name}
      shouldUpdate={shouldComponentUpdate}
    >
      {({ field, meta }) => {
        return <MyTextField {...props} field={field} meta={meta} ref={ref} />;
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
    nextProps.readOnly !== currentProps.readOnly || // Kiểm tra readOnly
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

const MyTextField = ({
  label,
  name,
  variant,
  size,
  type,
  endAdornment,
  validate,
  multiline,
  timeOut = 500,
  required = false,
  oldStyle = false,
  readOnly = false,  // Thêm prop readOnly
  ...otherProps
}) => {
  const [field, meta] = useField(name);
  const [value, setValue] = useState(field.value ?? "");
  const [t, setT] = useState(undefined);
  const classes = useStyles();

  const onChange = (e) => {
    if (readOnly) return;  // Nếu là readOnly thì không thay đổi giá trị
    e.persist();
    setValue(e.target.value);
    if (!otherProps.notDelay) {
      if (t) clearTimeout(t);
      setT(
        setTimeout(() => {
          if (otherProps.onChange) {
            otherProps.onChange(e);
          } else {
            field.onChange(e);
          }
        }, timeOut)
      );
    } else {
      if (otherProps.onChange) {
        otherProps.onChange(e);
      } else {
        field.onChange(e);
      }
    }
  };

  useEffect(() => {
    if (field.value !== undefined) {
      setValue(field.value ?? "");
    }
  }, [field.value]);

  useEffect(() => {
    if (otherProps.value !== undefined) {
      setValue(otherProps.value ?? "");
    }
  }, [otherProps.value]);

  const configTextfield = {
    ...field,
    ...otherProps,
    multiline: multiline,
    value: value,
    id: name,
    onChange: onChange,
    fullWidth: true,
    variant: variant ? variant : "outlined",
    size: size ? size : "small",
    type: type ? type : "",
    InputLabelProps: {
      htmlFor: name,
      shrink: true,
    },
    className: `${oldStyle ? '' : 'input-container'} ${multiline ? classes.multiline : ''} ${readOnly ? 'read-only' : ''}`, // Thêm class read-only khi readOnly là true
  };

  if (meta && meta.touched && meta.error) {
    configTextfield.error = true;
    configTextfield.helperText = meta.error;
  }

  return (
    <>
      {label && (
        <label htmlFor={name} className={`${oldStyle ? 'old-label' : 'label-container'}`}>
          {label} {(validate || required) ? <span style={{ color: "red" }}> * </span> : <></>}
        </label>
      )}

      <FormControl fullWidth>
        <TextField {...configTextfield} />
        <span className={classes.endAdo}>{endAdornment}</span>
      </FormControl>
    </>
  );
};

export default memo(GlobitsTextField);
