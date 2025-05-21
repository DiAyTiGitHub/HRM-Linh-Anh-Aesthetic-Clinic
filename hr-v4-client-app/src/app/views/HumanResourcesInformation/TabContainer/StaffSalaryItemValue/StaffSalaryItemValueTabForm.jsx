import { Button, Checkbox, DialogActions, DialogContent, FormControlLabel, Grid, Typography } from "@material-ui/core";
import BlockIcon from "@material-ui/icons/Block";
import SaveIcon from "@material-ui/icons/Save";
import GlobitsDateTimePicker from "app/common/form/GlobitsDateTimePicker";
import GlobitsPagingAutocomplete from "app/common/form/GlobitsPagingAutocomplete";
import GlobitsPagingAutocompleteV2 from "app/common/form/GlobitsPagingAutocompleteV2";
import GlobitsSelectInputV2 from "app/common/form/GlobitsSelectInputV2";
import GlobitsVNDCurrencyInput from "app/common/form/GlobitsVNDCurrencyInput";
import GlobitsPopupV2 from "app/common/GlobitsPopupV2";
import LocalConstants from "app/LocalConstants";
import { useStore } from "app/stores";
import { pagingSalaryItem } from "app/views/Salary/SalaryItemV2/SalaryItemV2Service";
import { Form, Formik } from "formik";
import { observer } from "mobx-react";
import { memo } from "react";
import { useTranslation } from "react-i18next";
import * as Yup from "yup";
import { pagingStaff } from "../../StaffService";
import SelectFile from "app/views/StaffDocumentItem/SelectFile";

function StaffSalaryItemValueForm(props) {
    const { handleAfterSave } = props;
    const { staffSalaryItemValueStore } = useStore();
    const { t } = useTranslation();

    const { handleClose, openCreateEditPopup, selectedStaffSalaryItemValue, saveStaffSalaryItemValue } =
        staffSalaryItemValueStore;

    async function handleSaveForm(values) {
        console.log("selectedStaffSalaryItemValue", selectedStaffSalaryItemValue);
        await saveStaffSalaryItemValue(values);
        if (typeof handleAfterSave === "function") {
            handleAfterSave(values);
        }
    }

    const validationSchema = Yup.object({
        staff: Yup.object().required(t("validation.required")).nullable(),
        salaryItem: Yup.object().required(t("validation.required")).nullable(),
    });

    return (
        <GlobitsPopupV2
            scroll={"body"}
            size='sm'
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
            <Formik
                enableReinitialize
                initialValues={selectedStaffSalaryItemValue}
                validationSchema={validationSchema}
                onSubmit={handleSaveForm}>
                {({ isSubmitting, values, setFieldValue }) => (
                    <Form autoComplete='off'>
                        <div className='dialog-body'>
                            <DialogContent className='p-12'>
                                <Grid container spacing={2}>
                                    {/* Staff readonly */}
                                    <Grid item xs={12}>
                                        <GlobitsPagingAutocomplete
                                            label='Nhân viên'
                                            name='staff'
                                            getOptionLabel={(option) =>
                                                `${option?.displayName || ""} - ${option?.staffCode || ""}`
                                            }
                                            api={pagingStaff}
                                            readOnly
                                            required={true}
                                        />
                                    </Grid>

                                    {/* SalaryItem readonly={values?.id ? true : false}} */}
                                    <Grid item xs={12}>
                                        <GlobitsPagingAutocompleteV2
                                            label='Phần tử lương'
                                            name='salaryItem'
                                            getOptionLabel={(option) => `${option?.name || ""} - ${option?.code || ""}`}
                                            api={pagingSalaryItem}
                                            readOnly={values?.id ? true : false}
                                            required={true}
                                            searchObject={{
                                                calculationType: 5,
                                            }}
                                            handleChange={(event, value) => {
                                                console.log("Selected SalaryItem:", value);
                                                setFieldValue("salaryItem", value);
                                                setFieldValue("value", value?.defaultValue || 0);

                                                if (value?.calculationType != null) {
                                                    setFieldValue("calculationType", value?.calculationType);
                                                }
                                            }}
                                        />
                                    </Grid>

                                    {/* Value */}
                                    <Grid item xs={12}>
                                        <GlobitsVNDCurrencyInput label='Giá trị' name='value' />
                                    </Grid>

                                    {/* CalculationType */}
                                    {/* <Grid item xs={12}>
                                        <GlobitsSelectInputV2
                                            label={"Cách tính giá trị"}
                                            name='calculationType'
                                            value={values?.calculationType}
                                            options={LocalConstants.SalaryItemCalculationType.getListData()}
                                            hideNullOption={true}
                                        />
                                    </Grid> */}

                                    {/* From Date */}
                                    <Grid item xs={6}>
                                        <GlobitsDateTimePicker
                                            label='Từ ngày'
                                            name='fromDate'
                                            onChange={(date) => setFieldValue("fromDate", date)}
                                            fullWidth
                                        />
                                    </Grid>

                                    {/* To Date */}
                                    <Grid item xs={6}>
                                        <GlobitsDateTimePicker
                                            label='Đến ngày'
                                            name='toDate'
                                            onChange={(date) => setFieldValue("toDate", date)}
                                            fullWidth
                                        />
                                    </Grid>

                                    {/* isCurrent */}
                                    <Grid item xs={6}>
                                        <FormControlLabel
                                            style={{ margin: 0 }}
                                            control={
                                                <Checkbox
                                                    style={{ margin: 0 }}
                                                    checked={values?.current || false}
                                                    color='primary'
                                                    onChange={(e) => setFieldValue("current", e.target.checked)}
                                                />
                                            }
                                            label='Đang áp dụng'
                                        />
                                    </Grid>

                                    <Grid item xs={6} className='pb-0 flex flex-col items-start'>
                                        <label className={`label-container`}>Tệp đính kèm </label>

                                        <SelectFile
                                            name={"file"}
                                            fileProp={values?.file}
                                            showPreview={true}
                                            showDowload={true}
                                            showDelete={true}
                                            showName={false}
                                            // handleAffterSubmit={handleAffterSubmitFile}
                                            maxFileSize={5242880}
                                        />
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
                )}
            </Formik>
        </GlobitsPopupV2>
    );
}

export default memo(observer(StaffSalaryItemValueForm));
