import { Button, DialogActions, DialogContent, Grid, Tooltip } from "@material-ui/core";
import BlockIcon from "@material-ui/icons/Block";
import SaveIcon from "@material-ui/icons/Save";
import { transformDateDatePicker } from "app/common/CommonFunctions";
import GlobitsCheckBox from "app/common/form/GlobitsCheckBox";
import GlobitsDateTimePicker from "app/common/form/GlobitsDateTimePicker";
import GlobitsPagingAutocompleteV2 from "app/common/form/GlobitsPagingAutocompleteV2";
import GlobitsSelectInput from "app/common/form/GlobitsSelectInput";
import GlobitsPopupV2 from "app/common/GlobitsPopupV2";
import LocalConstants from "app/LocalConstants";
import { useStore } from "app/stores";
import { pagingShiftWork } from "app/views/ShiftWork/ShiftWorkService";
import { saveMultipleStaffWorkSchedule } from "app/views/StaffWorkScheduleV2/StaffWorkScheduleService";
import ChooseUsingStaffSection from "app/views/User/UsingAccountStaff/ChooseUsingStaffSection";
import { Form, Formik } from "formik";
import { observer } from "mobx-react";
import { memo, useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import * as Yup from "yup";

function StaffWorkScheduleMultipleForm(props) {
    const { handleAfterSubmit } = props;

    const { t } = useTranslation();

    const { 
        staffWorkScheduleStore,
        hrRoleUtilsStore
     } = useStore();

     const {
        
     } = hrRoleUtilsStore;

    const {
        handleClose,
        openFormCreateMultipleStaffWorkSchedule,
        selectedStaffWorkSchedule

    } = staffWorkScheduleStore;

    const validationSchema = Yup.object({
        shiftWorks: Yup.array()
            .min(1, t("Bạn phải chọn ít nhất 1 ca làm việc"))
            .required(t("Bạn phải chọn ít nhất 1 ca làm việc")),
        staff: Yup.object().required(t("validation.required")).nullable(),
        workingDate: Yup.date().transform(transformDateDatePicker).required(t("validation.required")).nullable(),
    });

    async function handleSaveForm(values) {
        try {
            const response = await saveMultipleStaffWorkSchedule(values);
            if (typeof handleAfterSubmit === 'function') {
                handleAfterSubmit(values);
            }
            await handleClose();
        }
        catch (error) {

        }
    }

    const [initialValues, setInitialValues] = useState({});

    useEffect(() => {
        setInitialValues({
            ...selectedStaffWorkSchedule,
        });
    }, [selectedStaffWorkSchedule]);

    return (
        <GlobitsPopupV2
            scroll={"body"}
            size='xs'
            open={openFormCreateMultipleStaffWorkSchedule}
            title={"Thêm mới ca làm việc"}
            noDialogContent
            onClosePopup={handleClose}
        >
            <Formik
                validationSchema={validationSchema}
                enableReinitialize
                initialValues={initialValues}
                onSubmit={handleSaveForm}>
                {({ isSubmitting, values }) => (
                    <Form autoComplete='off'>
                        <DialogContent className='o-hidden dialog-body p-12'>
                            <Grid container spacing={2}>
                                <Grid item xs={12} className='pb-0'>
                                    <p className='m-0 p-0 borderThrough2'>Thông tin ca làm việc</p>
                                </Grid>

                                <Grid item xs={12}>
                                    <ChooseUsingStaffSection
                                        label={"Nhân viên được phân"}
                                        name='staff'
                                        required
                                        readOnly
                                    />
                                </Grid>
                                <Grid item xs={12}>
                                    <GlobitsDateTimePicker
                                        label={"Ngày làm việc"}
                                        name='workingDate'
                                        value={values?.workingDate}
                                        required
                                        readOnly
                                    />
                                </Grid>
                                <Grid item xs={12}>
                                    <GlobitsPagingAutocompleteV2
                                        name='shiftWorks'
                                        label={t("staffWorkSchedule.shiftWorks")}
                                        api={pagingShiftWork}
                                        searchObject={{
                                            // departmentId: values?.department?.id,
                                            staffId: values?.staff?.id
                                        }}
                                        getOptionLabel={(option) =>
                                            option?.name && option?.code
                                                ? `${option.name} - ${option.code}`
                                                : option?.name || option?.code || ""
                                        }
                                        disabled={!values?.department?.id}
                                        multiple
                                    // required
                                    />
                                </Grid>

                                {values?.allowOneEntryOnly && (
                                    <Grid item xs={12}>
                                        <GlobitsSelectInput
                                            label='Cách tính thời gian'
                                            name='timekeepingCalculationType'
                                            keyValue='value'
                                            hideNullOption
                                            options={LocalConstants.TimekeepingCalculationType.getListData()}
                                        />
                                    </Grid>
                                )}

                                <Grid item xs={12}>
                                    <Tooltip
                                        placement='top'
                                        arrow
                                        title='Chấm công chỉ yêu cầu nhân viên có 1 lần Checkin vào đầu buổi và 1 lần Checkout'>
                                        <GlobitsCheckBox
                                            label={"Chỉ chấm công vào ra 1 lần"}
                                            name='allowOneEntryOnly'
                                        />
                                    </Tooltip>
                                </Grid>



                                <Grid item xs={12}>
                                    <Tooltip
                                        placement='top'
                                        arrow
                                        title='Kết quả chấm công của nhân viên đối với ca làm việc cần sự xác nhận của người quản lý để dữ liệu được sử dụng cho tính công, tính lương'>
                                        <GlobitsCheckBox
                                            label={"Cần xác nhận của người quản lý"}
                                            name='needManagerApproval'
                                        />
                                    </Tooltip>
                                </Grid>
                            </Grid>
                        </DialogContent>

                        <DialogActions className='dialog-footer flex flex-end flex-middle px-12'>
                            <Tooltip arrow title='Đóng' placement='bottom'>
                                <Button
                                    startIcon={<BlockIcon />}
                                    className='btn btn-secondary d-inline-flex'
                                    onClick={handleClose}
                                    disabled={isSubmitting}>
                                    {t("general.button.cancel")}
                                </Button>
                            </Tooltip>

                            <Tooltip arrow title='Lưu' placement='bottom'>
                                <Button
                                    startIcon={<SaveIcon />}
                                    className='ml-12 btn bgc-lighter-dark-blue d-inline-flex text-white'
                                    type='submit'
                                    disabled={isSubmitting}>
                                    Lưu
                                </Button>
                            </Tooltip>
                        </DialogActions>
                    </Form>
                )}
            </Formik>
        </GlobitsPopupV2>
    );
}

export default memo(observer(StaffWorkScheduleMultipleForm));
