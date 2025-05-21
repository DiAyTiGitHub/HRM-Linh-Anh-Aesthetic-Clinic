import {
  Button,
  DialogActions,
  DialogContent,
  Grid,
} from "@material-ui/core";
import BlockIcon from "@material-ui/icons/Block";
import SaveIcon from "@material-ui/icons/Save";
import { Form, Formik } from "formik";
import { memo, useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import * as Yup from "yup";
import { useStore } from "app/stores";
import GlobitsEditor from "app/common/form/GlobitsEditor";
import GlobitsPopupV2 from "app/common/GlobitsPopupV2";
import LocalConstants from "app/LocalConstants";
import { observer } from "mobx-react";
import FormikFocusError from "app/common/FormikFocusError";
import GlobitsVNDCurrencyInput from "app/common/form/GlobitsVNDCurrencyInput";
import GlobitsPagingAutocomplete from "app/common/form/GlobitsPagingAutocomplete";
import GlobitsSelectInput from "app/common/form/GlobitsSelectInput";
import GlobitsDateTimePicker from "app/common/form/GlobitsDateTimePicker";
import { pagingRecruitmentRequest } from "../Recruitment/RecruitmentRequestV2/RecruitmentRequestV2Service";
import GlobitsTextField from "app/common/form/GlobitsTextField";
import ChooseUsingStaffSection from "../User/UsingAccountStaff/ChooseUsingStaffSection";
import { pagingShiftWork } from "../ShiftWork/ShiftWorkService";
import GlobitsCheckBox from "app/common/form/GlobitsCheckBox";

function ShiftRegistrationForm() {
  const { ShiftRegistrationStore } = useStore();
  const { t } = useTranslation();
  const {
    handleClose,
    saveShiftRegistration,
    pagingShiftRegistration,
    selectedShiftRegistration,
    openCreateEditPopup
  } = ShiftRegistrationStore;

  const validationSchema = Yup.object({
    shiftWork: Yup.object().required(t("validation.required")).nullable(),
    registerStaff: Yup.object().required(t("validation.required")).nullable(),
    workingDate: Yup.string().required(t("validation.required")).nullable(),
  });

  async function handleSaveForm(values) {
    try {
      const response = await saveShiftRegistration(values);
      if (response) await pagingShiftRegistration();
    } catch (error) {
      console.error(error);
    }
  }

  const [initialValues, setInitialValues] = useState(selectedShiftRegistration);

  useEffect(function () {
    setInitialValues(selectedShiftRegistration);
  }, [selectedShiftRegistration, selectedShiftRegistration?.id]);

  return (
    <GlobitsPopupV2
      scroll={"body"}
      size="xs"
      open={openCreateEditPopup}
      noDialogContent
      title={(selectedShiftRegistration?.id ? t("general.button.edit") : t("general.button.add")) + ' ' + t("navigation.shiftRegistration.title")}
      onClosePopup={handleClose}
    >
      <Formik
        validationSchema={validationSchema}
        enableReinitialize
        initialValues={initialValues}
        onSubmit={handleSaveForm}
      >
        {({ isSubmitting, values, setFieldValue, initialValues }) => {

          return (
            <Form autoComplete="off">
              <div className="dialog-body">
                <DialogContent className="p-12">
                  <FormikFocusError />

                  <Grid container spacing={2}>
                    <Grid item xs={12}>
                      <GlobitsDateTimePicker
                        label={t("Ngày làm việc")}
                        name="workingDate"
                        required
                      />
                    </Grid>

                    <Grid item xs={12}>
                      <ChooseUsingStaffSection
                        label={t("Nhân viên đăng ký")}
                        name="registerStaff"
                        required
                      />
                    </Grid>

                    <Grid item xs={12}>
                      <GlobitsPagingAutocomplete
                        name='shiftWork' label={t("Ca làm việc")}
                        api={pagingShiftWork}
                        required
                      />
                    </Grid>

                    {/* <Grid item xs={12}>
                      <GlobitsSelectInput
                        label={"Loại làm việc"}
                        name="workingType"
                        options={LocalConstants.StaffWorkScheduleWorkingType.getListData()}
                        hideNullOption={true}
                        keyValue="value"
                      />
                    </Grid> */}

                    <Grid item xs={12}>
                      {
                        values?.workingType == LocalConstants?.StaffWorkScheduleWorkingType?.EXTENDED_OVERTIME.value && (
                          <GlobitsVNDCurrencyInput
                            label={"Số giờ đăng ký làm thêm"}
                            name='overtimeHours'
                          />
                        )
                      }
                    </Grid>

                    <Grid item xs={12}>
                      <GlobitsSelectInput
                        hideNullOption
                        label={"Trạng thái phê duyệt"}
                        name="approvalStatus"
                        keyValue="value"
                        readOnly
                        options={LocalConstants.ShiftRegistrationApprovalStatus.getListData()}
                      />
                    </Grid>

                    {/* <Grid item xs={12} sm={6} className="pt-35 pl-20">
                      <GlobitsCheckBox
                        label={"Chỉ chấm công vào ra 1 lần"}
                        name="allowOneEntryOnly"
                      />
                    </Grid> */}

                    {values?.approvalStaff?.id && (
                      <Grid item xs={12}>
                        <ChooseUsingStaffSection
                          label={t("Người phê duyệt")}
                          name="approvalStaff"
                          readOnly
                        />
                      </Grid>
                    )}

                  </Grid>

                </DialogContent>
              </div>

              <div className="dialog-footer dialog-footer-v2 py-8">
                <DialogActions className="p-0">
                  <div className="flex flex-space-between flex-middle">
                    <Button
                      startIcon={<BlockIcon />}
                      variant="contained"
                      className="mr-12 btn btn-secondary d-inline-flex"
                      color="secondary"
                      onClick={handleClose}
                      disabled={isSubmitting}
                    >
                      {t("general.button.cancel")}
                    </Button>
                    <Button
                      startIcon={<SaveIcon />}
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
              </div>
            </Form>
          );
        }}
      </Formik>
      {/* <ResetPassWord open={open} setOpen={setOpen} /> */}
    </GlobitsPopupV2>
  );
}

export default memo(observer(ShiftRegistrationForm));