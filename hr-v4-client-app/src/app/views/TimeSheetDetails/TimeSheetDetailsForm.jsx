import React, { useState } from "react";
import { useTranslation } from "react-i18next";
import { useStore } from "../../stores";
import { observer } from "mobx-react";
import { toast } from "react-toastify";
import { Formik, Form, } from "formik";
import * as Yup from "yup";
import { Grid, DialogActions, Button, DialogContent } from "@material-ui/core";
import SaveIcon from "@material-ui/icons/Save";
import BlockIcon from "@material-ui/icons/Block";
import ListIcon from "@material-ui/icons/List";
import { makeStyles } from "@material-ui/core/styles";
import { pagingWorkingStatus } from "../WorkingStatus/WorkingStatusService";
import { pagingProject, } from "../Project/ProjectService";
import GlobitsTextField from "../../common/form/GlobitsTextField";
import GlobitsSelectInput from "../../common/form/GlobitsSelectInput";
import GlobitsTimePicker from "../../common/form/GlobitsTimePicker";
import FormikFocusError from "../../common/FormikFocusError";
import LocalConstants from "../../LocalConstants";
import { formatDate, transformDate } from "app/LocalFunction";
import GlobitsPopup from "app/common/GlobitsPopup";
import { pagingTimeSheet } from "../TimeSheet/TimeSheetService";
import ProjectActivity from "../Task/ProjectActivity/ProjectActivityInTaskInfo";
import GlobitsPagingAutocomplete from "app/common/form/GlobitsPagingAutocomplete";
import moment from "moment";
import ChoosePAPopup from "./Popup/ChoosePAPopup";

const useStyles = makeStyles((theme) => ({
  backdrop: {
    zIndex: theme.zIndex.drawer + 1,
    scrollbarWidth: "none",
  },
  root: {
    "& .MuiDialogContent-root": {
      overflow: "auto !important",
    },

    "& .MuiFormGroup-root": {
      display: "flex",
      flexDirection: "row",
    },
  },
  fieldset: {
    borderRadius: "8px",
    border: "3px solid #01c0c8",
  },
  legend: {
    background: "#01c0c8",
    color: "#fff",
    borderRadius: "5px",
    padding: "5px 10px ",
    fontWeight: "500",
  },
}));

export default observer(function TimeSheetDetailsForm() {
  const { t } = useTranslation();
  const classes = useStyles();

  const { timeSheetDetailsStore } = useStore();

  const {
    handleClosePopup,
    handleSubmitFormTimeSheet,
    dataEditTimeSheet,
    openFormTimeSheet,
    currentStaff,
  } = timeSheetDetailsStore;

  const [isOpenListActivity, setIsOpenListActivity] = useState(false);
  const openPopupPA = (project) => {
    if (!project) {
      toast.warning("Bạn chưa chọn dự án");
    } else {
      setIsOpenListActivity(true);
    }
  };

  const validationSchema = Yup.object({
    // project: Yup.object().required(t("validation.required")).nullable(),
    // workingStatus: Yup.object().required(t("validation.required")).nullable(),
    employee: Yup.object().required('Cần có nhân viên!').nullable(),
    startTime: Yup.date().transform(transformDate).test('test', t("Thời gian kết thúc phải sau thời gian bắt đầu"), function () {
      const { startTime, endTime } = this.parent;
      if (startTime && endTime)
        if (moment(endTime).isBefore(moment(startTime))) {
          return false;
        }
      return true;
    }).nullable(),
    endTime: Yup.date().transform(function transformDate(castValue, originalValue) {         return originalValue ? new Date(originalValue) : castValue;       }).required('Thời gian kết thúc không được bỏ trống').nullable(),
  });


  //open and close project activity
  const [openChoosePA, setOpenChoosePA] = useState(false);

  return (
    <GlobitsPopup
      open={openFormTimeSheet}
      size='lg'
      title={`${t("timeSheet.title")} - ${currentStaff ? currentStaff.name : ''}`}
      onClosePopup={handleClosePopup}

    >
      <Formik
        initialValues={dataEditTimeSheet}
        validationSchema={validationSchema}
        enableReinitialize
        onSubmit={(values, actions) => {
          handleSubmitFormTimeSheet(values);
          actions.setSubmitting(false);
        }}
      >
        {({ isSubmitting, values, setFieldValue, setValues }) => {
          return (
            <Form autoComplete="off">
              <FormikFocusError />
              <div className={`dialog-body ${classes.root}`}>
                <DialogContent style={{ overflowY: "auto" }}>
                  <Grid container spacing={2}>
                    {/* <Grid item sm={6} xs={12}>
                      <GlobitsPagingAutocomplete
                        label={t("timeSheet.project")}
                        name="project"
                        requiredLabel
                        api={pagingProject}
                        handleChange={(value) => setValues({ ...values, project: value, projectActivity: null })}
                      />
                    </Grid> */}
                    <Grid item sm={6} xs={12}>
                      <GlobitsPagingAutocomplete
                        disabled={!values.employee}
                        label={t("timeSheet.workingDate")}
                        requiredLabel
                        name="timeSheet"
                        searchObject={{ staffId: values.employee?.id }}
                        getOptionLabel={(option) => ` ${formatDate('HH:mm', option.startTime)} - ${formatDate('HH:mm', option.endTime)} (${formatDate('DD/MM/YYYY', option.workingDate)})`}
                        onChange={(_, value) =>
                          setValues({ ...values, timeSheet: value, endTime: value?.endTime, startTime: value?.startTime, })
                        }
                        api={pagingTimeSheet}
                      />
                    </Grid>
                    <Grid item sm={6} xs={12}>
                      <GlobitsTimePicker
                        required
                        label={t("timeSheet.startTime")}
                        name="startTime"
                        format="HH:mm "
                      />
                    </Grid>
                    <Grid item sm={6} xs={12}>
                      <GlobitsTimePicker
                        required
                        label={t("timeSheet.endTime")}
                        name="endTime"
                        format="HH:mm "
                      />
                    </Grid>
                    {/* <Grid item spacing={1} container xs={12}>
                      <Grid item lg={10} md={9} sm={12} xs={12}>
                        <GlobitsTextField
                          disabled={true}
                          label={t("timeSheet.activityName")}
                          name="projectActivity.name"
                          value={values?.projectActivity ? values?.projectActivity?.name : ""}
                        />
                      </Grid>
                      <Grid item lg={2} md={3} sm={6} xs={12}>
                        <Button
                          className="btn btn-primary d-inline-flex mb-10 w-100 mt-22"
                          // onClick={() => openPopupPA(values.project)}
                          onClick={function () {
                            if (!values || !values?.project || !values?.project?.id) {
                              toast.info("Chưa chọn dự án để chọn hoạt động");
                              return;
                            }

                            setOpenChoosePA(true);
                          }}
                        >
                          <ListIcon />
                          {t("timeSheet.chooseActivity")}
                        </Button> */}

                        {/* choose activity without paging */}
                        {/* {isOpenListActivity && (
                          <ProjectActivity
                            open={isOpenListActivity}
                            setIsOpenListActivity={setIsOpenListActivity}
                            name='projectActivity'
                          />
                        )} */}

                        {/* choose project's activity with paging and search */}
                        {/* {
                          openChoosePA && (
                            <ChoosePAPopup
                              open={openChoosePA}
                              handleClose={function () {
                                setOpenChoosePA(false);
                              }}
                            />
                          )
                        }

                      </Grid>
                    </Grid> */}

                    {/* <Grid item sm={12} xs={12}>
                      <GlobitsSelectInput
                        label={t("timeSheet.priority")}
                        name="priority"
                        keyValue="id"
                        options={LocalConstants.Priority}
                      />
                    </Grid>
                    <Grid item sm={6} xs={12}>
                      <GlobitsPagingAutocomplete
                        label={t("timeSheet.workingStatus")}
                        name="workingStatus"
                        api={pagingWorkingStatus}
                      />
                    </Grid> */}

                    <Grid item sm={6} xs={12}>
                      <GlobitsSelectInput
                        label={t("timeSheet.approveStatus")}
                        name="approveStatus"
                        keyValue="id"
                        hideNullOption={true}
                        options={LocalConstants.CandidateApprovalStatus?.getListData()}
                      />
                    </Grid>
                    <Grid item xs={12}>
                      <GlobitsTextField
                        label={t("timeSheet.description")}
                        name="description"
                        multiline
                        rows={6}
                      />
                    </Grid>
                  </Grid>
                </DialogContent>
              </div>
              <div className="dialog-footer">
                <DialogActions className="p-0">
                  <div className="flex flex-space-between flex-middle">
                    <Button
                      startIcon={<BlockIcon />}
                      variant="contained"
                      className="mr-12 btn btn-secondary d-inline-flex"
                      color="secondary"
                      onClick={() => handleClosePopup()}
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
    </GlobitsPopup>
  );
});