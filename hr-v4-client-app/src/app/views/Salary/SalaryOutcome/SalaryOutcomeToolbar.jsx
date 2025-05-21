import { observer } from "mobx-react";
import React, { memo, useState } from "react";
import DeleteOutlineIcon from "@material-ui/icons/DeleteOutline";
import { useHistory } from "react-router-dom";
import GlobitsTextField from "app/common/form/GlobitsTextField";
import AddIcon from "@material-ui/icons/Add";
import SearchIcon from '@material-ui/icons/Search';
import { Form, Formik } from "formik";
import { Grid, Button, IconButton, Tabs, Tab, AppBar, makeStyles, ButtonGroup, Tooltip, Menu, MenuItem } from "@material-ui/core";
import { useStore } from "app/stores";
import { useTranslation } from "react-i18next";
import FilterListIcon from '@material-ui/icons/FilterList';
import SalaryOutcomeFilter from "./SalaryOutcomeFilter";
import moment from "moment";
import * as Yup from "yup";
import PieChartOutlinedIcon from '@material-ui/icons/PieChartOutlined';
import LockOpenIcon from '@material-ui/icons/LockOpen';
import BorderAllIcon from '@material-ui/icons/BorderAll';
import PictureAsPdfIcon from '@material-ui/icons/PictureAsPdf';
import SalaryValueToolbarSection from "./SalaryValueToolbarSection";

function SalaryOutcomeToolbar() {
    const { t } = useTranslation();
    const history = useHistory();

    const {
        salaryOutcomeStore,
    } = useStore();

    const {
        handleSetSearchObject,
        getSalaryOutcome,
        listChosenPayslip,
        handleExportPopup,
        handlePdfPopup,
        searchObject,
        handleCalculateSalaryStaffs,
        handleOpenConfirmLockPayslipPopup,
        handleOpenChooseStaffsPopup,
        onViewSalaryResult
    } = salaryOutcomeStore;


    async function handleFilter(values) {
        const newSearchObject = {
            ...values,
            pageIndex: 1,
        };

        handleSetSearchObject(newSearchObject);
        // await handleCalculateSalaryStaffs();
        handleOpenChooseStaffsPopup();
    }

    const validationSchema = Yup.object({

    });

    // console.log("onViewSalaryResult", onViewSalaryResult);

    const isDisabledExport = !onViewSalaryResult?.salaryResultStaffs?.length;


    const [anchorEl, setAnchorEl] = useState(null);

    const handleOpenMenu = (event) => {
        setAnchorEl(event.currentTarget);
    };

    const handleCloseMenu = () => {
        setAnchorEl(null);
    };


    return (
        <Formik
            validationSchema={validationSchema}
            enableReinitialize
            initialValues={searchObject}
            onSubmit={handleFilter}
        >
            {({ resetForm, values, setFieldValue, setValues }) => {

                return (
                    <Form autoComplete="off">
                        <Grid container spacing={2}>
                            <Grid item xs={12}>
                                <ButtonGroup
                                    color="container"
                                    aria-label="outlined primary button group"
                                >
                                    <Tooltip
                                        arrow
                                        placement='top'
                                        title="Xuất Excel danh sách lương theo bộ lọc"
                                    >
                                        <Button
                                            startIcon={<BorderAllIcon />}
                                            onClick={handleOpenMenu}
                                            disabled={isDisabledExport}
                                        >
                                            Xuất danh sách
                                        </Button>
                                    </Tooltip>

                                    {
                                        Boolean(anchorEl) && (
                                            <Menu
                                                anchorEl={anchorEl}
                                                open={Boolean(anchorEl)}
                                                onClose={handleCloseMenu}
                                                anchorOrigin={{
                                                    vertical: "bottom", // Menu sẽ bắt đầu từ phía dưới của nút
                                                    horizontal: "left", // Căn trái với nút
                                                }}
                                                transformOrigin={{
                                                    vertical: "top", // Điểm gốc của menu là phía trên
                                                    horizontal: "left", // Căn trái
                                                }}
                                            >
                                                <MenuItem
                                                    className='flex items-center justify-center'
                                                    onClick={() => {
                                                        handleExportPopup(true);
                                                        handleCloseMenu();
                                                    }}
                                                    disabled={isDisabledExport}
                                                >
                                                    Xuất danh sách Excel
                                                </MenuItem>

                                                <MenuItem
                                                    className='flex items-center justify-center'
                                                    onClick={() => {
                                                        handlePdfPopup(true);
                                                        handleCloseMenu();
                                                    }}
                                                    disabled={isDisabledExport}
                                                >
                                                    Xuất danh sách PDF
                                                </MenuItem>
                                            </Menu>
                                        )
                                    }

                                    {/* <Tooltip
                                        arrow
                                        placement='top'
                                        title="Xuất Excel danh sách lương theo bộ lọc"
                                    >
                                        <Button
                                            startIcon={<BorderAllIcon />}
                                            onClick={handleOpenMenu}
                                            disabled={isDisabledExport}
                                        >
                                            Giá trị lương
                                        </Button>
                                    </Tooltip> */}

                                    <SalaryValueToolbarSection />



                                    {listChosenPayslip?.length > 0 && (
                                        <Tooltip
                                            arrow
                                            placement='top'
                                            title="Phiếu lương đã chọn sẽ được khóa lại và không thể sửa đổi"
                                        >
                                            <Button
                                                onClick={handleOpenConfirmLockPayslipPopup}
                                                startIcon={<LockOpenIcon />}
                                            >
                                                Khóa phiếu lương
                                            </Button>
                                        </Tooltip>
                                    )}


                                </ButtonGroup>

                                
                            </Grid>

                            <Grid item xs={12} className="pt-0">

                                <SalaryOutcomeFilter
                                    handleFilter={handleFilter}
                                />

                            </Grid>
                        </Grid>

                    </Form>
                );
            }}
        </Formik>
    );
}

export default memo(observer(SalaryOutcomeToolbar));