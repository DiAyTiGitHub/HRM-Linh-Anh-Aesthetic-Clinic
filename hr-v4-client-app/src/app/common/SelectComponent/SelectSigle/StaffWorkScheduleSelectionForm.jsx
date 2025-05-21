import { Button, ButtonGroup, Collapse, Grid, Tooltip } from "@material-ui/core";
import GlobitsDateTimePicker from "app/common/form/GlobitsDateTimePicker";
import { formatDate, getCheckInAndCheckOutTimeOfShiftWork, getDate } from "app/LocalFunction";
import { useStore } from "app/stores";
import { observer } from "mobx-react";
import ObjectSelectorSection from "./ObjectSelectorSection";

import SearchIcon from "@material-ui/icons/Search";
import { useFormikContext } from "formik";
import React, { useEffect, useState } from "react";
import FilterListIcon from "@material-ui/icons/FilterList";
import { useTranslation } from "react-i18next";
import GlobitsPagingAutocompleteV2 from "../../form/GlobitsPagingAutocompleteV2";
import { pagingAllOrg } from "../../../views/Organization/OrganizationService";
import { pagingHasPermissionDepartments, pagingStaff } from "../../../views/HumanResourcesInformation/StaffService";
import { pagingPositionTitle } from "../../../views/PositionTitle/PositionTitleService";
import RotateLeftIcon from "@material-ui/icons/RotateLeft";
import HighlightOffIcon from "@material-ui/icons/HighlightOff";
import GlobitsTextField from "../../form/GlobitsTextField";

function StaffWorkScheduleSelectionForm(props) {
    const {
        label,
        placeholder = "Chưa chọn ca làm việc",
        required = false,
        disabled = false,
        disabledTextFieldOnly = false,
        name = "staffWorkSchedule",
        handleAfterSubmit,
        readOnly,
        isFutureDate = false,
    } = props;

    const {
        staffWorkScheduleStore,
        hrRoleUtilsStore

    } = useStore();

    const {
        listStaffWorkSchedules,
        totalPages,
        totalElements,
        searchObject,
        handleChangePage,
        setPageSize,
        pagingStaffWorkSchedule,
        resetStore,
        handleSetSearchObject,
        intactSearchObject
    } = staffWorkScheduleStore;

    // Define columns for staff selection
    let columns = [
        {
            title: "Mã nhân viên",
            field: "staff.staffCode",
            align: "left",
        },
        {
            title: "Nhân viên",
            field: "staff.displayName",
            width: "20%",
            align: "left",
        },
        {
            title: "Ngày làm việc",
            field: "workingDate",
            width: "10%",
            align: "left",
            render: (row) => <span className='px-2'>{getDate(row?.workingDate)}</span>,
        },
        {
            title: "Ca làm việc",
            width: "30%",
            align: "left",
            field: "shiftWork.name",
            render: (rowData) => {
                const { shiftWork } = rowData;
                const { name, code, totalHours, timePeriods } = shiftWork || {};

                const { checkInTime, checkOutTime } = getCheckInAndCheckOutTimeOfShiftWork(shiftWork);

                return (
                    <div className='px-4'>
                        {name && (
                            <p className='m-0 pb-4'>
                                <span>
                                    <strong>{`${name} - ${code}`} </strong>
                                </span>
                            </p>
                        )}

                        {checkInTime && checkOutTime && (
                            <p className='m-0 no-wrap-text'>
                                {`${formatDate("HH:mm", checkInTime)} - ${formatDate("HH:mm", checkOutTime)}`}
                            </p>
                        )}
                    </div>
                );
            },
        },
        {
            title: "Người phân ca",
            field: "coordinator",
            width: "18%",
            align: "center",
            render: (row) => (
                <span className='px-2'>
                    {`${row?.coordinator?.displayName || ""} - ${row?.coordinator?.staffCode || ""}`}
                </span>
            ),
        },
    ];

    // Function to handle after staff selection
    function handleStaffSelected(staff) {
        if (typeof handleAfterSubmit === "function") {
            handleAfterSubmit(staff);
        } else {
            console.log(staff);
        }
    }

    // Đây là hàm getOptionLabel sửa lại để hiển thị đúng dữ liệu đã chọn
    const getWorkScheduleLabel = (option) => {
        if (!option) return "";

        // Kiểm tra xem option có dữ liệu shiftWork hay không
        const shiftName = option?.shiftWork?.name || "";
        const shiftCode = option?.shiftWork?.code || "";

        // Kiểm tra xem option có dữ liệu staff hay không
        const staffName = option?.staff?.displayName || "";
        const staffCode = option?.staff?.staffCode || "";

        // Kiểm tra xem option có ngày làm việc hay không
        const workDate = option?.workingDate ? formatDate("DD/MM/YYYY", option.workingDate) : "";

        // Tạo string hiển thị với đầy đủ thông tin
        if (shiftName && staffName && workDate) {
            return `${staffName} - ${shiftName} (${workDate})`;
        }

        // Hoặc chỉ với thông tin ca làm việc và ngày
        if (shiftName && workDate) {
            return `${shiftName} (${workDate})`;
        }

        // Hoặc chỉ với thông tin nhân viên
        if (staffName && staffCode) {
            return `${staffName} - ${staffCode}`;
        }

        // Fallback nếu không có đủ thông tin
        return shiftName || staffName || "";
    };

    useEffect(() => {
        let newValue = {
            ...searchObject,
            isFutureDate: isFutureDate
        }
        intactSearchObject.isFutureDate = isFutureDate;
        handleSetSearchObject(newValue)
    }, []);

    return (
        <ObjectSelectorSection
            popupSize="lg"
            name={name}
            label={label || "Ca làm việc"}
            placeholder={placeholder}
            required={required}
            disabled={disabled}
            disabledTextFieldOnly={disabledTextFieldOnly}
            fetchDataFunction={pagingStaffWorkSchedule}
            resetDataFunction={resetStore}
            fetchAutocompleteFunction={pagingStaffWorkSchedule}
            columns={columns}
            dataList={listStaffWorkSchedules}
            totalElements={totalElements}
            totalPages={totalPages}
            handleChangePage={handleChangePage}
            setPageSize={setPageSize}
            searchObject={searchObject}
            handleSetSearchObject={handleSetSearchObject}
            popupTitle='Danh sách ca làm việc'
            searchPlaceholder='Tìm kiếm ca làm việc...'
            handleAfterSubmit={handleStaffSelected}
            getOptionLabel={getWorkScheduleLabel}
            buttonTooltip='Chọn ca làm việc'
            customFilter={<CustomFilter />}
            readOnly={readOnly}
        />
    );
}

export default observer(StaffWorkScheduleSelectionForm);

const CustomFilter = observer(() => {
    const { values } = useFormikContext();
    const { staffWorkScheduleStore } = useStore();
    const { t } = useTranslation();

    const {
        searchObject,
        intactSearchObject,
        handleSetSearchObject,
        isOpenFilter,
        setIsOpenFilter,
    } = staffWorkScheduleStore;


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

    function handleResetFilter() {
        const newSearchObject = {
            ...JSON.parse(JSON.stringify(intactSearchObject)),
            pageIndex: searchObject.pageIndex,
            pageSize: searchObject.pageSize,
        };
        handleSetSearchObject(newSearchObject)
    }

    const { hrRoleUtilsStore } = useStore();

    const {
        hasShiftAssignmentPermission,
        checkHasShiftAssignmentPermission,
        isManager,
        isAdmin,
        checkAllUserRoles
    } = hrRoleUtilsStore;
    useEffect(() => {
        checkAllUserRoles()
        checkHasShiftAssignmentPermission()
    }, []);
    return (
        <Grid container spacing={2}>
            <Grid item xs={12} lg={6}>
                <div className='flex justify-end align-center'>
                    <div className='flex flex-center w-100'>
                        <Grid container spacing={2}>
                            <Grid item xs={12} sm={6} md={4}>
                                <div className='flex items-center h-100 flex-end'>
                                    <p className='no-wrap-text'>
                                        <b>Từ ngày:</b>
                                    </p>
                                </div>
                            </Grid>
                            <Grid item xs={12} sm={6} md={8}>
                                <GlobitsDateTimePicker
                                    // label="Từ ngày"
                                    name='fromDate'
                                    value={values?.fromDate}
                                // placeholder="Ngày từ"
                                />
                            </Grid>
                        </Grid>
                    </div>
                    <div className='flex flex-center w-100'>
                        <Grid container spacing={2}>
                            <Grid item xs={12} sm={6} md={4}>
                                <div className='flex items-center h-100 flex-end'>
                                    <p className='no-wrap-text'>
                                        <b>Đến ngày:</b>
                                    </p>
                                </div>
                            </Grid>

                            <Grid item xs={12} sm={6} md={8}>
                                <GlobitsDateTimePicker
                                    // label="Đến ngày"
                                    name='toDate'
                                    value={values?.toDate}
                                // placeholder="Đến ngày"
                                />
                            </Grid>
                        </Grid>
                    </div>
                </div>
            </Grid>

            <Grid item xs={12} lg={6}>
                <div className='flex justify-between align-center'>
                    <Tooltip placement='top' title='Tìm kiếm theo từ khóa'>
                        <GlobitsTextField
                            placeholder='Tìm kiếm theo từ khóa'
                            name='keyword'
                            variant='outlined'
                            notDelay
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
                        {(isManager || isAdmin) && (
                            <Button
                                startIcon={
                                    <FilterListIcon
                                        className={` filterRotateIcon ${isOpenFilter && "onRotate"}`}
                                    />
                                }
                                className=' d-inline-flex py-2 px-8 btnHrStyle'
                                onClick={handleTogglePopupFilter}>
                                Bộ lọc
                            </Button>
                        )}

                    </ButtonGroup>
                </div>
            </Grid>
            {(isManager || isAdmin) && (
                <Grid item xs={12}>
                    <Collapse in={isOpenFilter} className='filterPopup'>
                        <div className='flex flex-column'>
                            <Grid container spacing={2}>
                                <Grid item xs={12}>
                                    <div className='filterContent pt-8'>
                                        <Grid container spacing={2}>
                                            <Grid item xs={12}>
                                                <p className='m-0 p-0 borderThrough2'>Lọc theo đối tượng</p>
                                            </Grid>
                                            <Grid item xs={12}>
                                                <Grid
                                                    container
                                                    spacing={2}
                                                //  className='justify-end'
                                                >
                                                    <Grid item xs={12} sm={6} md={4} lg={3}>
                                                        <GlobitsPagingAutocompleteV2
                                                            name='organization'
                                                            label='Đơn vị'
                                                            api={pagingAllOrg}
                                                        />
                                                    </Grid>

                                                    <Grid item xs={12} sm={6} md={4} lg={3}>
                                                        <GlobitsPagingAutocompleteV2
                                                            label={"Phòng ban"}
                                                            name='department'
                                                            api={pagingHasPermissionDepartments}
                                                            searchObject={{
                                                                pageIndex: 1,
                                                                pageSize: 10,
                                                                keyword: "",
                                                                organizationId: values?.organization?.id,
                                                            }}
                                                            getOptionLabel={(option) =>
                                                                [option?.name, option?.code].filter(Boolean).join(" - ") || ""
                                                            }
                                                        />
                                                    </Grid>

                                                    <Grid item xs={12} sm={6} md={4} lg={3}>
                                                        <GlobitsPagingAutocompleteV2
                                                            name='positionTitle'
                                                            label='Chức danh'
                                                            api={pagingPositionTitle}
                                                            searchObject={{
                                                                departmentId: values?.department?.id,
                                                            }}
                                                        />
                                                    </Grid>

                                                    <Grid item xs={12} sm={6} md={4} lg={3}>
                                                        <GlobitsPagingAutocompleteV2
                                                            label={t("Nhân viên")}
                                                            name='staff'
                                                            api={pagingStaff}
                                                            getOptionLabel={(option) => {
                                                                return `${option?.displayName} - ${option?.staffCode}`;
                                                            }}
                                                            readOnly={!hasShiftAssignmentPermission}
                                                        />
                                                    </Grid>
                                                </Grid>
                                            </Grid>

                                        </Grid>

                                        <div className='py-8 mt-12 border-bottom-fade border-top-fade'>
                                            <div className='flex justify-end'>
                                                <ButtonGroup color='container'
                                                    aria-label='outlined primary button group'>
                                                    <Button
                                                        onClick={handleResetFilter}
                                                        startIcon={<RotateLeftIcon />}
                                                    >
                                                        Đặt lại
                                                    </Button>

                                                    <Button
                                                        type='button'
                                                        onClick={handleCloseFilter}
                                                        startIcon={<HighlightOffIcon />}
                                                    >
                                                        Đóng bộ lọc
                                                    </Button>
                                                </ButtonGroup>
                                            </div>
                                        </div>
                                    </div>
                                </Grid>
                            </Grid>
                        </div>
                    </Collapse>

                </Grid>
            )}
        </Grid>
    );
});