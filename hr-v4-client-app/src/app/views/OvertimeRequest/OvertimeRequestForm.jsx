import {
  Button,
  DialogActions,
  DialogContent,
  Grid,
} from "@material-ui/core";
import BlockIcon from "@material-ui/icons/Block";
import SaveIcon from "@material-ui/icons/Save";
import { Form, Formik } from "formik";
import React, { memo } from "react";
import { useTranslation } from "react-i18next";
import * as Yup from "yup";
import { useStore } from "app/stores";
// import "./RequestStyle.scss";
import GlobitsPopupV2 from "app/common/GlobitsPopupV2";
import { observer } from "mobx-react";
import FormikFocusError from "app/common/FormikFocusError";
import GlobitsPagingAutocompleteV2 from "app/common/form/GlobitsPagingAutocompleteV2";
import { pagingStaffWorkSchedule } from "../StaffWorkScheduleV2/StaffWorkScheduleService";
import GlobitsSelectInput from "app/common/form/GlobitsSelectInput";
import LocalConstants from "app/LocalConstants";
import ChooseUsingStaffSection from "../User/UsingAccountStaff/ChooseUsingStaffSection";
import moment from "moment";
import GlobitsTextField from "app/common/form/GlobitsTextField";
import GlobitsPagingAutocomplete from "app/common/form/GlobitsPagingAutocomplete";
import { pagingStaff } from "../HumanResourcesInformation/StaffService";
import OvertimeRequestRequestTableInfo from "./OvertimeRequestRequestTableInfo";
import { formatNumber } from "../../LocalFunction";

function OvertimeRequestForm (props) {
  const {t} = useTranslation ();
  const {readOnly} = props;
  const {
    overtimeRequestStore,
    hrRoleUtilsStore
  } = useStore ();

  const {
    handleClose,
    saveOvertimeRequest,
    pagingOvertimeRequest,
    selectedOvertimeRequest,
    openCreateEditPopup,
    openViewPopup
  } = overtimeRequestStore;

  const {
    isAdmin,
    isManager
  } = hrRoleUtilsStore;

  const validationSchema = Yup.object ({
    staff:Yup.object ().required (t ("validation.required")).nullable (),
    staffWorkSchedule:Yup.object ().required (t ("validation.required")).nullable (),
    requestOTHoursBeforeShift:Yup.number ()
        .nullable ()
        .notRequired ()
        .test (
            "max-earlyArrival",
            "Số giờ yêu cầu trước ca phải nhỏ hơn hoặc bằng số giờ đến sớm",
            function (value) {
              const {staffWorkSchedule} = this.parent;
              const maxValue = staffWorkSchedule?.earlyArrivalMinutes
                  ? formatNumber (staffWorkSchedule.earlyArrivalMinutes / 60) // Convert to hours
                  : 0;
              return value == null || value <= maxValue;
            }
        ),
    requestOTHoursAfterShift:Yup.number ()
        .nullable ()
        .notRequired ()
        .test (
            "max-lateExit",
            "Số giờ yêu cầu sau ca phải nhỏ hơn hoặc bằng số giờ về muộn",
            function (value) {
              const {staffWorkSchedule} = this.parent;
              const maxValue = staffWorkSchedule?.lateExitMinutes
                  ? formatNumber (staffWorkSchedule.lateExitMinutes / 60) // Convert to hours
                  : 0;
              return value == null || value <= maxValue;
            }
        ),
  });

  async function handleSaveForm (values) {
    try {
      const response = await saveOvertimeRequest (values);
      if (response) await pagingOvertimeRequest ();
    } catch (error) {
      console.error (error);
    }
  }


  return (
      <GlobitsPopupV2
          scroll={"body"}
          size="md"
          open={openCreateEditPopup || openViewPopup}
          noDialogContent
          title={openViewPopup? ("Xem chi tiết " + t ("navigation.overtimeRequest.title")) : (selectedOvertimeRequest?.id? t ("general.button.edit") : t ("general.button.add")) + ' ' + t ("navigation.overtimeRequest.title")}
          onClosePopup={handleClose}
      >
        <Formik
            validationSchema={validationSchema}
            enableReinitialize
            initialValues={selectedOvertimeRequest}
            onSubmit={handleSaveForm}
        >
          {({isSubmitting, values, setFieldValue, initialValues}) => {

            const hideSubmitButton = !isAdmin && !isManager && values?.approvalStatus === LocalConstants.OvertimeRequestApprovalStatus.APPROVED.value;

            return (
                <Form autoComplete="off">
                  <div className="dialog-body">
                    <DialogContent className="p-12">
                      <FormikFocusError/>

                      <Grid container spacing={2}>
                        <Grid item xs={12} className="pb-0">
                          <p className="m-0 p-0 borderThrough2">
                            Thông tin yêu cầu
                          </p>
                        </Grid>

                        {!values?.id? (
                            <Grid item sm={6} xs={12} md={6}>
                              <ChooseUsingStaffSection
                                  label="Nhân viên yêu cầu"
                                  name="staff"
                                  required
                                  disabled={!isAdmin}
                                  clearFields={['staffWorkSchedule']}
                                  readOnly={readOnly}
                                  isBasic={true}
                              />
                            </Grid>
                        ) : (
                            <Grid item sm={6} xs={12} md={6}>
                              <GlobitsTextField
                                  label="Nhân viên yêu cầu"
                                  name="staff.displayName"
                                  disabled
                                  readOnly={readOnly}
                              />
                            </Grid>
                        )}

                        <Grid item sm={6} xs={12} md={6}>
                          <GlobitsPagingAutocompleteV2
                              name="staffWorkSchedule"
                              label="Ngày làm việc"
                              api={pagingStaffWorkSchedule}
                              searchObject={{
                                pageIndex:1,
                                pageSize:9999,
                                keyword:"",
                                staffId:values?.staff?.id
                              }}
                              disabled={!values?.staff}
                              getOptionLabel={(option) =>
                                  (option?.workingDate)? (
                                      moment (option.workingDate).format ("DD/MM/YYYY") + ' - ' + option?.shiftWork?.name
                                  ) : ""
                              }
                              required
                              readOnly={readOnly}
                          />
                        </Grid>

                        <Grid item xs={12} sm={6} md={6}>
                          <GlobitsSelectInput
                              label={"Trạng thái yêu cầu"}
                              name='approvalStatus'
                              keyValue='value'
                              hideNullOption={true}
                              disabled
                              options={LocalConstants.OvertimeRequestApprovalStatus.getListData ()}
                              readOnly={readOnly}
                          />
                        </Grid>

                        {values?.staffWorkSchedule?.otEndorser?.id && (
                            <Grid item xs={12} sm={6}>
                              <GlobitsPagingAutocomplete
                                  name='staffWorkSchedule.otEndorser'
                                  label="Người xác nhận OT"
                                  api={pagingStaff}
                                  disabled
                                  required
                                  getOptionLabel={(option) =>
                                      option?.displayName && option?.staffCode
                                          ? `${option.displayName} - ${option.staffCode}`
                                          : option?.displayName || option?.staffCode || ''
                                  }
                                  displayData='displayName'
                                  readOnly={readOnly}
                              />
                            </Grid>
                        )}


                        <Grid item xs={12} className="pb-0">
                          <p className="m-0 p-0 borderThrough2">
                            Yêu cầu xác nhận giờ làm thêm
                          </p>
                        </Grid>

                        <Grid item xs={12}>
                          {values?.staffWorkSchedule?.id && (
                              <OvertimeRequestRequestTableInfo readOnly={readOnly}/>
                          )}

                          {!values?.staffWorkSchedule?.id && (
                              <div className="w-100 flex flex-center justify-center">
                                Chưa chọn ca làm việc
                              </div>
                          )}
                        </Grid>

                      </Grid>

                    </DialogContent>
                  </div>

                  <div className="dialog-footer dialog-footer-v2 py-8">
                    <DialogActions className="p-0">
                      <div className="flex flex-space-between flex-middle">
                        <Button
                            startIcon={<BlockIcon/>}
                            variant="contained"
                            className={`${!readOnly && "mr-12"} btn-secondary d-inline-flex`}
                            color="secondary"
                            onClick={handleClose}
                            disabled={isSubmitting}
                        >
                          {t ("general.button.cancel")}
                        </Button>

                        {!hideSubmitButton && !readOnly && (
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
                        )}

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

export default memo (observer (OvertimeRequestForm));