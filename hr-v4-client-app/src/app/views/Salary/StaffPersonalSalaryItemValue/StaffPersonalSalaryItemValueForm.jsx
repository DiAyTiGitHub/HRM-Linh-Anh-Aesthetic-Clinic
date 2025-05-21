import React, { memo, useEffect } from "react";
import { Formik, Form, useField } from "formik";
import { Grid, DialogActions, Button, DialogContent } from "@material-ui/core";
import SaveIcon from "@material-ui/icons/Save";
import BlockIcon from "@material-ui/icons/Block";
import { useTranslation } from "react-i18next";
import { useStore } from "app/stores";
import { observer } from "mobx-react";
import GlobitsPopup from "app/common/GlobitsPopup";
import FormikFocusError from "app/common/FormikFocusError";
import { pagingStaff } from "../../HumanResourcesInformation/StaffService";
import GlobitsPagingAutocomplete from "../../../common/form/GlobitsPagingAutocomplete";
import { pagingSalaryTemplates } from "../SalaryTemplate/SalaryTemplateService";
import TableStaffSalaryItemValueList from "./TableStaffSalaryItemValueList";
import GlobitsPopupV2 from "app/common/GlobitsPopupV2";

function StaffPersonalSalaryItemValueForm() {
    const { staffSalaryItemValueStore } = useStore();
    const { t } = useTranslation();

    const { handleClose, openCreateEditPopup } = staffSalaryItemValueStore;

    async function handleCalculate(values) {
        console.log(values);
    }

    return (
        <GlobitsPopupV2
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
            <Formik enableReinitialize initialValues={{}} onSubmit={handleCalculate}>
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
                                                // displayData={"displayName"}
                                                api={pagingStaff}
                                                required
                                                getOptionLabel={(option) => {
                                                    return `${option?.displayName || ""} - ${option?.staffCode || ""}`;
                                                }}
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
                                            {t("general.button.calculation")}
                                        </Button>
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

export default memo(observer(StaffPersonalSalaryItemValueForm));
