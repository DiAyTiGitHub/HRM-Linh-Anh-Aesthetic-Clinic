import { Grid, IconButton, Radio, Tooltip } from "@material-ui/core";
import Search from "@material-ui/icons/Search";
import GlobitsTextField from "app/common/form/GlobitsTextField";
import GlobitsPopupV2 from "app/common/GlobitsPopupV2";
import GlobitsTable from "app/common/GlobitsTable";
import { Form, Formik, useFormikContext } from "formik";
import { observer } from "mobx-react";
import { memo, useEffect, useState } from "react";
import { useTranslation } from "react-i18next";

function ObjectSelectorPopup(props) {
    const {
        open,
        handleClose,
        name,
        handleAfterSubmit,
        title = "Danh sách dữ liệu",
        columns = [],
        fetchDataFunction,
        resetDataFunction,
        searchObjectKey = "keyword",
        searchPlaceholder = "Tìm kiếm...",
        dataList = [],
        totalElements = 0,
        totalPages = 0,
        handleChangePage,
        setPageSize,
        searchObject = {},
        handleSetSearchObject,
        size = "md",
        customFilter,
    } = props;

    const { t } = useTranslation();
    const { values, setFieldValue } = useFormikContext();

    useEffect(
        function () {
            if (open && fetchDataFunction) {
                fetchDataFunction();
            }
            return resetDataFunction || (() => {});
        },
        [open]
    );

    function handleSelectObject(chosenObject) {
        if (chosenObject?.id === values[name]?.id) {
            setFieldValue(name, null);
        } else {
            setFieldValue(name, chosenObject);
        }
        if (handleAfterSubmit) {
            handleAfterSubmit(chosenObject);
        }
        handleClose();
    }

    // Add radio selection column if not already included
    const finalColumns = [
        {
            title: t("general.popup.select"),
            align: "center",
            cellStyle: {
                textAlign: "center",
            },
            width: "48px",
            render: (rowData) => (
                <Tooltip title='Chọn' placement='top'>
                    <Radio
                        className='pr-16'
                        id={`radio${rowData?.id}`}
                        name='radSelected'
                        value={rowData.id}
                        checked={values[name]?.id === rowData?.id}
                        onClick={(event) => handleSelectObject(rowData)}
                    />
                </Tooltip>
            ),
        },
        ...columns,
    ];

    async function handleSearch(searchValues) {
        const newSearchObj = {
            ...searchObject,
            // [searchObjectKey]: searchValues[searchObjectKey],
            ...searchValues,
            pageIndex: 1,
        };

        if (handleSetSearchObject) {
            handleSetSearchObject(newSearchObj);
        }

        setInitialValues(searchValues);

        if (fetchDataFunction) {
            await fetchDataFunction(newSearchObj);
        }
    }

    const [initialValues, setInitialValues] = useState({ [searchObjectKey]: null, ...searchObject });

    return (
        <GlobitsPopupV2
            noDialogContent
            popupId={"objectSelectorPopup"}
            open={open}
            title={title}
            size={size}
            scroll={"body"}
            onClosePopup={handleClose}>
            <Grid container className='p-12'>
                <Grid item xs={12}>
                    <Grid container spacing={2}>
                        <Grid item xs={12}>
                            {customFilter ? (
                                <Formik enableReinitialize initialValues={initialValues} onSubmit={handleSearch}>
                                    {(formikProps) => (
                                        <Form autoComplete='off'>
                                            {typeof customFilter === "function"
                                                ? customFilter(formikProps)
                                                : customFilter}
                                        </Form>
                                    )}
                                </Formik>
                            ) : (
                                <Formik enableReinitialize initialValues={initialValues} onSubmit={handleSearch}>
                                    {({ resetForm, values, setFieldValue, setValues }) => {
                                        return (
                                            <Form autoComplete='off'>
                                                <GlobitsTextField
                                                    placeholder={searchPlaceholder}
                                                    name={searchObjectKey}
                                                    variant='outlined'
                                                    notDelay
                                                    InputProps={{
                                                        endAdornment: (
                                                            <IconButton
                                                                className='py-0 px-4'
                                                                aria-label='search'
                                                                type='submit'>
                                                                <Search />
                                                            </IconButton>
                                                        ),
                                                    }}
                                                />
                                            </Form>
                                        );
                                    }}
                                </Formik>
                            )}
                        </Grid>
                    </Grid>
                </Grid>

                <Grid item xs={12} className='pt-12'>
                    <GlobitsTable
                        data={dataList}
                        columns={finalColumns}
                        totalPages={totalPages}
                        handleChangePage={handleChangePage}
                        setRowsPerPage={setPageSize}
                        pageSize={searchObject?.pageSize}
                        pageSizeOption={[10, 25, 50]}
                        totalElements={totalElements}
                        page={searchObject?.pageIndex}
                    />
                </Grid>
            </Grid>
        </GlobitsPopupV2>
    );
}

export default memo(observer(ObjectSelectorPopup));
