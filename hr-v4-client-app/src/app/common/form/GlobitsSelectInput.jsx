// /* eslint-disable react-hooks/exhaustive-deps */
// import { Popper, TextField } from "@material-ui/core";
// import Autocomplete from "@material-ui/lab/Autocomplete";
// import React, { useMemo } from "react";
// import { FastField, getIn } from "formik";
// import { forwardRef } from "react";

// const AutoWidthPopper = (props) => {
//   const { style: { width } } = props;
//   const style = { minWidth: 'fit-content', width }
//   return <Popper {...props} style={style} />;
// }


// const GlobitsSelectInput = (props, ref) => {
//   return (
//     <FastField
//       {...props}
//       name={props.name}
//       shouldUpdate={shouldComponentUpdate}
//     >
//       {({ field, meta, form }) => (
//         <Component
//           {...props}
//           field={field}
//           meta={meta}
//           setFieldValue={form.setFieldValue}
//           ref={ref}
//         />
//       )}
//     </FastField>
//   );
// };

// const Component = forwardRef((props, ref) => {
//   const {
//     disabled,
//     label,
//     name,
//     className = "",
//     required,
//     requiredLabel,
//     onChange,
//     readOnly,
//     value,
//     displayLabel = 'name',
//     displayValue = 'value',
//     options = [],
//     field,
//     meta,
//     setFieldValue,
//     getOptionLabel,
//     defaultValue = null,
//     disableClearable,
//     getOptionDisabled,
//     onKeyDown,
//     noNullOption = false,
//     disableTyping,
//     getOptionSelected,
//     getValueInputProps,
//     placeholder,
//     autoWidthPopper = false,
//     freeSolo = false,
//     tabIndex,
//     autoUpercase,
//     oldStyle,
//     fullWidth,
//     size = "small",

//     ...otherProps
//   } = props;

//   const isError = meta && meta.touched && meta.error;

//   const getOptionLabelDefault = useMemo(() => getOptionLabel ? getOptionLabel : (option) => {
//     let label=""
//     if (option === field?.value) {
//       const item = options?.find(e => getIn(e, displayValue) === option);
//       label= item ? getIn(item, displayLabel) : (typeof option === 'string' ? option : "");
//     }else {
//       label =getIn(option, displayLabel) ?? ''
//     }
//     if(autoUpercase) {
//       return label.toUpperCase()
//     }
//     return label

//   }, [getOptionLabel]);

//   const handleChange = (event, value) => {
//     let newValue;
//     if (value && typeof value === "object") {
//       newValue = getIn(value, displayValue) ?? null;
//     } else {
//       newValue = value ?? null;
//     }
//     setFieldValue(name, newValue);
//     if (onChange) {
//       const e = {
//         ...event,
//         target: {
//           ...event.target,
//           value: newValue,
//           id: name,
//           name: name,
//         },
//       };
//       onChange(e);
//     }
//   };


//   return (
//     <>

//         <label className={`${oldStyle ? 'old-label' : 'label-container'}`} htmlFor={name}>
//           {label} {required ? <span style={{ color: "red" }}> * </span> : <></>}
//         </label>

//       <Autocomplete
//         {...field}
//         {...otherProps}
//         ref={ref}
//         id={name}
//         name={name}
//         disabled={disabled || readOnly}
//         value={value !== undefined ? value : field?.value ?? defaultValue ?? null}
//         onChange={handleChange}
//         options={options || []}
//         getOptionLabel={getOptionLabelDefault}
//         getOptionSelected={getOptionSelected ? getOptionSelected : (option, value) => getIn(option, displayValue) === value}
//         noOptionsText="Không có dữ liệu"
//         autoHighlight
//         openOnFocus
//         disableClearable={readOnly || disableClearable || noNullOption}
//         readOnly={readOnly}
//         PopperComponent={autoWidthPopper ? AutoWidthPopper : undefined}
//         getOptionDisabled={getOptionDisabled}
//         freeSolo={freeSolo}
//         onKeyDown={(event) => {
//           if (event.key === "Enter") {
//             event.stopPropagation();
//             event.preventDefault();
//           }

//           if (onKeyDown) {
//             onKeyDown(event);
//           }
//           return true;
//         }}
//         renderInput={
//           (params) => {
//             if (!params?.inputProps?.value && field?.value !== undefined) {
//               const item = options?.find(e => getIn(e, displayValue) === field.value);
//               params.inputProps.value = item ? getIn(item, displayLabel) : '';
//             }

//             return (
//               <TextField
//                 {...params}
//                 label={label}
//                 variant={otherProps?.variant || "outlined"}

//                 error={isError}
//                 helperText={(fullWidth && isError) ? meta.error : ""}
//                 className={`input-container ${className} ${readOnly ? 'read-only-autocomplete' : ''}`}
//                 InputLabelProps={{
//                   htmlFor: name,
//                 }}
//                 placeholder={placeholder}
//                 inputProps={{
//                   ...params.inputProps,
//                   autoComplete: "off",
//                   readOnly: readOnly || disableTyping,
//                   // value: getValueInputProps ? getValueInputProps(params?.inputProps?.value) :
//                   //   ((params?.inputProps?.value !== null || params?.inputProps?.value !== undefined) ?
//                   //     params?.inputProps?.value : null),
//                   value: getValueInputProps
//                   ? getValueInputProps(params?.inputProps?.value)
//                   : (params?.inputProps?.value !== null || params?.inputProps?.value !== undefined)
//                     ? (autoUpercase ? params?.inputProps?.value?.toUpperCase() : params?.inputProps?.value) // Viết hoa giá trị trong input
//                     : null,
//                   tabIndex: tabIndex,
//                 }}
//               />
//             )
//           }
//         }
//       />
//     </>
//   )
// });

// const shouldComponentUpdate = (nextProps, currentProps) => (
//   nextProps?.options !== currentProps?.options ||
//   nextProps.name !== currentProps.name ||
//   nextProps.value !== currentProps.value ||
//   nextProps.onChange !== currentProps.onChange ||
//   nextProps.label !== currentProps.label ||
//   nextProps.required !== currentProps.required ||
//   nextProps.disabled !== currentProps.disabled ||
//   nextProps.readOnly !== currentProps.readOnly ||
//   nextProps.formik.isSubmitting !== currentProps.formik.isSubmitting ||
//   Object.keys(nextProps).length !== Object.keys(currentProps).length ||
//   getIn(nextProps.formik.values, currentProps.name) !== getIn(currentProps.formik.values, currentProps.name) ||
//   getIn(nextProps.formik.errors, currentProps.name) !== getIn(currentProps.formik.errors, currentProps.name) ||
//   getIn(nextProps.formik.touched, currentProps.name) !== getIn(currentProps.formik.touched, currentProps.name)
// );

// export default React.memo(forwardRef(GlobitsSelectInput));






import React, { memo } from "react";
import { TextField, MenuItem, FormControl } from "@material-ui/core";
import { FastField, getIn, useField, useFormikContext } from "formik";

const GlobitsSelectInput = (props, ref) => {
  return (
    <FastField
      {...props}
      name={props.name}
      shouldUpdate={shouldComponentUpdate}
    >
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
    getIn(nextProps.formik.values, currentProps.name) !==
    getIn(currentProps.formik.values, currentProps.name) ||
    getIn(nextProps.formik.errors, currentProps.name) !==
    getIn(currentProps.formik.errors, currentProps.name) ||
    getIn(nextProps.formik.touched, currentProps.name) !==
    getIn(currentProps.formik.touched, currentProps.name)
  );
};

const MySelectInput = ({
  name,
  keyValue = 'value',
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
  ...otherProps
}) => {
  const { setFieldValue } = useFormikContext();
  const [field, meta] = useField(name);

  // Tạo wrapper cho handleChange từ bên ngoài
  const handleExternalChange = (evt) => {
    if (readOnly) return; // Không thực hiện khi readOnly
    if (externalHandleChange) {
      externalHandleChange(evt);
    }
  };

  // Default handleChange
  const handleInternalChange = (evt) => {
    if (readOnly) return; // Không thực hiện khi readOnly
    const { value } = evt.target;
    setFieldValue(name, value);
  };

  const configSelectInput = {
    ...field,
    ...otherProps,
    select: true,
    variant: variant ? variant : "outlined",
    size: size ? size : "small",
    fullWidth: true,
    // Chọn hàm onChange phù hợp và đảm bảo readOnly hoạt động
    onChange: externalHandleChange ? handleExternalChange : handleInternalChange,
    InputLabelProps: {
      htmlFor: name,
      shrink: true,
    },
    // Thêm disabled khi ở chế độ readOnly để không cho mở dropdown
    disabled: readOnly || otherProps.disabled,
    className: `${oldStyle ? '' : 'input-container'} ${readOnly ? 'read-only' : ''}`,
  };

  if (meta && meta.touched && meta.error) {
    configSelectInput.error = true;
    configSelectInput.helperText = meta.error;
  }

  return (
    <>
      {label && (
        <label className={`${oldStyle ? 'old-label' : 'label-container'}`} htmlFor={name}>
          {label} {required ? <span style={{ color: "red" }}> * </span> : <></>}
        </label>
      )}

      <FormControl fullWidth>
        <TextField {...configSelectInput}>
          {!hideNullOption && (
            <MenuItem value={null}>
              <em>---</em>
            </MenuItem>
          )}
          {options?.map((item, pos) => {
            const isDisabled = getOptionDisabled ? getOptionDisabled(item) : false;

            return (
              <MenuItem
                key={pos}
                value={item[keyValue]}
                disabled={isDisabled}
              >
                {item[displayvalue ? displayvalue : "name"]}
              </MenuItem>
            );
          })}
        </TextField>
      </FormControl>
    </>
  );
};

export default memo(GlobitsSelectInput);