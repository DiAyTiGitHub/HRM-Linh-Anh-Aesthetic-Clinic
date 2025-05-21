import { observer } from "mobx-react";
import React, { memo, useState } from "react";
import { Form, Formik } from "formik";
import { Grid, Button, ButtonGroup, Tooltip, Menu, MenuItem } from "@material-ui/core";
import { useStore } from "app/stores";
import PayrollFilter from "./PayrollFilter";
import BorderAllIcon from '@material-ui/icons/BorderAll';
import SalaryValueToolbarSection from "./SalaryValueToolbarSection";
import ArrowBackIcon from '@material-ui/icons/ArrowBack';
import ConstantList from "app/appConfig";
import { useHistory } from "react-router-dom";
import DeleteOutlineIcon from "@material-ui/icons/DeleteOutline";
import { useTranslation } from "react-i18next";
import GlobitsConfirmationDialog from "app/common/GlobitsConfirmationDialog";
import WarningIcon from '@material-ui/icons/Warning';
import ExposureIcon from '@material-ui/icons/Exposure';
import LockOutlinedIcon from '@material-ui/icons/LockOutlined';
import LockOpenOutlinedIcon from '@material-ui/icons/LockOpenOutlined';

function PayrollToolbar() {
    const {
        payrollStore
    } = useStore();

    const {
        handleExportPopup,
        handleRecalculation,
        searchObject,
        onViewSalaryResult,
        handleSetSearchObj,
        handleDeleteList,
        orphanedPayslipsCount,
        handleViewOrphanedPayslipsPopup,
        listSalaryResultStaffs,
        pagingSalaryResultStaff,
        getListSumSalaryResultStaff,
        handleOpenLockPayrollPopup,
        handleOpenUnlockPayrollPopup
    } = payrollStore;

    const history = useHistory();

    const isDisabledExport = !listSalaryResultStaffs?.length;

    const [anchorEl, setAnchorEl] = useState(null);
    const { t } = useTranslation();

    const handleOpenMenu = (event) => {
        setAnchorEl(event.currentTarget);
    };

    const handleCloseMenu = () => {
        setAnchorEl(null);
    };

    function handleReturn() {
        const redirectUrl = ConstantList.ROOT_PATH + `salary/salary-result`;
        history.push(redirectUrl);
    }

    async function handleSearchPayroll(values) {
        try {
            const newSO = {
                ...searchObject,
                ...values,
            };
            handleSetSearchObj(newSO);
            await pagingSalaryResultStaff(values);
            await getListSumSalaryResultStaff(values);
        }
        catch (error) {
            console.error(error);
        }

    }


    return (
        <Formik
            enableReinitialize
            initialValues={JSON.parse(JSON.stringify(searchObject))}
            onSubmit={handleSearchPayroll}
        >
            {({ resetForm, values, setFieldValue, setValues }) => {
                return (
                    <Form autoComplete="off">
                        <Grid container spacing={2}>
                            <Grid item xs={12} className="justify-between">
                                <div className="flex justify-between">
                                    <ButtonGroup
                                        color="container"
                                        aria-label="outlined primary button group"
                                    >
                                        <Button
                                            type="button"
                                            onClick={handleReturn}
                                        >
                                            <ArrowBackIcon className="mr-6" />
                                            Quay lại
                                        </Button>

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

                                                    {/* <MenuItem
                                                    className='flex items-center justify-center' 
                                                    onClick={() => {
                                                        handlePdfPopup(true);
                                                        handleCloseMenu();
                                                    }}
                                                    disabled={isDisabledExport}
                                                >
                                                    Xuất danh sách PDF
                                                </MenuItem> */}
                                                </Menu>
                                            )
                                        }

                                        {
                                            !onViewSalaryResult?.isLocked && (
                                                <Tooltip
                                                    arrow
                                                    placement="top"
                                                    disabled={isDisabledExport}
                                                    title="Tính toán lại toàn bộ bảng lương"
                                                >
                                                    <Button
                                                        startIcon={<ExposureIcon />}
                                                        onClick={(handleRecalculation)}
                                                    >
                                                        Tính toán lại
                                                    </Button>
                                                </Tooltip>
                                            )
                                        }


                                        {!onViewSalaryResult?.isLocked && (
                                            <SalaryValueToolbarSection />
                                        )}




                                        {!onViewSalaryResult?.isLocked && (
                                            <Tooltip
                                                arrow
                                                placement="top"
                                                title="Khóa bảng lương hiện tại"
                                            >
                                                <Button
                                                    startIcon={<LockOutlinedIcon />}
                                                    onClick={handleOpenLockPayrollPopup}
                                                >
                                                    Khóa
                                                </Button>
                                            </Tooltip>
                                        )}

                                        {onViewSalaryResult?.isLocked && (
                                            <Tooltip
                                                arrow
                                                placement="top"
                                                title="Hủy khóa bảng lương hiện tại"
                                            >
                                                <Button
                                                    startIcon={<LockOpenOutlinedIcon />}
                                                    onClick={handleOpenUnlockPayrollPopup}
                                                >
                                                    Hủy khóa
                                                </Button>
                                            </Tooltip>
                                        )}

                                        {!onViewSalaryResult?.isLocked && (
                                            <Tooltip
                                                arrow
                                                placement="top"
                                                title="Xóa bảng lương hiện tại"
                                            >
                                                <Button
                                                    startIcon={<DeleteOutlineIcon />}
                                                    onClick={handleDeleteList}
                                                >
                                                    {t("general.button.delete")}
                                                </Button>
                                            </Tooltip>
                                        )}



                                        {/* {listChosenPayslip?.length > 0 && (
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
                                    )} */}


                                    </ButtonGroup>

                                    {
                                        (orphanedPayslipsCount > 0 && !onViewSalaryResult?.isLocked) && (
                                            <Tooltip
                                                placement="top"
                                                arrow
                                                title="Tồn tại phiếu lương có thể được tổng hợp vào bảng lương hiện tại"
                                            >
                                                <div
                                                    className="flex flex-middle hyperLink cursor-pointer"
                                                    onClick={handleViewOrphanedPayslipsPopup}
                                                >
                                                    <WarningIcon className="mr-4" />
                                                    <span className="">
                                                        Tồn tại phiếu lương chưa được tổng hợp
                                                    </span>
                                                </div>
                                            </Tooltip>
                                        )
                                    }

                                </div>


                            </Grid>

                            <Grid item xs={12} className="pt-0">

                                <PayrollFilter
                                    handleFilter={handleSetSearchObj}
                                />

                            </Grid>

                        </Grid>

                    </Form>
                );
            }}
        </Formik>
    );
}

export default memo(observer(PayrollToolbar));


