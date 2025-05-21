import { Dialog, DialogTitle, Icon, IconButton } from "@material-ui/core";
import Draggable from "react-draggable";
import React, { useState } from "react";
import Paper from "@material-ui/core/Paper";
import { Form, Formik } from "formik";
import { makeStyles } from "@material-ui/core/styles";
import GlobitsPagingAutocomplete from "app/common/form/GlobitsPagingAutocomplete";
import GlobitsDateTimePicker from "app/common/form/GlobitsDateTimePicker";
import { DialogActions, Button, DialogContent, Grid } from "@material-ui/core";
import { useTranslation } from "react-i18next";
import SaveIcon from "@material-ui/icons/Save";
import DeleteIcon from "@material-ui/icons/Delete";
import BlockIcon from "@material-ui/icons/Block";
import { pagingProject } from "../Project/ProjectService";
import { pagingStaff } from "../HumanResourcesInformation/StaffService";
import GlobitsTextField from "app/common/form/GlobitsTextField";
import ListIcon from "@material-ui/icons/List";
import { toast } from "react-toastify";
import { useStore } from "../../stores";
import ProjectActivity from "./ProjectActivity/ProjectActivityInTaskInfo";
import { observer } from "mobx-react";
import GlobitsSelectInput from "app/common/form/GlobitsSelectInput";
import LocalConstants from 'app/LocalConstants';
import * as Yup from "yup";

const useStyles = makeStyles((theme) => ({
  root: {
    "& .MuiDialog-paper": {
      overflowY: "hidden !important",
    },
  },
}));

function PaperComponent(props) {
  return (
    <Draggable
      handle="#draggable-dialog-title"
      cancel={'[class*="MuiDialogContent-root"]'}
    >
      <Paper {...props} />
    </Draggable>
  );
}

export default observer(function TaskForm() {
  const { t } = useTranslation();
  const classes = useStyles();
  const { taskStore } = useStore();
  const { dataTaskForm, openPopup, optionWorkingStatus, handleClose, handleSubmitFormTask, handleDeleteTask } = taskStore;

  const [openListActivity, setOpenListActivity] = useState(false);

  function openPopupListActivity(values) {
    if (!values.project) {
      toast.warning("Bạn chưa chọn dự án");
      return;
    }
    setOpenListActivity(true);
  }

  const validationSchema = Yup.object({
    name: Yup.string().required(t("Chưa có tên công việc")).nullable(),
  });


  return (
    <Dialog
      open={openPopup}
      fullWidth
      maxWidth="md"
      PaperComponent={PaperComponent}
      className={`dialog-container ${classes.root}`}
    >
      <DialogTitle
        className="dialog-header"
        style={{ cursor: "move" }}
        id="draggable-dialog-title"
      >
        <span className="mb-20 ">Thêm Task</span>
      </DialogTitle>
      <IconButton
        style={{ position: "absolute", right: "10px", top: "10px" }}
        onClick={() => handleClose()}
      >
        <Icon color="disabled" title={"Đóng"}>
          close
        </Icon>
      </IconButton>
      <Formik
        enableReinitialize
        initialValues={dataTaskForm}
        onSubmit={handleSubmitFormTask}
        validationSchema={validationSchema}
      >
        {({ values }) => (
          <Form>
            <div className={`dialog-body ${classes.root}`}>
              <DialogContent style={{ overflowY: "auto", maxHeight: "70vh" }}>
                <Grid container spacing={2}>
                  <Grid item md={6} sm={6} xs={12}>
                    <GlobitsPagingAutocomplete
                      name="project"
                      label={t("timeSheet.project")}
                      api={pagingProject}
                      requiredLabel
                      displayData="displayName"
                      multiple
                    />
                  </Grid>
                  <Grid item md={6} sm={6} xs={12}>
                    <GlobitsPagingAutocomplete
                      name="staffs"
                      label={t("timeSheet.employee")}
                      displayData="displayName"
                      multiple
                      api={pagingStaff}
                      displayName="displayName"
                      isMulti
                      searchObject={{ projectId: values?.project?.id }}
                    />
                  </Grid>
                  <Grid item md={12} sm={12} xs={12}>
                    <GlobitsTextField
                      label={t("timeSheet.nameTask")}
                      name="name"
                      validate={true}
                    />
                    <div className="input-popup-container">
                      <GlobitsTextField
                        label={t("timeSheet.activityName")}
                        name="activity.name"
                        disabled
                        value={values.activity ? values.activity.name : null}
                      />
                      <Button
                        variant="contained"
                        className="btn-primary"
                        style={{
                          marginTop: "25px",
                        }}
                        onClick={() => openPopupListActivity(values)}
                      >
                        <ListIcon />
                        {t("timeSheet.chooseActivity")}
                      </Button>
                    </div>
                  </Grid>
                  <Grid item md={6} sm={6} xs={12}>
                    <GlobitsSelectInput
                      label={t("timeSheet.workingStatus")}
                      name="status"
                      keyValue={'id'}
                      validate
                      options={optionWorkingStatus}
                    />
                  </Grid>
                  <Grid item md={6} sm={6} xs={12}>
                    <GlobitsSelectInput
                      label={t("timeSheet.priority")}
                      name="priority"
                      keyValue="id"
                      options={LocalConstants.Priority}
                    />
                  </Grid>
                  <Grid item md={6} sm={6} xs={12}>
                    <GlobitsDateTimePicker
                      name="startTime"
                      label={t("timeSheet.startTime")}
                      format="dd/MM/yyyy HH:mm"
                      isDateTimePicker={true}
                    />
                  </Grid>
                  <Grid item md={6} sm={6} xs={12}>
                    <GlobitsDateTimePicker
                      name="endTime"
                      label={t("timeSheet.endTime")}
                      format="dd/MM/yyyy HH:mm"
                      isDateTimePicker={true}
                    />
                  </Grid>
                  <Grid item md={12} sm={12} xs={12}>
                    <GlobitsTextField
                      label={t("timeSheet.estimateHour")}
                      name="estimateHour"
                      type={"number"}
                    />
                  </Grid>
                  <Grid item md={12} sm={12} xs={12}>
                    <GlobitsTextField
                      label={t("timeSheet.description")}
                      name="description"
                      multiline
                      rows={3}
                    />
                  </Grid>
                </Grid>
              </DialogContent>
            </div>
            <ProjectActivity
              open={openListActivity}
              handleClose={() => setOpenListActivity(false)}
              setIsOpenListActivity={setOpenListActivity}
            />
            <div className="dialog-footer">
              <DialogActions className="p-0">
                <div className="flex flex-space-between flex-middle">
                  <Button
                    startIcon={<BlockIcon />}
                    variant="contained"
                    className="mr-12 btn btn-secondary d-inline-flex"
                    color="secondary"
                    onClick={() => handleClose()}
                  >
                    {t("general.button.cancel")}
                  </Button>
                  <Button
                    startIcon={<DeleteIcon />}
                    variant="contained"
                    className="mr-12 btn btn-secondary d-inline-flex"
                    color="secondary"
                    onClick={() => handleDeleteTask(values?.id)}
                  >
                    {t("general.button.delete")}
                  </Button>
                  <Button
                    startIcon={<SaveIcon />}
                    className="mr-0 btn btn-primary d-inline-flex"
                    variant="contained"
                    color="primary"
                    type="submit"
                  >
                    {t("general.button.save")}
                  </Button>
                </div>
              </DialogActions>
            </div>
          </Form>
        )}
      </Formik>
    </Dialog>
  );
})