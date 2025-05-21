import { observer } from "mobx-react";
import React, { memo, useState } from "react";
import DeleteOutlineIcon from "@material-ui/icons/DeleteOutline";
import { useHistory } from "react-router-dom";
import GlobitsTextField from "app/common/form/GlobitsTextField";
import AddIcon from "@material-ui/icons/Add";
import SearchIcon from '@material-ui/icons/Search';
import { Form, Formik } from "formik";
import { Grid, Button, IconButton, Tabs, Tab, AppBar, makeStyles, ButtonGroup, Tooltip } from "@material-ui/core";
import { useStore } from "app/stores";
import { useTranslation } from "react-i18next";
import MonetizationOnIcon from '@material-ui/icons/MonetizationOn';
import StaffSocialInsuranceFilter from "./StaffHasSocialInsuranceFilter";
import FilterListIcon from '@material-ui/icons/FilterList';
import LocalConstants from "app/LocalConstants";
import BorderAllIcon from '@material-ui/icons/BorderAll';
import GlobitsDateTimePicker from "app/common/form/GlobitsDateTimePicker";
import LibraryBooksIcon from '@material-ui/icons/LibraryBooks';
import ChromeReaderModeIcon from '@material-ui/icons/ChromeReaderMode';
import ArrowDropDownIcon from '@material-ui/icons/ArrowDropDown';
import StaffHasSocialInsuranceFilter from "./StaffHasSocialInsuranceFilter";

function StaffHasSocialInsuranceIndexToolbar() {
    const { t } = useTranslation();
    const history = useHistory();
    const { staffHasSocialInsuranceStore } = useStore();

    const {
        pagingStaffLabourAgreement,
        searchObject,
        handleSetSearchObject,
        handleExportExcelByFilter,
        isAdmin,
        handleOpenChooseExportType
    } = staffHasSocialInsuranceStore;

    async function handleFilter(values) {
        const newSearchObject = {
            ...values,
            pageIndex: 1,
        };
        handleSetSearchObject(newSearchObject);
        await pagingStaffLabourAgreement();
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
        if (isOpenFilter) {
            handleCloseFilter();
        } else {
            handleOpenFilter();
        }
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
                                    <ButtonGroup
                                        color="container"
                                        className="filterButtonV4"
                                        aria-label="outlined primary button group"
                                    >
                                        {/* <Tooltip title="Chức năng khác" placement="top">
                                                <Button
                                                    className="px-4"
                                                    type="button"
                                                    onClick={handleToggleBtnList}
                                                >
                                                    <ArrowDropDownIcon
                                                        className={` filterRotateIcon ${isOpenBtnList && 'onRotate'}`}
                                                    />
                                                </Button>
                                            </Tooltip> */}


                                        <Tooltip title="Kết xuất excel danh sách nhân viên đóng BHXH theo bộ lọc hiện tại" placement="top">
                                            <Button
                                                startIcon={<LibraryBooksIcon />}
                                                type="button"
                                                onClick={() => {
                                                    handleExportExcelByFilter();
                                                }}
                                            >
                                                Xuất Excel NV đóng BHXH
                                            </Button>
                                        </Tooltip>

                                        <Tooltip title="Xuất danh sách đóng BHXH theo nghiệp vụ" placement="top">
                                            <Button
                                                type="button"
                                                startIcon={<ChromeReaderModeIcon
                                                // className={` filterRotateIcon ${isOpenBtnList && 'onRotate'}`}
                                                />}
                                                onClick={handleOpenChooseExportType}
                                            >

                                                Xuất Excel nghiệp vụ
                                            </Button>
                                        </Tooltip>


                                    </ButtonGroup>
                                </Grid>

                                <Grid item xs={12}>
                                    <Grid container spacing={1}>
                                        <Grid item xs={12}>
                                            <div className="flex justify-end align-center">
                                                {/* <Tooltip placement="top" title="Tìm kiếm theo mã, tên nhân viên...">
                                                <GlobitsTextField
                                                    placeholder="Tìm kiếm theo mã, tên nhân viên......"
                                                    name="keyword"
                                                    variant="outlined"
                                                    notDelay
                                                />
                                            </Tooltip> */}
                                                <div className="flex flex-center w-100">
                                                    <Grid container spacing={2}>
                                                        <Grid item xs={12} sm={6} md={4}>
                                                            <div className="flex items-center h-100 flex-end" >
                                                                <p className="no-wrap-text">
                                                                    <b>
                                                                        Từ ngày:
                                                                    </b>
                                                                </p>
                                                            </div>
                                                        </Grid>

                                                        <Grid item xs={12} sm={6} md={8}>
                                                            <GlobitsDateTimePicker
                                                                // label="Từ ngày"
                                                                name="insuranceStartDate"
                                                            // placeholder="Ngày từ"
                                                            />
                                                        </Grid>
                                                    </Grid>
                                                </div>

                                                <div className="flex flex-center w-100 mr-5">
                                                    <Grid container spacing={2}>
                                                        <Grid item xs={12} sm={6} md={4}>
                                                            <div className="flex items-center h-100 flex-end" >
                                                                <p className="no-wrap-text">
                                                                    <b>
                                                                        Đến ngày:
                                                                    </b>
                                                                </p>
                                                            </div>
                                                        </Grid>

                                                        <Grid item xs={12} sm={6} md={8}>
                                                            <GlobitsDateTimePicker
                                                                // label="Đến ngày"
                                                                name="insuranceEndDate"
                                                            // placeholder="Đến ngày"
                                                            />
                                                        </Grid>
                                                    </Grid>
                                                </div>

                                                <Tooltip placement="top" title="Tìm kiếm theo mã, tên nhân viên...">
                                                    <GlobitsTextField
                                                        placeholder="Tìm kiếm theo mã, tên nhân viên......"
                                                        name="keyword"
                                                        variant="outlined"
                                                        notDelay
                                                    // InputProps={{
                                                    //     endAdornment: (
                                                    //         <IconButton className="py-0 px-4" aria-label="search" type="submit">
                                                    //             <SearchIcon />
                                                    //         </IconButton>
                                                    //     ),
                                                    // }}
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
                                                        startIcon={<FilterListIcon className={` filterRotateIcon ${isOpenFilter && 'onRotate'}`} />}
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

                            <StaffHasSocialInsuranceFilter
                                handleFilter={handleFilter}
                                isOpenFilter={isOpenFilter}
                                handleCloseFilter={handleCloseFilter}
                            />

                        </Grid>
                    </Form>
                );
            }}
        </Formik>
    );
}

export default memo(observer(StaffHasSocialInsuranceIndexToolbar));