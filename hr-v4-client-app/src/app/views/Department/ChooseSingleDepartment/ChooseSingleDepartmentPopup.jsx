import React, { memo, useEffect, useState } from "react";
import { Grid, Tooltip, } from "@material-ui/core";
import { useStore } from "app/stores";
import { observer } from "mobx-react";
import { useTranslation } from "react-i18next";
import { Form, Formik, useFormikContext } from "formik"; 
import GlobitsPopup from "app/common/GlobitsPopup";
import { Radio } from "@material-ui/core";
import GlobitsTextField from "app/common/form/GlobitsTextField";
import { IconButton } from "@material-ui/core";
import Search from "@material-ui/icons/Search";
import GlobitsTable from "app/common/GlobitsTable";
import { formatDate } from "app/LocalFunction";
import GlobitsPopupV2 from "app/common/GlobitsPopupV2";
import { width } from "dom-helpers";

function ChooseSingleDepartmentPopup(props) {
    const {
        open,
        handleClose,
        name
    } = props;

    const { t } = useTranslation();
    const {
        resetStoreSubDepartments,
        pagingSubDeparments,
        setPageSizeSubDeparments,
        searchObjectSubDepartments,
        handleSetSearchObjectSubDepartments,
        departmentList,
        totalElements,
        totalPages,
        handleChangePageSubDepartments,
        updatePageData
    } = useStore().departmentStore;

    const { values, setFieldValue } = useFormikContext();

    useEffect(function () {
        if (open) {
            updatePageData();
        }
        return resetStoreSubDepartments;
    }, [open]);

    function handleSelectDepartment(chosenDepartment) {
        setFieldValue([name], chosenDepartment);
        handleClose();
    }

    const columns = [
        {
            title: t("general.popup.select"),
            align: "center",
            cellStyle: {
                textAlign: "center",
            },
            width: "48px",
            render: (rowData) => (
                <Tooltip title="Chọn sử dụng" placement="top">
                    <Radio
                        className="pr-16"
                        id={`radio${rowData?.id}`}
                        name="radSelected"
                        value={rowData.id}
                        checked={values[name]?.id === rowData?.id}
                        onClick={(event) => handleSelectDepartment(rowData)}
                    />
                </Tooltip>
            ),
        },
        {
            title: "Mã",
            field: "code",
            align: "left",
            cellStyle: {
                textAlign: "left",
            },
        },
        {
            title: "Tên Phòng ban",
            field: "name",
            align: "left",
            cellStyle: {
                textAlign: "left",
            },
        },
    ];
    
    const [initialValues, setInitialValues] = useState({ keyword: "" });

    function handleSearchDepartmentDepartment(values) {
        console.log("values", values);
        const searchObject = {
            ...searchObjectSubDepartments,
            keyword: values.keyword || "",
            pageIndex: 1,
        };
    
        //handleSetSearchObjectSubDepartments(searchObject);
        //pagingSubDeparments
        setInitialValues(searchObject);
        updatePageData(searchObject);
    }
    
    

    return (
        <GlobitsPopupV2
            noDialogContent
            open={open}
            popupId={"chooseSingleDepartmentPopup"}
            title='Danh sách phòng ban'
            size="md"
            scroll={"body"}
            onClosePopup={handleClose}
        >
            <Grid container className="p-12">
                <Grid item xs={12}>
                    <Grid container spacing={2}>
                        <Grid item xs={12} sm={6} lg={8}>

                        </Grid>

                        <Grid item xs={12} sm={6} lg={4}>
                            <Formik
                                enableReinitialize
                                initialValues={initialValues}
                                onSubmit={handleSearchDepartmentDepartment}
                            >
                                {({ resetForm, values, setFieldValue, setValues }) => {
                                    return (
                                        <Form autoComplete="off">
                                            <GlobitsTextField
                                                placeholder="Tìm kiếm phòng ban..."
                                                name="keyword"
                                                variant="outlined"
                                                notDelay
                                                value={values.keyword}
                                                onChange={(e) => setFieldValue("keyword", e.target.value)}
                                                InputProps={{
                                                    endAdornment: (
                                                        <IconButton className="py-0 px-4" aria-label="search" type="submit">
                                                            <Search />
                                                        </IconButton>
                                                    ),
                                                }}
                                            />
                                        </Form>
                                    );
                                }}
                            </Formik>
                        </Grid>
                    </Grid>

                </Grid>

                <Grid item xs={12} className="pt-12">
                    <GlobitsTable
                        data={departmentList}
                        columns={columns}
                        totalPages={totalPages}
                        handleChangePage={handleChangePageSubDepartments}
                        setRowsPerPage={setPageSizeSubDeparments}
                        pageSize={searchObjectSubDepartments?.pageSize}
                        pageSizeOption={[10, 25, 50]}
                        totalElements={totalElements}
                        page={searchObjectSubDepartments?.pageIndex}
                        colParent
                    />
                </Grid>
            </Grid>
        </GlobitsPopupV2>
    );
}

export default memo(observer(ChooseSingleDepartmentPopup));