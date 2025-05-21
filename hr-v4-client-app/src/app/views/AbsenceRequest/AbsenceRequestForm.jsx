import {
  Button,
  DialogActions,
  DialogContent,
  Grid,
} from "@material-ui/core";
import BlockIcon from "@material-ui/icons/Block";
import SaveIcon from "@material-ui/icons/Save";
import { Form, Formik, useFormikContext } from "formik";
import React, { memo, useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import * as Yup from "yup";
import { useStore } from "app/stores";
// import "./RequestStyle.scss";
import GlobitsEditor from "app/common/form/GlobitsEditor";
import GlobitsPopupV2 from "app/common/GlobitsPopupV2";
import { observer } from "mobx-react";
import FormikFocusError from "app/common/FormikFocusError";
import GlobitsPagingAutocompleteV2 from "app/common/form/GlobitsPagingAutocompleteV2";
import { pagingStaffWorkSchedule } from "../StaffWorkScheduleV2/StaffWorkScheduleService";
import GlobitsSelectInput from "app/common/form/GlobitsSelectInput";
import GlobitsDateTimePicker from "app/common/form/GlobitsDateTimePicker";
import LocalConstants from "app/LocalConstants";
import ChooseUsingStaffSection from "../User/UsingAccountStaff/ChooseUsingStaffSection";
import { format } from "date-fns";
import moment from "moment";
import GlobitsTextField from "app/common/form/GlobitsTextField";

function AbsenceRequestForm() {
  const { absenceRequestStore } = useStore();
  const { t } = useTranslation();
  const {
    handleClose,
    saveAbsenceRequest,
    pagingAbsenceRequest,
    selectedAbsenceRequest,
    openCreateEditPopup,
    handelOpenDepartmentPopup,
    openDepartmentPopup
  } = absenceRequestStore;

  const validationSchema = Yup.object({
    staff: Yup.object().required(t("validation.required")).nullable(),
    workSchedule: Yup.object().required(t("validation.required")).nullable(),
  });

  async function handleSaveForm(values) {
    try {
      const response = await saveAbsenceRequest(values);
      if (response) await pagingAbsenceRequest();
    } catch (error) {
      console.error(error);
    }
  }

  return (
    <GlobitsPopupV2
      scroll={"body"}
      size="sm"
      open={openCreateEditPopup}
      noDialogContent
      title={(selectedAbsenceRequest?.id ? t("general.button.edit") : t("general.button.add")) + ' ' + t("navigation.absenceRequest.title")}
      onClosePopup={handleClose}
    >
      <Formik
        validationSchema={validationSchema}
        enableReinitialize
        initialValues={selectedAbsenceRequest}
        onSubmit={handleSaveForm}
      >
        {({ isSubmitting, values, setFieldValue, initialValues }) => {
          return (
            <Form autoComplete="off">
              <div className="dialog-body">
                <DialogContent className="p-12">
                  <FormikFocusError />

                  <Grid container spacing={2}>
                    {!values?.id ? (
                      <Grid item sm={6} xs={12} md={6}>
                        <Grid item xs>
                          <ChooseUsingStaffSection
                            label="Nhân viên yêu cầu"
                            name="staff"
                            required
                            clearFields={['workSchedule']}
                          />
                        </Grid>
                      </Grid>
                    ) : (
                      <Grid item sm={6} xs={12} md={6}>
                        <GlobitsTextField
                          label="Nhân viên yêu cầu"
                          name="staff.displayName"
                          disabled
                        />
                      </Grid>
                    )}
                    
                    <Grid item sm={6} xs={12} md={6}>
                      <GlobitsPagingAutocompleteV2
                        name="workSchedule"
                        label="Ngày làm việc"
                        api={pagingStaffWorkSchedule}
                        searchObject={{
                          pageIndex: 1, 
                          pageSize: 9999, 
                          keyword: "",
                          staffId: values?.staff?.id
                        }}
                        disabled={!values?.staff}
                        getOptionLabel={(option) => 
                          (option?.workingDate) ? (
                            moment(option.workingDate).format("DD/MM/YYYY") + ' - ' + option?.shiftWork?.name
                          ) : ""
                        }
                        required
                      />
                    </Grid>

                    <Grid item sm={6} xs={12} md={6}>
                      <GlobitsDateTimePicker
                        label={t("Ngày yêu cầu nghỉ")}
                        name="requestDate"
                      />
                    </Grid>

                    <Grid item xs={12} sm={6} md={6}>
                      <GlobitsSelectInput
                          label={"Loại nghỉ phép"}
                          name='absenceType'
                          keyValue='value'
                          hideNullOption={true}
                          options={LocalConstants.AbsenceRequestType.getListData()}
                        />
                    </Grid>

                    <Grid item xs={12} sm={6} md={6}>
                      <GlobitsSelectInput
                          label={"Trạng thái yêu cầu"}
                          name='approvalStatus'
                          keyValue='value'
                          hideNullOption={true}
                          options={LocalConstants.AbsenceRequestApprovalStatus.getListData()}
                        />
                    </Grid>

                    <Grid item xs={12}>
                      <GlobitsTextField
                          label={"Lý do nghỉ"}
                          name="absenceReason"
                          multiline
                          rows={3}
                      />
                    </Grid>
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
    </GlobitsPopupV2>
  );
}

export default memo(observer(AbsenceRequestForm));