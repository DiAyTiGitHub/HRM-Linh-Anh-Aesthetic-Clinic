import { Button, DialogActions, DialogContent, Grid, Tooltip } from "@material-ui/core";
import GlobitsPopupV2 from "app/common/GlobitsPopupV2";
import { Form, Formik } from "formik";
import { observer } from "mobx-react";
import { memo } from "react";
import { useTranslation } from "react-i18next";
import * as Yup from "yup";
import BlockIcon from "@material-ui/icons/Block";
import GlobitsDateTimePicker from "app/common/form/GlobitsDateTimePicker";
import moment from "moment";
import { useStore } from "app/stores";
import FilterVintageIcon from '@material-ui/icons/FilterVintage';

function ExportLATimekeepingDataPopup() {
    const { t } = useTranslation();

    const {
        timeSheetDetailStore
    } = useStore();

    const {
        handleClose,
        openExportLATimekeepingDataPopup,
        handleExportLATimekeepingData
    } = timeSheetDetailStore;

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
            open={openExportLATimekeepingDataPopup}
            onClosePopup={handleClose}
            title={"Khoảng thời gian lấy dữ liệu chấm công"}
            noDialogContent
        >
            <Formik
                initialValues={{
                    salaryPeriod: null,
                }}
                onSubmit={(values) => handleExportLATimekeepingData(values)}
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

                                    <Tooltip placement="top" arrow title="Lấy dữ liệu chấm công từ máy chấm công">
                                        <Button
                                            startIcon={<FilterVintageIcon />}
                                            className="btn bgc-lighter-dark-blue d-inline-flex"
                                            variant="contained"
                                            color="primary"
                                            type="submit"
                                            disabled={isSubmitting}
                                        >
                                            Lấy dữ liệu chấm công
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

export default memo(observer(ExportLATimekeepingDataPopup));
