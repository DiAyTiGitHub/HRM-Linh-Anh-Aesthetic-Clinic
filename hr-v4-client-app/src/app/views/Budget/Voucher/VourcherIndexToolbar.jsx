import {observer} from "mobx-react";
import React, {memo, useState} from "react";
import DeleteOutlineIcon from "@material-ui/icons/DeleteOutline";
import {useHistory} from "react-router-dom";
import GlobitsTextField from "app/common/form/GlobitsTextField";
import AddIcon from "@material-ui/icons/Add";
import SearchIcon from '@material-ui/icons/Search';
import {Form, Formik} from "formik";
import {Grid, Button, IconButton, Tabs, Tab, AppBar, makeStyles, ButtonGroup, Tooltip} from "@material-ui/core";
import {useStore} from "app/stores";
import {useTranslation} from "react-i18next";
import FilterListIcon from '@material-ui/icons/FilterList';
import RemoveIcon from "@material-ui/icons/Remove";
import BorderAllIcon from "@material-ui/icons/BorderAll";
import DeleteIcon from "@material-ui/icons/Delete";
import VoucherForm from "./VoucherForm";
import useMediaQuery from "@material-ui/core/useMediaQuery";
import {useTheme} from "@material-ui/core/styles";
import VourcherFilter from "./VourcherFilter";

function VourcherIndexToolbar() {
    const {voucherStore, budgetStore} = useStore();
    const {t} = useTranslation();
    const theme = useTheme();
    const isMobile = useMediaQuery(theme.breakpoints.down("sm"));

    const {
        search,
        searchObject,
        handleAddVoucher,
        handleSetSearchObject,
        shouldOpenEditorDialog,
        selectedVoucherList,
        handleDeleteList,
        handleExportExcelByFilter
    } = voucherStore;

    async function handleFilter(values) {
        const newSearchObject = {
            ...values,
            pageIndex: 1,

        };
        handleSetSearchObject(newSearchObject);
        await budgetStore.getBudgetSummaryBalance({
            "budget": values?.budget,
            "fromDate": values?.fromDate,
            "toDate": values?.toDate,
        });
        await search();
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
            {({resetForm, values, setFieldValue, setValues}) => {

                return (
                    <Form autoComplete="off">
                        <Grid item xs={12}>
                            <Grid container spacing={2}>
                                <Grid item xs={12} md={6}>
                                    <ButtonGroup>
                                        <Button
                                            className='btn btn-primary d-inline-flex mr-2'
                                            startIcon={<AddIcon/>}
                                            onClick={() => handleAddVoucher(1)} // Truyền tham số để phân biệt khoản thu
                                        >
                                            {!isMobile && t("general.button.add_income")} {/* Sử dụng chuỗi dịch */}
                                        </Button>

                                        {/* Nút Thêm những khoản chi */}
                                        <Button
                                            className='btn btn-secondary d-inline-flex mr-2'
                                            startIcon={<RemoveIcon/>}
                                            onClick={() => handleAddVoucher(-1)} // Truyền tham số để phân biệt khoản chi
                                        >
                                            {!isMobile && t("general.button.add_expense")} {/* Sử dụng chuỗi dịch */}
                                        </Button>
                                        <Button
                                            disabled={selectedVoucherList.length <= 0}
                                            startIcon={<DeleteIcon/>}
                                            onClick={() => handleDeleteList()}>
                                            {!isMobile && t("general.button.delete")}
                                        </Button>
                                        <Button
                                            variant="outlined"
                                            startIcon={<BorderAllIcon/>}
                                            type="button"
                                            onClick={() => {
                                                handleExportExcelByFilter();
                                            }}
                                        >
                                            {!isMobile && t("general.button.export_excel")} {/* Sử dụng chuỗi dịch */}
                                        </Button>
                                    </ButtonGroup>
                                </Grid>

                                <Grid item xs={12} md={6}>
                                    <Grid container spacing={1}>
                                        <Grid item xs={12} sm={2}>

                                        </Grid>

                                        <Grid item xs={12} sm={10}>
                                            <div className="flex justify-between align-center">
                                                <Tooltip placement="top" title="Tìm kiếm theo mã, tên nhân viên...">
                                                    <GlobitsTextField
                                                        placeholder="Tìm kiếm theo mã hóa đơn, tên ngân sách ..."
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
                                                        startIcon={<SearchIcon className={``}/>}
                                                        className="ml-8 d-inline-flex py-2 px-8 btnHrStyle"
                                                        type="submit"
                                                    >
                                                        Tìm kiếm
                                                    </Button>
                                                    <Button
                                                        startIcon={<FilterListIcon
                                                            className={` filterRotateIcon ${isOpenFilter && 'onRotate'}`}/>}
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

                            <VourcherFilter
                                handleFilter={handleFilter}
                                isOpenFilter={isOpenFilter}
                                handleCloseFilter={handleCloseFilter}
                            />

                            {shouldOpenEditorDialog && <VoucherForm open={shouldOpenEditorDialog}/>}
                        </Grid>
                    </Form>
                );
            }}
        </Formik>
    );
}

export default memo(observer(VourcherIndexToolbar));