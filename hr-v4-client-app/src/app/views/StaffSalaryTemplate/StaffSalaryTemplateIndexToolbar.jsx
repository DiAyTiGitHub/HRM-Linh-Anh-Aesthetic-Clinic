import { observer } from "mobx-react";
import React, { memo, useState } from "react";
import GlobitsTextField from "app/common/form/GlobitsTextField";
import AddIcon from "@material-ui/icons/Add";
import SearchIcon from '@material-ui/icons/Search';
import { Form, Formik } from "formik";
import { Button, ButtonGroup, Grid, Tooltip } from "@material-ui/core";
import { useStore } from "app/stores";
import { useTranslation } from "react-i18next";
import FilterListIcon from '@material-ui/icons/FilterList';
import StaffSalaryTemplateFilter from "./StaffSalaryTemplateFilter";
import GlobitsDateTimePicker from "app/common/form/GlobitsDateTimePicker";
import DeleteOutlineIcon from "@material-ui/icons/DeleteOutline";
import GetAppIcon from "@material-ui/icons/GetApp";
import CloudUploadIcon from "@material-ui/icons/CloudUpload";

function StaffSalaryTemplateIndexToolbar() {
    const { t } = useTranslation();

    const {
        staffSalaryTemplateStore,
        hrRoleUtilsStore

    } = useStore();

    const {
        pagingStaffSalaryTemplate,
        handleOpenCreateEdit,
        searchObject,
        handleSetSearchObject,
        listOnDelete,
        handleDeleteList,
        setOpenPopupDownloadTemplate,
        uploadFileStaffSalaryTemplate,
    } = staffSalaryTemplateStore;

    const {
        isAdmin,
        isManager,
        isCompensationBenifit
    } = hrRoleUtilsStore;

    async function handleFilter(values) {
        const newSearchObject = {
            ...values,
            pageIndex: 1,
        };
        handleSetSearchObject(newSearchObject);
        await pagingStaffSalaryTemplate();
    }

    const [isOpenFilter, setIsOpenFilter] = useState(false);

    function handleCloseFilter() {
        if (isOpenFilter) {
            setIsOpenFilter(false);
        }
    }

    function handleOpenFilter() {
        if (!isOpenFilter) {
            setIsOpenFilter(true);
        }
    }

    function handleTogglePopupFilter() {
        if (isOpenFilter) handleCloseFilter();
        else handleOpenFilter();
    }

    return (
        <Formik
            enableReinitialize
            initialValues={searchObject}
            onSubmit={handleFilter}
        >
            {({ resetForm, values, setFieldValue, setValues }) => {

                return (
                    <Form autoComplete="off">
                        <Grid item xs={12}>
                            <Grid container spacing={2}>
                                <Grid item xs={12}>
                                    {(isCompensationBenifit) && (
                                        <ButtonGroup
                                            color="container"
                                            aria-label="outlined primary button group"
                                        >
                                            <Button
                                                startIcon={<AddIcon />}
                                                onClick={() => handleOpenCreateEdit()}
                                            >
                                                {t("general.button.add")}
                                            </Button>

                                            <Tooltip
                                                placement='top'
                                                title={"Tải xuống mẫu nhập"}
                                                arrow>
                                                <Button startIcon={<GetAppIcon />} onClick={() => setOpenPopupDownloadTemplate(true)}>
                                                    {t("Tải mẫu nhập")}
                                                </Button>
                                            </Tooltip>

                                            <Tooltip placement='top' title={"Nhập mẫu bảng lương nhân viên"} arrow>
                                                <Button
                                                    startIcon={<CloudUploadIcon />}
                                                    onClick={() => document.getElementById("fileExcel").click()}>
                                                    {t("general.button.importExcel")}
                                                </Button>
                                            </Tooltip>
                                            <Button
                                                disabled={listOnDelete?.length <= 0}
                                                startIcon={<DeleteOutlineIcon />}
                                                onClick={handleDeleteList}
                                            >
                                                {t("general.button.delete")}
                                            </Button>
                                        </ButtonGroup>
                                    )}
                                    
                                    <input
                                        type='file'
                                        id='fileExcel'
                                        style={{ display: "none" }}
                                        onChange={uploadFileStaffSalaryTemplate}
                                    />
                                </Grid>

                                <Grid item xs={12}>
                                    <Grid container spacing={2}>
                                        {/* <Grid item xs={12}>
                                            <p className="m-0 p-0 borderThrough2">
                                                Lọc và tìm kiếm
                                            </p>
                                        </Grid> */}

                                        {/* <Grid item xs={12} sm={6} lg={3} className="flex items-center">
                                            <div className="flex items-center h-100 flex-end pr-10">
                                                <p className="no-wrap-text ">
                                                    <b>
                                                        Ngày bắt đầu áp dụng:
                                                    </b>
                                                </p>
                                            </div>
                                            <GlobitsDateTimePicker
                                                // label="Từ ngày"
                                                name="fromDate"
                                            // placeholder="Ngày từ"
                                            />
                                        </Grid>

                                        <Grid item xs={12} sm={6} lg={3} className="flex items-center">
                                            <div className="flex items-center h-100 flex-end pr-10">
                                                <p className="no-wrap-text">
                                                    <b>
                                                        Ngày kết thúc:
                                                    </b>
                                                </p>
                                            </div>
                                            <GlobitsDateTimePicker
                                                name="toDate"
                                            />
                                        </Grid> */}
                                        <Grid item xs={12} lg={6}>  </Grid>
                                        <Grid item xs={12} lg={6}>
                                            {/* <div className="flex justify-center align-end"> */}
                                            <div className="flex justify-between align-center">
                                                <Tooltip
                                                    arrow
                                                    placement="top"
                                                    title="Tìm kiếm theo từ khóa"
                                                >
                                                    <GlobitsTextField
                                                        placeholder="Tìm kiếm theo từ khóa"
                                                        name="keyword"
                                                        variant="outlined"
                                                        notDelay
                                                    />
                                                </Tooltip>

                                                <ButtonGroup
                                                    className="filterButtonV4"
                                                    color="container"
                                                    aria-label="outlined primary button group"
                                                >
                                                    <Button
                                                        startIcon={<SearchIcon className={``} />}
                                                        className="ml-8 d-inline-flex py-2 px-8 btnHrStyle"
                                                        type="submit"
                                                    >
                                                        Tìm kiếm
                                                    </Button>
                                                    <Button
                                                        startIcon={<FilterListIcon
                                                            className={` filterRotateIcon ${isOpenFilter && 'onRotate'}`} />}
                                                        className=" d-inline-flex py-2 px-8 btnHrStyle"
                                                        onClick={handleTogglePopupFilter}
                                                    >
                                                        Bộ lọc
                                                    </Button>
                                                </ButtonGroup>
                                            </div>
                                        </Grid>
                                    </Grid>
                                </Grid>

                            </Grid>

                            <StaffSalaryTemplateFilter
                                isOpenFilter={isOpenFilter}
                                handleFilter={handleFilter}
                                handleCloseFilter={handleCloseFilter}
                            />

                        </Grid>
                    </Form>
                );
            }}
        </Formik >
    );
}

export default memo(observer(StaffSalaryTemplateIndexToolbar));