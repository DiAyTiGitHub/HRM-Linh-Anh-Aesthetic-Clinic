import { Button, DialogActions, DialogContent, Grid, Tooltip } from "@material-ui/core";
import GlobitsPopupV2 from "app/common/GlobitsPopupV2";
import { Form, Formik } from "formik";
import { observer } from "mobx-react";
import { memo } from "react";
import { useTranslation } from "react-i18next";
import * as Yup from "yup";
import { useStore } from "../../stores";
import BlockIcon from "@material-ui/icons/Block";
import GlobitsDateTimePicker from "app/common/form/GlobitsDateTimePicker";
import moment from "moment";
import DateRangeIcon from '@material-ui/icons/DateRange';

function StaffFixShiftDateRangePopup() {
    const { t } = useTranslation();

    const {
        staffStore
    } = useStore();

    const {
        handleClose,
        openFixShiftDateRangePopup,
        handeConfirmAssignFixShiftPopup
    } = staffStore;

    const validationSchema = Yup.object({
        fromDate: Yup.date()
            .transform((castValue, originalValue) => (originalValue ? new Date(originalValue) : castValue))
            // .required(t("validation.required"))
            .typeError("Từ ngày không đúng định dạng")
            .nullable(),

        toDate: Yup.date()
            .transform((castValue, originalValue) => (originalValue ? new Date(originalValue) : castValue))
            // .required(t("validation.required"))
            .typeError("Đến ngày không đúng định dạng")
            .nullable()
            .test("is-greater-or-equal", "Đến ngày phải lớn hơn hoặc bằng Từ ngày", function (value) {
                const { fromDate } = this.parent;
                if (fromDate && value) {
                    return moment(value).isSameOrAfter(moment(fromDate), "date");
                }
                return true;
            }),
    });

    return (
        <GlobitsPopupV2
            size={"xs"}
            scroll={"body"}
            open={openFixShiftDateRangePopup}
            onClosePopup={handleClose}
            title={"Khoảng thời gian phân ca"}
            noDialogContent
        >
            <Formik
                initialValues={{
                    fromDate: null,
                    toDate: null,
                }}
                onSubmit={(values) => handeConfirmAssignFixShiftPopup(values)}
                validationSchema={validationSchema}
            >
                {({ isSubmitting, setFieldValue, values }) => (
                    <Form autoComplete='off'>
                        <DialogContent
                            className='dialog-body p-12'
                        // style={{ maxHeight: "80vh", minWidth: "300px" }}
                        >
                            <Grid container spacing={2}>
                                <Grid item xs={12}>
                                    <Grid container spacing={2}>
                                        <Grid item xs={12}>
                                            <GlobitsDateTimePicker
                                                label="Từ ngày"
                                                name='fromDate'
                                                required
                                            />
                                        </Grid>

                                        <Grid item xs={12}>
                                            <GlobitsDateTimePicker
                                                label="Đến ngày"
                                                name='toDate'
                                                required
                                            />
                                        </Grid>
                                    </Grid>
                                </Grid>
                            </Grid>
                        </DialogContent>

                        <div className='dialog-footer dialog-footer-v2 py-8'>
                            <DialogActions className='p-0'>
                                <div className='flex flex-space-between flex-middle'>
                                    <Button
                                        startIcon={<BlockIcon />}
                                        variant='contained'
                                        className='mr-12 btn btn-secondary d-inline-flex'
                                        color='secondary'
                                        onClick={handleClose}>
                                        {t("general.button.cancel")}
                                    </Button>

                                    <Tooltip
                                        arrow
                                        placement="top"
                                        title="Tạo lịch làm việc cố định làm việc cho nhân viên"
                                    >
                                        <Button
                                            startIcon={<DateRangeIcon />}
                                            className='mr-0 btn btn-primary d-inline-flex'
                                            variant='contained'
                                            color='primary'
                                            type='submit'>
                                            Tạo lịch cố định
                                        </Button>
                                    </Tooltip>

                                </div>
                            </DialogActions>
                        </div>
                    </Form>
                )
                }
            </Formik >
        </GlobitsPopupV2 >
    );
}

export default memo(observer(StaffFixShiftDateRangePopup));
