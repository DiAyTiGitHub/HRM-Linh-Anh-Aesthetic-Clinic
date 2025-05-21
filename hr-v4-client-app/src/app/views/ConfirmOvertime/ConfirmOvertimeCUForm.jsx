import React, { useState, useEffect, memo } from "react";
import { Formik, Form } from "formik";
import { Grid, DialogActions, Button, DialogContent } from "@material-ui/core";
import SaveIcon from "@material-ui/icons/Save";
import BlockIcon from "@material-ui/icons/Block";
import { useTranslation } from "react-i18next";
import { useStore } from "app/stores";
import * as Yup from "yup";
import { observer } from "mobx-react";
import GlobitsPopupV2 from "app/common/GlobitsPopupV2";
import { pagingShiftWork } from "../ShiftWork/ShiftWorkService";
import GlobitsDateTimePicker from "app/common/form/GlobitsDateTimePicker";
import { pagingStaff } from "../HumanResourcesInformation/StaffService";
import GlobitsPagingAutocomplete from "app/common/form/GlobitsPagingAutocomplete";
import GlobitsSelectInput from "app/common/form/GlobitsSelectInput";
import LocalConstants from "app/LocalConstants";
import ConfirmOTTableSection from "./ConfirmOTTableSection";
import { formatNumber } from "../../LocalFunction";

function ConfirmOvertimeCUForm (props) {
  const {t} = useTranslation ();
  const {readOnly} = props;
  const {
    confirmOvertimeStore,
    hrRoleUtilsStore
  } = useStore ();

  const {
    isAdmin, isManager
  } = hrRoleUtilsStore;


  const {
    handleClose,
    updateScheduleOTHours,
    pagingStaffWorkSchedule,
    selectedStaffWorkSchedule,
    openCreateEditPopup,
    openViewPopup
  } = confirmOvertimeStore;

  const validationSchema = Yup.object ({
    shiftWork:Yup.object ().required (t ("validation.required")).nullable (),
    staff:Yup.object ().required (t ("validation.required")).nullable (),
    workingDate:Yup.date ().transform (function transformDate (castValue, originalValue) {
      return originalValue? new Date (originalValue) : castValue;
    }).required (t ("validation.required")).nullable (),
    confirmedOTHoursBeforeShift:Yup.number ()
        .nullable ()
        .notRequired ()
        .test (
            "max-earlyArrival",
            "Số giờ yêu cầu trước ca phải nhỏ hơn hoặc bằng số giờ đến sớm",
            function (value) {
              const {earlyArrivalMinutes} = this.parent;
              const maxValue = earlyArrivalMinutes
                  ? formatNumber (earlyArrivalMinutes / 60) // Convert to hours
                  : 0;
              return value == null || value <= maxValue;
            }
        ),

    confirmedOTHoursAfterShift:Yup.number ()
        .nullable ()
        .notRequired ()
        .test (
            "max-lateExit",
            "Số giờ yêu cầu sau ca phải nhỏ hơn hoặc bằng số giờ về muộn",
            function (value) {
              const {lateExitMinutes} = this.parent;
              const maxValue = lateExitMinutes
                  ? formatNumber (lateExitMinutes / 60) // Convert to hours
                  : 0;
              return value == null || value <= maxValue;
            })
  });

  async function handleSaveForm (values) {
    try {
      await updateScheduleOTHours (values);

    } catch (error) {
      console.error (error);
    }
  }

  const [initialValues, setInitialValues] = useState (selectedStaffWorkSchedule);

  useEffect (
      function () {
        setInitialValues (selectedStaffWorkSchedule);
      },
      [selectedStaffWorkSchedule, selectedStaffWorkSchedule?.id]
  );

  return (
      <GlobitsPopupV2
          scroll={"body"}
          size='md'
          open={openCreateEditPopup || openViewPopup}
          //title={t("general.button.edit") + " " + t("staffWorkSchedule.title")}
          title={openViewPopup? "Xem chi tiết Xác nhận làm thêm giờ" : t ("Xác nhận làm thêm giờ")}
          noDialogContent
          onClosePopup={handleClose}
      >
        <Formik
            validationSchema={validationSchema}
            enableReinitialize
            initialValues={initialValues}
            onSubmit={handleSaveForm}
        >
          {({isSubmitting, values, setFieldValue, initialValues}) => {

            const convertMinutesToHours = (event, nameValueMinutes, nameValueHours) => {
              const minutes = event.target.value;
              setFieldValue (nameValueMinutes, minutes);
              const hours = (parseFloat (minutes) / 60).toFixed (2);
              setFieldValue (nameValueHours, hours);
            };

            return (
                <Form autoComplete='off'>
                  <DialogContent className='o-hidden dialog-body p-12'>
                    <Grid container spacing={2}>
                      <Grid item xs={12} className="pb-0">
                        <p className="m-0 p-0 borderThrough2">
                          Thông tin ca làm việc
                        </p>
                      </Grid>

                      <Grid item xs={12} sm={6} md={4}>
                        <GlobitsPagingAutocomplete
                            name='staff'
                            label="Nhân viên làm việc"
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

                      <Grid item xs={12} sm={6} md={4}>
                        <GlobitsPagingAutocomplete
                            name='shiftWork'
                            label={t ("staffWorkSchedule.shiftWorks")}
                            api={pagingShiftWork}
                            required
                            disabled
                            readOnly={readOnly}
                        />
                      </Grid>
                      <Grid item xs={12} sm={6} md={4}>
                        <GlobitsDateTimePicker
                            label={"Ngày làm việc"}
                            name='workingDate'
                            disabled
                            readOnly={readOnly}
                        />
                      </Grid>

                      <Grid item xs={12} sm={6} md={4}>
                        <GlobitsSelectInput
                            label={"Trạng thái làm việc"}
                            name="workingStatus"
                            options={LocalConstants.StaffWorkScheduleWorkingStatus.getListData ()}
                            hideNullOption={true}
                            disabled
                            keyValue="value"
                            readOnly={readOnly}
                        />
                      </Grid>

                      <Grid item xs={12} sm={6} md={4}>
                        <GlobitsSelectInput
                            label={"Trạng thái làm việc"}
                            name="workingStatus"
                            options={LocalConstants.StaffWorkScheduleWorkingStatus.getListData ()}
                            hideNullOption={true}
                            disabled
                            keyValue="value"
                            readOnly={readOnly}
                        />
                      </Grid>

                      {values?.otEndorser?.id && (
                          <Grid item xs={12} sm={6} md={4}>
                            <GlobitsPagingAutocomplete
                                name='otEndorser'
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
                          Xác nhận số giờ làm thêm trước/sau ca
                        </p>
                      </Grid>

                      <Grid item xs={12}>
                        <ConfirmOTTableSection readOnly={readOnly}/>
                      </Grid>

                    </Grid>

                  </DialogContent>

                  <DialogActions className='dialog-footer flex flex-end flex-middle px-12'>
                    <Button
                        startIcon={<BlockIcon/>}
                        className={`${!readOnly && "mr-8"} btn-secondary d-inline-flex`}
                        onClick={handleClose}
                        disabled={isSubmitting}
                    >
                      {t ("general.button.cancel")}
                    </Button>

                    {(isAdmin || isManager) && !readOnly && (
                        <Button
                            startIcon={<SaveIcon/>}
                            className='btn btn-primary d-inline-flex'
                            type='submit'
                            disabled={isSubmitting}
                        >
                          {t ("Xác nhận")}
                        </Button>
                    )}
                  </DialogActions>
                </Form>
            )
          }}
        </Formik>
      </GlobitsPopupV2>
  );
}

export default memo (observer (ConfirmOvertimeCUForm));