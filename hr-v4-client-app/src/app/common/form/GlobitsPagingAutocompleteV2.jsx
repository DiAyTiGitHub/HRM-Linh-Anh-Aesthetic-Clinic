import { TextField, makeStyles } from "@material-ui/core";
import Autocomplete from "@material-ui/lab/Autocomplete";
import { FastField, getIn } from "formik";
import { isEqual } from "lodash";
import React, { useEffect, useState } from "react";

const PAGE_SIZE = 20;

const useStyles = makeStyles((theme) => ({
    container: {
        "& .MuiAutocomplete-inputRoot": {
            paddingTop: "0px !important",
            paddingBottom: "0px !important",
        },
    },
    autoHeight: {
        "& > div": {
            height: "auto !important",
        },
    },
}));

const GlobitsPagingAutocompleteV2 = (props) => {
    return (
        <FastField {...props} name={props.name} shouldUpdate={shouldComponentUpdate}>
            {({ field, meta, form }) => {
                return <MyPagingAutocomplete {...props} field={field} meta={meta} setFieldValue={form.setFieldValue} />;
            }}
        </FastField>
    );
};

function MyPagingAutocomplete({
    api,
    name,
    searchObject,
    allowLoadOptions = true,
    clearOptionOnClose,
    handleChange,
    field,
    meta,
    setFieldValue,
    label,
    oldStyle = false,
    required,
    getOptionDisabled,
    readOnly = false, // Add readOnly prop with default value false
    ...otherProps
}) {
    const [page, setPage] = useState(1);
    const [options, setOptions] = useState([]);
    const [loading, setLoading] = React.useState(false);
    const [keyword, setKeyword] = useState("");
    const [firstLoading, setFirstLoading] = React.useState(true);
    const [totalPage, setTotalPage] = React.useState(1);
    const [open, setOpen] = React.useState(false);
    const [t, setT] = React.useState();
    const [typing, setTyping] = React.useState(false);

    const classes = useStyles();

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
        api({
            ...searchObject,
            pageIndex: newPage,
            pageSize: PAGE_SIZE,
            keyword,
        }).then(({ data }) => {
            if (data && data.content) {
                setOptions([...data.content]);
                setTotalPage(data.totalPages);
            } else {
                setOptions([]);
            }
        });
    };

    const loadMoreResults = () => {
        const nextPage = page + 1;

        setPage(nextPage);
        api({
            ...searchObject,
            pageIndex: nextPage,
            pageSize: PAGE_SIZE,
            keyword,
        }).then(({ data }) => {
            if (data && data.content) {
                setOptions([...options, ...data.content]);
                setTotalPage(data.totalPages);
            }
        });
    };

    const handleScroll = (event) => {
        const listboxNode = event.currentTarget;

        const position = listboxNode.scrollTop + listboxNode.clientHeight;
        if (listboxNode.scrollHeight - position <= 8 && page < totalPage) {
            loadMoreResults();
        }
    };

    const onOpen = () => {
        if (readOnly) return; // Prevent opening if readOnly

        setOpen(true);
        if (firstLoading && allowLoadOptions) {
            getData();
        }
        setFirstLoading(false);
    };

    const onClose = () => {
        setOpen(false);
        setKeyword(null);
        if (clearOptionOnClose) {
            setOptions([]);
            setTotalPage(1);
        }
    };

    const handleChangeText = (value) => {
        if (readOnly) return; // Prevent text change if readOnly

        setTyping(true);
        if (t) clearTimeout(t);
        //@ts-ignore
        setT(
            setTimeout(() => {
                setKeyword(value || null);
                setTyping(false);
            }, 500)
        );
    };

    const defaultHandleChange = (_, value) => {
        if (readOnly) return; // Prevent value change if readOnly

        setFieldValue(name, value ? value : null);
    };

    const defaultGetOptionLabel = (option) => option[otherProps?.displayName ? otherProps?.displayName : "name"] || "";

    return (
        <Autocomplete
            {...field}
            {...otherProps}
            id={name}
            options={options}
            loading={loading && !readOnly}
            onOpen={onOpen}
            open={readOnly ? false : open} // Prevent opening dropdown if readOnly
            onClose={onClose}
            className={`${oldStyle ? "" : "input-container"} ${classes.container} ${readOnly ? "read-only" : ""}`}
            onChange={handleChange || defaultHandleChange}
            getOptionSelected={(option, value) => option?.id === value?.id}
            getOptionLabel={otherProps?.getOptionLabel ? otherProps.getOptionLabel : defaultGetOptionLabel}
            getOptionDisabled={readOnly ? () => true : getOptionDisabled} // Disable all options when readOnly
            noOptionsText="Không có dữ liệu"
            onKeyDown={(event) => {
                if (event.key === "Enter") {
                    event.stopPropagation();
                    event.preventDefault();
                }
                return true;
            }}
            onInputChange={(event) => {
                if (!readOnly) {
                    handleChangeText(event?.target?.value);
                }
            }}
            renderInput={(params) => (
                <>
                    {label && (
                        <label htmlFor={name} className={`${oldStyle ? "old-label" : "label-container"}`}>
                            {label} {required ? <span style={{ color: "red" }}> * </span> : <></>}
                        </label>
                    )}

                    <TextField
                        {...params}
                        variant={otherProps?.variant || "outlined"}
                        inputProps={{
                            ...params.inputProps,
                            autoComplete: "off", // disable autocomplete and autofill
                            readOnly: readOnly, // Make input readOnly
                            style: readOnly
                                ? {
                                    ...params.inputProps.style,
                                    color: "rgba(0, 0, 0, 0.87)", // Ensure text is dark black
                                    cursor: "text", // Keep cursor as text
                                    opacity: 1, // Ensure full opacity
                                }
                                : params.inputProps.style,
                        }}
                        InputProps={{
                            ...params.InputProps,
                            readOnly: readOnly,
                            style: readOnly
                                ? {
                                    ...params.InputProps.style,
                                    color: "rgba(0, 0, 0, 0.87)",
                                    backgroundColor: "rgba(0, 0, 0, 0.02)", // Very light background to indicate readOnly
                                    opacity: 1,
                                }
                                : params.InputProps.style,
                        }}
                        className={`${classes.autoHeight} ${readOnly ? "read-only" : ""}`}
                        error={meta && meta.touched && meta.error}
                        helperText={meta && meta.touched && meta.error ? meta.error : ""}
                        required={required} // Add this line
                    />
                </>
            )}
            ListboxProps={{
                onScroll: handleScroll,
            }}
        />
    );
}

const shouldComponentUpdate = (nextProps, currentProps) => {
    return (
        nextProps.name !== currentProps.name ||
        nextProps.value !== currentProps.value ||
        nextProps.handleChange !== currentProps.handleChange ||
        nextProps.label !== currentProps.label ||
        nextProps.required !== currentProps.required ||
        nextProps.api !== currentProps.api ||
        nextProps.disabled !== currentProps.disabled ||
        nextProps.readOnly !== currentProps.readOnly ||
        nextProps.getOptionDisabled !== currentProps.getOptionDisabled ||
        !isEqual(nextProps.searchObject, currentProps.searchObject) ||
        nextProps.formik.isSubmitting !== currentProps.formik.isSubmitting ||
        Object.keys(nextProps).length !== Object.keys(currentProps).length ||
        getIn(nextProps.formik.values, currentProps.name) !== getIn(currentProps.formik.values, currentProps.name) ||
        getIn(nextProps.formik.errors, currentProps.name) !== getIn(currentProps.formik.errors, currentProps.name) ||
        getIn(nextProps.formik.touched, currentProps.name) !== getIn(currentProps.formik.touched, currentProps.name)
    );
};

export default React.memo(GlobitsPagingAutocompleteV2);
