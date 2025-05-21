import { Button , DialogActions , DialogContent , Grid } from "@material-ui/core";
import GlobitsPopupV2 from "app/common/GlobitsPopupV2";
import { StaffSocialInsurance } from "app/common/Model/StaffSocialInsurance";
import GlobitsDateTimePicker from "app/common/form/GlobitsDateTimePicker";
import GlobitsTextField from "app/common/form/GlobitsTextField";
import GlobitsVNDCurrencyInput from "app/common/form/GlobitsVNDCurrencyInput";
import { useStore } from "app/stores";
import { Form , Formik } from "formik";
import { observer } from "mobx-react";
import moment from "moment";
import React , { memo, useEffect , useState } from "react";
import { useTranslation } from "react-i18next";
import * as Yup from "yup";

 function StaffSocialInsuranceV2CUForm(props) {
    const {t} = useTranslation();
    const {staffSocialInsuranceStore} = useStore();

    const {
        openCreateEditPopup ,
        handleClose ,
        selectedStaffSocialInsurance ,
        saveStaffSocialInsurance ,
    } = staffSocialInsuranceStore;

       const {selectedStaff} = useStore().staffStore;

    const handleSubmitForm = (values) => {
        let dto = {
            ... values ,
            staff:{
                id:selectedStaff?.id
            }
        }
        saveStaffSocialInsurance(dto)
        // pagingStaffSocialInsurance();
    };

 
    const validationSchema = Yup.object({
        startDate:Yup.date()
            .test(
                "is-greater-or-equal" ,
                "Ngày bắt đầu hiệu lực phải lớn hơn hoặc bằng ngày thiết lập" ,
                function (value) {
                    const {signedDate} = this.parent;
                    if (signedDate && value) {
                        return moment(value).isSameOrAfter(moment(signedDate) , "date");
                    }
                    return true;
                }
            )
            .transform(function transformDate(castValue , originalValue) {
                return originalValue ? new Date(originalValue) : castValue;
            })
            .required(t("validation.required"))
            .typeError("Ngày bắt đầu không đúng định dạng")
            .nullable() ,

        endDate:Yup.date()
            .test("is-greater" , "Ngày kết thúc phải lớn ngày bắt đầu" , function (value) {
                const {startDate} = this.parent;
                if (startDate && value) {
                    return moment(value).isAfter(moment(startDate) , "date");
                }
                return true;
            })
            .transform(function transformDate(castValue , originalValue) {
                return originalValue ? new Date(originalValue) : castValue;
            })
            .required(t("validation.required"))
            .typeError("Ngày kết thúc không đúng định dạng")
            .nullable() ,
    });

    const [formValues , setFormValues] = useState(null);

    const updateTotalInsuranceAmount = (values , setFieldValue) => {
        const insuranceSalary = values.insuranceSalary || 0;

        // Cá nhân đóng
        const staffSocial = (insuranceSalary * (values.staffSocialInsurancePercentage || 0)) / 100;
        const staffHealth = (insuranceSalary * (values.staffHealthInsurancePercentage || 0)) / 100;
        const staffUnemp = (insuranceSalary * (values.staffUnemploymentInsurancePercentage || 0)) / 100;
        const staffTotal = staffSocial + staffHealth + staffUnemp;

        // Công ty đóng
        const orgSocial = (insuranceSalary * (values.orgSocialInsurancePercentage || 0)) / 100;
        const orgHealth = (insuranceSalary * (values.orgHealthInsurancePercentage || 0)) / 100;
        const orgUnemp = (insuranceSalary * (values.orgUnemploymentInsurancePercentage || 0)) / 100;
        const orgTotal = orgSocial + orgHealth + orgUnemp;

        const totalInsurance = staffTotal + orgTotal;

        setFieldValue("staffSocialInsuranceAmount" , staffSocial);
        setFieldValue("staffHealthInsuranceAmount" , staffHealth);
        setFieldValue("staffUnemploymentInsuranceAmount" , staffUnemp);
        setFieldValue("staffTotalInsuranceAmount" , staffTotal);

        setFieldValue("orgSocialInsuranceAmount" , orgSocial);
        setFieldValue("orgHealthInsuranceAmount" , orgHealth);
        setFieldValue("orgUnemploymentInsuranceAmount" , orgUnemp);
        setFieldValue("orgTotalInsuranceAmount" , orgTotal);

        setFieldValue("totalInsuranceAmount" , totalInsurance);
    };

    return (
        <GlobitsPopupV2
            scroll={"body"}
            open={openCreateEditPopup}
            onClosePopup={handleClose}
            noDialogContent
            title={
                <span className=''>
                    {selectedStaffSocialInsurance?.id ? t("general.button.add") : t("general.button.edit")}{" "}
                    {t("navigation.insurance.staffSocialInsurance")}
                </span>
            }>
            <Formik
                initialValues={selectedStaffSocialInsurance}
                onSubmit={handleSubmitForm}
                validationSchema={validationSchema}>
                {({isSubmitting , values , setFieldValue}) => (
                    <Form autoComplete='off'>
                        <DialogContent className='dialog-body p-12' style={{maxHeight:"80vh" , minWidth:"300px"}}>
                            <Grid container spacing={2}>
                                <Grid item xs={12} sm={6} md={4}>
                                    <GlobitsDateTimePicker
                                        label={"Ngày bắt đầu"}
                                        name='startDate'
                                        value={values.startDate}
                                        required
                                    />
                                </Grid>

                                <Grid item xs={12} sm={6} md={4}>
                                    <GlobitsDateTimePicker
                                        label={"Ngày kết thúc"}
                                        name='endDate'
                                        value={values.endDate}
                                        required
                                    />
                                </Grid>

                                <Grid item xs={12} sm={6} md={4}>
                                    <GlobitsVNDCurrencyInput
                                        label={"Mức lương đóng bảo hiểm xã hội"}
                                        name='insuranceSalary'
                                        onChange={(e) => {
                                            let value = e.target.value;
                                            setFieldValue("insuranceSalary" , value);
                                            updateTotalInsuranceAmount({
                                                ... values ,
                                                insuranceSalary:value
                                            } , setFieldValue);
                                        }}
                                    />
                                </Grid>

                                <Grid item xs={12} className="pb-0">
                                    <p className="m-0 p-0 borderThrough2">
                                        Nhân viên đóng BHXH
                                    </p>
                                </Grid>

                                <Grid item xs={12} sm={6} md={4}>
                                    <GlobitsVNDCurrencyInput
                                        label={"Tỷ lệ cá nhân đóng BHXH(%)"}
                                        name='staffSocialInsurancePercentage'
                                        onChange={(e) => {
                                            let value = e.target.value;
                                            setFieldValue("staffSocialInsurancePercentage" , value);
                                            updateTotalInsuranceAmount({
                                                ... values ,
                                                staffSocialInsurancePercentage:value
                                            } , setFieldValue);
                                        }}
                                    />
                                </Grid>
                                
                                <Grid item xs={12} sm={6} md={4}>
                                    <GlobitsVNDCurrencyInput
                                        label={"Số tiền BHXH nhân viên đóng"}
                                        name='staffSocialInsuranceAmount'
                                        disabled
                                    />
                                </Grid>

                                <Grid item xs={12} sm={6} md={4}>
                                    <GlobitsVNDCurrencyInput
                                        label={"Tỷ lệ đóng BHYT của nhân viên(%)"}
                                        name='staffHealthInsurancePercentage'
                                        onChange={(e) => {
                                            let value = e.target.value;
                                            setFieldValue("staffHealthInsurancePercentage" , value);
                                            updateTotalInsuranceAmount({
                                                ... values ,
                                                staffHealthInsurancePercentage:value
                                            } , setFieldValue);
                                        }}
                                    />
                                </Grid>

                                <Grid item xs={12} sm={6} md={4}>
                                    <GlobitsVNDCurrencyInput
                                        label={"Số tiền BHYT nhân viên đóng"}
                                        name='staffHealthInsuranceAmount'
                                        disabled
                                    />
                                </Grid>

                                <Grid item xs={12} sm={6} md={4}>
                                    <GlobitsVNDCurrencyInput
                                        label={"Tỷ lệ đóng BHTN của nhân viên(%)"}
                                        name='staffUnemploymentInsurancePercentage'
                                        onChange={(e) => {
                                            let value = e.target.value;
                                            setFieldValue("staffUnemploymentInsurancePercentage" , value);
                                            updateTotalInsuranceAmount({
                                                ... values ,
                                                staffUnemploymentInsurancePercentage:value
                                            } , setFieldValue);
                                        }}
                                    />
                                </Grid>

                                <Grid item xs={12} sm={6} md={4}>
                                    <GlobitsVNDCurrencyInput
                                        label={"Số tiền BHTN nhân viên đóng"}
                                        name='staffUnemploymentInsuranceAmount'
                                        disabled
                                    />
                                </Grid>

                                <Grid item xs={12} sm={6} md={4}>
                                    <GlobitsVNDCurrencyInput
                                        label={"Tổng tiền bảo hiểm mà nhân viên đóng"}
                                        name='staffTotalInsuranceAmount'
                                        disabled
                                    />
                                </Grid>
                                <Grid item xs={12} className="pb-0">
                                    <p className="m-0 p-0 borderThrough2">
                                        Công ty đóng BHXH
                                    </p>
                                </Grid>
                                <Grid item xs={12} sm={6} md={4}>
                                    <GlobitsVNDCurrencyInput
                                        label={"Tỷ lệ đơn vị đóng bảo hiểm xã hội(%)"}
                                        name='orgSocialInsurancePercentage'
                                        onChange={(e) => {
                                            let value = e.target.value;
                                            setFieldValue("orgSocialInsurancePercentage" , value);
                                            updateTotalInsuranceAmount({
                                                ... values ,
                                                orgSocialInsurancePercentage:value
                                            } , setFieldValue);
                                        }}
                                    />
                                </Grid>

                                <Grid item xs={12} sm={6} md={4}>
                                    <GlobitsVNDCurrencyInput
                                        label={"Số tiền đơn vị đóng"}
                                        name='orgSocialInsuranceAmount'
                                        disabled
                                    />
                                </Grid>

                                <Grid item xs={12} sm={6} md={4}>
                                    <GlobitsVNDCurrencyInput
                                        label={"Tỷ lệ đóng BHYT của công ty(%)"}
                                        name='orgHealthInsurancePercentage'
                                        onChange={(e) => {
                                            let value = e.target.value;
                                            setFieldValue("orgHealthInsurancePercentage" , value);
                                            updateTotalInsuranceAmount({
                                                ... values ,
                                                orgHealthInsurancePercentage:value
                                            } , setFieldValue);
                                        }}
                                    />
                                </Grid>

                                <Grid item xs={12} sm={6} md={4}>
                                    <GlobitsVNDCurrencyInput
                                        label={"Số tiền BHYT công ty đóng"}
                                        name='orgHealthInsuranceAmount'
                                        disabled
                                    />
                                </Grid>

                                <Grid item xs={12} sm={6} md={4}>
                                    <GlobitsVNDCurrencyInput
                                        label={"Tỷ lệ đóng BHTN của công ty(%)"}
                                        name='orgUnemploymentInsurancePercentage'
                                        onChange={(e) => {
                                            let value = e.target.value;
                                            setFieldValue("orgUnemploymentInsurancePercentage" , value);
                                            updateTotalInsuranceAmount({
                                                ... values ,
                                                orgUnemploymentInsurancePercentage:value
                                            } , setFieldValue);
                                        }}
                                    />
                                </Grid>
                                <Grid item xs={12} sm={6} md={4}>
                                    <GlobitsVNDCurrencyInput
                                        label={"Số tiền BHTN công ty đóng"}
                                        name='orgUnemploymentInsuranceAmount'
                                        disabled
                                    />
                                </Grid>
                                <Grid item xs={12} sm={6} md={4}>
                                    <GlobitsVNDCurrencyInput
                                        label={"Tổng tiền bảo hiểm mà công ty đóng"}
                                        name='orgTotalInsuranceAmount'
                                        disabled
                                    />
                                </Grid>

                                <Grid item xs={12} sm={6} md={4}>
                                    <GlobitsVNDCurrencyInput
                                        disabled
                                        label={"Tổng tiền bảo hiểm"}
                                        name='totalInsuranceAmount'
                                    />
                                </Grid>

                                <Grid item xs={12}>
                                    <GlobitsTextField
                                        name='note'
                                        label={t("humanResourcesInformation.note")}
                                        multiline
                                        rows={4}
                                    />
                                </Grid>
                            </Grid>
                        </DialogContent>

                        <DialogActions className='dialog-footer p-12'>
                            <div className='flex flex-space-between flex-middle'>
                                <Button
                                    variant='contained'
                                    className='mr-12 btn btn-secondary d-inline-flex'
                                    color='secondary'
                                    onClick={() => {
                                        handleClose();
                                    }}>
                                    {t("general.button.close")}
                                </Button>
                                <Button
                                    className='mr-0 btn btn-primary d-inline-flex'
                                    variant='contained'
                                    color='primary'
                                    type='submit'
                                    disabled={isSubmitting}>
                                    {t("general.button.save")}
                                </Button>
                            </div>
                        </DialogActions>
                    </Form>
                )}
            </Formik>
        </GlobitsPopupV2>
    );
}

export default memo(observer(StaffSocialInsuranceV2CUForm));
