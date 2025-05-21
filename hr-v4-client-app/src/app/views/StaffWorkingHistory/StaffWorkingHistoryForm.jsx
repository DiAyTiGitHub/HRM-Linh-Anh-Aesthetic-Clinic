import { Button, DialogActions, DialogContent, Grid } from "@material-ui/core";
import GlobitsPopupV2 from "app/common/GlobitsPopupV2";
import GlobitsDateTimePicker from "app/common/form/GlobitsDateTimePicker";
import GlobitsPagingAutocompleteV2 from "app/common/form/GlobitsPagingAutocompleteV2";
import { Form, Formik } from "formik";
import { observer } from "mobx-react";
import moment from "moment";
import { memo, useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import * as Yup from "yup";
import { useStore } from "../../stores";
import { pagingAllDepartments } from "../Department/DepartmentService";
import { pagingAllOrg } from "../Organization/OrganizationService";
import { pagingPosition } from "../Position/PositionService";
import ChooseUsingStaffSection from "../User/UsingAccountStaff/ChooseUsingStaffSection";

function StaffWorkingHistoryForm() {
  const { t } = useTranslation();
  const { staffWorkingHistoryStore } = useStore();

  const { handleClose, saveStaffWorkingHistory, pagingStaffWorkingHistory, selectedStaffWorkingHistory, openCreateEditPopup, isAdmin } = staffWorkingHistoryStore;

  const validationSchema = Yup.object({
    startDate: Yup.date()
      .test("is-greater", "Ngày bắt đầu phải lớn thiết lập", function (value) {
        const { signedDate } = this.parent;
        if (signedDate && value) {
          return moment(value).isAfter(moment(signedDate), "date");
        }
        return true;
      })
      .transform(function transformDate(castValue, originalValue) {
        return originalValue ? new Date(originalValue) : castValue;
      })
      .required(t("validation.required"))
      .typeError("Ngày bắt đầu không đúng định dạng")
      .nullable(),

    endDate: Yup.date()
      .test("is-greater", "Ngày kết thúc phải lớn ngày bắt đầu", function (value) {
        const { startDate } = this.parent;
        if (startDate && value) {
          return moment(value).isAfter(moment(startDate), "date");
        }
        return true;
      })
      .transform(function transformDate(castValue, originalValue) {
        return originalValue ? new Date(originalValue) : castValue;
      })
      // .required(t("validation.required"))
      .typeError("Ngày kết thúc không đúng định dạng")
      .nullable(),
  });

  async function handleSaveForm(values) {
    await saveStaffWorkingHistory(values);
    await pagingStaffWorkingHistory();
  }

  const [initialValues, setInitialValues] = useState(selectedStaffWorkingHistory);

  useEffect(
    function () {
      setInitialValues({
        ...selectedStaffWorkingHistory,
      });
    },
    [selectedStaffWorkingHistory, selectedStaffWorkingHistory?.id]
  );

  return (
    <GlobitsPopupV2 size='md' scroll={"body"} open={openCreateEditPopup} noDialogContent title={(selectedStaffWorkingHistory?.id ? t("general.button.edit") : t("general.button.add")) + " " + t("Điều chuyển")} onClosePopup={handleClose}>
      <Formik validationSchema={validationSchema} enableReinitialize initialValues={initialValues} onSubmit={handleSaveForm}>
        {({ isSubmitting, values, setFieldValue, initialValues }) => {
          return (
            <Form autoComplete='off'>
              <DialogContent className='dialog-body p-12'>
                <Grid container spacing={2}>
                  <Grid item xs={12} sm={6} md={4}>
                    <ChooseUsingStaffSection required label={"Nhân viên áp dụng"} />
                  </Grid>

                  <Grid item sm={4}>
                    <GlobitsDateTimePicker label={"Ngày bắt đầu"} name='startDate' />
                  </Grid>

                  <Grid item sm={4}>
                    <GlobitsDateTimePicker label={"Ngày kết thúc"} name='endDate' />
                  </Grid>

                  <Grid item sm={4}>
                    <GlobitsPagingAutocompleteV2 required name='fromOrganization' label={t("Từ tổ chức")} api={pagingAllOrg} />
                  </Grid>

                  <Grid item sm={4}>
                    <GlobitsPagingAutocompleteV2 required name='fromDepartment' label={t("Từ phòng ban")} api={pagingAllDepartments} />
                  </Grid>
                  <Grid item sm={4}>
                    <GlobitsPagingAutocompleteV2 required name='fromPosition' label={t("Từ vị trí")} api={pagingPosition} />
                  </Grid>

                  <Grid item sm={4}>
                    <GlobitsPagingAutocompleteV2 required name='toOrganization' label={t("Đến tổ chức")} api={pagingAllOrg} />
                  </Grid>

                  <Grid item sm={4}>
                    <GlobitsPagingAutocompleteV2 required name='toDepartment' label={t("Đến phòng ban")} api={pagingAllDepartments} />
                  </Grid>

                  <Grid item sm={4}>
                    <GlobitsPagingAutocompleteV2 required name='toPosition' label={t("Đến vị trí")} api={pagingPosition} />
                  </Grid>
                </Grid>
              </DialogContent>

              <DialogActions className='dialog-footer px-12'>
                <div className='flex flex-space-between flex-middle'>
                  <Button variant='contained' className='mr-12 btn btn-secondary d-inline-flex' color='secondary' disabled={isSubmitting} onClick={handleClose}>
                    {t("general.button.close")}
                  </Button>
                  <Button className='mr-0 btn btn-primary d-inline-flex' variant='contained' color='primary' type='submit' disabled={isSubmitting}>
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

export default memo(observer(StaffWorkingHistoryForm));
