import { Button, Checkbox, DialogContent, FormControlLabel, Grid, Tooltip, Typography } from "@material-ui/core";
import BlockIcon from "@material-ui/icons/Block";
import SaveIcon from "@material-ui/icons/Save";
import { useTranslation } from "react-i18next";
import * as Yup from "yup";
import { useStore } from "app/stores";
import GlobitsPopupV2 from "app/common/GlobitsPopupV2";
import { observer } from "mobx-react";
import FormikFocusError from "app/common/FormikFocusError";
import GlobitsDateTimePicker from "app/common/form/GlobitsDateTimePicker";
import ChooseUsingStaffSection from "../User/UsingAccountStaff/ChooseUsingStaffSection";
import moment from "moment";
import GlobitsTextField from "app/common/form/GlobitsTextField";
import { Form, Formik, getIn } from "formik";
import React, { memo, useEffect, useState } from "react";
import DeleteIcon from "@material-ui/icons/Delete";
import EventIcon from "@material-ui/icons/Event";
import GlobitsConfirmationDialog from "../../common/GlobitsConfirmationDialog";
import PublicHolidayDatePopup from "./PublicHolidayDatePopup";
import GlobitsPagingAutocompleteV2 from "../../common/form/GlobitsPagingAutocompleteV2";
import LocalConstants from "../../LocalConstants";
import GlobitsSelectInput from "../../common/form/GlobitsSelectInput";
import GlobitsVNDCurrencyInput from "../../common/form/GlobitsVNDCurrencyInput";
import AssignmentIcon from "@material-ui/icons/Assignment";
import StaffWorkSchedulePopup from "./StaffWorkSchedulePopup";
import { pagingLeaveType } from "../LeaveType/LeaveTypeService";
import { toast } from "react-toastify";
import LocalAtmIcon from "@material-ui/icons/LocalAtm";
import GlobitsCheckBox from "../../common/form/GlobitsCheckBox";
import FormGroup from "@material-ui/core/FormGroup";
import { pagingShiftWork } from "../ShiftWork/ShiftWorkService";
import { pagingStaffWorkSchedule } from "../StaffWorkScheduleV2/StaffWorkScheduleService";
import GlobitsAutocomplete from "../../common/form/GlobitsAutocomplete";
import { useParams } from "react-router-dom/cjs/react-router-dom.min";

function LeaveRequestForm(props) {
    const { leaveRequestStore, hrRoleUtilsStore, staffStore } = useStore();
    const { t } = useTranslation();
    const { readOnly } = props;
    const { id } = useParams();
    const {
        handleClose,
        saveLeaveRequest,
        pagingLeaveRequest,
        selectedLeaveRequest,
        openCreateEditPopup,
        handleDelete,
        handleConfirmDelete,
        openConfirmDeletePopup,
        handleOpenConfirmDeletePopup,
        openViewPopup,
    } = leaveRequestStore;

    const { selectedStaff } = staffStore;

    const { isAdmin, isManager } = hrRoleUtilsStore;

    const [formValues, setFormValues] = useState(selectedLeaveRequest || {});

    const validationSchema = Yup.object({
        requestStaff: Yup.object().required(t("validation.required")).nullable(),
        requestDate: Yup.date()
            .transform((castValue, originalValue) => (originalValue ? new Date(originalValue) : castValue))
            .required(t("validation.required"))
            .nullable(),
        fromDate: Yup.date()
            .transform((castValue, originalValue) => (originalValue ? new Date(originalValue) : castValue))
            .test("is-greater-or-equal", "Nghỉ từ ngày phải lớn hơn hoặc bằng ngày yêu cầu", function (value) {
                const { requestDate } = this.parent;
                return requestDate && value ? moment(value).isSameOrAfter(moment(requestDate), "date") : true;
            })
            .required(t("validation.required"))
            .typeError("Nghỉ từ ngày không đúng định dạng")
            .nullable(),
        toDate: Yup.date()
            .transform((castValue, originalValue) => (originalValue ? new Date(originalValue) : castValue))
            .test("is-greater", "Nghỉ đến ngày phải lớn hơn nghỉ từ ngày", function (value) {
                const { fromDate } = this.parent;
                if (fromDate && value) {
                    return (
                        moment(value).isAfter(moment(fromDate), "date") ||
                        moment(value).isSame(moment(fromDate), "date")
                    );
                }
                return true;
            })
            .typeError("Nghỉ đến ngày không đúng định dạng")
            .required(t("validation.required"))
            .nullable(),
        leaveType: Yup.object().required(t("validation.required")).nullable(),
        halfDayLeave: Yup.boolean()
            .nullable()
            .when(
                ["halfDayLeaveStart", "halfDayLeaveEnd", "shiftWorkStart", "shiftWorkEnd"],
                function (halfDayLeaveStart, halfDayLeaveEnd, shiftWorkStart, shiftWorkEnd) {
                    return Yup.boolean().test(
                        "combined-halfday-validation",
                        "Lỗi không xác định", // default nếu không dùng `createError`
                        function (value) {
                            // Nếu halfDayLeave được bật
                            if (value) {
                                // 1. Phải chọn ít nhất 1 trong 2: Start hoặc End
                                if (!halfDayLeaveStart && !halfDayLeaveEnd) {
                                    return this.createError({
                                        message: "Không được để trống cả 2 lựa chọn nghỉ phép!!",
                                    });
                                }

                                // 2. Nếu chọn Start thì phải có shiftWorkStart và có nhiều hơn 1 giai đoạn
                                if (halfDayLeaveStart) {
                                    if (!shiftWorkStart) {
                                        return this.createError({ message: "Không được để trống ca làm việc bắt đầu" });
                                    }
                                    if (shiftWorkStart?.timePeriods?.length === 1) {
                                        return this.createError({
                                            message:
                                                "Ca làm việc bắt đầu chỉ có 1 giai đoạn làm việc, không thể nghỉ nửa ngày",
                                        });
                                    } else {
                                        const { timePeriodStart } = this.parent;
                                        if (!timePeriodStart) {
                                            return this.createError({
                                                message: "Không được để trống giai đoạn làm việc",
                                            });
                                        }
                                    }
                                }

                                // 3. Nếu chọn End thì phải có shiftWorkEnd và có nhiều hơn 1 giai đoạn
                                if (halfDayLeaveEnd) {
                                    if (!shiftWorkEnd) {
                                        return this.createError({
                                            message: "Không được để trống ca làm việc kết thúc",
                                        });
                                    }
                                    if (shiftWorkEnd?.timePeriods?.length === 1) {
                                        return this.createError({
                                            message:
                                                "Ca làm việc kết thúc chỉ có 1 giai đoạn làm việc, không thể nghỉ nửa ngày",
                                        });
                                    } else {
                                        const { timePeriodEnd } = this.parent;
                                        if (!timePeriodEnd) {
                                            return this.createError({
                                                message: "Không được để trống giai đoạn làm việc",
                                            });
                                        }
                                    }
                                }
                            }

                            return true;
                        }
                    );
                }
            ),
    });

    async function handleSaveForm(values) {
        try {
            if (!values.requestStaff?.id) {
                toast.warning("Vui lòng chọn nhân viên yêu cầu!");
                return;
            }

            const response = await saveLeaveRequest(values);
            if (response) await pagingLeaveRequest();
        } catch (error) {
            console.error(error);
        }
    }

    useEffect(() => {
        if (selectedLeaveRequest) {
            if (id) {
                selectedLeaveRequest.requestStaff = selectedStaff;
            }
            setFormValues(selectedLeaveRequest);
        }
    }, [selectedLeaveRequest, id]);
    return (
        <GlobitsPopupV2
            scroll={"body"}
            size='md'
            open={openCreateEditPopup || openViewPopup}
            noDialogContent
            title={
                openViewPopup
                    ? "Xem chi tiết" + " " + t("navigation.leaveRequest.title")
                    : (selectedLeaveRequest?.id ? t("general.button.edit") : t("general.button.add")) +
                      " " +
                      t("navigation.leaveRequest.title")
            }
            onClosePopup={handleClose}>
            <Formik
                validationSchema={validationSchema}
                enableReinitialize={false}
                initialValues={formValues}
                onSubmit={handleSaveForm}>
                {({ isSubmitting, setValues, values, initialValues, dirty, setFieldValue }) => {
                    const hasFormChanges = JSON.stringify(values) !== JSON.stringify(initialValues);

                    return (
                        <Form autoComplete='off'>
                            <div className='dialog-body'>
                                <DialogContent className='p-12'>
                                    <FormikFocusError />

                                    <Grid container spacing={2}>
                                        <Grid item xs={12} sm={8} md={9} lg={10}>
                                            <div className='dialogScrollContent pr-12'>
                                                <Grid container spacing={2}>
                                                    {!values?.id ? (
                                                        <Grid item sm={6} xs={12} md={6}>
                                                            <ChooseUsingStaffSection
                                                                label='Nhân viên yêu cầu'
                                                                name='requestStaff'
                                                                required
                                                                disabled={!isAdmin && !isManager}
                                                                readOnly={readOnly || !!id}
                                                            />
                                                        </Grid>
                                                    ) : (
                                                        <Grid item sm={6} xs={12} md={6}>
                                                            <GlobitsTextField
                                                                label='Nhân viên yêu cầu'
                                                                name='requestStaff.displayName'
                                                                required
                                                                disabled
                                                                readOnly={readOnly || !!id}
                                                            />
                                                        </Grid>
                                                    )}

                                                    <Grid item xs={12} sm={6} md={6}>
                                                        <GlobitsDateTimePicker
                                                            label={t("Ngày yêu cầu")}
                                                            name='requestDate'
                                                            required
                                                            readOnly={readOnly}
                                                        />
                                                    </Grid>
                                                    <Grid item xs={12} sm={6} md={6}>
                                                        <GlobitsDateTimePicker
                                                            label={t("Thời điểm bắt đầu nghỉ")}
                                                            name='fromDate'
                                                            required
                                                            readOnly={readOnly}
                                                            onChange={(e) => {
                                                                setFieldValue("fromDate", e);
                                                                if (
                                                                    values?.fromDate &&
                                                                    values?.toDate &&
                                                                    moment(values?.toDate).isSame(
                                                                        moment(values?.fromDate),
                                                                        "date"
                                                                    )
                                                                ) {
                                                                    setFieldValue("halfDayLeaveStart", false);
                                                                }
                                                            }}
                                                        />
                                                    </Grid>
                                                    <Grid item xs={12} sm={6} md={6}>
                                                        <GlobitsDateTimePicker
                                                            label={t("Thời điểm kết thúc")}
                                                            name='toDate'
                                                            required
                                                            readOnly={readOnly}
                                                        />
                                                    </Grid>
                                                    <Grid item xs={12} sm={12} md={12}>
                                                        <GlobitsCheckBox
                                                            label={t("Nghỉ nửa ngày")}
                                                            name='halfDayLeave'
                                                            handleChange={(event) => {
                                                                setFieldValue("halfDayLeave", event.target.checked);
                                                                if (!event.target.checked) {
                                                                    setFieldValue(
                                                                        "halfDayLeaveStart",
                                                                        event.target.checked
                                                                    );
                                                                    setFieldValue(
                                                                        "halfDayLeaveEnd",
                                                                        event.target.checked
                                                                    );
                                                                }
                                                            }}
                                                        />
                                                    </Grid>
                                                    {values?.halfDayLeave && (
                                                        <Grid item xs={12} sm={12} md={12}>
                                                            <FormGroup>
                                                                <Grid container spacing={2}>
                                                                    {[
                                                                        {
                                                                            label: t("Nghỉ nửa ngày đầu"),
                                                                            name: "halfDayLeaveStart",
                                                                            nameShiftWork: "shiftWorkStart",
                                                                            nameTimePeriods: "timePeriodStart",
                                                                            dateValue: values?.fromDate,
                                                                            visible:
                                                                                values?.toDate && values?.fromDate
                                                                                    ? !moment(
                                                                                          new Date(values?.toDate)
                                                                                      ).isSame(
                                                                                          moment(
                                                                                              new Date(values?.fromDate)
                                                                                          ),
                                                                                          "date"
                                                                                      )
                                                                                    : true,
                                                                        },
                                                                        {
                                                                            label: t("Nghỉ nửa ngày cuối"),
                                                                            name: "halfDayLeaveEnd",
                                                                            nameShiftWork: "shiftWorkEnd",
                                                                            nameTimePeriods: "timePeriodEnd",
                                                                            dateValue: values?.toDate,
                                                                            visible: true,
                                                                        },
                                                                    ].map((contract) => {
                                                                        return (
                                                                            <>
                                                                                {contract.visible && (
                                                                                    <>
                                                                                        <Grid
                                                                                            item
                                                                                            xs={3}
                                                                                            sm={3}
                                                                                            md={3}
                                                                                            style={{
                                                                                                paddingLeft: "17px",
                                                                                            }}>
                                                                                            <GlobitsCheckBox
                                                                                                label={
                                                                                                    <Typography>
                                                                                                        {contract.label}
                                                                                                    </Typography>
                                                                                                }
                                                                                                name={contract.name}
                                                                                            />
                                                                                        </Grid>
                                                                                        <Grid item xs={8} sm={8} md={8}>
                                                                                            {getIn(
                                                                                                values,
                                                                                                contract.name
                                                                                            ) &&
                                                                                                contract.dateValue && (
                                                                                                    <>
                                                                                                        <Grid
                                                                                                            container
                                                                                                            spacing={2}>
                                                                                                            <Grid
                                                                                                                item
                                                                                                                xs={6}
                                                                                                                sm={6}
                                                                                                                md={6}>
                                                                                                                <GlobitsPagingAutocompleteV2
                                                                                                                    name={
                                                                                                                        contract.nameShiftWork
                                                                                                                    }
                                                                                                                    label={t(
                                                                                                                        "staffWorkSchedule.shiftWorks"
                                                                                                                    )}
                                                                                                                    api={
                                                                                                                        pagingShiftWork
                                                                                                                    }
                                                                                                                    disabled={
                                                                                                                        !values
                                                                                                                            ?.requestStaff
                                                                                                                            ?.id
                                                                                                                    }
                                                                                                                    searchObject={{
                                                                                                                        staffId:
                                                                                                                            values
                                                                                                                                ?.requestStaff
                                                                                                                                ?.id,
                                                                                                                        date: contract.dateValue,
                                                                                                                    }}
                                                                                                                    getOptionLabel={(
                                                                                                                        option
                                                                                                                    ) =>
                                                                                                                        option?.name &&
                                                                                                                        option?.code
                                                                                                                            ? `${option.name} - ${option.code}`
                                                                                                                            : option?.name ||
                                                                                                                              option?.code ||
                                                                                                                              ""
                                                                                                                    }
                                                                                                                    handleChange={(
                                                                                                                        _,
                                                                                                                        value
                                                                                                                    ) => {
                                                                                                                        setFieldValue(
                                                                                                                            contract.nameShiftWork,
                                                                                                                            value
                                                                                                                        );
                                                                                                                        if (
                                                                                                                            !value
                                                                                                                        ) {
                                                                                                                            setFieldValue(
                                                                                                                                contract.nameTimePeriods,
                                                                                                                                value
                                                                                                                            );
                                                                                                                        }
                                                                                                                    }}
                                                                                                                />
                                                                                                            </Grid>
                                                                                                            <Grid
                                                                                                                item
                                                                                                                xs={6}
                                                                                                                sm={6}
                                                                                                                md={6}>
                                                                                                                <GlobitsAutocomplete
                                                                                                                    name={
                                                                                                                        contract.nameTimePeriods
                                                                                                                    }
                                                                                                                    options={
                                                                                                                        getIn(
                                                                                                                            values,
                                                                                                                            contract?.nameShiftWork
                                                                                                                        )
                                                                                                                            ?.timePeriods ||
                                                                                                                        []
                                                                                                                    }
                                                                                                                    label='Giai đoạn làm việc'
                                                                                                                    displayData={
                                                                                                                        "displayTime"
                                                                                                                    }
                                                                                                                    disabled={
                                                                                                                        !values
                                                                                                                            ?.requestStaff
                                                                                                                            ?.id
                                                                                                                    }
                                                                                                                />
                                                                                                            </Grid>
                                                                                                        </Grid>
                                                                                                    </>
                                                                                                )}
                                                                                        </Grid>
                                                                                    </>
                                                                                )}
                                                                            </>
                                                                        );
                                                                    })}
                                                                </Grid>
                                                            </FormGroup>
                                                        </Grid>
                                                    )}
                                                    <Grid item xs={12} sm={6} md={6}>
                                                        <GlobitsPagingAutocompleteV2
                                                            label={t("Loại nghỉ")}
                                                            name='leaveType'
                                                            required
                                                            api={pagingLeaveType}
                                                            searchObject={{
                                                                usedForRequest: true,
                                                            }}
                                                            getOptionLabel={(option) => option?.name || ""}
                                                            renderOption={(option) => (
                                                                <div className='w-100 flex justify-between'>
                                                                    <p className='m-0 p-0'>{option?.name}</p>
                                                                    {option?.isPaid && (
                                                                        <Tooltip
                                                                            title='Có tính lương'
                                                                            arrow
                                                                            placement='right'>
                                                                            <LocalAtmIcon style={{ color: "green" }} />
                                                                        </Tooltip>
                                                                    )}
                                                                </div>
                                                            )}
                                                            readOnly={readOnly}
                                                        />
                                                    </Grid>

                                                    {values?.id && (
                                                        <>
                                                            <Grid item xs={12} sm={6} md={6}>
                                                                <GlobitsSelectInput
                                                                    label={"Trạng thái yêu cầu"}
                                                                    name='approvalStatus'
                                                                    keyValue='value'
                                                                    hideNullOption={true}
                                                                    options={LocalConstants.LeaveRequestApprovalStatus.getListData()}
                                                                    readOnly
                                                                />
                                                            </Grid>

                                                            {/* <Grid item xs={12} sm={6} md={6}>
                                                                <GlobitsPagingAutocompleteV2
                                                                name="approvalStaff.displayName"
                                                                disabled
                                                                label={"Người xác nhận"}
                                                                api={pagingStaff}
                                                                getOptionLabel={(option) =>
                                                                    option?.displayName && option?.staffCode
                                                                    ? `${option.displayName} - ${option.staffCode}`
                                                                    : option?.displayName || option?.staffCode || ''
                                                                }
                                                                />
                                                            </Grid> */}

                                                            <Grid item xs={12} sm={6} md={6}>
                                                                <GlobitsTextField
                                                                    label='Người xác nhận'
                                                                    name='approvalStaff.displayName'
                                                                    readOnly
                                                                />
                                                            </Grid>

                                                            <Grid item xs={12} sm={6} md={6}>
                                                                <GlobitsVNDCurrencyInput
                                                                    label='Tổng số ngày nghỉ'
                                                                    name='totalDays'
                                                                    readOnly={true}
                                                                />
                                                            </Grid>
                                                        </>
                                                    )}
                                                    {/* <Grid item xs={12} sm={6} md={6}>
                                                        <GlobitsVNDCurrencyInput
                                                        label="Tổng số giờ nghỉ"
                                                        name="totalHours"
                                                        disabled
                                                        />
                                                    </Grid> */}
                                                    <Grid item xs={12}>
                                                        <GlobitsTextField
                                                            label={"Lý do nghỉ"}
                                                            name='requestReason'
                                                            multiline
                                                            rows={3}
                                                            readOnly={readOnly}
                                                        />
                                                    </Grid>
                                                </Grid>
                                            </div>
                                        </Grid>

                                        <Grid item xs={12} sm={4} md={3} lg={2}>
                                            <Grid container spacing={1}>
                                                <Grid item xs={12}>
                                                    <p className='m-0 p-0 borderThrough2'>Thao tác</p>
                                                </Grid>

                                                {hasFormChanges && !readOnly && (
                                                    <Grid item xs={6} sm={12}>
                                                        <Tooltip placement='top' title='Lưu thông tin' arrow>
                                                            <Button
                                                                startIcon={<SaveIcon />}
                                                                className={`${
                                                                    !readOnly && "mr-12"
                                                                } btn-secondary d-inline-flex`}
                                                                variant='contained'
                                                                fullWidth
                                                                color='primary'
                                                                type='submit'
                                                                disabled={isSubmitting}>
                                                                {t("general.button.save")}
                                                            </Button>
                                                        </Tooltip>
                                                    </Grid>
                                                )}

                                                {values?.fromDate && values?.toDate && (
                                                    <ButtonOpenPublicHolidayDatePopup readOnly={readOnly} />
                                                )}

                                                {values?.requestStaff &&
                                                    values?.requestStaff?.id &&
                                                    values?.fromDate &&
                                                    values?.toDate && (
                                                        <ButtonOpenStaffWorkSchedulePopup readOnly={readOnly} />
                                                    )}

                                                {values?.id && !readOnly && (
                                                    <Grid item xs={6} sm={12}>
                                                        <Tooltip placement='top' title='Xóa yêu cầu'>
                                                            <Button
                                                                startIcon={<DeleteIcon />}
                                                                variant='contained'
                                                                onClick={() => handleDelete(values)}
                                                                fullWidth
                                                                className='btn-danger'
                                                                disabled={isSubmitting}>
                                                                {t("general.button.delete")}
                                                            </Button>
                                                        </Tooltip>
                                                    </Grid>
                                                )}

                                                <Grid item xs={6} sm={12}>
                                                    <Tooltip placement='top' title='Hủy thay đổi' arrow>
                                                        <Button
                                                            startIcon={<BlockIcon />}
                                                            variant='contained'
                                                            fullWidth
                                                            onClick={handleClose}
                                                            disabled={isSubmitting}>
                                                            Đóng
                                                        </Button>
                                                    </Tooltip>
                                                </Grid>
                                            </Grid>
                                        </Grid>
                                    </Grid>
                                </DialogContent>
                            </div>

                            {openConfirmDeletePopup && (
                                <GlobitsConfirmationDialog
                                    open={openConfirmDeletePopup}
                                    onConfirmDialogClose={() => handleOpenConfirmDeletePopup(false)}
                                    onYesClick={handleConfirmDelete}
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

const ButtonOpenPublicHolidayDatePopup = ({ isSubmitting, readOnly }) => {
    const [openPublicHolidayDatePopup, setOpenPublicHolidayDatePopup] = useState(false);
    return (
        <Grid Grid item xs={6} sm={12}>
            <Tooltip placement='top' title='Xem các ngày nghỉ trong khoảng thời gian yêu cầu' arrow>
                <Button
                    startIcon={<EventIcon />}
                    className='btn bgc-pink d-inline-flex text-white'
                    variant='contained'
                    onClick={() => setOpenPublicHolidayDatePopup(true)}
                    fullWidth
                    disabled={isSubmitting}>
                    Ngày nghỉ
                </Button>
            </Tooltip>
            {openPublicHolidayDatePopup && (
                <PublicHolidayDatePopup
                    open={openPublicHolidayDatePopup}
                    handleClose={() => setOpenPublicHolidayDatePopup(false)}
                />
            )}
        </Grid>
    );
};

const ButtonOpenStaffWorkSchedulePopup = ({ isSubmitting, readOnly }) => {
    const [openStaffWorkSchedulePopup, setOpenStaffWorkSchedulePopup] = useState(false);

    return (
        <Grid item xs={6} sm={12}>
            <Tooltip placement='top' title='Xem các ca làm việc trong khoảng thời gian xin nghỉ' arrow>
                <Button
                    startIcon={<AssignmentIcon />}
                    className='btn btn-primary  d-inline-flex'
                    variant='contained'
                    color='primary'
                    fullWidth
                    disabled={isSubmitting}
                    onClick={() => setOpenStaffWorkSchedulePopup(true)}>
                    Ca làm việc
                </Button>
            </Tooltip>

            {openStaffWorkSchedulePopup && (
                <StaffWorkSchedulePopup
                    open={openStaffWorkSchedulePopup}
                    handleClose={() => setOpenStaffWorkSchedulePopup(false)}
                />
            )}
        </Grid>
    );
};

export default memo(observer(LeaveRequestForm));
