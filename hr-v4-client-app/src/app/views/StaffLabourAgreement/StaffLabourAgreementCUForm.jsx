import React, { memo, useEffect, useState } from "react";
import { Form, Formik, useFormikContext } from "formik";
import { Button, DialogActions, DialogContent, Grid } from "@material-ui/core";
import { useTranslation } from "react-i18next";
import { useStore } from "app/stores";
import * as Yup from "yup";
import { observer } from "mobx-react";
import GlobitsTextField from "app/common/form/GlobitsTextField";
import GlobitsPagingAutocompleteV2 from "app/common/form/GlobitsPagingAutocompleteV2";
import { pagingContractTypes } from "../Category/Staff/ContractType/ContractTypeService";
import GlobitsDateTimePicker from "app/common/form/GlobitsDateTimePicker";
import GlobitsVNDCurrencyInput from "app/common/form/GlobitsVNDCurrencyInput";
import StaffLabourAgreementAttachmentSection
  from "../HumanResourcesInformation/TabContainer/Popup/StaffLabourAgreementAttachmentSection";
import moment from "moment";
import ChooseUsingStaffSection from "../User/UsingAccountStaff/ChooseUsingStaffSection";
import { pagingSalaryUnit } from "../Salary/SalaryUnit/SalaryUnitService";
import GlobitsPopupV2 from "app/common/GlobitsPopupV2";
import GlobitsPagingAutocomplete from "app/common/form/GlobitsPagingAutocomplete";
import GlobitsCheckBox from "app/common/form/GlobitsCheckBox";
import { pagingAllOrg } from "../Organization/OrganizationService";
import TabAccordion from "app/common/Accordion/TabAccordion";
import LocalConstants, { OrganizationType } from "../../LocalConstants";
import GlobitsSelectInput from "../../common/form/GlobitsSelectInput";
import GlobitsNumberInput from "../../common/form/GlobitsNumberInput";
import { getTaxBHXHByStaffId } from "../Salary/StaffSalaryItemValue/StaffSalaryItemValueService";

function StaffLabourAgreementCUForm (props) {
  const {hasStaff = false} = props;
  const {staffLabourAgreementStore} = useStore ();
  const {t} = useTranslation ();
  const {
    handleClose,
    saveStaffLabourAgreement,
    pagingStaffLabourAgreement,
    selectedStaffLabourAgreement,
    openCreateEditPopup,
    handleHasAndPagingOverdueContract,
  } = staffLabourAgreementStore;

  const validationSchema = Yup.object ({
    signedDate:Yup.date ()
        .transform (function transformDate (castValue, originalValue) {
          return originalValue? new Date (originalValue) : castValue;
        })
        .required (t ("validation.required"))
        .nullable (),

    startDate:Yup.date ()
        .test ("is-greater", "Ngày bắt đầu phải lớn hoặc bằng ngày thiết lập", function (value) {
          const {signedDate} = this.parent;
          if (signedDate && value) {
            return moment (value).isSameOrAfter (moment (signedDate), "date");
          }
          return true;
        })
        .transform (function transformDate (castValue, originalValue) {
          return originalValue? new Date (originalValue) : castValue;
        })
        .required (t ("validation.required"))
        .typeError ("Ngày bắt đầu không đúng định dạng")
        .nullable (),

    endDate:Yup.date ()
        .test ("is-greater", "Ngày kết thúc phải lớn ngày bắt đầu", function (value) {
          const {startDate} = this.parent;
          if (startDate && value) {
            return moment (value).isAfter (moment (startDate), "date");
          }
          return true;
        })
        .transform (function transformDate (castValue, originalValue) {
          return originalValue? new Date (originalValue) : castValue;
        })
        // .required(t("validation.required"))
        .typeError ("Ngày kết thúc không đúng định dạng")
        .nullable (),

    // agreementStatus: Yup.number()
    //   .min(0, "Giá trị phải lớn hơn hoặc bằng 0 !")
    //   .required(t("validation.required"))
    //   .nullable(),

    contractType:Yup.object ().required (t ("validation.required")).nullable (),
    contractOrganization:Yup.object ().required (t ("validation.required")).nullable (),
    durationMonths:Yup.number ()
        .nullable ()
        .when ("contractType", {
          is:(contractType) => contractType?.code !== "HDLD_KXDTH",
          then:(schema) => schema.required (t ("validation.required")),
          otherwise:(schema) => schema.nullable ()
        }),
    labourAgreementNumber:Yup.string ().required (t ("validation.required")).nullable (),
    // socialInsuranceNumber:Yup.string ()
    //     .nullable ()
    //     .when ("hasSocialIns", {
    //       is:true, // Điều kiện: chỉ kiểm tra khi hasSocialIns = true
    //       then:(schema) => schema.required (t ("validation.required")).nullable (),
    //       otherwise:Yup.string ().nullable (),
    //     }),

    // insuranceStartDate:Yup.date ()
    //     .when ("hasSocialIns", {
    //       is:true, // Điều kiện: chỉ kiểm tra khi hasSocialIns = true
    //       then:Yup.date ()
    //           .required (t ("validation.required")) // Bắt buộc khi hasSocialIns = true
    //           .test (
    //               "is-greater-or-equal",
    //               "Ngày bắt đầu đóng BHXH phải sau hoặc bằng ngày kết thúc đóng BHXH",
    //               function (value) {
    //                 const {insuranceEndDate} = this.parent;
    //                 if (insuranceEndDate && value) {
    //                   return moment (value).isSameOrBefore (moment (insuranceEndDate), "date");
    //                 }
    //                 return true;
    //               }
    //           )
    //           .typeError ("Ngày bắt đầu đóng BHXH không đúng định dạng")
    //           .nullable (),
    //       otherwise:Yup.date ().nullable (), // Không bắt buộc khi hasSocialIns = false
    //     })
    //     .transform (function transformDate (castValue, originalValue) {
    //       return originalValue? new Date (originalValue) : castValue;
    //     }),

    // insuranceEndDate:Yup.date ()
    //     .test (
    //         "is-smaller-or-equal",
    //         "Ngày kết thúc đóng BHXH phải trước hoặc bằng ngày bắt đầu đóng BHXH",
    //         function (value) {
    //           const {insuranceStartDate} = this.parent;
    //           if (insuranceStartDate && value) {
    //             return moment (value).isSameOrAfter (moment (insuranceStartDate), "date");
    //           }
    //           return true;
    //         }
    //     )
    //     .transform (function transformDate (castValue, originalValue) {
    //       return originalValue? new Date (originalValue) : castValue;
    //     })
    //     // .required(t("validation.required"))
    //     .typeError ("Ngày kết thúc đóng BHXH không đúng định dạng")
    //     .nullable (),
  });

  async function handleSaveForm (values) {
    try {
      // save single staff labour agreement
      const payloadLabourAgreement = {
        ... values,
      };
      await saveStaffLabourAgreement (payloadLabourAgreement);

      await pagingStaffLabourAgreement ();

      // await handleHasAndPagingOverdueContract ();
    } catch (error) {
      console.error (error);
    }
  }

  const [initialValues, setInitialValues] = useState (selectedStaffLabourAgreement);

  useEffect (
      function () {
        if (selectedStaffLabourAgreement?.id) {
          setInitialValues ({
            ... selectedStaffLabourAgreement,
          });
        } else {
          setInitialValues ({
            ... selectedStaffLabourAgreement,
            agreementStatus:1,
          });
        }
      },
      [selectedStaffLabourAgreement?.id]
  );

  const handleChangeHasSocialIns = (event, setFieldValue, setFieldTouched, setFieldError) => {
    const checked = event.target.checked;
    if (!checked) {
      setFieldValue ("hasSocialIns", false);
      setFieldValue ("socialInsuranceNumber", "");
      setFieldValue ("insuranceSalary", null);

      setFieldValue ("staffSocialInsurancePercentage", null);
      setFieldValue ("staffHealthInsurancePercentage", null);
      setFieldValue ("staffUnemploymentInsurancePercentage", null);
      setFieldValue ("staffTotalInsuranceAmount", null);

      setFieldValue ("orgSocialInsurancePercentage", null);
      setFieldValue ("orgHealthInsurancePercentage", null);
      setFieldValue ("orgUnemploymentInsurancePercentage", null);
      setFieldValue ("orgTotalInsuranceAmount", null);

      setFieldValue ("totalInsuranceAmount", null);
      setFieldValue ("insuranceStartDate", null);
      setFieldValue ("insuranceEndDate", null);
      setFieldValue ("salaryInsuranceUnit", null);
      setFieldValue ("paidStatus", null);

      // Reset lỗi xác thực (validation errors)
      setFieldTouched ("insuranceStartDate", false);
      setFieldTouched ("insuranceEndDate", false);
      setFieldTouched ("socialInsuranceNumber", false);

      setFieldError ("insuranceStartDate", false);
      setFieldError ("insuranceEndDate", false);
      setFieldError ("socialInsuranceNumber", false);
    } else {
      setFieldValue ("staffSocialInsurancePercentage", 8);
      setFieldValue ("staffHealthInsurancePercentage", 1.5);
      setFieldValue ("staffUnemploymentInsurancePercentage", 1);
      setFieldValue ("orgSocialInsurancePercentage", 17.5);
      setFieldValue ("orgHealthInsurancePercentage", 3);
      setFieldValue ("orgUnemploymentInsurancePercentage", 1);
    }
  };

  const updateTotalInsuranceAmount = (values, setFieldValue) => {
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

    setFieldValue ("staffSocialInsuranceAmount", staffSocial);
    setFieldValue ("staffHealthInsuranceAmount", staffHealth);
    setFieldValue ("staffUnemploymentInsuranceAmount", staffUnemp);
    setFieldValue ("staffTotalInsuranceAmount", staffTotal);

    setFieldValue ("orgSocialInsuranceAmount", orgSocial);
    setFieldValue ("orgHealthInsuranceAmount", orgHealth);
    setFieldValue ("orgUnemploymentInsuranceAmount", orgUnemp);
    setFieldValue ("orgTotalInsuranceAmount", orgTotal);

    setFieldValue ("totalInsuranceAmount", totalInsurance);
  };

  return (
      <GlobitsPopupV2
          scroll={"body"}
          size='md'
          open={openCreateEditPopup}
          noDialogContent
          title={
              (selectedStaffLabourAgreement?.id? t ("general.button.edit") : t ("general.button.add")) +
              " " +
              t ("navigation.staff.staffLabourAgreement")
          }
          onClosePopup={handleClose}>
        <Formik
            validationSchema={validationSchema}
            enableReinitialize
            initialValues={initialValues}
            onSubmit={handleSaveForm}>
          {({isSubmitting, values, setFieldValue, initialValues, setFieldTouched, setFieldError}) => {
            return (
                <Form autoComplete='off'>
                  <DialogContent className='o-hidden dialog-body p-12'>
                    <TabAccordion title='Thông tin hợp đồng' className='pb-0 mb-0'>
                      <Grid container spacing={2}>
                        {!hasStaff && (
                            <Grid item xs={12} sm={6} md={4}>
                              <AutoFillContractNumber/>
                            </Grid>
                        )}
                        <Grid item xs={12} sm={6} md={4}>
                          {/* Số hợp đồng */}
                          <GlobitsTextField
                              validate
                              required
                              // type="number"
                              label={t ("agreements.labourAgreementNumber")}
                              name='labourAgreementNumber'
                          />
                        </Grid>

                        <Grid item xs={12} sm={6} md={4}>
                          {/* Loại hợp đồng */}
                          <GlobitsPagingAutocompleteV2
                              name='contractType'
                              required
                              requiredLabel
                              label={t ("agreements.contractType")}
                              api={pagingContractTypes}
                              handleChange={(_, value) => {
                                setFieldValue ("contractType", value);
                                // Clear duration months when switching to indefinite contract
                                if (value?.code === "HDLD_KXDTH") {
                                  setFieldValue ("durationMonths", null);
                                  setFieldValue ("endDate", null);
                                }
                              }}
                          />
                        </Grid>

                        <Grid item xs={12}>
                          <AutoCalculateEndTime/>
                        </Grid>
                        <Grid item xs={12} sm={6} md={4}>
                          {/* Ngày thiết lập */}
                          <GlobitsDateTimePicker
                              required
                              label={t ("agreements.signedDate")}
                              name='signedDate'
                          />
                        </Grid>

                        <Grid item xs={12} sm={6} md={4}>
                          {/* Nơi làm việc */}
                          <GlobitsTextField
                              label={t ("agreements.workingPlace")}
                              name='workingPlace'
                          />
                        </Grid>
                        <Grid item xs={12} sm={6} md={4}>
                          {/* Mức lương */}
                          <GlobitsVNDCurrencyInput
                              // type="number"
                              label={t ("agreements.salary")}
                              name='salary'
                          />
                        </Grid>

                        <Grid item xs={12} sm={6} md={4}>
                          <GlobitsPagingAutocompleteV2
                              name='contractOrganization'
                              label={t ("Đơn vị kí hợp đồng")}
                              api={pagingAllOrg}
                              // value={values?.contractOrganization}
                              searchObject={{
                                organizationType:OrganizationType.LEGAL_ENTITY.value
                              }}
                              required
                          />
                        </Grid>

                        <Grid item xs={12} sm={6} md={4}>
                          <GlobitsPagingAutocompleteV2
                              name='workOrganization'
                              label={t ("Đơn vị làm việc")}
                              api={pagingAllOrg}
                              // value={values?.workOrganization}
                          />
                        </Grid>
                        <Grid item xs={12} sm={6} md={4}>
                          <GlobitsSelectInput
                              label={"Trạng thái hợp đồng"}
                              name='agreementStatus'
                              keyValue='value'
                              options={LocalConstants.AgreementStatus.getListData ()}
                          />
                        </Grid>
                      </Grid>
                    </TabAccordion>

                    {/*<TabAccordion title='Thông tin bảo hiểm' className='pb-0 mb-0'>*/}
                    {/*  <Grid container spacing={2}>*/}
                    {/*    <Grid item xs={12} sm={6} md={4} className='pt-25 pl-15'>*/}
                    {/*      <GlobitsCheckBox*/}
                    {/*          label={t ("Có đóng BHXH")}*/}
                    {/*          name='hasSocialIns'*/}
                    {/*          handleChange={(event) => {*/}
                    {/*            setFieldValue ("hasSocialIns", event.target.checked);*/}
                    {/*            handleChangeHasSocialIns (*/}
                    {/*                event,*/}
                    {/*                setFieldValue,*/}
                    {/*                setFieldTouched,*/}
                    {/*                setFieldError*/}
                    {/*            );*/}
                    {/*          }}*/}
                    {/*      />*/}
                    {/*    </Grid>*/}

                    {/*    /!* Sổ BHXH *!/*/}
                    {/*    <Grid item md={4} sm={6} xs={12}>*/}
                    {/*      <GlobitsNumberInput*/}
                    {/*          required={values?.hasSocialIns}*/}
                    {/*          label={t ("humanResourcesInformation.socialInsuranceNumber")}*/}
                    {/*          name='socialInsuranceNumber'*/}
                    {/*          inputProps={{maxLength:10}}*/}
                    {/*          disabled={!values?.hasSocialIns}*/}
                    {/*      />*/}
                    {/*    </Grid>*/}

                    {/*    /!* Ngày bắt đầu và ngày kết thúc BHXH *!/*/}
                    {/*    <Grid item xs={12} sm={6} md={4}>*/}
                    {/*      <GlobitsDateTimePicker*/}
                    {/*          required={values?.hasSocialIns}*/}
                    {/*          label={"Ngày bắt đầu đóng BHXH"}*/}
                    {/*          name='insuranceStartDate'*/}
                    {/*          disabled={!values?.hasSocialIns}*/}
                    {/*      />*/}
                    {/*    </Grid>*/}

                    {/*    <Grid item xs={12} sm={6} md={4}>*/}
                    {/*      <GlobitsDateTimePicker*/}
                    {/*          required={values?.hasSocialIns}*/}
                    {/*          label={"Ngày kết thúc đóng BHXH"}*/}
                    {/*          name='insuranceEndDate'*/}
                    {/*          disabled={!values?.hasSocialIns}*/}
                    {/*      />*/}
                    {/*    </Grid>*/}

                    {/*    <Grid item xs={12} sm={6} md={4}>*/}
                    {/*      <GlobitsVNDCurrencyInput*/}
                    {/*          label={"Mức lương đóng bảo hiểm xã hội"}*/}
                    {/*          name='insuranceSalary'*/}
                    {/*          required={values?.hasSocialIns}*/}
                    {/*          onChange={(e) => {*/}
                    {/*            let value = e.target.value;*/}
                    {/*            setFieldValue ("insuranceSalary", value);*/}
                    {/*            updateTotalInsuranceAmount (*/}
                    {/*                {*/}
                    {/*                  ... values,*/}
                    {/*                  insuranceSalary:value,*/}
                    {/*                },*/}
                    {/*                setFieldValue*/}
                    {/*            );*/}
                    {/*          }}*/}
                    {/*      />*/}
                    {/*    </Grid>*/}

                    {/*    <Grid item xs={12} className='pb-0'>*/}
                    {/*      <p className='m-0 p-0 borderThrough2'>Nhân viên đóng BHXH</p>*/}
                    {/*    </Grid>*/}

                    {/*    <Grid item xs={12} sm={6} md={4}>*/}
                    {/*      <GlobitsVNDCurrencyInput*/}
                    {/*          label={"Tỷ lệ cá nhân đóng BHXH(%)"}*/}
                    {/*          name='staffSocialInsurancePercentage'*/}
                    {/*          disabled={!values?.hasSocialIns}*/}
                    {/*          onChange={(e) => {*/}
                    {/*            let value = e.target.value;*/}
                    {/*            setFieldValue ("staffSocialInsurancePercentage", value);*/}
                    {/*            updateTotalInsuranceAmount (*/}
                    {/*                {*/}
                    {/*                  ... values,*/}
                    {/*                  staffSocialInsurancePercentage:value,*/}
                    {/*                },*/}
                    {/*                setFieldValue*/}
                    {/*            );*/}
                    {/*          }}*/}
                    {/*      />*/}
                    {/*    </Grid>*/}
                    {/*    <Grid item xs={12} sm={6} md={4}>*/}
                    {/*      <GlobitsVNDCurrencyInput*/}
                    {/*          label={"Số tiền BHXH nhân viên đóng"}*/}
                    {/*          name='staffSocialInsuranceAmount'*/}
                    {/*          disabled*/}
                    {/*      />*/}
                    {/*    </Grid>*/}

                    {/*    <Grid item xs={12} sm={6} md={4}>*/}
                    {/*      <GlobitsVNDCurrencyInput*/}
                    {/*          label={"Tỷ lệ đóng BHYT của nhân viên(%)"}*/}
                    {/*          name='staffHealthInsurancePercentage'*/}
                    {/*          disabled={!values?.hasSocialIns}*/}
                    {/*          onChange={(e) => {*/}
                    {/*            let value = e.target.value;*/}
                    {/*            setFieldValue ("staffHealthInsurancePercentage", value);*/}
                    {/*            updateTotalInsuranceAmount (*/}
                    {/*                {*/}
                    {/*                  ... values,*/}
                    {/*                  staffHealthInsurancePercentage:value,*/}
                    {/*                },*/}
                    {/*                setFieldValue*/}
                    {/*            );*/}
                    {/*          }}*/}
                    {/*      />*/}
                    {/*    </Grid>*/}

                    {/*    <Grid item xs={12} sm={6} md={4}>*/}
                    {/*      <GlobitsVNDCurrencyInput*/}
                    {/*          label={"Số tiền BHYT nhân viên đóng"}*/}
                    {/*          name='staffHealthInsuranceAmount'*/}
                    {/*          disabled*/}
                    {/*      />*/}
                    {/*    </Grid>*/}

                    {/*    <Grid item xs={12} sm={6} md={4}>*/}
                    {/*      <GlobitsVNDCurrencyInput*/}
                    {/*          label={"Tỷ lệ đóng BHTN của nhân viên(%)"}*/}
                    {/*          name='staffUnemploymentInsurancePercentage'*/}
                    {/*          disabled={!values?.hasSocialIns}*/}
                    {/*          onChange={(e) => {*/}
                    {/*            let value = e.target.value;*/}
                    {/*            setFieldValue ("staffUnemploymentInsurancePercentage", value);*/}
                    {/*            updateTotalInsuranceAmount (*/}
                    {/*                {*/}
                    {/*                  ... values,*/}
                    {/*                  staffUnemploymentInsurancePercentage:value,*/}
                    {/*                },*/}
                    {/*                setFieldValue*/}
                    {/*            );*/}
                    {/*          }}*/}
                    {/*      />*/}
                    {/*    </Grid>*/}

                    {/*    <Grid item xs={12} sm={6} md={4}>*/}
                    {/*      <GlobitsVNDCurrencyInput*/}
                    {/*          label={"Số tiền BHTN nhân viên đóng"}*/}
                    {/*          name='staffUnemploymentInsuranceAmount'*/}
                    {/*          disabled*/}
                    {/*      />*/}
                    {/*    </Grid>*/}

                    {/*    <Grid item xs={12} sm={6} md={4}>*/}
                    {/*      <GlobitsVNDCurrencyInput*/}
                    {/*          label={"Tổng tiền bảo hiểm mà nhân viên đóng"}*/}
                    {/*          name='staffTotalInsuranceAmount'*/}
                    {/*          disabled*/}
                    {/*      />*/}
                    {/*    </Grid>*/}
                    {/*    <Grid item xs={12} className='pb-0'>*/}
                    {/*      <p className='m-0 p-0 borderThrough2'>Công ty đóng BHXH</p>*/}
                    {/*    </Grid>*/}
                    {/*    <Grid item xs={12} sm={6} md={4}>*/}
                    {/*      <GlobitsVNDCurrencyInput*/}
                    {/*          label={"Tỷ lệ đơn vị đóng bảo hiểm xã hội(%)"}*/}
                    {/*          name='orgSocialInsurancePercentage'*/}
                    {/*          disabled={!values?.hasSocialIns}*/}
                    {/*          onChange={(e) => {*/}
                    {/*            let value = e.target.value;*/}
                    {/*            setFieldValue ("orgSocialInsurancePercentage", value);*/}
                    {/*            updateTotalInsuranceAmount (*/}
                    {/*                {*/}
                    {/*                  ... values,*/}
                    {/*                  orgSocialInsurancePercentage:value,*/}
                    {/*                },*/}
                    {/*                setFieldValue*/}
                    {/*            );*/}
                    {/*          }}*/}
                    {/*      />*/}
                    {/*    </Grid>*/}

                    {/*    <Grid item xs={12} sm={6} md={4}>*/}
                    {/*      <GlobitsVNDCurrencyInput*/}
                    {/*          label={"Số tiền đơn vị đóng"}*/}
                    {/*          name='orgSocialInsuranceAmount'*/}
                    {/*          disabled*/}
                    {/*      />*/}
                    {/*    </Grid>*/}

                    {/*    <Grid item xs={12} sm={6} md={4}>*/}
                    {/*      <GlobitsVNDCurrencyInput*/}
                    {/*          label={"Tỷ lệ đóng BHYT của công ty(%)"}*/}
                    {/*          name='orgHealthInsurancePercentage'*/}
                    {/*          disabled={!values?.hasSocialIns}*/}
                    {/*          onChange={(e) => {*/}
                    {/*            let value = e.target.value;*/}
                    {/*            setFieldValue ("orgHealthInsurancePercentage", value);*/}
                    {/*            updateTotalInsuranceAmount (*/}
                    {/*                {*/}
                    {/*                  ... values,*/}
                    {/*                  orgHealthInsurancePercentage:value,*/}
                    {/*                },*/}
                    {/*                setFieldValue*/}
                    {/*            );*/}
                    {/*          }}*/}
                    {/*      />*/}
                    {/*    </Grid>*/}

                    {/*    <Grid item xs={12} sm={6} md={4}>*/}
                    {/*      <GlobitsVNDCurrencyInput*/}
                    {/*          label={"Số tiền BHYT công ty đóng"}*/}
                    {/*          name='orgHealthInsuranceAmount'*/}
                    {/*          disabled*/}
                    {/*      />*/}
                    {/*    </Grid>*/}

                    {/*    <Grid item xs={12} sm={6} md={4}>*/}
                    {/*      <GlobitsVNDCurrencyInput*/}
                    {/*          label={"Tỷ lệ đóng BHTN của công ty(%)"}*/}
                    {/*          name='orgUnemploymentInsurancePercentage'*/}
                    {/*          disabled={!values?.hasSocialIns}*/}
                    {/*          onChange={(e) => {*/}
                    {/*            let value = e.target.value;*/}
                    {/*            setFieldValue ("orgUnemploymentInsurancePercentage", value);*/}
                    {/*            updateTotalInsuranceAmount (*/}
                    {/*                {*/}
                    {/*                  ... values,*/}
                    {/*                  orgUnemploymentInsurancePercentage:value,*/}
                    {/*                },*/}
                    {/*                setFieldValue*/}
                    {/*            );*/}
                    {/*          }}*/}
                    {/*      />*/}
                    {/*    </Grid>*/}
                    {/*    <Grid item xs={12} sm={6} md={4}>*/}
                    {/*      <GlobitsVNDCurrencyInput*/}
                    {/*          label={"Số tiền BHTN công ty đóng"}*/}
                    {/*          name='orgUnemploymentInsuranceAmount'*/}
                    {/*          disabled*/}
                    {/*      />*/}
                    {/*    </Grid>*/}
                    {/*    <Grid item xs={12} sm={6} md={4}>*/}
                    {/*      <GlobitsVNDCurrencyInput*/}
                    {/*          label={"Tổng tiền bảo hiểm mà công ty đóng"}*/}
                    {/*          name='orgTotalInsuranceAmount'*/}
                    {/*          disabled*/}
                    {/*      />*/}
                    {/*    </Grid>*/}

                    {/*    <Grid item xs={12} sm={6} md={4}>*/}
                    {/*      <GlobitsVNDCurrencyInput*/}
                    {/*          disabled*/}
                    {/*          label={"Tổng tiền bảo hiểm"}*/}
                    {/*          name='totalInsuranceAmount'*/}
                    {/*      />*/}
                    {/*    </Grid>*/}

                    {/*    /!* Đơn vị tính lương *!/*/}
                    {/*    <Grid item xs={12} sm={6} md={4}>*/}
                    {/*      <GlobitsPagingAutocomplete*/}
                    {/*          name='salaryInsuranceUnit'*/}
                    {/*          label={t ("agreements.salaryInsuranceUnit")}*/}
                    {/*          api={pagingSalaryUnit}*/}
                    {/*          disabled={!values?.hasSocialIns}*/}
                    {/*      />*/}
                    {/*    </Grid>*/}
                    {/*  </Grid>*/}
                    {/*</TabAccordion>*/}

                    <TabAccordion title='Tệp đính kèm' className='pb-0 mb-0'>
                      <Grid container spacing={2}>
                        <Grid item xs={12}>
                          <StaffLabourAgreementAttachmentSection/>
                        </Grid>
                      </Grid>
                    </TabAccordion>
                  </DialogContent>

                  <DialogActions className='dialog-footer px-12'>
                    <div className='flex flex-space-between flex-middle'>
                      <Button
                          variant='contained'
                          className='mr-12 btn btn-secondary d-inline-flex'
                          color='secondary'
                          disabled={isSubmitting}
                          onClick={handleClose}>
                        {t ("general.button.close")}
                      </Button>
                      <Button
                          className='mr-0 btn btn-primary d-inline-flex'
                          variant='contained'
                          color='primary'
                          type='submit'
                          disabled={isSubmitting}>
                        {t ("general.button.save")}
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

const AutoFillContractNumber = () => {
  const {values, setFieldValue} = useFormikContext ();

  const handleAfterStaffChosen = (staff) => {
    console.log (staff);
    if (values?.id) {
      return;
    }
    if (staff) {
      // Lấy 6 số cuối của mã nhân viên
      const staffCode = staff?.staffCode || "";
      const last6Digits = staffCode.replace (/\D/g, "").slice (-6); // Chỉ lấy số và cắt 6 ký tự cuối

      // Nếu mã nhân viên không đủ số, có thể xử lý thêm
      const contractNumber = last6Digits? `HĐ_${last6Digits}` : `HĐ_000000`; // Fallback nếu không có số

      setFieldValue ("labourAgreementNumber", contractNumber);
    } else {
      setFieldValue ("labourAgreementNumber", null);
    }
  };

  return <ChooseUsingStaffSection required label={"Nhân viên áp dụng"} handleAfterSubmit={handleAfterStaffChosen}/>;
};

const AutoCalculateEndTime = () => {
  const {values, setFieldValue} = useFormikContext ();
  const {t} = useTranslation ();

  // Tự động tính toán endDate khi durationMonths hoặc startDate thay đổi
  useEffect (() => {
    if (values?.durationMonths && values?.startDate && values?.contractType?.code !== "KXĐTH") {
      const endDate = moment (values.startDate)
          .add (values.durationMonths, "months")
          .subtract (1, "day") // Trừ đi 1 ngày
          .toDate ();
      setFieldValue ("endDate", endDate);
    }
  }, [values.durationMonths, values.startDate, values.contractType?.code, setFieldValue]);

  // Tự động tính toán durationMonths khi endDate thay đổi (chỉ áp dụng cho hợp đồng không xác định thời hạn)
  useEffect (() => {
    if (values?.endDate && values?.startDate && values?.contractType?.code === "KXĐTH") {
      const months = moment (values.endDate).diff (moment (values.startDate), "months");
      setFieldValue ("durationMonths", months);
    }
  }, [values.endDate, values.startDate, values.contractType?.code, setFieldValue]);

  const fillBHXH = async () => {
    try {
      const response = await getTaxBHXHByStaffId (values?.staff?.id);
      setFieldValue ("insuranceSalary", response.data.value);
      return response.data;
    } catch (error) {
      console.error ("Lỗi khi lấy thông tin BHXH:", error);
      return null;
    }
  };

  useEffect (() => {
    const updateInsuranceInfo = async () => {
      if (values?.staff && values?.hasSocialIns) {
        setFieldValue ("socialInsuranceNumber", values?.staff?.socialInsuranceNumber);
        try {
          const {data} = await fillBHXH ();
        } catch (error) {
          console.error ("Lỗi khi cập nhật lương BHXH:", error);
        }
      } else {
        setFieldValue ("socialInsuranceNumber", null);
        setFieldValue ("insuranceSalary", null);
      }
    };

    updateInsuranceInfo ();
  }, [values?.staff, values?.hasSocialIns]);

  return (
      <Grid container spacing={2}>
        {values?.contractType?.code !== "HDLD_KXDTH" && (
            <Grid item xs={12} sm={6} md={4}>
              <GlobitsNumberInput
                  label={"Số tháng hợp đồng"}
                  name='durationMonths'
                  inputProps={{maxLength:13}}
                  required={values?.contractType?.code !== "HDLD_KXDTH"}
              />
            </Grid>
        )}
        <Grid item xs={12} sm={6} md={4}>
          {/* Ngày bắt đầu hiệu lực */}
          <GlobitsDateTimePicker required label={t ("agreements.startDate")} name='startDate'/>
        </Grid>
        <Grid item xs={12} sm={6} md={4}>
          {/* Ngày hết hạn */}
          <GlobitsDateTimePicker label={t ("agreements.endDate")} name='endDate'/>
        </Grid>
      </Grid>
  );
};

export default memo (observer (StaffLabourAgreementCUForm));
