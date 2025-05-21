import { Icon, IconButton } from "@material-ui/core";
import React, { useState } from "react";
import { Form, Formik } from "formik";
import { Button, Grid } from "@material-ui/core";
import { useTranslation } from "react-i18next";
import SaveIcon from "@material-ui/icons/Save";
import DeleteIcon from "@material-ui/icons/Delete";
import BlockIcon from "@material-ui/icons/Block";
import GlobitsTextField from "app/common/form/GlobitsTextField";
import CreditCardIcon from "@material-ui/icons/CreditCard";
import SubjectIcon from "@material-ui/icons/Subject";
import MessageIcon from "@material-ui/icons/Message";
import LocalOfferIcon from '@material-ui/icons/LocalOffer';
import AddIcon from "@material-ui/icons/Add";
import { useStore } from "../../stores";
import ProjectActivity from "./ProjectActivity/ProjectActivityInTaskInfo";
import { observer } from "mobx-react";
import * as Yup from "yup";
import "./_task.scss";
import StaffPopup from "./TaskPopup/StaffPopup";
import DatePopup from "./TaskPopup/DatePopup";
import moment from "moment";
import GlobitsPopup from "app/common/GlobitsPopup";
import ProjectPopupTask from "./TaskPopup/ProjectPopupTask";
import SubTaskPopup from "./TaskPopup/SubTaskPopup";
import SubTask from "./SubTask";
import PersonIcon from "@material-ui/icons/Person";
import StatusPopupTask from "./TaskPopup/StatusPopupTask";
import PriorityPopup from "./TaskPopup/PriorityPopup";
import LayersIcon from "@material-ui/icons/Layers";
import AccessTimeIcon from "@material-ui/icons/AccessTime";
import GlobitsConfirmationDialog from "app/common/GlobitsConfirmationDialog";
import FormikFocusError from "app/common/FormikFocusError";
import { transformDate } from "app/LocalFunction";

export default observer(function TaskNewForm() {
  const { t } = useTranslation();
  const { taskStore } = useStore();

  const { dataTaskForm,
    openConfirmDeleteTask,
    openNewPopup,
    handleClose,
    handleSubmitFormTask,
    handleConfirmDeleteTask,
    handleClosePopupConfirmDelete,
    handleDeleteTask,
  } = taskStore;

  const [openListActivity, setOpenListActivity] = useState(false);

  const validationSchema = Yup.object({
    name: Yup.string().required(t("validation.required")).nullable(),
    startTime: Yup.date().transform(transformDate).typeError('Chưa đúng định dạng!').test('test', t("Thời gian kết thúc phải sau thời gian bắt đầu"), function () {
      const { startTime, endTime } = this.parent;
      if (startTime && endTime)
        if (moment(endTime).isBefore(moment(startTime))) {
          return false;
        }
      return true;
    }).nullable(),
    status: Yup.object().required(t("validation.required")).nullable(),
  });


  return (
    <GlobitsPopup
      open={openNewPopup}
      onClosePopup={handleClose}
      styleTitle={{ borderBottom: 'none', backgroundColor: "#091e4214" }}
      styleContent={{ backgroundColor: "#f4f5f7", paddingBottom: "24px" }}
      noHeader
    >
      <Formik
        enableReinitialize
        initialValues={dataTaskForm}
        onSubmit={handleSubmitFormTask}
        validationSchema={validationSchema}
      >
        {({ values, setFieldValue }) => (
          <Form style={{ backgroundColor: "#f4f5f7" }}>
            <FormikFocusError />
            <div style={{ width: "95%" }} >
              <div className="NameField">
                <div style={{ display: "contents" }}>
                  <CreditCardIcon />
                </div>
                <div style={{ width: "100%" }}>
                  <GlobitsTextField name="name" placeholder={t("task.name")} />
                </div>
              </div>
              <div className="d-flex align-middle">
                <ProjectPopupTask />
                <p className="project-popup-task">
                  Hoạt động
                  <button type="button" onClick={() => setOpenListActivity(true)}>
                    {values.activity ? values.activity.name : 'Chưa chọn hoạt động'}
                  </button>
                </p>
              </div>
            </div>
            <IconButton
              style={{ position: "absolute", right: "10px", top: "10px" }}
              onClick={() => handleClose()}
            >
              <Icon color="disabled" title={t("general.close")}>
                close
              </Icon>
            </IconButton>
            <Grid container spacing={2}>
              <Grid item md={9} sm={9} xs={9}>
                <div style={{ margin: "10px 0px 10px 0px", display: "flex" }}>
                  <PersonIcon />
                  <div style={{ paddingLeft: "10px" }}>
                    <div style={{ color: "#5e6c84" }}>{t("task.members")}</div>
                    <div className="TextField">
                      <StaffPopup textButton={<AddIcon fontSize="20px" />} />
                      {values.staffs.map((item, index) => (
                        <div key={index} className="Member" style={{ marginLeft: "10px", alignItems: "center", display: "flex" }}>
                          {item.displayName}
                          <div style={{ paddingTop: "5px" }}>
                            <Icon color="disabled" title={"Xóa"} fontSize="small" style={{ cursor: "pointer" }} onClick={() => setFieldValue('staffs', values.staffs.filter(e => e.id !== item.id))} >
                              close
                            </Icon>
                          </div>
                        </div>
                      ))}
                    </div>
                  </div>
                </div>
                {(values?.startTime || values?.endTime) &&
                  <div style={{ margin: "10px 0px 10px 0px", display: "flex" }}>
                    <AccessTimeIcon />
                    <div style={{ paddingLeft: "10px" }}>
                      <div style={{ color: "#5e6c84" }} >{(values?.startTime && !values?.endTime) ? t("task.time.startTime") : (!values?.startTime && values?.endTime) ? t("task.time.endTime") : t("task.time.title")}</div>
                      <div className="Member" style={{ margin: "10px 0px" }}>
                        {values?.startTime && moment(values?.startTime).format("DD/MM/yyyy - HH:mm")}
                        {(values?.startTime && values?.endTime) && " đến "}
                        {values?.endTime && moment(values?.endTime).format("DD/MM/yyyy - HH:mm ")}
                        {(values?.startTime && values?.endTime && values?.estimateHour) && " (" + values?.estimateHour + " giờ) "}
                      </div>
                    </div>
                  </div>}
                <div className="TextField" style={{ alignItems: "unset" }}>
                  <SubjectIcon />
                  <div className="w-100 ml-10">
                    <GlobitsTextField
                      label={t("timeSheet.description")}
                      name="description"
                      multiline
                      rows={3}
                    />
                  </div>
                </div>
                {(values.subTasks) && <div className="SubField" style={{ alignItems: "unset" }}>
                  <LocalOfferIcon />
                  <div className="w-100 ml-10">
                    <SubTask />
                  </div>
                </div>}
                <div className="TextField" style={{ alignItems: "unset" }}>
                  <MessageIcon />
                  <div className="w-100 ml-10">
                    <GlobitsTextField
                      label={t("task.comment")}
                      name="comment"
                    />
                  </div>
                </div>
              </Grid>
              <Grid item md={3} sm={3} xs={3}>
                <div className="listButton">
                  <StaffPopup startIcon={<PersonIcon />} textButton={t("task.members")} />
                  <StatusPopupTask />
                  <Button variant="contained" startIcon={<LayersIcon />} onClick={() => setOpenListActivity(true)} >
                    {t("task.acitivity")}
                  </Button>
                  <PriorityPopup />
                  <SubTaskPopup />
                  <DatePopup titleButton={t("task.time.title")} />
                </div>
                <div style={{ color: "#5e6c84" }}>{t("task.action")}</div>
                <div className="listButton">
                  <Button variant="contained" startIcon={<SaveIcon />} type="submit">
                    {t("general.button.save")}
                  </Button>
                  {values.id && (
                    <Button startIcon={<DeleteIcon />} variant="contained" onClick={() => handleDeleteTask(values?.id)}>
                      {t("general.button.delete")}
                    </Button>
                  )}
                  <Button startIcon={<BlockIcon />} variant="contained" onClick={() => handleClose()}>
                    {t("general.button.cancel")}
                  </Button>
                </div>
              </Grid>
            </Grid>
            <ProjectActivity
              open={openListActivity}
              handleClose={() => setOpenListActivity(false)}
              setIsOpenListActivity={setOpenListActivity}
            />
          </Form>
        )}
      </Formik>
      <GlobitsConfirmationDialog
        open={openConfirmDeleteTask}
        onConfirmDialogClose={handleClosePopupConfirmDelete}
        onYesClick={handleConfirmDeleteTask}
        title={t("confirm_dialog.delete.title")}
        text={t("confirm_dialog.delete.text")}
        agree={t("confirm_dialog.delete.agree")}
        cancel={t("confirm_dialog.delete.cancel")}
      />
    </GlobitsPopup>
  );
})