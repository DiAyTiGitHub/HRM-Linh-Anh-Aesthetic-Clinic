import {observer} from "mobx-react";
import React, {memo, useEffect, useState} from "react";
import {useStore} from "app/stores";
import {Grid, Button, IconButton} from "@material-ui/core";
import GlobitsBreadcrumb from "app/common/GlobitsBreadcrumb";
import {useTranslation} from "react-i18next";
import {Form, Formik} from "formik";
import GlobitsTextField from "../../../common/form/GlobitsTextField";
import SearchIcon from "@material-ui/icons/Search";
import StaffSalaryItemValueList from "./StaffSalaryItemValueList";
import AddIcon from "@material-ui/icons/Add";
import GlobitsConfirmationDialog from "../../../common/GlobitsConfirmationDialog";
import StaffSalaryItemValueForm from "./StaffSalaryItemValueForm";

function StaffSalaryItemValueIndex() {
    const {staffSalaryItemValueStore} = useStore();
    const {t} = useTranslation();

    const {
        pagingStaffSalaryItemValue,
        handleOpenCreateEdit,
        openConfirmDeleteListPopup,
        openConfirmDeletePopup,
        openCreateEditPopup,
        handleClose,
        handleConfirmDelete,
        handleConfirmDeleteList,
        updatePageData,
        pageIndex,
        pageSize,
        keyword
    } = staffSalaryItemValueStore;
    useEffect(() => {
        pagingStaffSalaryItemValue();
    }, []);

    const handleFilter = async (values) => {
        updatePageData(values?.keyword);
    }
    const [initialValues, setInitialValues] = useState({
        pageIndex: pageIndex,
        pageSize: pageSize,
        keyword: keyword,
    });

    return (
        <div className="content-index">
            <div className="index-breadcrumb">
                <GlobitsBreadcrumb
                    routeSegments={[{name: t("navigation.salary")}, {name: t("navigation.staffSalaryItemValue.title")}]}/>
            </div>
            <Grid className="index-card" container spacing={2}>
                <Grid item xs={12}>
                    <Formik
                        enableReinitialize
                        initialValues={initialValues}
                        onSubmit={handleFilter}
                    >
                        {({resetForm, values, setFieldValue, setValues}) => {
                            return (
                                <Form autoComplete="off">
                                    <div className="">
                                        <Grid container spacing={2} className="align-center mainBarFilter">
                                            <Grid item xs={12} md={3}>
                                                <Grid container spacing={2} className="">
                                                    <Grid item xs={12} sm={6}>
                                                        <Button
                                                            className="btn text-white d-inline-flex bgc-lighter-dark-green"
                                                            startIcon={<AddIcon/>}
                                                            variant="contained"
                                                            fullWidth
                                                            onClick={() => handleOpenCreateEdit()}
                                                        >
                                                            Thêm mới, Sửa
                                                        </Button>
                                                    </Grid>
                                                </Grid>
                                            </Grid>

                                            <Grid item xs={12} md={9}>
                                                <div className="flex justify-between align-center">
                                                    <GlobitsTextField
                                                        placeholder="Tìm kiếm theo tên nhân viên, tên phân tử lương..."
                                                        name="keyword"
                                                        variant="outlined"
                                                        notDelay
                                                        timeOut={0}
                                                        InputProps={{
                                                            endAdornment: (
                                                                <IconButton className="py-0 px-4" aria-label="search"
                                                                            type="submit">
                                                                    <SearchIcon/>
                                                                </IconButton>
                                                            ),
                                                        }}
                                                    />

                                                    <Button
                                                        startIcon={<SearchIcon className={`mr-2`}/>}
                                                        className="ml-8 d-inline-flex filterButtonV4 bgc-warning-d1 py-2 px-8 btn text-white"
                                                        // onClick={handleLoadViewingData}
                                                        type="submit"
                                                    >
                                                        Tìm kiếm
                                                    </Button>
                                                </div>
                                            </Grid>
                                        </Grid>

                                    </div>
                                </Form>
                            );
                        }}
                    </Formik>
                </Grid>

                <Grid item xs={12}>
                    <StaffSalaryItemValueList/>
                </Grid>
            </Grid>

            {openCreateEditPopup && (
                <StaffSalaryItemValueForm/>
            )}


            {openConfirmDeletePopup && (
                <GlobitsConfirmationDialog
                    open={openConfirmDeletePopup}
                    onConfirmDialogClose={handleClose}
                    onYesClick={handleConfirmDelete}
                    title={t("confirm_dialog.delete.title")}
                    text={t("confirm_dialog.delete.text")}
                    agree={t("confirm_dialog.delete.agree")}
                    cancel={t("confirm_dialog.delete.cancel")}
                />
            )}


            {openConfirmDeleteListPopup && (
                <GlobitsConfirmationDialog
                    open={openConfirmDeleteListPopup}
                    onConfirmDialogClose={handleClose}
                    onYesClick={handleConfirmDeleteList}
                    title={t("confirm_dialog.delete_list.title")}
                    text={t("confirm_dialog.delete_list.text")}
                    agree={t("confirm_dialog.delete_list.agree")}
                    cancel={t("confirm_dialog.delete_list.cancel")}
                />
            )}
        </div>
    );
}

export default memo(observer(StaffSalaryItemValueIndex));