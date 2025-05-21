import { Button, DialogActions, DialogContent, Grid, makeStyles, Radio } from "@material-ui/core";
import Paper from "@material-ui/core/Paper";
import Table from "@material-ui/core/Table";
import TableBody from "@material-ui/core/TableBody";
import TableCell from "@material-ui/core/TableCell";
import TableContainer from "@material-ui/core/TableContainer";
import TableHead from "@material-ui/core/TableHead";
import TableRow from "@material-ui/core/TableRow";
import BlockIcon from "@material-ui/icons/Block";
import SaveIcon from "@material-ui/icons/Save";
import GlobitsDateTimePicker from "app/common/form/GlobitsDateTimePicker";
import GlobitsPagingAutocompleteV2 from 'app/common/form/GlobitsPagingAutocompleteV2';
import FormikFocusError from "app/common/FormikFocusError";
import GlobitsPopupV2 from "app/common/GlobitsPopupV2";
import { formatDate } from "app/LocalFunction";
import { Form, Formik, useFormikContext } from "formik";
import { observer } from "mobx-react";
import { memo, useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import { useParams } from "react-router";
import * as Yup from "yup";
import GlobitsTextField from "../../common/form/GlobitsTextField";
import { useStore } from "../../stores";
import { pagingStaffWorkSchedule } from "../StaffWorkScheduleV2/StaffWorkScheduleService";
import ViewStatisticPopup from "../StaffWorkScheduleV2/ViewStatisticPopup";
import ChooseUsingStaffSection from "../User/UsingAccountStaff/ChooseUsingStaffSection";
import { pagingShiftWork } from "../ShiftWork/ShiftWorkService";
import GlobitsCheckBox from "app/common/form/GlobitsCheckBox";

const useStyles = makeStyles({
    root: {
        "& .MuiDialogContent-root": {
            overflow: "auto !important",
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
        minWidth: 650,
        border: "3px solid #2a80c8 !important",
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

function TimeSheetDetailCUForm(props) {
    const { handleAfterSubmit, readOnly } = props
    const { t } = useTranslation();
    const { id } = useParams();

    const {
        timeSheetDetailStore
    } = useStore();

    const classes = useStyles();

    const {
        openTimeSheetDetailCUForm,
        handleClose,
        saveTimeSheetDetail,
        selectedTimeSheetDetail,
        openViewPopup
    } = timeSheetDetailStore;

    const validationSchema = Yup.object().shape({
        workingDate: Yup.date()
            .transform(function transformDate(castValue, originalValue) {
                return originalValue ? new Date(originalValue) : castValue;
            })
            .required("Ngày không được để trống")
            .typeError("Ngày bắt đầu không đúng định dạng")
            .nullable(),
        employee: Yup.object().required("Nhân viên không được để trống").nullable(),
        // staffWorkSchedule: Yup.object().required("Ca làm việc không được để trống").nullable(),
        startTime: Yup.date()
            .transform(function transformDate(castValue, originalValue) {
                return originalValue ? new Date(originalValue) : castValue;
            })
            // .required("Thời gian bắt đầu không được để trống")
            .typeError("Ngày bắt đầu không đúng định dạng")
            .nullable()
            .test('match-working-date', 'Thời gian bắt đầu phải trùng với ngày làm việc', function (value) {
                const { workingDate } = this.parent; // Lấy giá trị của workingDate từ object cha
                if (!value || !workingDate) return true; // Nếu một trong hai giá trị là null/undefined, bỏ qua kiểm tra
                return value.toDateString() === workingDate.toDateString(); // So sánh ngày (bỏ qua giờ)
            }),
        endTime: Yup.date()
            .transform(function transformDate(castValue, originalValue) {
                return originalValue ? new Date(originalValue) : castValue;
            })
            .typeError("Ngày kết thúc không đúng định dạng")
            .nullable()
            .test('greater-or-equal-to-startTime', 'Thời gian kết thúc phải lớn hơn hoặc bằng thời gian bắt đầu', function (value) {
                const { startTime } = this.parent;
                if (!value || !startTime) return true;
                return value >= startTime;
            })
            .test('match-working-date', 'Thời gian kết thúc phải trùng với ngày làm việc', function (value) {
                const { workingDate } = this.parent;
                if (!value || !workingDate) return true;
                return value.toDateString() === workingDate.toDateString();
            }),
        shiftWorkTimePeriod: Yup.object()
            .when('staffWorkSchedule', {
                is: (staffWorkSchedule) =>
                    staffWorkSchedule && staffWorkSchedule?.id && // Đảm bảo staffWorkSchedule tồn tại
                    !staffWorkSchedule.allowOneEntryOnly && // Điều kiện allowOneEntryOnly là false
                    staffWorkSchedule.shiftWork?.timePeriods?.length > 0, // Có timePeriods
                then: Yup.object().required("Chưa chọn giai đoạn làm việc trong ca").nullable(),
                otherwise: Yup.object().nullable(), // Nếu không thỏa mãn điều kiện, không bắt buộc
            }),
        // shiftWorkTimePeriod: Yup.object().required("Chưa chọn giai đoạn làm việc trong ca").nullable()
    });

    const handleSubmit = async (values) => {
        await saveTimeSheetDetail(values)
        if (typeof handleAfterSubmit === "function") {
            handleAfterSubmit();
        }
    }

    return (
        <GlobitsPopupV2
            size='sm'
            popupId="timesheetdetail"
            open={openTimeSheetDetailCUForm || openViewPopup}
            noDialogContent
            title={
                (openViewPopup && t("Xem chi tiết") + " Thông tin lần chấm công") ||
                ((selectedTimeSheetDetail?.id
                    ? t("general.button.edit")
                    : t("general.button.add")) + ' ' + t("Thông tin lần chấm công"))
            }
            onClosePopup={handleClose}
        >
            <Formik
                enableReinitialize
                initialValues={selectedTimeSheetDetail}
                validationSchema={validationSchema}
                onSubmit={handleSubmit}
            >
                {({ values, setFieldValue, submitForm }) => {
                    function handleChooseTimePeriod(timePeriod) {
                        setFieldValue("shiftWorkTimePeriod", timePeriod);
                    }

                    function handleChooseWorkingDate(workingDate) {
                        setFieldValue("staffWorkSchedule", null);
                        setFieldValue("shiftWorkTimePeriod", null);
                        setFieldValue("workingDate", workingDate);
                        setFieldValue("startTime", workingDate);
                    }

                    return (
                        <Form autoComplete='off'>
                            <div className={`dialog-body`}>
                                <DialogContent className='p-12'>
                                    <FormikFocusError />

                                    <Grid container spacing={2}>

                                        <Grid item xs={12} className="pb-0">
                                            <p className="m-0 p-0 borderThrough2">
                                                Thông tin ca làm việc
                                            </p>
                                        </Grid>

                                        <Grid item xs={12} sm={6}>
                                            <TimesheetDetailChangeStaff readOnly={readOnly} />
                                        </Grid>

                                        <Grid item xs={12} sm={6}>
                                            <GlobitsDateTimePicker
                                                name='workingDate'
                                                label='Ngày làm việc'
                                                onChange={(value) => handleChooseWorkingDate(value)}
                                                required
                                                readOnly={readOnly || values?.id}
                                            />
                                        </Grid>

                                        <Grid item xs={12}>
                                            <Grid container spacing={1}>
                                                {/* <Grid item xs={9}>
                                                    <GlobitsPagingAutocompleteV2
                                                        label={"Ca làm việc"}
                                                        name='staffWorkSchedule'
                                                        searchObject={{
                                                            fromDate: values?.workingDate,
                                                            toDate: values?.workingDate,
                                                            staffId: values?.employee ? values?.employee?.id : null,
                                                        }}
                                                        getOptionLabel={(option) => {
                                                            return `${option?.shiftWork?.name || ""} - ${option?.shiftWork?.code || ""}`;
                                                        }}
                                                        api={pagingStaffWorkSchedule}
                                                        displayData='shiftWork.name'
                                                        required
                                                        disabled={!values.workingDate || (values?.id ? true : false)}
                                                        readOnly={readOnly}
                                                    />
                                                </Grid> */}

                                                <Grid item xs={9}>
                                                    <GlobitsPagingAutocompleteV2
                                                        name='staffWorkSchedule.shiftWork'
                                                        label={"Ca làm việc"}
                                                        api={pagingShiftWork}
                                                        // required
                                                        disabled={!values?.employee?.id || !values.workingDate}
                                                        searchObject={{
                                                            staffId: values?.employee ? values?.employee?.id : null,
                                                        }}
                                                        getOptionLabel={(option) =>
                                                            option?.name && option?.code
                                                                ? `${option.name} - ${option.code}`
                                                                : option?.name || option?.code || ""
                                                        }
                                                        readOnly={readOnly || values.staffWorkSchedule?.id}
                                                    />
                                                </Grid>

                                                <Grid item xs={3} className="flex align-end">
                                                    <ViewStatisticPopup />
                                                </Grid>
                                            </Grid>
                                        </Grid>

                                        {values?.isSync && (
                                            <Grid item xs={12} className="pb-0">
                                                <GlobitsCheckBox
                                                    label={"Dữ liệu từ máy chấm công"}
                                                    name='isSync'
                                                    readOnly
                                                />
                                            </Grid>
                                        )}

                                        <Grid item xs={12} className="pb-0">
                                            <p className="m-0 p-0 borderThrough2">
                                                Thời gian chấm công
                                            </p>
                                        </Grid>

                                        {values?.staffWorkSchedule?.id && !values?.staffWorkSchedule?.allowOneEntryOnly && values?.staffWorkSchedule?.shiftWork?.timePeriods?.length > 0 && (
                                            <Grid item xs={12}>

                                                <strong>
                                                    Giai đoạn làm việc trong ca
                                                </strong>

                                                <TableContainer component={Paper}>
                                                    <Table
                                                        className={`${classes.table}`}
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

                                                                {/* <TableCell
                                  align="center"
                                  className="py-4"
                                  style={{ width: "20%" }}
                                >
                                  Được phép đi muộn
                                </TableCell> */}

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
                                                            {values?.staffWorkSchedule?.shiftWork?.timePeriods.length > 0
                                                                ? values?.staffWorkSchedule?.shiftWork?.timePeriods?.map(
                                                                    function (timePeriod, index) {

                                                                        return (
                                                                            <TableRow key={index}
                                                                                className={classes.tableBody}>
                                                                                <TableCell align="center">
                                                                                    <Radio
                                                                                        name="radSelected"
                                                                                        value={values?.shiftWorkTimePeriod?.id}
                                                                                        checked={values?.shiftWorkTimePeriod?.id === timePeriod?.id}
                                                                                        onClick={(event) => handleChooseTimePeriod(timePeriod)}
                                                                                        readOnly={readOnly} />
                                                                                </TableCell>

                                                                                <TableCell align="center">
                                                                                    {formatDate("HH:mm", timePeriod?.startTime)}
                                                                                </TableCell>

                                                                                <TableCell align="center">
                                                                                    {formatDate("HH:mm", timePeriod?.endTime)}
                                                                                </TableCell>

                                                                                {/* <TableCell align="center">
                                          {`${timePeriod?.allowedLateMinutes || 0} phút`}
                                        </TableCell> */}

                                                                                <TableCell align="center">
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
                                        )}

                                        <Grid item xs={12} sm={6}>
                                            <GlobitsDateTimePicker
                                                label={"Thời gian chấm công vào"}
                                                name='startTime'
                                                isDateTimePicker
                                                // required
                                                readOnly={readOnly}
                                            />
                                        </Grid>

                                        {values?.id && (
                                            <Grid item xs={12} sm={6}>
                                                <GlobitsTextField
                                                    label={"Địa chỉ IP checkin"}
                                                    name='addressIPCheckIn'
                                                    readOnly={readOnly}
                                                    disabled
                                                />
                                            </Grid>
                                        )}


                                        <Grid item xs={12} sm={6}>
                                            <GlobitsDateTimePicker
                                                label={"Thời gian chấm công ra"}
                                                name='endTime'
                                                isDateTimePicker
                                                readOnly={readOnly}
                                            />
                                        </Grid>

                                        {values?.id && (
                                            <Grid item xs={12} sm={6}>
                                                <GlobitsTextField
                                                    label={"Địa chỉ IP checkout"}
                                                    name='addressIPCheckOut'
                                                    readOnly={readOnly}
                                                    disabled
                                                />
                                            </Grid>
                                        )}

                                    </Grid>

                                </DialogContent>
                            </div>

                            <div className='dialog-footer dialog-footer-v2 py-8 px-12'>
                                <DialogActions className='p-0'>
                                    <div className='flex flex-space-between flex-middle'>
                                        <Button startIcon={<BlockIcon />} variant='contained'
                                            className={`${!readOnly && 'mr-12'} btn btn-secondary d-inline-flex`}
                                            color='secondary'
                                            onClick={handleClose}>
                                            {t("general.button.cancel")}
                                        </Button>
                                        {!readOnly && (
                                            <Button startIcon={<SaveIcon />}
                                                className='mr-0 btn btn-primary d-inline-flex'
                                                variant='contained' color='primary' type='submit'>
                                                {t("general.button.save")}
                                            </Button>
                                        )}
                                    </div>
                                </DialogActions>
                            </div>
                        </Form>
                    );
                }}
            </Formik>
        </GlobitsPopupV2>
    );
}

export default memo(observer(TimeSheetDetailCUForm));


function TimesheetDetailChangeStaff(props) {
    // function handleChooseEmployee(employee) {
    //   setFieldValue("workingDate", null);
    //   setFieldValue("staffWorkSchedule", null);
    //   setFieldValue("shiftWorkTimePeriod", null);
    //   setFieldValue("employee", employee);
    // }

    const {
        values,
        setFieldValue
    } = useFormikContext();

    const { readOnly = false } = props;

    const [isFirstRender, setIsFirstRender] = useState(true);

    useEffect(function () {

        if (isFirstRender) {
            setIsFirstRender(false);
            return;
        }

        setFieldValue("workingDate", null);
        setFieldValue("staffWorkSchedule", null);
        setFieldValue("shiftWorkTimePeriod", null);

    }, [values?.employee?.id]);

    return (
        <>
            {/* <GlobitsPagingAutocompleteV2
                        label={"Nhân viên"}
                        name='employee'
                        api={pagingStaff}
                        displayData='displayName'
                        required
                        getOptionLabel={(option) =>
                          option?.displayName && option?.staffCode
                            ? `${option.displayName} - ${option.staffCode}`
                            : option?.displayName || option?.staffCode || ''
                        }
                        handleChange={(event, value) => {
                          handleChooseEmployee(value); // Truyền giá trị được chọn vào hàm
                        }}
                      /> */}

            <ChooseUsingStaffSection
                label="Nhân viên"
                name="employee"
                required
                disabled={values?.id ? true : false}
                readOnly={readOnly}
            />
        </>

    );
}



