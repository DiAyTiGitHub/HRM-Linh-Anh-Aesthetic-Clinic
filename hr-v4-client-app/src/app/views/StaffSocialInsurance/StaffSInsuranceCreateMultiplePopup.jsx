import { Button, DialogActions, DialogContent, Grid, Tooltip } from "@material-ui/core";
import GlobitsPopupV2 from "app/common/GlobitsPopupV2";
import { Form, Formik } from "formik";
import { observer } from "mobx-react";
import { memo } from "react";
import { useTranslation } from "react-i18next";
import * as Yup from "yup";
import BlockIcon from "@material-ui/icons/Block";
import { useStore } from "app/stores";
import GlobitsPagingAutocomplete from "app/common/form/GlobitsPagingAutocomplete";
import FilterVintageIcon from '@material-ui/icons/FilterVintage';
import { formatDate } from "app/LocalFunction";
import GlobitsPagingAutocompleteV2 from "app/common/form/GlobitsPagingAutocompleteV2";
import { pagingStaff } from "../HumanResourcesInformation/StaffService";
import PeopleIcon from '@material-ui/icons/People';
import { pagingSalaryPeriod } from "../Salary/SalaryPeriod/SalaryPeriodService";
import ConfirmationNumberIcon from '@material-ui/icons/ConfirmationNumber';

function StaffSInsuranceCreateMultiplePopup() {
    const { t } = useTranslation();

    const {
        staffSocialInsuranceStore
    } = useStore();

    const {
        handleClose,
        openAutoCreateInsuranceTicketPopup,
        handleGenerateSocialInsuranceTicketsForStaffsBySalaryPeriod,
        createInsuranceTicket
    } = staffSocialInsuranceStore;

    const validationSchema = Yup.object({
        salaryPeriod: Yup.object().required(t("validation.required")).nullable(),
    });

    return (
        <GlobitsPopupV2
            size={"xs"}
            scroll={"body"}
            open={openAutoCreateInsuranceTicketPopup}
            onClosePopup={handleClose}
            title={"Tạo phiếu BHXH"}
            noDialogContent
        >
            <Formik
                initialValues={createInsuranceTicket}
                onSubmit={(values) => handleGenerateSocialInsuranceTicketsForStaffsBySalaryPeriod(values)}
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
                                                label={"Kỳ lương tạo phiếu BHXH"}
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

                                    <Tooltip placement="top" arrow title="Tạo phiếu đóng BHXH cho toàn bộ nhân viên được đóng BHXH theo kỳ lương">
                                        <Button
                                            startIcon={<ConfirmationNumberIcon />}
                                            className="btn bgc-lighter-dark-blue d-inline-flex"
                                            variant="contained"
                                            color="primary"
                                            type="submit"
                                            disabled={isSubmitting}
                                        >
                                            Tạo phiếu BHXH
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

export default memo(observer(StaffSInsuranceCreateMultiplePopup));
