import { Button, DialogActions, DialogContent, Grid } from "@material-ui/core";
import GlobitsDateTimePicker from "app/common/form/GlobitsDateTimePicker";
import GlobitsTextField from "app/common/form/GlobitsTextField";
import { memo, useEffect, useState } from "react";
import GlobitsPopupV2 from "app/common/GlobitsPopupV2";
import { useStore } from "app/stores";
import { Form, Formik } from "formik";
import moment from "moment";
import { useTranslation } from "react-i18next";
import { useParams } from "react-router-dom/cjs/react-router-dom.min";
import * as Yup from "yup";
import BlockIcon from "@material-ui/icons/Block";
import SaveIcon from "@material-ui/icons/Save";
import { observer } from "mobx-react";

function StaffMaternityHistoryPopupAdd ({open}) {
  const {t} = useTranslation ();
  const {id} = useParams ();

  const {
    staffMaternityHistoryStore
  } = useStore ();

  const {
    selectedStaffMaternityHistory,
    initialStaffMaternityHistory,
    saveStaffMaternityHistory,
    handleClose,

  } = staffMaternityHistoryStore;

  const [formValues, setFormValues] = useState (initialStaffMaternityHistory);

  useEffect (() => {
    setFormValues (selectedStaffMaternityHistory || initialStaffMaternityHistory);
  }, [selectedStaffMaternityHistory, initialStaffMaternityHistory]);

  const validationSchema = Yup.object ({
    startDate:Yup.date ()
        .transform ((castValue, originalValue) => (originalValue? new Date (originalValue) : castValue))
        // .required (t ("validation.required"))
        .typeError ("Ngày bắt đầu không đúng định dạng")
        .nullable (),

    endDate:Yup.date ()
        .test ("is-greater", "Ngày kết thúc phải lớn hơn ngày bắt đầu", function (value) {
          return value && this.parent.startDate? moment (value).isAfter (this.parent.startDate, "date") : true;
        })
        .transform ((castValue, originalValue) => (originalValue? new Date (originalValue) : castValue))
        // .required (t ("validation.required"))
        .typeError ("Ngày kết thúc không đúng định dạng")
        .nullable (),

    birthNumber:Yup.number ().min (1, "Giá trị phải lớn hơn hoặc bằng 1 ").required (t ("validation.required")).nullable (),
  });

  const handleSubmit = (values) => {
    saveStaffMaternityHistory ({... values, staff:{id}});
  };

  return (
      <GlobitsPopupV2
          size={"sm"}
          open={open}
          onClosePopup={handleClose}
          title={<span
              // className="mb-20"
          >
                {selectedStaffMaternityHistory?.id? t ("general.button.edit")
                    : t ("general.button.add")} {t ("maternityHistory.title")}
            </span>
          }
          noDialogContent>

        <Formik
            initialValues={formValues}
            enableReinitialize
            onSubmit={handleSubmit}
            validationSchema={validationSchema}>
          {({isSubmitting, setFieldValue}) => (
              <Form autoComplete="off">
                <DialogContent className="dialog-body p-12" style={{maxHeight:"80vh", minWidth:"300px"}}>
                  <Grid container spacing={2}>
                    <Grid item xs={12} sm={6}>
                      <GlobitsDateTimePicker
                          label={t ("maternityHistory.maternityLeaveStartDate")}
                          name="maternityLeaveStartDate"
                          onChange={(value) => {
                            setFieldValue ("maternityLeaveStartDate", value)
                            const startDate = new Date (value);
                            const endDate = new Date (startDate);

                            const originalDay = endDate.getDate ();
                            endDate.setMonth (endDate.getMonth () + 6);

                            // Nếu ngày sau khi cộng 6 tháng không còn đúng (vì bị tràn), thì chỉnh về ngày cuối cùng của tháng
                            if (endDate.getDate () < originalDay) {
                              // Lùi về ngày 0 của tháng tiếp theo → tức là ngày cuối tháng hiện tại
                              endDate.setDate (0);
                            }
                            setFieldValue ("maternityLeaveEndDate", endDate);
                          }}
                      />
                    </Grid>
                    <Grid item xs={12} sm={6}>
                      <GlobitsDateTimePicker
                          label={t ("maternityHistory.maternityLeaveEndDate")}
                          name="maternityLeaveEndDate"
                      />
                    </Grid>
                    <Grid item xs={12} sm={6}>
                      <GlobitsDateTimePicker
                          label={t ("maternityHistory.startDate")}
                          // required
                          name="startDate"
                      />
                    </Grid>
                    <Grid item xs={12} sm={6}>
                      <GlobitsDateTimePicker
                          label={t ("maternityHistory.endDate")}
                          name="endDate"
                          // required
                      />
                    </Grid>
                    <Grid item xs={12} sm={6}>
                      <GlobitsTextField
                          name="birthNumber"
                          label={t ("maternityHistory.birthNumber")}
                          type="number"
                      />
                    </Grid>
                    <Grid item xs={12}>
                      <GlobitsTextField
                          name="note"
                          label={t ("maternityHistory.note")}
                          multiline
                          rows={4}
                      />
                    </Grid>
                  </Grid>
                </DialogContent>

                <div className='dialog-footer dialog-footer-v2 py-8 px-12'>
                  <DialogActions className='p-0'>
                    <div className='flex flex-space-between flex-middle'>
                      <Button
                          startIcon={<BlockIcon/>}
                          variant='contained'
                          className='mr-12 btn btn-secondary d-inline-flex'
                          color='secondary'
                          onClick={handleClose}
                          disabled={isSubmitting}>
                        {t ("general.button.cancel")}
                      </Button>

                      <Button
                          startIcon={<SaveIcon/>}
                          className='mr-0 btn btn-primary d-inline-flex'
                          variant='contained'
                          color='primary'
                          type='submit'
                          disabled={isSubmitting}>
                        {t ("general.button.save")}
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


export default memo (observer (StaffMaternityHistoryPopupAdd));





















