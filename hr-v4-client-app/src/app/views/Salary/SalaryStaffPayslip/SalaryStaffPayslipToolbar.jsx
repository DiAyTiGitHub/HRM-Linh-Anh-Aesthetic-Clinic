import { Button, ButtonGroup, Grid, Tooltip } from "@material-ui/core";
import DeleteOutlineIcon from "@material-ui/icons/DeleteOutline";
import FilterListIcon from "@material-ui/icons/FilterList";
import SearchIcon from "@material-ui/icons/Search";
import GlobitsTextField from "app/common/form/GlobitsTextField";
import LocalConstants from "app/LocalConstants";
import { useStore } from "app/stores";
import { Form, Formik } from "formik";
import { observer } from "mobx-react";
import { memo, useState } from "react";
import { useTranslation } from "react-i18next";
import { useHistory } from "react-router-dom";
import SalaryStaffPayslipFilter from "./SalaryStaffPayslipFilter";
import MoneyOffIcon from '@material-ui/icons/MoneyOff';
import AttachMoneyIcon from '@material-ui/icons/AttachMoney';

function SalaryStaffPayslipToolbar() {
    const history = useHistory();

    const {
        salaryStaffPayslipStore,
        hrRoleUtilsStore

    } = useStore();

    const { t } = useTranslation();

    const {
        pagingSalaryStaffPayslip,
        searchObject,
        listOnDelete,
        handleSetSearchObject,
        handleDeleteList,
        handleOpenConfirmChangeStatus,
        handleOpenPopupSalary,
        openViewSalaryBoard,
        downloadSalaryResultStaffItemImportTemplate,
        uploadSalaryResultStaffItemImportTemplate,
    } = salaryStaffPayslipStore;

    const {
        isAdmin,
        isManager,
        checkHasShiftAssignmentPermission,
        isCompensationBenifit
    } = hrRoleUtilsStore;

    async function handleFilter(values) {
        const newSearchObject = {
            ...values,
            pageIndex: 1,
        };

        handleSetSearchObject(newSearchObject);

        await pagingSalaryStaffPayslip();
    }

    const [isOpenFilter, setIsOpenFilter] = useState(true);

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

    let canApproveOrUnApprove = false;
    if (isCompensationBenifit) {
        canApproveOrUnApprove = true;
    }

    return (
        <Formik enableReinitialize initialValues={searchObject} onSubmit={handleFilter}>
            {({ resetForm, values, setFieldValue, setValues }) => {
                return (
                    <Form autoComplete='off'>
                        <Grid item xs={12}>
                            <Grid container spacing={2}>
                                {
                                    canApproveOrUnApprove && (
                                        <Grid item xs={12}>

                                            <ButtonGroup color='container' aria-label='outlined primary button group'>
                                                {/* <Tooltip title='Tính lương cho nhân viên' placement='top' arrow>
                                                    <Button
                                                        startIcon={<MonetizationOnIcon />}
                                                        onClick={() => handleOpenPopupSalary()}>
                                                        Tính lương
                                                    </Button>
                                                </Tooltip> */}

                                                {/* <Tooltip
                                                    title="Tải mẫu excel nhập giá trị lương"
                                                    placement="top"
                                                    arrow
                                                >
                                                    <Button
                                                        startIcon={<GetAppIcon />}
                                                        onClick={() => downloadSalaryResultStaffItemImportTemplate()}
                                                    >
                                                        Tải mẫu giá trị lương
                                                    </Button>
                                                </Tooltip>
    
                                                <Tooltip
                                                    title="Nhập mẫu excel giá trị lương của nhân viên"
                                                    placement="top"
                                                    arrow
                                                >
                                                    <Button
                                                        startIcon={<CloudUploadIcon />}
                                                        onClick={() => document.getElementById("fileExcel").click()}
                                                    >
                                                        {t("general.button.importExcel")}
                                                    </Button>
    
                                                </Tooltip>
    
                                                <input
                                                    type="file"
                                                    id="fileExcel"
                                                    style={{ display: "none" }}
                                                    onChange={handleFileChange}
                                                /> */}

                                                {/* <Button
                                                    startIcon={<TableChartIcon />}
                                                    onClick={() => handeOpenViewSalaryBoard()}
    
                                                >
                                                    Xem bảng lương
                                                </Button> */}

                                                {
                                                    canApproveOrUnApprove && (
                                                        <Tooltip
                                                            title='Đánh dấu đã chi trả phiếu lương đã chọn'
                                                            placement='top'
                                                            arrow
                                                        >
                                                            <Button
                                                                disabled={listOnDelete?.length <= 0}
                                                                startIcon={<AttachMoneyIcon />}
                                                                onClick={() =>
                                                                    handleOpenConfirmChangeStatus(
                                                                        LocalConstants.StaffPayslipsPaidStatus.PAID
                                                                            .value
                                                                    )
                                                                }>
                                                                Đã chi trả
                                                            </Button>
                                                        </Tooltip>
                                                    )
                                                }


                                                {
                                                    canApproveOrUnApprove && (
                                                        <Tooltip title='Đánh dấu chưa chi trả phiếu lương đã chọn'
                                                            placement='top'
                                                            arrow
                                                        >
                                                            <Button
                                                                disabled={listOnDelete?.length <= 0}
                                                                startIcon={<MoneyOffIcon />}
                                                                onClick={() =>
                                                                    handleOpenConfirmChangeStatus(
                                                                        LocalConstants.StaffPayslipsPaidStatus.UNPAID
                                                                            .value
                                                                    )
                                                                }>
                                                                Chưa chi trả
                                                            </Button>
                                                        </Tooltip>
                                                    )
                                                }


                                                {
                                                    canApproveOrUnApprove && (
                                                        <Button
                                                            disabled={listOnDelete?.length <= 0}
                                                            startIcon={<DeleteOutlineIcon />}
                                                            onClick={handleDeleteList}>
                                                            {t("general.button.delete")}
                                                        </Button>
                                                    )
                                                }


                                            </ButtonGroup>

                                        </Grid>
                                    )
                                }


                                <Grid item xs={12}>
                                    <Grid container spacing={1}>
                                        <Grid item xs={12} sm={3} lg={2}>
                                            {/* <div className="flex justify-end align-center h-100 text-label-horizon">
                                                Ngày nhận việc:
                                            </div> */}
                                        </Grid>

                                        <Grid item xs={12} sm={3} lg={3}>
                                            {/* <Tooltip title="Ngày nhận việc" placement="top">
                                                <GlobitsDateTimePicker
                                                    // label="Đến ngày"
                                                    name="onboardDate"
                                                    placeholder="Ngày nhận việc"
                                                    disabled={values?.onboardDateFrom || values?.onboardDateTo}
                                                />
                                            </Tooltip> */}
                                        </Grid>

                                        <Grid item xs={12} sm={6} lg={7}>
                                            <div className='flex justify-between align-center'>
                                                <Tooltip placement='top' title='Tìm kiếm theo mã, tên nhân viên...'>
                                                    <GlobitsTextField
                                                        placeholder='Tìm kiếm theo mã, tên nhân viên......'
                                                        name='keyword'
                                                        variant='outlined'
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
                                                    className='filterButtonV4'
                                                    color='container'
                                                    aria-label='outlined primary button group'>
                                                    <Button
                                                        startIcon={<SearchIcon className={``} />}
                                                        className='ml-8 d-inline-flex py-2 px-8 btnHrStyle'
                                                        type='submit'>
                                                        Tìm kiếm
                                                    </Button>
                                                    <Button
                                                        startIcon={
                                                            <FilterListIcon
                                                                className={` filterRotateIcon ${isOpenFilter && "onRotate"
                                                                    }`}
                                                            />
                                                        }
                                                        className=' d-inline-flex py-2 px-8 btnHrStyle'
                                                        onClick={handleTogglePopupFilter}>
                                                        Bộ lọc
                                                    </Button>
                                                </ButtonGroup>
                                            </div>
                                        </Grid>
                                    </Grid>
                                </Grid>
                            </Grid>

                            <SalaryStaffPayslipFilter
                                handleFilter={handleFilter}
                                isOpenFilter={isOpenFilter}
                                handleCloseFilter={handleCloseFilter}
                            />
                        </Grid>
                    </Form >
                );
            }}
        </Formik >
    );
}

export default memo(observer(SalaryStaffPayslipToolbar));
