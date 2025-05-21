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
import DateRangeIcon from '@material-ui/icons/DateRange';
import { useStore } from "app/stores";
import GlobitsPagingAutocomplete from "app/common/form/GlobitsPagingAutocomplete";
import FilterVintageIcon from '@material-ui/icons/FilterVintage';
import { formatDate } from "app/LocalFunction";
import { pagingSalaryPeriod } from "../SalaryPeriod/SalaryPeriodService";

function ExportCommissionPayrollPopup() {
    const { t } = useTranslation();

    const {
        salaryResultStore
    } = useStore();

    const {
        handleClose,
        openExportCMPPopup,
        handleExportExcelCommissionPayroll
    } = salaryResultStore;

    const validationSchema = Yup.object({
        salaryPeriod: Yup.object().required(t("validation.required")).nullable(),

    });

    return (
        <GlobitsPopupV2
            size={"xs"}
            scroll={"body"}
            open={openExportCMPPopup}
            onClosePopup={handleClose}
            title={"Tổng hợp bảng lương hoa hồng"}
            noDialogContent
        >
            <Formik
                initialValues={{
                    salaryPeriod: null,
                }}
                onSubmit={(values) => handleExportExcelCommissionPayroll(values)}
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
                                            <GlobitsPagingAutocomplete
                                                label={"Kỳ lương"}
                                                name="salaryPeriod"
                                                required
                                                api={pagingSalaryPeriod}
                                                getOptionLabel={(option) =>
                                                    option?.name && option?.code
                                                        ? `${option.name} - ${option.code} (${formatDate("DD/MM/YYYY", option.fromDate)} - ${formatDate("DD/MM/YYYY", option.toDate)})`
                                                        : `${option?.name || option?.code || ""}`
                                                }

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

                                    <Tooltip placement="top" arrow title="Tổng hợp bảng lương hoa hồng">
                                        <Button
                                            startIcon={<FilterVintageIcon />}
                                            className="btn bgc-lighter-dark-blue d-inline-flex"
                                            variant="contained"
                                            color="primary"
                                            type="submit"
                                            disabled={isSubmitting}
                                        >
                                            Tạo bảng lương
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

export default memo(observer(ExportCommissionPayrollPopup));
