import { observer } from "mobx-react";
import React, { memo, useState } from "react";
import DeleteOutlineIcon from "@material-ui/icons/DeleteOutline";
import GlobitsTextField from "app/common/form/GlobitsTextField";
import AddIcon from "@material-ui/icons/Add";
import SearchIcon from '@material-ui/icons/Search';
import { Form, Formik } from "formik";
import { Grid, Button, ButtonGroup, Tooltip } from "@material-ui/core";
import { useStore } from "app/stores";
import { useTranslation } from "react-i18next";
import DepartmentV2Filter from "./DepartmentV2Filter";
import GlobitsPagingAutocomplete from "app/common/form/GlobitsPagingAutocomplete";
import { pagingAllOrg } from "../Organization/OrganizationService";
import GetAppIcon from "@material-ui/icons/GetApp";
import CloudUploadIcon from "@material-ui/icons/CloudUpload";
import CloudDownloadIcon from "@material-ui/icons/CloudDownload";
import { useTheme } from "@material-ui/core/styles";
import useMediaQuery from "@material-ui/core/useMediaQuery";
import FilterListIcon from "@material-ui/icons/FilterList";
import GlobitsCheckBox from "../../common/form/GlobitsCheckBox";

function DepartmentV2Toolbar() {
    const { departmentV2Store } = useStore();
    const { t } = useTranslation();

    const {
        pagingAllDepartment,
        handleOpenCreateEdit,
        searchObject,
        handleSetSearchObject,
        listOnDelete,
        handleDeleteList,
        uploadFileExcel,
        handleDownloadDepartmentTemplate,
        handleExportExcelDepartmentData
    } = departmentV2Store;

    async function handleFilter(values) {
        const newSearchObject = {
            ...values,
            pageIndex: 1,
        };
        handleSetSearchObject(newSearchObject);
        await pagingAllDepartment();
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

    const { isAdmin } = useStore().hrRoleUtilsStore;

    const theme = useTheme();
    const isMobile = useMediaQuery(theme.breakpoints.down("xs"));

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
                                    {(isAdmin) && (
                                        <ButtonGroup
                                            color="container"
                                            aria-label="outlined primary button group"
                                        >
                                            <Tooltip
                                                placement="top"
                                                title={"Thêm mới phòng ban"}
                                                arrow
                                            >
                                                <Button
                                                    startIcon={<AddIcon />}
                                                    onClick={() => handleOpenCreateEdit()}
                                                >
                                                    {!isMobile && t("general.button.add")}
                                                </Button>
                                            </Tooltip>

                                            <Tooltip
                                                placement="top"
                                                title={"Tải xuống mẫu nhập danh sách phòng ban"}
                                                arrow
                                            >
                                                <Button
                                                    startIcon={<GetAppIcon />}
                                                    onClick={handleDownloadDepartmentTemplate}
                                                >
                                                    {!isMobile && t("Tải mẫu nhập")}
                                                </Button>
                                            </Tooltip>

                                            <Tooltip
                                                placement="top"
                                                title={"Nhập dữ liệu danh sách phòng ban"}
                                                arrow
                                            >
                                                <Button
                                                    startIcon={<CloudUploadIcon />}
                                                    onClick={() => document.getElementById("fileExcel").click()}
                                                >
                                                    {!isMobile && t("general.button.importExcel")}
                                                </Button>
                                            </Tooltip>

                                            <Tooltip
                                                placement="top"
                                                title={"Tải xuống Excel danh sách phòng ban theo bộ lọc"}
                                                arrow
                                            >
                                                <Button
                                                    startIcon={<CloudDownloadIcon />}
                                                    onClick={() => handleExportExcelDepartmentData()}
                                                >
                                                    {!isMobile && t(" Xuất Excel")}
                                                </Button>
                                            </Tooltip>
                                            <Tooltip
                                                placement="top"
                                                title={"Xóa theo danh sách đã chọn"}
                                                arrow
                                            >

                                                <Button
                                                    disabled={listOnDelete?.length === 0}
                                                    startIcon={<DeleteOutlineIcon />}
                                                    onClick={handleDeleteList}
                                                >
                                                    {!isMobile && t("general.button.delete")}
                                                </Button>
                                            </Tooltip>
                                        </ButtonGroup>
                                    )}
                                    <input
                                        type="file"
                                        id="fileExcel"
                                        style={{ display: "none" }}
                                        onChange={uploadFileExcel}
                                    />
                                </Grid>

                                <Grid item xs={12}>
                                    <Grid container spacing={2} className={"flex justify-end"}>
                                        {/* <Grid item container spacing={1} xs={12} md={7} className={"flex justify-end"}>
                                            <Grid item xs={12} md={5} className={"flex justify-end"}>
                                                <GlobitsCheckBox
                                                    label={"Cần bổ nhiệm quản lý"}
                                                    name='isManager'
                                                    checked={values?.isManager}
                                                />
                                            </Grid>
                                            <Grid item container spacing={1} xs={12} md={7}
                                                className={"flex justify-end"}>
                                                <Grid item xs={5}>
                                                    <div
                                                        className="flex justify-end align-center h-100 text-label-horizon">
                                                        {t("department.organization")}
                                                    </div>
                                                </Grid>

                                                <Grid item xs={7}>
                                                    <Tooltip title={t("department.organization")} placement="top" arrow>
                                                        <GlobitsPagingAutocomplete
                                                            name="organization"
                                                            api={pagingAllOrg}
                                                        />
                                                    </Tooltip>
                                                </Grid>
                                            </Grid>
                                        </Grid> */}

                                        <Grid item xs={12} lg={6} className='flex items-center' style={{ width: "150px" }}>
                                            <div className='flex items-center h-100 flex-end pr-10'>
                                                <p className='no-wrap-text'>
                                                    <b>{t("department.organization")}:</b>
                                                </p>
                                            </div>
                                            <div style={{ width: "200px" }}>
                                                <GlobitsPagingAutocomplete
                                                    name="organization"
                                                    api={pagingAllOrg}
                                                />
                                            </div>
                                        </Grid>

                                        <Grid item xs={12} lg={6}>
                                            <div className="flex justify-between align-center">

                                                <Tooltip placement="top" title="Tìm kiếm theo từ khóa..." arrow>
                                                    <GlobitsTextField
                                                        placeholder="Tìm kiếm theo từ khóa..."
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

                            <DepartmentV2Filter
                                isOpenFilter={isOpenFilter}
                                handleFilter={handleFilter}
                                handleCloseFilter={handleCloseFilter}
                            />

                        </Grid>
                    </Form>
                );
            }}
        </Formik>
    );
}

export default memo(observer(DepartmentV2Toolbar));