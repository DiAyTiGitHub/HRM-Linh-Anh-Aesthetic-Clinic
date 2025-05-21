import React, { memo, useEffect, useState } from "react";
import { Grid, DialogActions, Button, DialogContent, } from "@material-ui/core";
import { Formik, Form } from "formik";
import { useTranslation } from "react-i18next";
import GlobitsDateTimePicker from "app/common/form/GlobitsDateTimePicker";
import GlobitsTextField from "app/common/form/GlobitsTextField";
import GlobitsPagingAutocomplete from "app/common/form/GlobitsPagingAutocomplete";
import { pagingContractTypes } from "../../../Category/Staff/ContractType/ContractTypeService";
import * as Yup from "yup";
import moment from "moment";
import { observer } from "mobx-react";
import StaffLabourAgreementAttachmentSection from "./StaffLabourAgreementAttachmentSection";
import GlobitsVNDCurrencyInput from "app/common/form/GlobitsVNDCurrencyInput";
import { pagingSalaryArea } from "app/views/Salary/SalaryArea/SalaryAreaService";
import { pagingSalaryUnit } from "app/views/Salary/SalaryUnit/SalaryUnitService";
import GlobitsPopupV2 from "app/common/GlobitsPopupV2";
import GlobitsCheckBox from "app/common/form/GlobitsCheckBox";
import TabAccordion from "app/common/Accordion/TabAccordion";
import GlobitsPagingAutocompleteV2 from "app/common/form/GlobitsPagingAutocompleteV2";
import { pagingAllOrg } from "app/views/Organization/OrganizationService";
import { pagingSalaryTemplates } from "app/views/Salary/SalaryTemplate/SalaryTemplateService";

function StaffAgreementPopupAdd(props) {
  const { t } = useTranslation();
  const { open, handleClose, item, handleSubmit, editable } = props;

  const initialItem = {
    signedDate: null,
    startDate: null,
    endDate: new Date(2100, 0, 1),
    agreementStatus: "",
    labourAgreementType: null,
    files: [],
    salaryUnit: null,
    salary: null,
    salaryArea: null,
    workingPlace: null,
    workingHourWeekMax: null,
    workingHourWeekMin: null,
    workingHour: null,
    labourAgreementNumber: null,
    contractType: null,

    hasSocialIns: null,
    // socialInsuranceNumber: null,
    insuranceStartDate: null,
    insuranceEndDate: null,
    salaryInsurance: null,
    staffPercentage: null,
    staffInsuranceAmount: null,
    orgPercentage: null,
    orgInsuranceAmount: null,
    unionDuesPercentage: null,
    unionDuesAmount: null,
    totalInsuranceAmount: null,
    contractOrganization: null,
    workOrganization: null
  };

  const validationSchema = Yup.object({
    // signedDate: Yup.date().transform(function transformDate(castValue, originalValue) {         return originalValue ? new Date(originalValue) : castValue;       }).required(t("validation.required")).nullable(),

    startDate: Yup.date()
      .test(
        "is-greater-or-equal",
        "Ngày bắt đầu hiệu lực phải lớn hơn hoặc bằng ngày thiết lập",
        function (value) {
          const { signedDate } = this.parent;
          if (signedDate && value) {
            return moment(value).isSameOrAfter(moment(signedDate), "date");
          }
          return true;
        }
      )
      .transform(function transformDate(castValue, originalValue) {
        return originalValue ? new Date(originalValue) : castValue;
      })
      .required(t("validation.required"))
      .typeError("Ngày bắt đầu không đúng định dạng")
      .nullable(),

    endDate: Yup.date()
      .test(
        "is-greater",
        "Ngày kết thúc phải lớn ngày bắt đầu",
        function (value) {
          const { startDate } = this.parent;
          if (startDate && value) {
            return moment(value).isAfter(moment(startDate), "date");
          }
          return true;
        }
      )
      .transform(function transformDate(castValue, originalValue) {
        return originalValue ? new Date(originalValue) : castValue;
      })
      // .required(t("validation.required"))
      .typeError("Ngày kết thúc không đúng định dạng")
      .nullable(),

    insuranceStartDate: Yup.date()
      .when('hasSocialIns', {
        is: true, // Điều kiện: chỉ kiểm tra khi hasSocialIns = true
        then: Yup.date()
          .required(t("validation.required")) // Bắt buộc khi hasSocialIns = true
          .test(
            "is-greater-or-equal",
            "Ngày bắt đầu đóng BHXH phải sau hoặc bằng ngày kết thúc đóng BHXH",
            function (value) {
              const { insuranceEndDate } = this.parent;
              if (insuranceEndDate && value) {
                return moment(value).isSameOrBefore(moment(insuranceEndDate), "date");
              }
              return true;
            }
          )
          .typeError("Ngày bắt đầu đóng BHXH không đúng định dạng")
          .nullable(),
        otherwise: Yup.date().nullable() // Không bắt buộc khi hasSocialIns = false
      })
      .transform(function transformDate(castValue, originalValue) {
        return originalValue ? new Date(originalValue) : castValue;
      }),

    insuranceEndDate: Yup.date()
      .test(
        "is-smaller-or-equal",
        "Ngày kết thúc đóng BHXH phải trước hoặc bằng ngày bắt đầu đóng BHXH",
        function (value) {
          const { insuranceStartDate } = this.parent;
          if (insuranceStartDate && value) {
            return moment(value).isSameOrAfter(moment(insuranceStartDate), "date");
          }
          return true;
        }
      )
      .transform(function transformDate(castValue, originalValue) {
        return originalValue ? new Date(originalValue) : castValue;
      })
      // .required(t("validation.required"))
      .typeError("Ngày kết thúc đóng BHXH không đúng định dạng")
      .nullable(),

    contractType: Yup.object()
      .required(t("validation.required"))
      .nullable(),
  });

  const [formValues, setFormValues] = useState(null);

  useEffect(() => {
    if (item) {
      setFormValues({ ...item });
    } else {
      setFormValues({ ...initialItem });
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [item]);

  const calculateStaffInsuranceAmount = (salaryInsurance, staffPercentage) => {
    if (
      isNaN(salaryInsurance) ||
      isNaN(staffPercentage) ||
      salaryInsurance < 0 ||
      staffPercentage < 0
    ) {
      return 0;
    }
    return (Number.parseFloat(salaryInsurance || 0) * Number.parseFloat(staffPercentage || 0) / 100);
  };

  const calculateOrgInsuranceAmount = (salaryInsurance, orgPercentage) => {
    if (
      isNaN(salaryInsurance) ||
      isNaN(orgPercentage) ||
      salaryInsurance < 0 ||
      orgPercentage < 0
    ) {
      return 0;
    }
    return (Number.parseFloat(salaryInsurance || 0) * Number.parseFloat(orgPercentage || 0) / 100);
  };

  const calculateUnionDuesAmount = (salaryInsurance, unionDuesPercentage) => {
    if (
      isNaN(salaryInsurance) ||
      isNaN(unionDuesPercentage) ||
      salaryInsurance < 0 ||
      unionDuesPercentage < 0
    ) {
      return 0;
    }
    return (Number.parseFloat(salaryInsurance || 0) * Number.parseFloat(unionDuesPercentage || 0) / 100);
  };

  const calculateTotalInsuranceAmount = (orgInsuranceAmount, staffInsuranceAmount, unionDuesAmount) => {
    if (
      isNaN(orgInsuranceAmount) ||
      isNaN(staffInsuranceAmount) ||
      isNaN(unionDuesAmount) ||
      orgInsuranceAmount < 0 ||
      staffInsuranceAmount < 0 ||
      unionDuesAmount < 0
    ) {
      return 0;
    }
    return (Number.parseFloat(orgInsuranceAmount || 0) + Number.parseFloat(staffInsuranceAmount || 0)) + Number.parseFloat(unionDuesAmount || 0)
  };

  const handleChangeHasSocialIns = (event, setFieldValue, setFieldTouched, setFieldError) => {
    const checked = event.target.checked;
    setFieldValue("hasSocialIns", checked);
    if (!checked) {
      //setFieldValue("socialInsuranceNumber", '');
      setFieldValue("insuranceStartDate", null);
      setFieldValue("insuranceEndDate", null);
      setFieldValue("salaryInsurance", null);
      setFieldValue("staffPercentage", null);
      setFieldValue("staffInsuranceAmount", null);
      setFieldValue("orgPercentage", null);
      setFieldValue("orgInsuranceAmount", null);
      setFieldValue("unionDuesPercentage", null);
      setFieldValue("unionDuesAmount", null);
      setFieldValue("totalInsuranceAmount", null);
      setFieldValue("salaryInsuranceUnit", null);

      // Reset lỗi xác thực (validation errors)
      setFieldTouched("insuranceStartDate", false);
      setFieldTouched("insuranceEndDate", false);
      setFieldError("insuranceStartDate", undefined);
      setFieldError("insuranceEndDate", undefined);
    }
  }

  return (
    <GlobitsPopupV2
      scroll={"body"}
      // size="xl"
      noDialogContent
      open={open}
      onClosePopup={handleClose}
      title={
        <span className="">
          {editable ? t("general.button.add") : t("general.button.edit")}{" "}
          {t("agreements.title")}
        </span>
      }
    >
      <Formik
        initialValues={formValues}
        onSubmit={(values) => handleSubmit(values)}
        validationSchema={validationSchema}
      >
        {({ isSubmitting, values, setFieldValue, setFieldTouched, setFieldError }) => {

          return (
            <Form autoComplete="off">
              <DialogContent
                className="dialog-body p-12"
              >
                <TabAccordion
                  title='Thông tin hợp đồng'
                  className="pb-0 mb-0"
                >
                  <Grid container spacing={2}>
                    <Grid item xs={12} sm={6} md={4}>
                      {/* Số hợp đồng */}
                      <GlobitsTextField
                        validate
                        required
                        // type="number"
                        label={t("agreements.labourAgreementNumber")}
                        name="labourAgreementNumber"
                      />
                    </Grid>

                    <Grid item xs={12} sm={6} md={4}>
                      {/* Loại hợp đồng theo thời gian */}
                      <GlobitsPagingAutocomplete
                        name="contractType"
                        requiredLabel
                        label={t("agreements.contractType")}
                        api={pagingContractTypes}
                      />
                    </Grid>

                    <Grid item xs={12} sm={6} md={4}>
                      {/* Ngày thiết lập */}
                      <GlobitsDateTimePicker
                        required
                        label={t("agreements.signedDate")}
                        name="signedDate"
                      />
                    </Grid>
                    <Grid item xs={12} sm={6} md={4}>
                      {/* Ngày bắt đầu hiệu lực */}
                      <GlobitsDateTimePicker
                        required
                        label={t("agreements.startDate")}
                        name="startDate"
                      />
                    </Grid>
                    <Grid item xs={12} sm={6} md={4}>
                      {/* Ngày hết hạn */}
                      <GlobitsDateTimePicker
                        label={t("agreements.endDate")}
                        name="endDate"
                      />
                    </Grid>

                    <Grid item xs={12} sm={6} md={4}>
                      {/* Giờ công chuẩn 1 ngày */}
                      <GlobitsTextField
                        type="number"
                        label={t("agreements.workingHour")}
                        name="workingHour"
                      />
                    </Grid>

                    <Grid item xs={12} sm={6} md={4}>
                      {/* Giờ công tối thiểu 1 tuần */}
                      <GlobitsTextField
                        type="number"
                        label={t("agreements.workingHourWeekMin")}
                        name="workingHourWeekMin"
                      />
                    </Grid>

                    <Grid item xs={12} sm={6} md={4}>
                      {/* Giờ công tối đa 1 tuần */}
                      <GlobitsTextField
                        type="number"
                        label={t("agreements.workingHourWeekMax")}
                        name="workingHourWeekMax"
                      />
                    </Grid>

                    <Grid item xs={12} sm={6} md={4}>
                      {/* Nơi làm việc */}
                      <GlobitsTextField
                        label={t("agreements.workingPlace")}
                        name="workingPlace"
                      />
                    </Grid>

                    <Grid item xs={12} sm={6} md={4}>
                      {/* Vùng lương */}
                      <GlobitsPagingAutocomplete
                        name="salaryArea"
                        label={t("agreements.salaryArea")}
                        api={pagingSalaryArea}
                      />
                    </Grid>

                    <Grid item xs={12} sm={6} md={4}>
                      {/* Mức lương */}
                      <GlobitsVNDCurrencyInput
                        // type="number"
                        label={t("agreements.salary")}
                        name="salary"
                      />
                    </Grid>

                    <Grid item xs={12} sm={6} md={4}>
                      {/* Đơn vị tính lương */}
                      <GlobitsPagingAutocomplete
                        name="salaryUnit"
                        label={t("agreements.salaryUnit")}
                        api={pagingSalaryUnit}
                      />
                    </Grid>

                    <Grid item xs={12} sm={6} md={4}>
                      <GlobitsPagingAutocompleteV2
                        name="contractOrganization"
                        label={t("Đơn vị kí hợp đồng")}
                        api={pagingAllOrg}
                        // value={values?.contractOrganization}
                      />
                    </Grid>

                    <Grid item xs={12} sm={6} md={4}>
                      <GlobitsPagingAutocompleteV2
                        name="workOrganization"
                        label={t("Đơn vị làm việc")}
                        api={pagingAllOrg}
                        // value={values?.workOrganization}
                      />
                    </Grid>

                    <Grid item xs={12} sm={6} md={4}>
                      <GlobitsPagingAutocompleteV2
                        name="salaryTemplate"
                        label={t("Mẫu bảng lương")}
                        api={pagingSalaryTemplates}
                        // value={values?.workOrganization}
                      />
                    </Grid>
                  </Grid>
                </TabAccordion>

                <TabAccordion
                  title='Thông tin bảo hiểm'
                  className="pb-0 mb-0"
                >
                  <Grid container spacing={2}>
                    <Grid item md={4} sm={6} xs={12} className="pt-25 pl-15">
                      <GlobitsCheckBox
                        label={t("Có đóng BHXH")}
                        name="hasSocialIns"
                        onChange={(event) => { handleChangeHasSocialIns(event, setFieldValue,setFieldTouched, setFieldError) }}
                      />
                    </Grid>

                    <Grid item xs={12} sm={6} md={4}>
                      <GlobitsDateTimePicker
                        label={"Ngày bắt đầu đóng BHXH"}
                        name="insuranceStartDate"
                        disabled={!values?.hasSocialIns}
                      />
                    </Grid>

                    <Grid item xs={12} sm={6} md={4}>
                      <GlobitsDateTimePicker
                        label={"Ngày kết thúc đóng BHXH"}
                        name="insuranceEndDate"
                        disabled={!values?.hasSocialIns}
                      />
                    </Grid>

                    <Grid item xs={12} sm={6} md={4}>
                      <GlobitsVNDCurrencyInput
                        label={t("agreements.salaryInsurance")}
                        name="salaryInsurance"
                        disabled={!values?.hasSocialIns}
                        onChange={(e) => {
                          const salaryInsurance = Number(e.target.value);
                          setFieldValue("salaryInsurance", salaryInsurance);

                          const staffInsuranceAmount = calculateStaffInsuranceAmount(
                            salaryInsurance,
                            values.staffPercentage
                          );
                          setFieldValue("staffInsuranceAmount", staffInsuranceAmount);

                          const orgInsuranceAmount = calculateOrgInsuranceAmount(
                            salaryInsurance,
                            values?.orgPercentage
                          );
                          setFieldValue("orgInsuranceAmount", orgInsuranceAmount);

                          const unionDuesAmount = calculateUnionDuesAmount(
                            salaryInsurance,
                            values.unionDuesPercentage
                          );
                          setFieldValue("unionDuesAmount", unionDuesAmount);
                        }}
                      />
                    </Grid>

                    <Grid item xs={12} sm={6} md={4}>
                      <GlobitsVNDCurrencyInput
                        label={"Tỷ lệ cá nhân đóng BHXH"}
                        name="staffPercentage"
                        disabled={!values?.hasSocialIns}
                        onChange={(e) => {
                          const staffPercentage = Number.parseFloat(e.target.value);
                          setFieldValue("staffPercentage", staffPercentage);

                          const staffInsuranceAmount = calculateStaffInsuranceAmount(
                            values?.salaryInsurance,
                            staffPercentage
                          );
                          setFieldValue("staffInsuranceAmount", staffInsuranceAmount);
                        }}
                      />
                    </Grid>

                    <Grid item xs={12} sm={6} md={4}>
                      <GlobitsVNDCurrencyInput
                        label={"Số tiền BHXH cá nhân đóng"}
                        name="staffInsuranceAmount"
                        disabled={!values?.hasSocialIns}
                        onChange={(e) => {
                          const staffInsuranceAmount = Number.parseFloat(e.target.value);
                          setFieldValue("staffInsuranceAmount", staffInsuranceAmount);

                          const totalInsuranceAmount = calculateTotalInsuranceAmount(
                            staffInsuranceAmount,
                            values?.orgInsuranceAmount,
                            values?.unionDuesAmount
                          );
                          setFieldValue("totalInsuranceAmount", totalInsuranceAmount);

                        }}
                      />
                    </Grid>

                    <Grid item xs={12} sm={6} md={4}>
                      <GlobitsVNDCurrencyInput
                        label={"Tỷ lệ đơn vị đóng BHXH"}
                        name="orgPercentage"
                        disabled={!values?.hasSocialIns}
                        onChange={(e) => {
                          const orgPercentage = Number.parseFloat(e.target.value);
                          setFieldValue("orgPercentage", orgPercentage);

                          const orgInsuranceAmount = calculateOrgInsuranceAmount(
                            values?.salaryInsurance,
                            orgPercentage
                          );
                          setFieldValue("orgInsuranceAmount", orgInsuranceAmount);

                        }}
                      />
                    </Grid>

                    <Grid item xs={12} sm={6} md={4}>
                      <GlobitsVNDCurrencyInput
                        label={"Số tiền BHXH đơn vị đóng"}
                        name="orgInsuranceAmount"
                        disabled={!values?.hasSocialIns}
                        onChange={(e) => {
                          const orgInsuranceAmount = Number.parseFloat(e.target.value);
                          setFieldValue("orgInsuranceAmount", orgInsuranceAmount);

                          const totalInsuranceAmount = calculateTotalInsuranceAmount(
                            values?.staffInsuranceAmount,
                            orgInsuranceAmount,
                            values?.unionDuesAmount
                          );
                          setFieldValue("totalInsuranceAmount", totalInsuranceAmount);

                        }}
                      />
                    </Grid>

                    <Grid item xs={12} sm={6} md={4}>
                      <GlobitsVNDCurrencyInput
                        label={"Tỷ lệ đóng phí công đoàn"}
                        name="unionDuesPercentage"
                        disabled={!values?.hasSocialIns}
                        onChange={(e) => {
                          const unionDuesPercentage = Number.parseFloat(e.target.value);
                          setFieldValue("unionDuesPercentage", unionDuesPercentage);

                          const unionDuesAmount = calculateUnionDuesAmount(
                            values?.salaryInsurance,
                            unionDuesPercentage
                          );
                          setFieldValue("unionDuesAmount", unionDuesAmount);
                        }}
                      />
                    </Grid>

                    <Grid item xs={12} sm={6} md={4}>
                      <GlobitsVNDCurrencyInput
                        label={"Số tiền đóng phí công đoàn"}
                        name="unionDuesAmount"
                        disabled={!values?.hasSocialIns}
                        onChange={(e) => {
                          const unionDuesAmount = Number.parseFloat(e.target.value);
                          setFieldValue("unionDuesAmount", unionDuesAmount);

                          const totalInsuranceAmount = calculateTotalInsuranceAmount(
                            values?.orgInsuranceAmount,
                            values?.staffInsuranceAmount,
                            unionDuesAmount
                          );
                          setFieldValue("totalInsuranceAmount", totalInsuranceAmount);

                        }}
                      />
                    </Grid>


                    <Grid item xs={12} sm={6} md={4}>
                      <GlobitsVNDCurrencyInput
                        disabled
                        label={"Tổng tiền bảo hiểm"}
                        name="totalInsuranceAmount"
                      />
                    </Grid>

                    <Grid item xs={12} sm={6} md={4}>
                      <GlobitsPagingAutocomplete
                        name="salaryInsuranceUnit"
                        label={t("agreements.salaryInsuranceUnit")}
                        api={pagingSalaryUnit}
                        disabled={!values?.hasSocialIns}
                      />
                    </Grid>
                  </Grid>
                </TabAccordion>
                <TabAccordion
                  title='Bảo hiểm'
                  className="pb-0 mb-0"
                >
                  <Grid item xs={12}>
                    <StaffLabourAgreementAttachmentSection />
                  </Grid>
                </TabAccordion>
              </DialogContent>

              <DialogActions className="dialog-footer px-12">
                <div className="flex flex-space-between flex-middle">
                  <Button
                    variant="contained"
                    className="mr-12 btn btn-secondary d-inline-flex"
                    color="secondary"
                    onClick={handleClose}
                  >
                    {t("general.button.close")}
                  </Button>
                  <Button
                    className="mr-0 btn btn-primary d-inline-flex"
                    variant="contained"
                    color="primary"
                    type="submit"
                    disabled={isSubmitting}
                  >
                    {t("general.button.save")}
                  </Button>
                </div>
              </DialogActions>
            </Form>
          );
        }}
      </Formik>
    </GlobitsPopupV2>
  );
}


export default memo(observer(StaffAgreementPopupAdd));

