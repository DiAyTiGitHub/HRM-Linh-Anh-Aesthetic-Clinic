import { Button , DialogActions , DialogContent , Grid } from "@material-ui/core";
import BlockIcon from "@material-ui/icons/Block";
import SaveIcon from "@material-ui/icons/Save";
import GlobitsDateTimePicker from "app/common/form/GlobitsDateTimePicker";
import GlobitsPagingAutocompleteV2 from "app/common/form/GlobitsPagingAutocompleteV2";
import GlobitsSelectInput from "app/common/form/GlobitsSelectInput";
import GlobitsTextField from "app/common/form/GlobitsTextField";
import FormikFocusError from "app/common/FormikFocusError";
import GlobitsPopupV2 from "app/common/GlobitsPopupV2";
import LocalConstants from "app/LocalConstants";
import { useStore } from "app/stores";
import { Form , Formik , getIn } from "formik";
import { observer } from "mobx-react";
import moment from "moment";
import { memo , useEffect } from "react";
import { useTranslation } from "react-i18next";
import * as Yup from "yup";
import { pagingStaff } from "../HumanResourcesInformation/StaffService";
import { pagingShiftWork } from "../ShiftWork/ShiftWorkService";
import ChooseUsingStaffSection from "../User/UsingAccountStaff/ChooseUsingStaffSection";
import StaffWorkScheduleSelectionForm from "app/common/SelectComponent/SelectSigle/StaffWorkScheduleSelectionForm";
import StaffSelectionForm from "app/common/SelectComponent/SelectSigle/StaffSelectionForm";
import SCRFormAutoCompleteToShiftWork from "./SCRFormAutoCompleteToShiftWork";
import { formatDate } from "../../LocalFunction";
import { getCurrentStaff } from "../profile/ProfileService";

function ShiftChangeRequestForm(props) {
    const {readOnly} = props;
    const {shiftChangeRequestStore , hrRoleUtilsStore} = useStore();
    const {t} = useTranslation();
    const {
        handleClose ,
        saveShiftChangeRequest ,
        pagingShiftChangeRequest ,
        selectedShiftChangeRequest ,
        openCreateEditPopup ,
        openViewPopup ,
        handleSetSelectedShiftChangeRequest
    } = shiftChangeRequestStore;

    const {
        isAdmin ,
        isManager
    } = hrRoleUtilsStore;


    const validationSchema = Yup.object({
        registerStaff:Yup.object().required(t("validation.required")).nullable() ,
        requestDate:Yup.date()
            .transform(function transformDate(castValue , originalValue) {
                return originalValue ? new Date(originalValue) : castValue;
            })
            .required(t("validation.required")) ,

        fromWorkingDate:Yup.date()
            .transform(function transformDate(castValue , originalValue) {
                return originalValue ? new Date(originalValue) : castValue;
            })
            .test(
                "is-greater-or-equal" ,
                "Ngày làm việc cần thay đổi phải lớn hơn hoặc bằng ngày yêu cầu" ,
                function (value) {
                    const {requestDate} = this.parent;
                    if (requestDate && value) {
                        return moment(value).isSameOrAfter(moment(requestDate) , "date");
                    }
                    return true;
                }
            )
            .transform(function transformDate(castValue , originalValue) {
                return originalValue ? new Date(originalValue) : castValue;
            })
            .required(t("validation.required"))
            .typeError("Ngày làm việc cần thay đổi không đúng định dạng") ,
        fromShiftWork:Yup.object().required(t("validation.required")).nullable() ,
        toWorkingDate:Yup.date()
            .transform(function transformDate(castValue , originalValue) {
                return originalValue ? new Date(originalValue) : castValue;
            })
            .test(
                "not-same-as-fromWorkingDate" ,
                "Ngày làm việc yêu cầu đổi không được trùng với ngày làm việc hiện tại" ,
                function (value) {
                    const {fromWorkingDate} = this.parent;
                    if (value && fromWorkingDate) {
                        return !moment(value).isSame(moment(fromWorkingDate) , "date");
                    }
                    return true;
                }
            )
            .test(
                "not-same-date-and-shift" ,
                "Ngày làm việc và ca làm việc yêu cầu đổi không được trùng với ngày và ca hiện tại" ,
                function (value) {
                    const {fromWorkingDate , fromShiftWork , toShiftWork} = this.parent;
                    if (value && fromWorkingDate && fromShiftWork && toShiftWork) {
                        const sameDate = moment(value).isSame(moment(fromWorkingDate) , "date");
                        const sameShift = fromShiftWork?.id === toShiftWork?.id;
                        // Chỉ không hợp lệ nếu cùng ngày và cùng ca
                        return !(sameDate && sameShift);
                    }
                    return true;
                }
            )
            .required(t("validation.required"))
            .typeError("Ngày làm việc yêu cầu đổi không đúng định dạng") ,

        toShiftWork:Yup.object().required(t("validation.required")).nullable() ,
    });

    async function handleSaveForm(values) {
        try {
            const response = await saveShiftChangeRequest(values);
            if (response) await pagingShiftChangeRequest();
        } catch (error) {
            console.error(error);
        }
    }

    const setUpInnitData = async () => {
        const {data} = await getCurrentStaff();
        if (!(isAdmin || isManager)) {
            handleSetSelectedShiftChangeRequest({
                registerStaff:data ,
            });
        }
    }

    useEffect(() => {
        setUpInnitData();
    } , []);
    return (
        <GlobitsPopupV2
            scroll={"body"}
            size='sm'
            open={openCreateEditPopup || openViewPopup}
            noDialogContent
            title={openViewPopup ? ("Xem chi tiết " + t("navigation.shiftChangeRequest.title")) : ((selectedShiftChangeRequest?.id ? t("general.button.edit") : t("general.button.add")) +
                " " +
                t("navigation.shiftChangeRequest.title"))}
            onClosePopup={handleClose}>
            <Formik
                validationSchema={validationSchema}
                enableReinitialize
                initialValues={{
                    ... selectedShiftChangeRequest ,
                    staffWorkSchedule:null ,
                }}
                onSubmit={handleSaveForm}>
                {({isSubmitting , values , setFieldValue , initialValues}) => {
                    return (
                        <Form autoComplete='off'>
                            <div className='dialog-body'>
                                <DialogContent className='p-12'>
                                    <FormikFocusError/>
                                    <Grid container spacing={2}>
                                        {selectedShiftChangeRequest?.id ? null : (
                                            <>
                                                <Grid item xs={12} className='pb-0'>
                                                    <p className='m-0 p-0 borderThrough2'>
                                                        Tự động điền từ ca làm việc
                                                    </p>
                                                </Grid>

                                                <Grid item xs={12} sm={8}>
                                                    <StaffWorkScheduleSelectionForm
                                                        handleAfterSubmit={(staffWorkSchedule) => {
                                                            setFieldValue("registerStaff" , staffWorkSchedule?.staff);
                                                            setFieldValue(
                                                                "fromWorkingDate" ,
                                                                staffWorkSchedule?.workingDate
                                                            );
                                                            setFieldValue(
                                                                "fromShiftWork" ,
                                                                staffWorkSchedule?.shiftWork
                                                            );
                                                        }}
                                                        readOnly={readOnly || !(isAdmin || isManager)}
                                                        isFutureDate={true}
                                                    />
                                                </Grid>
                                            </>
                                        )}

                                        <Grid item xs={12} sm={4}>
                                            <GlobitsDateTimePicker
                                                label={t("Ngày tạo yêu cầu")}
                                                name='requestDate'
                                                required
                                                readOnly={!isAdmin && !isManager || readOnly}
                                            />
                                        </Grid>

                                        <Grid item xs={12} className='pb-0'>
                                            <p className='m-0 p-0 borderThrough2'>Ca làm việc hiện tại</p>
                                        </Grid>

                                        <Grid item xs={12} sm={6} md={6}>
                                            <Grid item xs>
                                                <ChooseUsingStaffSection
                                                    label='Nhân viên đăng ký'
                                                    name='registerStaff'
                                                    required
                                                    readOnly={true}
                                                />
                                            </Grid>
                                        </Grid>

                                        <Grid item xs={12} sm={6} md={6}>
                                            <GlobitsDateTimePicker
                                                label={t("Ngày làm việc cần thay đổi")}
                                                name='fromWorkingDate'
                                                readOnly
                                                required
                                            />
                                        </Grid>

                                        <Grid item xs={12} sm={6} md={6}>
                                            <GlobitsPagingAutocompleteV2
                                                name='fromShiftWork'
                                                label={"Ca làm việc cần thay đổi"}
                                                api={pagingShiftWork}
                                                readOnly
                                                required
                                                getOptionDisabled={(option) => {
                                                    const toShiftWork = getIn(values , "toShiftWork");
                                                    if (toShiftWork?.id === option.id) {
                                                        return true;
                                                    }
                                                    return false;
                                                }}
                                                getOptionLabel={(option) =>
                                                    option?.name && option?.code
                                                        ? `${option.name} - ${option.code}`
                                                        : option?.name || option?.code || ""
                                                }
                                            />
                                        </Grid>


                                        <Grid item xs={12} className='pb-0'>
                                            <p className='m-0 p-0 borderThrough2'>
                                                Thông tin ca mong muốn
                                            </p>
                                        </Grid>

                                        <Grid item xs={12} sm={6} md={6}>
                                            <GlobitsDateTimePicker
                                                label={t("Ngày làm việc được yêu cầu đổi")}
                                                name='toWorkingDate'
                                                required
                                                readOnly={
                                                    readOnly ||
                                                    values?.approvalStatus ===
                                                    LocalConstants.ShiftChangeRequestApprovalStatus.APPROVED.value
                                                }
                                            />
                                        </Grid>

                                        <Grid item xs={12} sm={6} md={6}>
                                            <SCRFormAutoCompleteToShiftWork/>
                                        </Grid>

                                        {selectedShiftChangeRequest?.id && (
                                            <>
                                                <Grid item xs={12} sm={6} md={6}>
                                                    <GlobitsSelectInput
                                                        label={"Trạng thái yêu cầu"}
                                                        name='approvalStatus'
                                                        keyValue='value'
                                                        hideNullOption={true}
                                                        readOnly
                                                        options={LocalConstants.ShiftChangeRequestApprovalStatus.getListData()}
                                                    />
                                                </Grid>

                                                <Grid item xs={12} sm={6} md={6}>
                                                    <GlobitsPagingAutocompleteV2
                                                        name='approvalStaff.displayName'
                                                        readOnly
                                                        label={"Người xác nhận"}
                                                        api={pagingStaff}
                                                        getOptionLabel={(option) =>
                                                            option?.displayName && option?.staffCode
                                                                ? `${option.displayName} - ${option.staffCode}`
                                                                : option?.displayName || option?.staffCode || ""
                                                        }
                                                    />
                                                </Grid>
                                            </>
                                        )}

                                        <Grid item xs={12}>
                                            <GlobitsTextField
                                                label={"Lý do đổi ca"}
                                                name='requestReason'
                                                multiline
                                                rows={3}
                                                readOnly={readOnly}
                                            />
                                        </Grid>
                                    </Grid>
                                </DialogContent>
                            </div>

                            <div className='dialog-footer dialog-footer-v2 py-8'>
                                <DialogActions className='p-0'>
                                    <div className='flex flex-space-between flex-middle'>
                                        <Button
                                            startIcon={<BlockIcon/>}
                                            variant='contained'
                                            className={`${!readOnly && "mr-12"} btn-secondary d-inline-flex`}
                                            color='secondary'
                                            onClick={handleClose}
                                            disabled={isSubmitting}>
                                            {t("general.button.cancel")}
                                        </Button>
                                        {!readOnly && (
                                            <Button
                                                startIcon={<SaveIcon/>}
                                                className='mr-0 btn btn-primary d-inline-flex'
                                                variant='contained'
                                                color='primary'
                                                type='submit'
                                                disabled={isSubmitting}>
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

export default memo(observer(ShiftChangeRequestForm));
