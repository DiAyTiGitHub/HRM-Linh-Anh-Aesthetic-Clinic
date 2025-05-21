import { observer } from "mobx-react";
import React, { memo, useState } from "react";
import DeleteOutlineIcon from "@material-ui/icons/DeleteOutline";
import { useHistory } from "react-router-dom";
import GlobitsTextField from "app/common/form/GlobitsTextField";
import AddIcon from "@material-ui/icons/Add";
import SearchIcon from '@material-ui/icons/Search';
import { Form, Formik } from "formik";
import { Button, ButtonGroup, Grid, Tooltip } from "@material-ui/core";
import { useStore } from "app/stores";
import { useTranslation } from "react-i18next";
import StaffSocialInsuranceFilter from "./StaffAdvancePaymentFilter";
import FilterListIcon from '@material-ui/icons/FilterList';
import LocalConstants from "app/LocalConstants";
import CloseIcon from '@material-ui/icons/Close';
import DoneAll from '@material-ui/icons/DoneAll';

function StaffAdvancePaymentIndexToolbar() {
    const history = useHistory();
    const { staffAdvancePaymentStore, hrRoleUtilsStore } = useStore();
    const { t } = useTranslation();

    const {
        isAdmin,
        isManager,
        isCompensationBenifit
    } = hrRoleUtilsStore;

    const {
        pagingStaffAdvancePayment,
        searchObject,
        listOnDelete,
        handleSetSearchObject,
        handleOpenCreateEdit,
        handleDeleteList,
        handleOpenConfirmChangeStatus
    } = staffAdvancePaymentStore;

    async function handleFilter(values) {
        const newSearchObject = {
            ...values,
            pageIndex: 1,
        };
        handleSetSearchObject(newSearchObject);
        await pagingStaffAdvancePayment();
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
                                <Grid item xs={12} lg={6}>

                                    <ButtonGroup
                                        color="container"
                                        aria-label="outlined primary button group"
                                    >
                                        <Tooltip
                                            title="Thêm mới khoản ứng lương"
                                            arrow
                                            placement="top"
                                        >
                                            <Button
                                                startIcon={<AddIcon />}
                                                type="button"
                                                onClick={() => {
                                                    handleOpenCreateEdit();
                                                }}
                                            // disabled={!isAdmin && !isManager}
                                            >
                                                Thêm mới
                                            </Button>
                                        </Tooltip>

                                        {canApproveOrUnApprove && (
                                            <Tooltip
                                                title="Phê duyệt khoản ứng lương"
                                                arrow
                                                placement="top"
                                            >
                                                <Button
                                                    disabled={listOnDelete?.length <= 0}
                                                    startIcon={<DoneAll />}
                                                    onClick={() => handleOpenConfirmChangeStatus(LocalConstants.StaffAdvancePaymentApprovalStatus.APPROVED.value)}
                                                >
                                                    Duyệt
                                                </Button>
                                            </Tooltip>
                                        )}


                                        {canApproveOrUnApprove && (
                                            <Tooltip
                                                title="Không phê duyệt khoản ứng lương"
                                                arrow
                                                placement="top"
                                            >
                                                <Button
                                                    disabled={listOnDelete?.length <= 0}
                                                    startIcon={<CloseIcon />}
                                                    onClick={() => handleOpenConfirmChangeStatus(LocalConstants.StaffAdvancePaymentApprovalStatus.NOT_APPROVED.value)}

                                                >
                                                    Không duyệt
                                                </Button>
                                            </Tooltip>
                                        )}


                                        {canApproveOrUnApprove && (
                                            <Tooltip
                                                title="Xóa yêu cầu ứng lương"
                                                arrow
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


                                </Grid>

                                <Grid item xs={12} lg={6}>
                                    <Grid container spacing={1}>
                                        <Grid item xs={12} sm={2}>

                                        </Grid>

                                        <Grid item xs={12} sm={10}>
                                            <div className="flex justify-between align-center">
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

                            <StaffSocialInsuranceFilter
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

export default memo(observer(StaffAdvancePaymentIndexToolbar));