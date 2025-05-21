import {
    Button,
    ButtonGroup,
    DialogActions,
    DialogContent,
    Grid,
    Icon,
    IconButton,
    Tooltip
} from "@material-ui/core";
import AddIcon from "@material-ui/icons/Add";
import BlockIcon from "@material-ui/icons/Block";
import CachedIcon from "@material-ui/icons/Cached";
import { transformDateDatePicker } from "app/common/CommonFunctions";
import GlobitsCheckBox from "app/common/form/GlobitsCheckBox";
import GlobitsDateTimePicker from "app/common/form/GlobitsDateTimePicker";
import GlobitsPagingAutocomplete from "app/common/form/GlobitsPagingAutocomplete";
import GlobitsPagingAutocompleteV2 from "app/common/form/GlobitsPagingAutocompleteV2";
import GlobitsSelectInput from "app/common/form/GlobitsSelectInput";
import GlobitsPopupV2 from "app/common/GlobitsPopupV2";
import GlobitsTable from "app/common/GlobitsTable";
import LocalConstants from "app/LocalConstants";
import { getDateTime } from "app/LocalFunction";
import { useStore } from "app/stores";
import { pagingLeaveType } from "app/views/LeaveType/LeaveTypeService";
import { FieldArray, Form, Formik, useFormikContext } from "formik";
import { observer } from "mobx-react";
import { memo, useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import * as Yup from "yup";
import { pagingStaff } from "../HumanResourcesInformation/StaffService";
import { pagingShiftWork } from "../ShiftWork/ShiftWorkService";
import TimeSheetDetailCUForm from "../TimeSheetDetail/TimeSheetDetailCUForm";
import StaffWorkScheduleSummary from "./StaffWorkScheduleSummary";
import GlobitsConfirmationDialog from "app/common/GlobitsConfirmationDialog";
import GlobitsTextField from "../../common/form/GlobitsTextField";
import GlobitsNumberInput from "app/common/form/GlobitsNumberInput";
import RemoveRedEyeIcon from '@material-ui/icons/RemoveRedEye';
import SaveIcon from "@material-ui/icons/Save";

import { makeStyles, Radio } from "@material-ui/core";
import Paper from "@material-ui/core/Paper";
import Table from "@material-ui/core/Table";
import TableBody from "@material-ui/core/TableBody";
import TableCell from "@material-ui/core/TableCell";
import TableContainer from "@material-ui/core/TableContainer";
import TableHead from "@material-ui/core/TableHead";
import TableRow from "@material-ui/core/TableRow";
import { formatDate } from "app/LocalFunction";

const useStyles = makeStyles({
    root: {
        "& .MuiDialogContent-root": {
            overflow: "unset !important",
        },
    },
    marginAuto: {
        display: "flex",
        "& label": {
            margin: "auto",
            marginRight: "10px",
            fontWeight: "500",
            fontSize: "16px",
        },
    },
    table: {
        width: "100%",
        border: "2px solid #2a80c8 !important",
        borderCollapse: "collapse",

        "& .MuiTableCell-root": {
            border: "none",
        },

        "& .MuiTableRow-head": {
            backgroundColor: "#2a80c8",
            border: "1px solid #2a80c8",
        },

        "& .MuiTableCell-head": {
            border: "1px solid #2a80c8",
            color: "#fff",
        },

        "& .MuiTableCell-body": {
            border: "1px solid #2a80c8",
        },

        "& .MuiFormGroup-root": {
            display: "flex",
            justifyContent: "center",
            alignItems: "center",
        },
    },
    tableBody: {
        "& .MuiCheckbox-root": {
            margin: "auto",
        },
        "& .MuiTextField-root": {
            padding: "5px",
        },
    },
    headerDate: {
        fontSize: "22px",
        fontWeight: "700",
    },
    displayFlex: {
        display: "flex",
        justifyContent: "center",
        alignItems: "center",
    },
});


function StaffWorkScheduleV2CUForm(props) {
    const { t } = useTranslation();
    const classes = useStyles();


    const { pagingAfterEdit, readOnly = false } = props;

    const {
        staffWorkScheduleStore,
        timeSheetDetailStore,
        hrRoleUtilsStore

    } = useStore();

    const {
        hasShiftAssignmentPermission

    } = hrRoleUtilsStore;

    // console.log("hasShiftAssignmentPermission", hasShiftAssignmentPermission);

    const {
        handleClose,
        pagingStaffWorkSchedule,
        selectedStaffWorkSchedule,
        openCreateEditPopup,
        handleRecalculateStaffWorkTime,
        openViewPopup,
        getStaffWorkScheduleWorkingStatusName,
        getStaffWorkScheduleWorkingTypeName,
        handleOpenViewStatistic,
        saveOneStaffWorkSchedule
    } = staffWorkScheduleStore;

    const {
        handleOpenCreateEditInStaffWorkSchedule,
        openTimeSheetDetailCUForm,
        openConfirmDeletePopup,
        handleConfirmDelete: handleConfirmDeleteTimeSheetDetail,
        handleClose: handleCloseConfirmDeleteTimeSheetDetail,
    } = timeSheetDetailStore;

    const validationSchema = Yup.object({
        shiftWork: Yup.object().required(t("validation.required")).nullable(),
        staff: Yup.object().required(t("validation.required")).nullable(),
        workingDate: Yup.date().transform(transformDateDatePicker).required(t("validation.required")).nullable(),
    });

    async function handleSaveForm(values) {
        const response = await handleRecalculateStaffWorkTime(values?.id);

        if (typeof pagingAfterEdit === "function") {
            await pagingAfterEdit();
        } else {
            await pagingStaffWorkSchedule();
        }
    }

    const [initialValues, setInitialValues] = useState(selectedStaffWorkSchedule);

    useEffect(
        function () {
            setInitialValues(selectedStaffWorkSchedule);
        },
        [selectedStaffWorkSchedule, selectedStaffWorkSchedule?.id]
    );

    const handelClickAddTimeSheetDetail = () => {
        const dto = {
            workingDate: selectedStaffWorkSchedule?.workingDate,
            staffWorkSchedule: selectedStaffWorkSchedule,
            employee: selectedStaffWorkSchedule?.staff,
            startTime: selectedStaffWorkSchedule?.workingDate
        };
        handleOpenCreateEditInStaffWorkSchedule(dto);
    };

    const getDateOnly = (date) => new Date(date.getFullYear(), date.getMonth(), date.getDate());
    const currentDate = getDateOnly(new Date());

    async function handleSaveStatistics(values) {
        try {
            await saveOneStaffWorkSchedule(values);
            handleClose();
        } catch (error) {

        }

    }

    return (
        <GlobitsPopupV2
            scroll={"body"}
            size='sm'
            open={openCreateEditPopup || openViewPopup}
            title={"Thông tin ca làm việc được phân"}
            noDialogContent
            onClosePopup={handleClose}
        >
            <Formik
                validationSchema={validationSchema}
                enableReinitialize
                initialValues={initialValues}
                onSubmit={handleSaveForm}
            >
                {({ isSubmitting, values, setFieldValue }) => {
                    // const shouldShowLeaveType = (
                    //     values?.workingStatus === LocalConstants.StaffWorkScheduleWorkingStatus.NOT_ATTENDANCE.value ||
                    //     (values?.workingStatus === null && getDateOnly(new Date(values?.workingDate)) < currentDate) ||
                    //     Boolean(values?.leaveType)
                    // );

                    return (
                        <Form autoComplete='off'>
                            <DialogContent className='o-hidden dialog-body p-12'>
                                <Grid container spacing={2}>
                                    <Grid item xs={12} className='pb-0'>
                                        <p className='m-0 p-0 borderThrough2'>
                                            Thông tin ca làm việc
                                        </p>
                                    </Grid>

                                    <Grid item xs={12} sm={6} md={6}>
                                        <GlobitsDateTimePicker
                                            label={"Ngày làm việc"}
                                            readOnly
                                            name='workingDate'
                                        />
                                    </Grid>

                                    <Grid item xs={12} sm={6} md={6}>
                                        <GlobitsPagingAutocomplete
                                            name='shiftWork'
                                            label={t("staffWorkSchedule.shiftWorks")}
                                            api={pagingShiftWork}
                                            required
                                        // readOnly

                                        />
                                    </Grid>

                                    <Grid item xs={12} sm={6} md={6}>
                                        <GlobitsPagingAutocompleteV2
                                            name='staff'
                                            label={t("staffWorkSchedule.staffs")}
                                            api={pagingStaff}
                                            readOnly
                                            required
                                            getOptionLabel={(option) => {
                                                if (!option) return "";

                                                const name = option.displayName || "";
                                                const code = option.staffCode ? ` - ${option.staffCode}` : "";
                                                const position = option.currentPosition?.name ? ` (${option.currentPosition.name})` : "";

                                                return `${name}${code}${position}`;
                                            }}
                                        />
                                    </Grid>

                                    <Grid item xs={12} sm={6} md={6}>
                                        <GlobitsPagingAutocompleteV2
                                            name='coordinator'
                                            label={t("Người phân ca")}
                                            api={pagingStaff}
                                            readOnly={readOnly}
                                            disabled
                                            required
                                            getOptionLabel={(option) => {
                                                if (!option) return "";

                                                const name = option.displayName || "";
                                                const code = option.staffCode ? ` - ${option.staffCode}` : "";
                                                const position = option.currentPosition?.name ? ` (${option.currentPosition.name})` : "";

                                                return `${name}${code}${position}`;
                                            }}
                                        />
                                    </Grid>


                                    <Grid item xs={12} sm={6}>
                                        <GlobitsTextField
                                            value={getStaffWorkScheduleWorkingStatusName(values)}
                                            label='Trạng thái làm việc'
                                            name={"workingStatusDisplay"}//fake name đê hiển thị trạng thái làm việc
                                            readOnly
                                        />
                                    </Grid>

                                    <Grid item xs={12} sm={6}>
                                        <GlobitsPagingAutocomplete
                                            name='leaveType'
                                            label='Loại xin nghỉ được đã được xác nhận'
                                            api={pagingLeaveType}
                                            readOnly={readOnly}
                                        />
                                    </Grid>

                                    {values?.leaveType?.code?.includes("NUA_NGAY") && (
                                        <Grid item xs={12}>
                                            <Grid container spacing={2}>
                                                <Grid item xs={12} className='pb-0'>
                                                    <p className='m-0 p-0 borderThrough2'>
                                                        Giai đoạn nghỉ trong ca
                                                    </p>
                                                </Grid>

                                                <Grid item xs={12}>
                                                    <TableBody>
                                                        {values?.shiftWork?.timePeriods.length > 0
                                                            ? <Grid item xs={12}>

                                                                <TableContainer component={Paper}>
                                                                    <Table
                                                                        className={`${classes.table} mb-12`}
                                                                        aria-label="simple table"
                                                                    >
                                                                        <TableHead>
                                                                            <TableRow>
                                                                                <TableCell
                                                                                    align="center"
                                                                                    className="py-4"
                                                                                    style={{ width: "10%" }}
                                                                                >
                                                                                    Chọn
                                                                                </TableCell>

                                                                                <TableCell
                                                                                    align="center"
                                                                                    className="py-4"
                                                                                    style={{ width: "15%" }}
                                                                                >
                                                                                    {t("timeKeeping.startTime")}
                                                                                </TableCell>

                                                                                <TableCell
                                                                                    align="center"
                                                                                    className="py-4"
                                                                                    style={{ width: "15%" }}
                                                                                >
                                                                                    {t("timeKeeping.endTime")}
                                                                                </TableCell>

                                                                                <TableCell
                                                                                    align="center"
                                                                                    className="py-4"
                                                                                    style={{ width: "20%" }}
                                                                                >
                                                                                    Tỷ lệ
                                                                                </TableCell>
                                                                            </TableRow>
                                                                        </TableHead>

                                                                        <TableBody>
                                                                            {values?.shiftWork?.timePeriods.length > 0
                                                                                ? values?.shiftWork?.timePeriods?.map(
                                                                                    function (timePeriod, index) {
                                                                                        const isRadioChecked = values?.leavePeriod?.id === timePeriod?.id;

                                                                                        return (
                                                                                            <TableRow key={index}
                                                                                                className={classes.tableBody}>
                                                                                                <TableCell
                                                                                                    align="center">
                                                                                                    <Radio
                                                                                                        name="radSelected"
                                                                                                        value={timePeriod?.id}
                                                                                                        checked={values?.leavePeriod?.id === timePeriod?.id}
                                                                                                        onChange={() => setFieldValue("leavePeriod", timePeriod)}
                                                                                                    />


                                                                                                </TableCell>

                                                                                                <TableCell
                                                                                                    align="center">
                                                                                                    {formatDate("HH:mm", timePeriod?.startTime)}
                                                                                                </TableCell>

                                                                                                <TableCell
                                                                                                    align="center">
                                                                                                    {formatDate("HH:mm", timePeriod?.endTime)}
                                                                                                </TableCell>
                                                                                                <TableCell
                                                                                                    align="center">
                                                                                                    {`${(timePeriod?.workRatio || 0) * 100}% ngày công`}
                                                                                                </TableCell>


                                                                                            </TableRow>
                                                                                        );
                                                                                    }
                                                                                )
                                                                                : "Chưa có dữ liệu !"}
                                                                        </TableBody>


                                                                    </Table>
                                                                </TableContainer>
                                                            </Grid>
                                                            : "Chưa có dữ liệu !"}
                                                    </TableBody>
                                                </Grid>
                                            </Grid>
                                        </Grid>
                                    )}


                                    <Grid item xs={12} className='pb-0 '>
                                        <div className='m-0 p-0 borderThrough2 justify-between flex'>
                                            <p className='m-0 p-0 '>
                                                Quy tắc tính công
                                            </p>

                                            <Tooltip
                                                placement="top"
                                                arrow
                                                title="Xem thống kê kết quả ca làm việc"
                                            >
                                                <IconButton
                                                    className="ml-4"
                                                    size="small"
                                                    onClick={() => handleOpenViewStatistic(selectedStaffWorkSchedule?.id)}
                                                >
                                                    <Icon fontSize="small" style={{ color: "#13529f" }}>
                                                        av_timer
                                                    </Icon>
                                                </IconButton>
                                            </Tooltip>

                                        </div>
                                    </Grid>

                                    <Grid item xs={12} sm={6}>
                                        <GlobitsCheckBox
                                            label={"Chỉ chấm công vào ra 1 lần"}
                                            name='allowOneEntryOnly'
                                            readOnly
                                        />
                                    </Grid>

                                    {
                                        values?.allowOneEntryOnly && (
                                            <Grid item xs={12} sm={6}>
                                                <GlobitsSelectInput
                                                    hideNullOption
                                                    label='Cách tính thời gian'
                                                    name='timekeepingCalculationType'
                                                    keyValue='value'
                                                    readOnly
                                                    options={LocalConstants.TimekeepingCalculationType.getListData()}
                                                />
                                            </Grid>
                                        )
                                    }


                                    {/* <Grid item xs={12} sm={6}>
                                        <GlobitsNumberInput
                                            label='Số công được tính lương'
                                            name={"totalPaidWork"}//fake name đê hiển thị trạng thái làm việc
                                            disabled
                                        />
                                    </Grid> */}

                                    <Grid item xs={12} sm={6}>
                                        <GlobitsCheckBox
                                            label={"Cần xác nhận của quản lý"}
                                            name='needManagerApproval'
                                            readOnly={readOnly}
                                            disabled
                                        />
                                    </Grid>

                                    <Grid item xs={12} sm={6}>
                                        <GlobitsCheckBox
                                            label={"Đã chốt ca"}
                                            name='isLocked'
                                            readOnly={readOnly}
                                            disabled
                                        />
                                    </Grid>

                                    {values?.needManagerApproval &&
                                        <Grid item xs={12} sm={6}>
                                            <GlobitsSelectInput
                                                label={"Trạng thái xác nhận"}
                                                name='approvalStatus'
                                                keyValue='value'
                                                hideNullOption={true}
                                                readOnly
                                                options={LocalConstants.StaffWorkScheduleApprovalStatus.getListData()}
                                            />
                                        </Grid>
                                    }

                                    {/* <Grid item xs={12} className='pb-0'>
                                        <p className='m-0 p-0 borderThrough2'>Nếu nhân viên làm thiếu giờ được phân loại</p>
                                    </Grid> */}


                                    <Grid item xs={12} className='pb-0'>
                                        <p className='m-0 p-0 borderThrough2'>Lịch sử chấm công</p>
                                    </Grid>

                                    {!readOnly && hasShiftAssignmentPermission && (
                                        <>
                                            {(!values?.allowOneEntryOnly ||
                                                (values?.allowOneEntryOnly &&
                                                    (!values?.timeSheetDetails ||
                                                        values?.timeSheetDetails?.length === 0))) && (
                                                    <Grid item xs={12}>
                                                        <ButtonGroup
                                                            color='container'
                                                            aria-label='outlined primary button group'>
                                                            <Tooltip arrow placement='top'
                                                                title='Tạo lần chấm công thủ công'>
                                                                <Button
                                                                    startIcon={<AddIcon />}
                                                                    onClick={() => handelClickAddTimeSheetDetail()}>
                                                                    {t("general.button.add")}
                                                                </Button>
                                                            </Tooltip>
                                                        </ButtonGroup>
                                                    </Grid>
                                                )}
                                        </>
                                    )}

                                    <Grid item xs={12}>
                                        <WorkScheduleCheckInOutHistory readOnly={readOnly} />
                                    </Grid>

                                    {/*<Grid item xs={12} className='pb-0'>*/}
                                    {/*    <p className='m-0 p-0 borderThrough2'>Kết quả ca làm việc theo dữ liệu chấm công</p>*/}
                                    {/*</Grid>*/}

                                    {/*<Grid item xs={12}>*/}
                                    {/*    <StaffWorkScheduleSummary staffWorkSchedule={values} />*/}
                                    {/*</Grid>*/}
                                </Grid>
                            </DialogContent>

                            {!readOnly && (
                                <DialogActions className='dialog-footer flex flex-end flex-middle px-12'>
                                    <Tooltip arrow title='Đóng' placement='bottom'>
                                        <Button
                                            startIcon={<BlockIcon />}
                                            className='btn btn-secondary d-inline-flex'
                                            onClick={handleClose}
                                            disabled={isSubmitting}
                                        >
                                            {t("general.button.cancel")}
                                        </Button>
                                    </Tooltip>

                                    <Button startIcon={<SaveIcon />}
                                        className='ml-12 btn btn-primary d-inline-flex'
                                        disabled={isSubmitting}
                                        variant='contained' color='primary'
                                        onClick={() => handleSaveStatistics(values)}
                                    >
                                        {t("general.button.save")}
                                    </Button>

                                    <Tooltip
                                        arrow
                                        title='Thực hiện thống kê lại dữ liệu chấm công của nhân viên'
                                        placement='bottom'>
                                        <Button
                                            startIcon={<CachedIcon />}
                                            className='ml-12 btn bgc-lighter-dark-blue d-inline-flex text-white'
                                            type='submit'
                                            disabled={isSubmitting}>
                                            Thống kê lại
                                        </Button>
                                    </Tooltip>
                                </DialogActions>
                            )}

                            {hasShiftAssignmentPermission && openTimeSheetDetailCUForm && (
                                <TimeSheetDetailCUForm
                                    // readOnly={true}
                                    handleAfterSubmit={() => {
                                        handleSaveForm(values);
                                    }}
                                />
                            )}

                            {openConfirmDeletePopup && (
                                <GlobitsConfirmationDialog
                                    open={openConfirmDeletePopup}
                                    onConfirmDialogClose={handleCloseConfirmDeleteTimeSheetDetail}
                                    onYesClick={handleConfirmDeleteTimeSheetDetail}
                                    handleAfterConfirm={() => {
                                        handleSaveForm(values);
                                    }}
                                    title={t("confirm_dialog.delete.title")}
                                    text={t("confirm_dialog.delete.text")}
                                    agree={t("confirm_dialog.delete.agree")}
                                    cancel={t("confirm_dialog.delete.cancel")}
                                />
                            )}
                        </Form>
                    );
                }}
            </Formik>
        </GlobitsPopupV2>
    );
}

export default memo(observer(StaffWorkScheduleV2CUForm));

const WorkScheduleCheckInOutHistory = (props) => {
    const { readOnly = false } = props;

    const { timeSheetDetailStore } = useStore();

    const { handleOpenCreateEdit, handleDelete } = timeSheetDetailStore;

    const { t } = useTranslation();
    const { values } = useFormikContext();
    const handleEdit = (rowData) => {
        handleOpenCreateEdit(rowData?.id);
    };

    const columns = [
        ... (!readOnly
            ? [
                {
                    title: "Thao tác",
                    field: "custom",
                    align: "center",
                    render: (rowData) => (
                        <div className='flex flex-middle justify-center'>
                            <Tooltip title='Chỉnh sửa'>
                                <IconButton size='small' onClick={() => handleEdit(rowData)}>
                                    <Icon color='primary' fontSize='small'>
                                        edit
                                    </Icon>
                                </IconButton>
                            </Tooltip>

                            <Tooltip title='Xóa' placement='top'>
                                <IconButton className='ml-8' size='small' onClick={() => handleDelete(rowData)}>
                                    <Icon fontSize='small' color='secondary'>
                                        delete
                                    </Icon>
                                </IconButton>
                            </Tooltip>
                        </div>
                    ),
                },
            ]
            : []),
        {
            title: "Checkin",
            field: "startTime",
            render: (value) => getDateTime(value?.startTime),
        },
        {
            title: "Checkout",
            field: "endTime",
            render: (value) => getDateTime(value?.endTime),
        },
        { title: "IP checkin", field: "addressIPCheckIn" },
        { title: "IP checkout", field: "addressIPCheckOut" },
    ];

    return (
        <>
            <FieldArray
                name='timeSheetDetails'
                render={({ remove, replace, push }) => (
                    <>
                        <GlobitsTable nonePagination columns={columns} data={values?.timeSheetDetails || []} />
                    </>
                )}
            />
        </>
    );
};
