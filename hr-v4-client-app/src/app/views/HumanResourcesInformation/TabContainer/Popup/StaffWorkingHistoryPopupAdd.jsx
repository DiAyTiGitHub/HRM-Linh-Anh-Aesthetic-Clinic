import { Button, DialogActions, DialogContent, Grid } from "@material-ui/core";
import BlockIcon from "@material-ui/icons/Block";
import LoopIcon from "@material-ui/icons/Loop";
import GlobitsCheckBox from "app/common/form/GlobitsCheckBox";
import GlobitsDateTimePicker from "app/common/form/GlobitsDateTimePicker";
import GlobitsPagingAutocompleteV2 from "app/common/form/GlobitsPagingAutocompleteV2";
import GlobitsTextField from "app/common/form/GlobitsTextField";
import GlobitsPopupV2 from "app/common/GlobitsPopupV2";
import { useStore } from "app/stores";
import { pagingAllDepartments } from "app/views/Department/DepartmentService";
import { pagingAllOrg } from "app/views/Organization/OrganizationService";
import { pagingPosition } from "app/views/Position/PositionService";
import { Form, Formik, useFormikContext } from "formik";
import moment from "moment";
import { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import { useParams } from "react-router-dom/cjs/react-router-dom.min";
import * as Yup from "yup";
import localStorageService from "../../../../services/localStorageService";

export default function StaffWorkingHistoryPopupAdd (props) {
  const {t} = useTranslation ();
  const {staffWorkingHistoryStore, positionStore} = useStore ();

  const {id:staffId} = useParams ();

  const {
    openCreateEditPopup,
    handleClose,
    saveStaffWorkingHistory,
    selectedStaffWorkingHistory,
    pagingStaffWorkingHistory,
    typeOfForm,
  } = staffWorkingHistoryStore;
  const {fetchPositions} = positionStore;

  const initialItem = {
    startDate:new Date (),
    endDate:null,
    fromOrganization:null,
    toOrganization:null,
    fromDepartment:null,
    toDepartment:null,
    fromPosition:null,
    toPosition:null,
    transferType:null,
    vacantOnly:true,
    note:"",
  };

  const validationSchema = Yup.object ({
    startDate:Yup.date ()
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
  });

  const [formValues, setFormValues] = useState (initialItem);
  const {values} = useFormikContext ()

  const handleSubmit = async (values) => {
    console.log (values);
    let dto = {
      ... values,
      staff:{
        id:staffId,
      },
    };

    if (typeOfForm === 3) {
      dto.transferType = 3;
    }
    await saveStaffWorkingHistory (dto);
    await fetchPositions (staffId);
  };

  useEffect (() => {
    if (selectedStaffWorkingHistory) {
      setFormValues ({... selectedStaffWorkingHistory, vacantOnly:true});
    } else {
      setFormValues ({... initialItem});
    }
    if (values?.department) {
      setFormValues ((prev) => ({
        ... prev,
        fromDepartment:values?.department,
      }));
    }
    if (values?.organization) {
      setFormValues ((prev) => ({
        ... prev,
        fromOrganization:values?.organization,
        toOrganization:values?.organization,
      }));
    }

    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [selectedStaffWorkingHistory, selectedStaffWorkingHistory?.id]);
  return (
      <GlobitsPopupV2
          size={typeOfForm !== 3? "md" : "sm"}
          scroll={"body"}
          open={openCreateEditPopup}
          onClosePopup={handleClose}
          title={
              (selectedStaffWorkingHistory?.id? t ("general.button.edit") : t ("general.button.add")) +
              " " +
              (typeOfForm !== 3? t ("Điều chuyển") : "Tạm nghỉ")
          }
          noDialogContent>
        <Formik
            initialValues={{
              startDate:new Date (),
              fromOrganization:null,
              fromDepartment:null,
              fromPosition:null,
              toOrganization:null,
              toDepartment:null,
              toPosition:null,
              ... formValues,
            }}
            onSubmit={(values) => handleSubmit (values)}
            validationSchema={validationSchema}>
          {({isSubmitting, setFieldValue, values}) => (
              <Form autoComplete='off'>
                <DialogContent
                    className='dialog-body p-12'
                    // style={{ maxHeight: "80vh", minWidth: "300px" }}
                >
                  <Grid container spacing={2}>
                    {typeOfForm !== 3 && (
                        <Grid item xs={12}>
                          <Grid container spacing={2}>
                            <Grid item xs={6}>
                              <Grid container spacing={2}>
                                <Grid item xs={12}>
                                  <GlobitsPagingAutocompleteV2
                                      label={"Đơn vị hiện tại"}
                                      name='fromOrganization'
                                      api={pagingAllOrg}
                                      getOptionLabel={(option) => option?.name || ""}
                                      disabled
                                  />
                                </Grid>
                                <Grid item xs={12}>
                                  <GlobitsPagingAutocompleteV2
                                      label={"Phòng ban hiện tại"}
                                      name='fromDepartment'
                                      api={pagingAllDepartments}
                                      disabled
                                      getOptionLabel={(option) => {
                                        return option?.code
                                            ? `${option?.name} - ${option?.code}`
                                            : option?.name;
                                      }}
                                  />
                                </Grid>
                                <Grid item xs={12}>
                                  <GlobitsPagingAutocompleteV2
                                      label={t ("staffWorkingHistory.fromPosition")}
                                      name='fromPosition'
                                      api={pagingPosition}
                                      searchObject={{
                                        staffId:staffId,
                                      }}
                                      handleChange={(_, value) => {
                                        setFieldValue ("fromPosition", value);
                                        setFieldValue ("staff", value?.staff);
                                        if (value?.department) {
                                          setFieldValue ("toDepartment", value?.department);
                                          setFieldValue ("fromDepartment", value?.department);
                                          setFieldValue ("transferType", 1);
                                          if (value?.department?.organization) {
                                            setFieldValue (
                                                "fromOrganization",
                                                value?.department?.organization
                                            );
                                            setFieldValue (
                                                "toOrganization",
                                                value?.department?.organization
                                            );
                                          }
                                        } else {
                                          setFieldValue ("fromOrganization", null);
                                          setFieldValue ("fromDepartment", null);
                                          setFieldValue ("toOrganization", null);
                                          setFieldValue ("toDepartment", null);
                                          setFieldValue ("toPosition", null);
                                        }
                                      }}
                                      readOnly={values?.id}
                                  />
                                </Grid>
                              </Grid>
                            </Grid>
                            <Grid item xs={6}>
                              <Grid container spacing={2}>
                                <Grid item xs={12}>
                                  <GlobitsPagingAutocompleteV2
                                      label={"Đơn vị đích"}
                                      name='toOrganization'
                                      api={pagingAllOrg}
                                      getOptionLabel={(option) => option?.name || ""}
                                      searchObject={{pageSize:10, pageIndex:1}}
                                      value={values.toOrganization} // Thêm value để hiển thị
                                      handleChange={(e, newValue) => {
                                        setFieldValue ("toOrganization", newValue);
                                        setFieldValue ("toDepartment", null);
                                        setFieldValue ("toPosition", null);
                                      }}
                                      readOnly={values?.id}
                                  />
                                </Grid>
                                <Grid item xs={12}>
                                  <GlobitsPagingAutocompleteV2
                                      label={"Phòng ban đích"}
                                      name='toDepartment'
                                      api={pagingAllDepartments}
                                      searchObject={{
                                        pageSize:10,
                                        pageIndex:1,
                                        organizationId:values?.toOrganization?.id,
                                      }}
                                      value={values.toDepartment} // Thêm value để hiển thị
                                      handleChange={(e, newValue) => {
                                        setFieldValue ("toDepartment", newValue);
                                        setFieldValue ("toPosition", null);
                                        if (newValue?.organization) {
                                          setFieldValue (
                                              "toOrganization",
                                              newValue?.organization
                                          );
                                        }
                                      }}
                                      // allowLoadOptions={!!values?.toOrganization?.id}
                                      clearOptionOnClose
                                      getOptionLabel={(option) => {
                                        return option?.code
                                            ? `${option?.name} - ${option?.code}`
                                            : option?.name;
                                      }}
                                      readOnly={values?.id}
                                  />
                                </Grid>
                                <Grid item xs={12}>
                                  <GlobitsPagingAutocompleteV2
                                      label={"Vị trí đích"}
                                      name='toPosition'
                                      api={pagingPosition}
                                      searchObject={{
                                        pageSize:10,
                                        pageIndex:1,
                                        departmentId:values?.toDepartment?.id,
                                        vacant:values?.vacantOnly,
                                      }}
                                      value={values.toPosition} // Thêm value để hiển thị
                                      handleChange={(e, newValue) => {
                                        setFieldValue ("toPosition", newValue);
                                        if (newValue?.department) {
                                          setFieldValue ("toDepartment", newValue?.department);
                                          if (newValue?.department?.organization) {
                                            setFieldValue (
                                                "toOrganization",
                                                newValue?.department?.organization
                                            );
                                          }
                                        }
                                      }}
                                      getOptionLabel={(option) => {
                                        return option?.code
                                            ? `${option?.name} - ${option?.code}`
                                            : option?.name || "";
                                      }}
                                      readOnly={values?.id}
                                      // allowLoadOptions={!!values?.toDepartment?.id}
                                  />
                                </Grid>
                                {!values?.id && (
                                    <Grid item xs={12}>
                                      <GlobitsCheckBox
                                          name='vacantOnly'
                                          label='Chỉ hiển thị vị trí trống'
                                      />
                                    </Grid>
                                )}
                              </Grid>
                            </Grid>
                          </Grid>
                        </Grid>
                    )}
                    <Grid item xs={6}>
                      <GlobitsDateTimePicker
                          required
                          label={typeOfForm != 3? t ("staffWorkingHistory.startDate") : "Ngày nghỉ việc"}
                          name='startDate'
                          value={values?.startDate}
                      />
                    </Grid>

                    <Grid item xs={6}>
                      <GlobitsDateTimePicker
                          label={typeOfForm != 3? t ("staffWorkingHistory.endDate") : "Ngày làm lại"}
                          name='endDate'
                      />
                    </Grid>
                    <Grid item xs={12}>
                      <GlobitsTextField
                          label={typeOfForm != 3? t ("staffWorkingHistory.note") : "Lý do nghỉ việc"}
                          name='note'
                          multiline
                          rows={4}
                      />
                    </Grid>
                  </Grid>
                </DialogContent>

                <div className='dialog-footer dialog-footer-v2 py-8'>
                  <DialogActions className='p-0'>
                    <div className='flex flex-space-between flex-middle'>
                      <Button
                          startIcon={<BlockIcon/>}
                          variant='contained'
                          className='mr-12 btn btn-secondary d-inline-flex'
                          color='secondary'
                          onClick={handleClose}>
                        {t ("general.button.cancel")}
                      </Button>
                      <Button
                          startIcon={<LoopIcon/>}
                          className='mr-0 btn btn-primary d-inline-flex'
                          variant='contained'
                          color='primary'
                          type='submit'>
                        {typeOfForm != 3? "Điều chuyển" : "Thêm"}
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
