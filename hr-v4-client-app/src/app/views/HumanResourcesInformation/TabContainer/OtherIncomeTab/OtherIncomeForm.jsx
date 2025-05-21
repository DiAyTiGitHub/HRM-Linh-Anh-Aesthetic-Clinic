import React, { useEffect, useState } from "react";
import { Button, DialogActions, DialogContent, Grid, Tooltip, } from "@material-ui/core";
import GlobitsDateTimePicker from "app/common/form/GlobitsDateTimePicker";
import { useTranslation } from "react-i18next";
import * as Yup from "yup";
import { Form, Formik } from "formik";
import { useStore } from "app/stores";
import GlobitsPopupV2 from "app/common/GlobitsPopupV2";
import { observer } from "mobx-react";
import { useParams } from "react-router-dom";
import { pagingRewards } from "../../../Reward/RewardService";
import SelectFile from "../../../StaffDocumentItem/SelectFile";
import CloudUploadIcon from "@material-ui/icons/CloudUpload";
import VisibilityIcon from "@material-ui/icons/Visibility";
import GetAppIcon from "@material-ui/icons/GetApp";
import DeleteIcon from "@material-ui/icons/Delete";
import GlobitsPagingAutocompleteV2 from "../../../../common/form/GlobitsPagingAutocompleteV2";
import { pagingOrganization } from "../../../Organization/OrganizationService";
import { pagingAllDepartments } from "../../../Department/DepartmentService";
import SaveIcon from "@material-ui/icons/Save";
import BlockIcon from "@material-ui/icons/Block";
import { pagingSalaryPeriod } from "../../../Salary/SalaryPeriod/SalaryPeriodService";
import GlobitsVNDCurrencyInput from "../../../../common/form/GlobitsVNDCurrencyInput";
import GlobitsSelectInput from "../../../../common/form/GlobitsSelectInput";
import { OtherIncomeType } from "../../../../LocalConstants";
import GlobitsTextField from "../../../../common/form/GlobitsTextField";

export default observer (function StaffRewardHistoryForm () {
  const {otherIncomeStore} = useStore ();
  const {t} = useTranslation ();
  const {id} = useParams ();
  const {
    handleClose,
    saveOtherIncome,
    openViewPopup,
    openCreateEditPopup,
    selectedOtherIncome
  } = otherIncomeStore;
  const validationSchema = Yup.object ({
    type:Yup.number ().nullable (),
    // staff:Yup.object().nullable() ,
    salaryPeriod:Yup.object ().nullable (),
    income:Yup.number ().nullable (),
    note:Yup.string ().nullable (),
    decisionDate:Yup.date ()
        .transform (function transformDate (castValue, originalValue) {
          return originalValue? new Date (originalValue) : castValue;
        })
        // .required(t("validation.required"))
        .typeError ("Ngày đúng định dạng")
        .nullable (),

  });

  async function handleSubmit (values) {
    const newValue = {
      ... values,
      staff:{
        id:id
      }
    }
    await saveOtherIncome (newValue);
  }

  return (
      <GlobitsPopupV2
          size={"sm"}
          open={openCreateEditPopup || openViewPopup}
          onClosePopup={handleClose}
          noDialogContent
          title={openViewPopup? (t ("general.button.view") + " " + "Thu nhập/ khấu trừ") : (selectedOtherIncome?.id? t ("general.button.edit") : t ("general.button.add") + " " + "Thu nhập/ khấu trừ")}
      >
        <Formik
            initialValues={selectedOtherIncome}
            onSubmit={(values) => handleSubmit (values)}
            validationSchema={validationSchema}
        >
          {({isSubmitting, values}) => (
              <Form autoComplete="off">
                <DialogContent className='dialog-body p-12'>
                  <Grid container spacing={2}>
                    <Grid item xs={12} md={6}>
                      <GlobitsTextField
                          name="name"
                          label={t ("Tên khoản thu nhập/khấu trừ")}
                      />
                    </Grid>
                    <Grid item xs={12} md={6}>
                      <GlobitsTextField
                          name="code"
                          label={t ("Mã khoản thu nhập/khấu trừ")}
                      />
                    </Grid>
                    <Grid item xs={12} md={6}>
                      <GlobitsPagingAutocompleteV2
                          name="salaryPeriod"
                          label={t ("Kỳ lương")}
                          api={pagingSalaryPeriod}
                      />
                    </Grid>
                    <Grid item xs={12} md={6}>
                      <GlobitsVNDCurrencyInput
                          name="income"
                          label={t ("Số tiền")}
                      />
                    </Grid>
                    <Grid item xs={12} md={6}>
                      <GlobitsSelectInput
                          name="type"
                          label={t ("Loại khoản thu nhập/khấu trừ")}
                          options={OtherIncomeType.getListData ()}
                      />
                    </Grid>
                    <Grid item xs={12} md={6}>
                      <GlobitsDateTimePicker
                          label={t ("Ngày quyết định")}
                          name="decisionDate"
                      />
                    </Grid>
                    <Grid item xs={12}>
                      <GlobitsTextField
                          name="note"
                          label={t ("Ghi chú")}
                          multiline
                          rows={3}
                      />
                    </Grid><Grid item xs={12}>
                    {/*<GlobitsTextField*/}
                    {/*    name="description"*/}
                    {/*    label={t ("Mô tả")}*/}
                    {/*    multiline*/}
                    {/*    rows={3}*/}
                    {/*/>*/}
                  </Grid>
                  </Grid>
                </DialogContent>

                <DialogActions className='dialog-footer px-12'>
                  <div className="flex flex-space-between flex-middle">
                    <Button
                        startIcon={<BlockIcon/>}
                        variant="contained"
                        className="mr-12 btn btn-secondary d-inline-flex"
                        color="secondary"
                        onClick={() => {
                          handleClose ();
                        }}
                    >
                      {t ("general.button.close")}
                    </Button>
                    <Button
                        startIcon={<SaveIcon/>}
                        className="mr-0 btn btn-primary d-inline-flex"
                        variant="contained"
                        color="primary"
                        type="submit"
                        disabled={isSubmitting}
                    >
                      {t ("general.button.save")}
                    </Button>
                  </div>
                </DialogActions>
              </Form>
          )}
        </Formik>
      </GlobitsPopupV2>
  )
})