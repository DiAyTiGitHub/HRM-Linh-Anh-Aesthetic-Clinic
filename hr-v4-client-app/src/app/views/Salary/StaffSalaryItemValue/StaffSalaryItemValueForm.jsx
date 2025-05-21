import { Button, DialogActions, DialogContent, Grid } from "@material-ui/core";
import BlockIcon from "@material-ui/icons/Block";
import SaveIcon from "@material-ui/icons/Save";
import GlobitsPopup from "app/common/GlobitsPopup";
import { useStore } from "app/stores";
import { Form, Formik } from "formik";
import { observer } from "mobx-react";
import { memo } from "react";
import { useTranslation } from "react-i18next";
import GlobitsPagingAutocomplete from "../../../common/form/GlobitsPagingAutocomplete";
import { pagingStaff } from "../../HumanResourcesInformation/StaffService";
import { pagingSalaryTemplates } from "../SalaryTemplate/SalaryTemplateService";
import TableStaffSalaryItemValueList from "./TableStaffSalaryItemValueList";

function StaffSalaryItemValueForm() {
    const { staffSalaryItemValueStore } = useStore();
    const { t } = useTranslation();

    const { handleClose, saveStaffSalaryItemValue, openCreateEditPopup, selectedStaffSalaryItemValue } =
        staffSalaryItemValueStore;

    async function handleSaveForm(values) {
        saveStaffSalaryItemValue(values);
    }

    return (
        <GlobitsPopup
            scroll={"body"}
            size='md'
            open={openCreateEditPopup}
            noDialogContent
            title={
                t("general.button.add") +
                ", " +
                t("general.button.edit") +
                " " +
                t("navigation.staffSalaryItemValue.title")
            }
            onClosePopup={handleClose}>
            <Formik enableReinitialize initialValues={{}} onSubmit={handleSaveForm}>
                {({ isSubmitting, values, setFieldValue, initialValues }) => {
                    return (
                        <Form autoComplete='off' autocomplete='off'>
                            <div className='dialog-body'>
                                <DialogContent className='p-12'>
                                    <Grid container spacing={2}>
                                        <Grid item xs={12}>
                                            <GlobitsPagingAutocomplete
                                                label='Chọn nhân viên'
                                                name={"staff"}
                                                getOptionLabel={(option) => {
                                                    return `${option?.displayName || ""} - ${option?.staffCode || ""}`;
                                                }}
                                                api={pagingStaff}
                                                required
                                            />
                                        </Grid>

                                        <Grid item xs={12}>
                                            <GlobitsPagingAutocomplete
                                                label='Chọn mẫu bảng lương'
                                                name={"salaryTemplate"}
                                                api={pagingSalaryTemplates}
                                                displayData={"name"}
                                                required
                                            />
                                        </Grid>
                                        <Grid item xs={12}>
                                            <TableStaffSalaryItemValueList />
                                        </Grid>
                                    </Grid>
                                </DialogContent>
                            </div>

                            <div className='dialog-footer py-8'>
                                <DialogActions className='p-0'>
                                    <div className='flex flex-space-between flex-middle'>
                                        <Button
                                            startIcon={<BlockIcon />}
                                            variant='contained'
                                            className='mr-12 btn btn-secondary d-inline-flex'
                                            color='secondary'
                                            onClick={handleClose}
                                            disabled={isSubmitting}>
                                            {t("general.button.cancel")}
                                        </Button>
                                        <Button
                                            startIcon={<SaveIcon />}
                                            className='mr-0 btn btn-primary d-inline-flex'
                                            variant='contained'
                                            color='primary'
                                            type='submit'
                                            disabled={isSubmitting}>
                                            {t("general.button.save")}
                                        </Button>
                                    </div>
                                </DialogActions>
                            </div>
                        </Form>
                    );
                }}
            </Formik>
        </GlobitsPopup>
    );
}

export default memo(observer(StaffSalaryItemValueForm));
