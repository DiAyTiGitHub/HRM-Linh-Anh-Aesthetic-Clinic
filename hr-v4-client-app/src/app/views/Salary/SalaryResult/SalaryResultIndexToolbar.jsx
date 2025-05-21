import {
    Button,
    ButtonGroup,
    Grid,
    Tooltip
} from "@material-ui/core";
import AddIcon from "@material-ui/icons/Add";
import CloseIcon from "@material-ui/icons/Close";
import DeleteOutlineIcon from "@material-ui/icons/DeleteOutline";
import DoneAll from "@material-ui/icons/DoneAll";
import LockIcon from "@material-ui/icons/Lock";
import SearchIcon from '@material-ui/icons/Search';
import GlobitsTextField from "app/common/form/GlobitsTextField";
import { useStore } from "app/stores";
import { Form, Formik } from "formik";
import { observer } from "mobx-react";
import { memo, useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import { useHistory } from "react-router-dom";
import LocalConstants from "../../../LocalConstants";
import SalaryResultFilter from "./SalaryResultFilter";
import FilterVintageIcon from '@material-ui/icons/FilterVintage';

function SalaryResultIndexToolbar() {
    const history = useHistory();
    const { salaryResultStore, hrRoleUtilsStore } = useStore();
    const { t } = useTranslation();

    const {
        handleDeleteList,
        pagingSalaryResult,
        searchObject,
        listOnDelete,
        handleSetSearchObject,
        handleOpenCreateEdit,
        handleOpenConfirmChangeStatus,
        handleOpenCMPPopup
    } = salaryResultStore;

    async function handleFilter(values) {
        const newSearchObject = {
            ...values,
            pageIndex: 1,
        };
        handleSetSearchObject(newSearchObject);
        await pagingSalaryResult();
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

    const { isAdmin, isManager, checkAllUserRoles, isCompensationBenifit } = hrRoleUtilsStore;

    useEffect(() => {
        checkAllUserRoles()
    }, []);

    return (
        <Formik
            enableReinitialize
            initialValues={JSON.parse(JSON.stringify(searchObject))}
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
                                            <Tooltip
                                                title='Tạo mới bảng lương'
                                                placement='top' 
                                                arrow
                                            >
                                                <Button
                                                    startIcon={<AddIcon />}
                                                    type="button"
                                                    onClick={() => {
                                                        handleOpenCreateEdit();
                                                    }}
                                                >
                                                    Tạo bảng lương theo mẫu
                                                </Button>
                                            </Tooltip>


                                            <Tooltip
                                                arrow
                                                title="Tạo bảng lương hoa hồng từ dữ liệu các bảng lương hoa hồng đã lập"
                                                placement="top"
                                            >
                                                <Button
                                                    startIcon={<FilterVintageIcon />}
                                                    onClick={() =>
                                                        handleOpenCMPPopup()
                                                    }
                                                >
                                                    Tạo bảng lương hoa hồng
                                                </Button>
                                            </Tooltip>

                                            {searchObject?.status !== LocalConstants.SalaryStaffPayslipApprovalStatus?.LOCKED.value && (
                                                <Tooltip title='Đánh dấu bảng lương là Được phê duyệt' placement='top' arrow>
                                                    <Button
                                                        disabled={listOnDelete?.length <= 0}
                                                        startIcon={<DoneAll />}
                                                        onClick={() =>
                                                            handleOpenConfirmChangeStatus(
                                                                LocalConstants.SalaryStaffPayslipApprovalStatus.APPROVED
                                                                    .value
                                                            )
                                                        }
                                                    >
                                                        Duyệt
                                                    </Button>
                                                </Tooltip>
                                            )}


                                            {searchObject?.status !== LocalConstants.SalaryStaffPayslipApprovalStatus?.LOCKED.value && (
                                                <Tooltip
                                                    arrow
                                                    title="Đánh dấu bảng lương là không được phê duyệt"
                                                    placement="top"
                                                >
                                                    <Button
                                                        disabled={listOnDelete?.length <= 0}
                                                        startIcon={<CloseIcon />}
                                                        onClick={() =>
                                                            handleOpenConfirmChangeStatus(
                                                                LocalConstants.SalaryStaffPayslipApprovalStatus.NOT_APPROVED
                                                                    .value
                                                            )
                                                        }>
                                                        Không duyệt
                                                    </Button>
                                                </Tooltip>
                                            )}




                                            {searchObject?.status !== LocalConstants.SalaryStaffPayslipApprovalStatus?.LOCKED.value && (
                                                <Tooltip
                                                    arrow
                                                    title="Xóa các bảng lương đã chọn"
                                                    placement="top"
                                                >
                                                    <Button
                                                        disabled={listOnDelete?.length <= 0}
                                                        startIcon={<DeleteOutlineIcon />}
                                                        onClick={handleDeleteList}
                                                    >
                                                        {t("general.button.delete")}
                                                    </Button>
                                                </Tooltip>
                                            )}


                                        </ButtonGroup>
                                    )}
                                </Grid>

                                <Grid item xs={12}>
                                    <div className="flex justify-between align-center">
                                        <GlobitsTextField
                                            placeholder="Tìm kiếm theo mã, tên thành phần lương..."
                                            name="keyword"
                                            variant="outlined"
                                            notDelay
                                        />

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
                                            {/* <Button
                                                startIcon={<FilterListIcon className={` filterRotateIcon ${isOpenFilter && 'onRotate'}`} />}
                                                className=" d-inline-flex py-2 px-8 btnHrStyle"
                                                onClick={handleTogglePopupFilter}
                                            >
                                                Bộ lọc
                                            </Button> */}
                                        </ButtonGroup>
                                    </div>
                                </Grid>
                            </Grid>

                            <SalaryResultFilter
                                handleFilter={handleFilter}
                                handleCloseFilter={handleCloseFilter}
                                isOpenFilter={isOpenFilter}
                            />
                        </Grid>
                    </Form>
                );
            }}
        </Formik>
    );
}

export default memo(observer(SalaryResultIndexToolbar));