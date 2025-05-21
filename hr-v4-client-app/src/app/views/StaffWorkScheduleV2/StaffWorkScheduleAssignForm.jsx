import { Button, DialogActions, DialogContent, Grid, Paper } from "@material-ui/core";
import BlockIcon from "@material-ui/icons/Block";
import PieChartOutlinedIcon from "@material-ui/icons/PieChartOutlined";
import TabAccordion from "app/common/Accordion/TabAccordion";
import GlobitsCheckBox from "app/common/form/GlobitsCheckBox";
import GlobitsDateTimePicker from "app/common/form/GlobitsDateTimePicker";
import GlobitsPopupV2 from "app/common/GlobitsPopupV2";
import { useStore } from "app/stores";
import { Form, Formik, useFormikContext } from "formik";
import { observer } from "mobx-react";
import moment from "moment";
import { memo, useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import * as Yup from "yup";
import TabResultStaffs from "../Salary/SalaryResultBoardConfig/SalaryResultConfigTabs/TabResultStaffs";
import SWSChooseShiftWorkSection from "./SWSAssignFormSections/SWSChooseShiftWorkSection";
import SelectMultipleStaffsComponent
  from "app/common/SelectComponent/SelectMultipleStaffs/SelectMultipleStaffsComponent";
import SWSDivisionFilter from "./SWSAssignFormSections/SWSDivisionFilter";
import { reaction } from "mobx";
import { toast } from "react-toastify";

function StaffWorkScheduleAssignForm (props) {
  const {additionalFunction} = props;
  const [listSelectedStaffs, setListSelectedStaffs] = useState ([]);
  const {t} = useTranslation ();
  const {timeSheetDetailStore, staffWorkScheduleStore, hrRoleUtilsStore, userStore} = useStore ();
  const {
    resetUsingStaffSection
  } = userStore;
  const {
    handleClose,
    createStaffWorkScheduleList,
    openAssignForm,
    initialShiftAssignmentForm,
    // handleSetDefaultSearchObject
  } = staffWorkScheduleStore;

  const validationSchema = Yup.object ({
    fromDate:Yup.date ()
        .transform ((castValue, originalValue) => (originalValue? new Date (originalValue) : castValue))
        .required (t ("validation.required"))
        .typeError ("Ngày bắt đầu không đúng định dạng")
        .nullable (),

    toDate:Yup.date ()
        .test ("is-greater-or-equal", "Ngày kết thúc phải lớn hơn hoặc bằng ngày bắt đầu", function (value) {
          const {fromDate} = this.parent;
          return fromDate && value? moment (value).isSameOrAfter (moment (fromDate), "date") : true;
        })
        .transform ((castValue, originalValue) => (originalValue? new Date (originalValue) : castValue))
        .required (t ("validation.required"))
        .typeError ("Ngày kết thúc không đúng định dạng")
        .nullable (),

    shiftWorks:Yup.array ()
        .min (1, "Cần chọn ít nhất một ca làm việc")
        .required ("Cần chọn ít nhất một ca làm việc"),

    staffs:Yup.array ()
        .min (1, "Cần chọn ít nhất một nhân viên")
        .required ("Cần chọn ít nhất một nhân viên"),
  });


  async function handleFormSubmit (values) {
    try {

      await createStaffWorkScheduleList (values);

      if (additionalFunction) {
        additionalFunction ();
      }
    } catch (error) {
      console.error (error);
      // toast.error();
    }
  }

  const weekDaysLoopList = [
    {name:"loopOnMonday", label:"Thứ 2"},
    {name:"loopOnTuesDay", label:"Thứ 3"},
    {name:"loopOnWednesday", label:"Thứ 4"},
    {name:"loopOnThursday", label:"Thứ 5"},
    {name:"loopOnFriday", label:"Thứ 6"},
    {name:"loopOnSaturday", label:"Thứ 7"},
    {name:"loopOnSunday", label:"Chủ nhật"},
  ];

  useEffect (() => {
    // handleSetDefaultSearchObject ()
    return resetUsingStaffSection;
  }, []);

  return (
      <GlobitsPopupV2
          open={openAssignForm}
          title={t ("navigation.timeSheet.staffWorkSchedule")}
          onClosePopup={handleClose}
          noDialogContent
          scroll={"body"}
          size='md'
      >
        <Formik
            validationSchema={validationSchema}
            enableReinitialize
            initialValues={initialShiftAssignmentForm}
            onSubmit={handleFormSubmit}
            >
          {({isSubmitting, values, setFieldValue}) => (
              <Form autoComplete='off'>
                <UseCheckLeaveRequestErrorComponent/>
                <DialogContent className='o-hidden dialog-body p-12'>
                  <Grid container spacing={2}>
                    <Grid item xs={12}>
                      <TabAccordion className='pb-0 mb-2' title='Đối tượng phân ca'>
                        <Grid item xs={12}>
                          <SWSDivisionFilter/>
                        </Grid>
                      </TabAccordion>
                    </Grid>

                    <Grid item xs={12}>
                      <TabAccordion className='pb-0 mb-0' title='Ca làm việc'>
                        <SWSChooseShiftWorkSection/>
                      </TabAccordion>
                    </Grid>

                    <Grid item xs={12}>
                      <TabAccordion className='pb-0 mb-2' title='Thời gian áp dụng'>
                        <Grid container spacing={2}>
                          <Grid item xs={12} sm={6} md={4}>
                            <Grid container spacing={2}>
                              <Grid item xs={12}>
                                <GlobitsDateTimePicker
                                    label='Ngày bắt đầu phân ca'
                                    name='fromDate'
                                    required
                                />
                              </Grid>
                              <Grid item xs={12}>
                                <GlobitsDateTimePicker
                                    label='Ngày kết thúc phân ca'
                                    name='toDate'
                                    required
                                />
                              </Grid>
                            </Grid>
                          </Grid>

                          <Grid item xs={12} sm={6} md={8}>
                            {/* Thêm marginLeft cho label để căn chỉnh với Paper */}
                            <Grid item xs={12} style={{marginLeft:"16px"}}>
                              <p style={{fontSize:"14px", fontWeight:"bold", margin:0}}>
                                Ngày làm việc:
                              </p>
                            </Grid>
                            <Paper
                                variant='outlined'
                                style={{
                                  backgroundColor:"#f5f5f5", // Nền xám
                                  // padding: '16px',
                                  // marginLeft: '16px'
                                }}>
                              <Grid container spacing={2}>
                                {weekDaysLoopList?.map ((weekDay, index) => {
                                  const {name, label} = weekDay;
                                  return (
                                      <Grid item xs={4} md={3} key={index}>
                                        <GlobitsCheckBox label={label} name={name}/>
                                      </Grid>
                                  );
                                })}
                              </Grid>
                            </Paper>
                          </Grid>
                        </Grid>
                      </TabAccordion>
                    </Grid>

                    <Grid item xs={12}>
                      <TabAccordion className='pb-0 mb-2' title='Nhân viên được phân ca'>
                        <Grid item xs={12} sm={12} md={12}>
                          <SelectMultipleStaffsComponent
                              name="staffs"
                              searchObject={{
                                department:values?.department,
                                organization:values?.organization,
                                positionTitle:values?.positionTitle,
                              }}
                              isDisableFilter={false}
                              isResetStore={false}
                              setListSelectedStaffs={setListSelectedStaffs}
                              listSelectedStaffs={listSelectedStaffs}
                              required
                          />
                        </Grid>
                      </TabAccordion>
                    </Grid>
                  </Grid>
                </DialogContent>

                <DialogActions className='dialog-footer flex flex-end flex-middle px-12'>
                  <Button
                      startIcon={<BlockIcon/>}
                      className='btn btn-secondary d-inline-flex'
                      onClick={handleClose}
                      disabled={isSubmitting}
                  >
                    {t ("general.button.cancel")}
                  </Button>

                  <Button
                      startIcon={<PieChartOutlinedIcon/>}
                      className='ml-12 btn btn-primary d-inline-flex'
                      type='submit'
                      disabled={isSubmitting}>
                    Phân ca
                  </Button>
                </DialogActions>
              </Form>
          )}
        </Formik>
      </GlobitsPopupV2>
  );
}

export default memo (observer (StaffWorkScheduleAssignForm));

function UseCheckLeaveRequestErrorComponent () {
  const {values} = useFormikContext ();
  const {isExistLeaveRequestInPeriod} = useStore ().staffWorkScheduleStore;

  useEffect (() => {
    const leaveRequest = {
      staffIds:values.staffs.map (value => value.id),
      fromDate:values.fromDate,
      toDate:values.toDate,
    };
    isExistLeaveRequestInPeriod (leaveRequest);
  }, [values.staffs, values.fromDate, values.toDate]);
  return null; // hoặc JSX nếu cần hiển thị gì đó

}
