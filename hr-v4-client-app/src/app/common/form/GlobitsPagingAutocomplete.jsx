import React, { useEffect, useMemo, useState } from "react";
import { FastField, getIn } from "formik";
import { isEqual } from "lodash";
import clsx from "clsx";
import ErrorIcon from "../ErrorIcon/ErrorIcon";
import { RequiredLabel } from "../CommonFunctions";
// import AutoWidthPopper from "../custom/AutoWidthPopper";
import { TextField } from "@material-ui/core";
import AutoComplete from "@material-ui/lab/Autocomplete";

const PAGE_SIZE = 10;

const defaultGetOptionSelected = (option, value) => option?.id === value?.id;

const GlobitsPagingAutocomplete = (props) => {
    return (
        <FastField {...props} name={props.name} shouldUpdate={shouldComponentUpdate}>
            {({ field, meta, form }) => {
                return (
                    <MyPagingAutocompleteV3 {...props} field={field} meta={meta} setFieldValue={form.setFieldValue} />
                );
            }}
        </FastField>
    );
};

function MyPagingAutocompleteV3(props) {
    const {
        name,
        api,
        displayData,
        // variant = "outlined",
        size = "small",
        searchObject,
        label,
        sortOptions,
        field,
        meta,
        setFieldValue,
        onChange,
        getOptionSelected,
        getOptionDisabled,
        getOptionLabel,
        allowLoadOptions = true,
        sx,
        disableClearable,
        fullWidth = true,
        requiredLabel = false,
        required = false,
        clearOptionOnClose = false,
        willShrink,
        multiple,
        disableCloseOnSelect,
        endAdornment,
        placeholder = "",
        InputProps,
        oldStyle,
        readOnly = false, // Thêm prop readOnly, mặc định là false
        customData,
        ...otherProps
    } = props;

    const [page, setPage] = useState(1);
    const [options, setOptions] = useState([]);
    const [loading, setLoading] = React.useState(false);
    const [keyword, setKeyword] = useState("");
    const [totalPage, setTotalPage] = React.useState(1);
    const [open, setOpen] = React.useState(false);
    const [t, setT] = React.useState();
    const [typing, setTyping] = React.useState(false);

    useEffect(() => {
        if (!allowLoadOptions) {
            setOptions([]);
        }
    }, [allowLoadOptions]);

    useEffect(() => {
        if (loading && allowLoadOptions) {
            loadMoreResults();
        }
    }, [page, loading]);

    useEffect(() => {
        if (open && allowLoadOptions) {
            getData();
        }
    }, [keyword, open, searchObject]);

    const getData = () => {
        let newPage = 1;
        setPage(newPage);
        api &&
            api({
                ...searchObject,
                pageIndex: newPage,
                pageSize: PAGE_SIZE,
                keyword: keyword || "",
            })
                .then((response) => {
                    if (response) {
                        const { data } = customData ? response[customData] : response;
                        if (data?.content?.length > 0) {
                            setOptions(sortOptions ? sortOptions(data.content) : data.content);
                            setTotalPage(data.totalPages);
                        }
                    }
                })
                .catch((err) => {
                    console.error(err);
                });
    };

    const loadMoreResults = async () => {
        const nextPage = page + 1;

        setPage(nextPage);
        api &&
            (await api({
                ...searchObject,
                pageIndex: nextPage,
                pageSize: PAGE_SIZE,
                keyword: keyword || "",
            })
                .then(({ data }) => {
                    const content = customData ? data[customData]?.content : data?.content
                    if (content?.length > 0) {
                        setOptions((options) =>
                            sortOptions ? sortOptions([...options, ...content]) : [...options, ...content]
                        );
                        setTotalPage(data.totalPages);
                    }
                })
                .catch((err) => {
                    console.error(err);
                }));
    };

    const handleScroll = (event) => {
        const listboxNode = event.currentTarget;

        const position = listboxNode.scrollTop + listboxNode.clientHeight;
        if (listboxNode.scrollHeight - position <= 5 && page < totalPage) {
            loadMoreResults();
        }
    };

    const onOpen = () => {
        if (readOnly) return; // Prevent opening the dropdown in read-only mode
        setKeyword("");
        setOpen(true);
    };

    const onClose = () => {
        setOpen(false);
        if (clearOptionOnClose) {
            setOptions([]);
            setTotalPage(1);
        }
    };

    const handleChangeText = (value) => {
        if (readOnly) return; // Prevent text input change in read-only mode
        setTyping(true);
        if (t) clearTimeout(t);
        // @ts-ignore
        setT(
            setTimeout(() => {
                setKeyword(value);
                setTyping(false);
            }, 300)
        );
    };

    const defaultHandleChange = (_, value) => {
        if (readOnly) return; // Prevent value change in read-only mode
        setFieldValue(name, value || null);
    };

    const defaultGetOptionLabel = (option) => {
        if (!option) {
            return "---";
        }

        // Hàm lấy giá trị từ key động (hỗ trợ cả trường hợp có dấu chấm và không có)
        const getNestedValue = (obj, path) => {
            return path.includes(".") ? path.split(".").reduce((o, k) => o?.[k], obj) : obj?.[path];
        };

        return getNestedValue(option, displayData ?? "name") || "";
    };

    const value = otherProps?.value || field?.value || null;

    const isError = meta && meta.touched && meta.error;

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
            <AutoComplete
                {...field}
                {...otherProps}
                value={value ? value : multiple ? [] : null}
                loading={loading}
                onOpen={onOpen}
                open={open && !readOnly} // Prevent opening if readOnly
                onClose={onClose}
                className={clsx(`${oldStyle ? "" : "input-container"} `, readOnly && "read-only")} // Add 'read-only' class
                multiple={multiple}
                id={name}
                onChange={onChange || defaultHandleChange}
                size={size}
                getOptionSelected={getOptionSelected || defaultGetOptionSelected}
                getOptionDisabled={getOptionDisabled}
                getOptionLabel={getOptionLabel || defaultGetOptionLabel}
                options={options}
                autoHighlight
                openOnFocus
                disableClearable={disableClearable}
                // PopperComponent={AutoWidthPopper}
                noOptionsText="Không có dữ liệu"
                fullWidth={fullWidth}
                disableCloseOnSelect={disableCloseOnSelect}
                onInputChange={(event) => {
                    if (!readOnly) handleChangeText(event?.target?.value);
                }}
                renderInput={(params) => {
                    return (
                        <TextField
                            {...params}
                            placeholder={placeholder}
                            // label={fullWidth ? displayLabel : null}
                            className={clsx(otherProps?.className, params?.className, "input-container", readOnly && "read-only")}
                            variant={otherProps?.variant || "outlined"}
                            fullWidth={fullWidth}
                            error={isError}
                            helperText={fullWidth && isError && meta.error}
                            InputProps={{
                                ...params.InputProps,
                                ...InputProps,
                                readOnly: readOnly, // Thêm thuộc tính readOnly
                                endAdornment: <>{endAdornment || params.InputProps.endAdornment}</>,
                            }}
                        />
                    );
                }}
                onKeyDown={(event) => {
                    if (readOnly) {
                        event.preventDefault(); // Prevent any keyboard input in read-only mode
                    } else if (event.key === "Enter") {
                        event.stopPropagation();
                        event.preventDefault();
                    }
                    return true;
                }}
                ListboxProps={{
                    onScroll: handleScroll,
                }}
            />
            {isError && !fullWidth && <ErrorIcon helperText={meta.error} />}
        </>
    );
}

const shouldComponentUpdate = (nextProps, currentProps) => {
    return (
        nextProps.name !== currentProps.name ||
        nextProps.value !== currentProps.value ||
        nextProps.onChange !== currentProps.onChange ||
        nextProps.label !== currentProps.label ||
        nextProps.required !== currentProps.required ||
        nextProps.disabled !== currentProps.disabled ||
        nextProps.readOnly !== currentProps.readOnly ||
        nextProps.api !== currentProps.api ||
        !isEqual(nextProps.searchObject, currentProps.searchObject) ||
        nextProps.formik.isSubmitting !== currentProps.formik.isSubmitting ||
        Object.keys(nextProps).length !== Object.keys(currentProps).length ||
        getIn(nextProps.formik.values, currentProps.name) !== getIn(currentProps.formik.values, currentProps.name) ||
        getIn(nextProps.formik.errors, currentProps.name) !== getIn(currentProps.formik.errors, currentProps.name) ||
        getIn(nextProps.formik.touched, currentProps.name) !== getIn(currentProps.formik.touched, currentProps.name)
    );
};

export default React.memo(GlobitsPagingAutocomplete);
