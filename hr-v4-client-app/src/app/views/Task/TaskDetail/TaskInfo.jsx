import React, {memo, useMemo, useState} from "react";
import {Form, Formik} from "formik";
import {Button, Grid, DialogContent, Tooltip, Collapse} from "@material-ui/core";
import {useTranslation} from "react-i18next";
import SaveIcon from "@material-ui/icons/Save";
import DeleteIcon from "@material-ui/icons/Delete";
import BlockIcon from "@material-ui/icons/Block";
import GlobitsTextField from "app/common/form/GlobitsTextField";
import CreditCardIcon from "@material-ui/icons/CreditCard";
import SubjectIcon from "@material-ui/icons/Subject";
import {useStore} from "../../../stores";
import {observer} from "mobx-react";
import * as Yup from "yup";
import DatePopup from "../TaskPopup/DatePopup";
import moment from "moment";
import ProjectPopupTask from "../TaskPopup/ProjectPopupTask";
import SubTaskPopup from "../TaskPopup/SubTaskPopup";
import PersonIcon from "@material-ui/icons/Person";
import StatusPopupTask from "../TaskPopup/StatusPopupTask";
import PriorityPopup from "../TaskPopup/PriorityPopup";
import LayersIcon from "@material-ui/icons/Layers";
import AccessTimeIcon from "@material-ui/icons/AccessTime";
import GlobitsConfirmationDialog from "app/common/GlobitsConfirmationDialog";
import FormikFocusError from "app/common/FormikFocusError";
import {transformDate} from "app/LocalFunction";
import ChooseAssigneePopup from "../TaskPopup/ChooseAssigneePopup";
import GlobitsEditor from "app/common/form/GlobitsEditor";
import ChooseActivityPopup from "./ChooseActivityPopup";
import SubTaskContainer from "./SubTaskV2/SubTaskContainer";
import LocalOfferIcon from "@material-ui/icons/LocalOffer";
import TaskHistoryIndex from "./TaskHistory/TaskHistoryIndex";
import IntegratedTaskHistory from "./TaskHistory/IntegratedTaskHistory";
import TimelineIcon from '@material-ui/icons/Timeline';
import HistoryAndCommentSection from "./TaskHistory/HistoryAndCommentSection";
import ReplayIcon from '@material-ui/icons/Replay';
import TaskInfoClipboardCopying from "./TaskInfoClipboardCopying";
import TitleIcon from '@material-ui/icons/Title';

function TaskInfo() {
    const {t} = useTranslation();
    const {taskStore} = useStore();

    const {
        dataTaskForm,
        openConfirmDeleteTask,
        handleClose,
        handleSubmitFormTask,
        handleConfirmDeleteTask,
        handleClosePopupConfirmDelete,
        handleDeleteTask,
    } = taskStore;

    const validationSchema = Yup.object({
        name: Yup.string().required(t("validation.required")).nullable(),
        startTime: Yup.date()
            .transform(transformDate)
            .typeError("Chưa đúng định dạng!")
            .test(
                "test",
                t("Thời gian kết thúc phải sau thời gian bắt đầu"),
                function () {
                    const {startTime, endTime} = this.parent;
                    return !(startTime && endTime && moment(endTime).isBefore(moment(startTime)));
                }
            )
            .nullable(),
        subTasks: Yup.array(
            Yup.object({
                name: Yup.string().required("Tên công việc con là bắt buộc").nullable(),
                items: Yup.array(
                    Yup.object({
                        startTime: Yup.date()
                            .transform(transformDate)
                            .typeError("Chưa đúng định dạng!")
                            .test(
                                "test",
                                t("Thời gian bắt đầu và kết thúc của công việc con cần làm không hợp lệ, vui lòng kiểm tra lại"),
                                function () {
                                    const {startTime, endTime} = this.parent;
                                    return !(startTime && endTime && moment(endTime).isBefore(moment(startTime)));
                                }
                            )
                            .nullable(),
                        endTime: Yup.date()
                            .transform(transformDate)
                            .typeError("Chưa đúng định dạng!")
                            .nullable(),
                        name: Yup.string().required(t("validation.required")).nullable(),
                    }).nullable(),
                ).nullable(),
            }).nullable()
        ).nullable(),
        status: Yup.object()
            .required("Chưa chọn trạng thái thực hiện")
            .nullable(),
    });


    return (
        <>
            <Formik
                enableReinitialize
                initialValues={JSON.parse(JSON.stringify(dataTaskForm))}
                onSubmit={handleSubmitFormTask}
                validationSchema={validationSchema}
            >
                {({values, setFieldValue, isSubmitting, resetForm, setValues}) => {

                    function handleRenderTaskCode() {
                        if (!values?.project?.name) return "";

                        if (values?.id && values?.code) {
                            if (values?.project?.id == dataTaskForm?.project?.id) return values?.code;
                        }

                        return "NEW";
                    }

                    const onRenderTaskCode = handleRenderTaskCode();

                    const isChanged = JSON.stringify(dataTaskForm) != JSON.stringify(values);

                    return (
                        <Form autoComplete="off" style={{backgroundColor: "#f4f5f7"}}>
                            <DialogContent className="dialog-body pb-18 pt-12 px-12">
                                <FormikFocusError/>

                                <Grid container spacing={2}>
                                    <Grid item xs={12} sm={8} md={9} className="pr-4">
                                        <div className="dialogScrollContent pr-12">
                                            {onRenderTaskCode != null &&
                                                onRenderTaskCode.length > 0 && (
                                                    <div className="taskCodeWrapper flex justify-between">
                                                        <h6 className="taskCode" id="toCopyElement">
                                                            {values?.project?.code
                                                                ? values?.project?.code + "#"
                                                                : ""}
                                                            {onRenderTaskCode}
                                                        </h6>

                                                        {onRenderTaskCode != "NEW" && (
                                                            <TaskInfoClipboardCopying/>
                                                        )}
                                                    </div>
                                                )}

                                            <div className="NameField">
                                                <div style={{display: "contents"}}>
                                                    <TitleIcon/>
                                                </div>
                                                <div className="w-100 pl-4">
                                                    <GlobitsTextField
                                                        timeOut={0}
                                                        name="name"
                                                        placeholder={"Tiêu đề phần việc..."}
                                                    />
                                                </div>
                                            </div>
                                            <div className="d-flex align-middle">
                                                <ProjectPopupTask/>
                                            </div>

                                            {(values?.startTime || values?.endTime) && (
                                                <div
                                                    className="flex my-10 mx-0"
                                                >
                                                    <AccessTimeIcon/>
                                                    <div className="pl-10">
                                                        <div>
                                                            {values?.startTime && !values?.endTime
                                                                ? t("task.time.startTime")
                                                                : !values?.startTime && values?.endTime
                                                                    ? t("task.time.endTime")
                                                                    : t("task.time.title")}:
                                                        </div>
                                                        <div
                                                            className="Member mx-0 my-10"
                                                        >
                                                            {values?.startTime &&
                                                                moment(values?.startTime).format(
                                                                    "DD/MM/yyyy - HH:mm"
                                                                )}
                                                            {values?.startTime && values?.endTime && " đến "}
                                                            {values?.endTime &&
                                                                moment(values?.endTime).format(
                                                                    "DD/MM/yyyy - HH:mm "
                                                                )}
                                                            {values?.startTime &&
                                                                values?.endTime &&
                                                                values?.estimateHour &&
                                                                " (" + values?.estimateHour + " giờ) "}
                                                        </div>
                                                    </div>
                                                </div>
                                            )}

                                            <Grid container spacing={2} className="pt-8">
                                                <Grid item xs={12}>
                                                    <div className="flex justify-left">
                                                        <SubjectIcon className="mr-8"/>{" "}
                                                        Nội dung công việc:
                                                    </div>
                                                </Grid>
                                                <Grid item xs={12}>
                                                    <GlobitsEditor
                                                        name="description"
                                                        placeholder="Mô tả công việc..."
                                                    />
                                                </Grid>
                                            </Grid>

                                            {/*{values?.subTasks && values?.subTasks?.length > 0 && (*/}
                                            {/*    <>*/}
                                            {/* <Collapse in={values?.subTasks?.length > 0}> */}
                                            {/* //new subtask written by DiAyTi */}
                                            <SubTaskContainer/>
                                            {/* </Collapse> */}
                                            {/*</>*/}
                                            {/*)}*/}

                                            {/* Combine History section and Comment in Separate section */}
                                            {values?.id && (
                                                <HistoryAndCommentSection/>
                                            )}

                                        </div>
                                    </Grid>

                                    <Grid item xs={12} sm={4} md={3}>
                                        <div className="listButton">
                                            <ChooseAssigneePopup
                                                startIcon={<PersonIcon/>}
                                                textButton={t("task.assignee")}
                                                classButton="bgc-blue-aqua"
                                            />

                                            <StatusPopupTask classButton="bgc-blue-aqua"/>

                                            <PriorityPopup classButton="bgc-blue-aqua"/>

                                            <ChooseActivityPopup
                                                startIcon={<LayersIcon/>}
                                                textButton={t("task.acitivity")}
                                                classButton="bgc-blue-aqua"
                                            />

                                            {/* <Button
                        variant="contained"
                        startIcon={<LayersIcon />}
                        onClick={() => setOpenListActivity(true)}
                        className="bgc-blue-aqua"
                      >
                        {t("task.acitivity")}
                      </Button> */}

                                            <SubTaskPopup classButton="bgc-blue-aqua"/>

                                            <DatePopup
                                                titleButton={t("task.time.title")}
                                                classButton="bgc-blue-aqua"
                                            />
                                        </div>
                                        <div className="pt-8" style={{color: "#5e6c84"}}>
                                            {t("task.action")}
                                        </div>
                                        <div className="listButton">
                                            <Collapse in={isChanged} className="">
                                                <Tooltip placement="top" title="Lưu thông tin">
                                                    <Button
                                                        variant="contained"
                                                        className="btn-green"
                                                        startIcon={<SaveIcon/>}
                                                        type="submit"
                                                        disabled={isSubmitting}
                                                    >
                                                        {values?.id ? "Cập nhật" : "Tạo mới"}
                                                        {/* {t("general.button.save")} */}
                                                    </Button>
                                                </Tooltip>
                                            </Collapse>

                                            {values?.id && (
                                                <Tooltip placement="top" title="Xóa phần việc">
                                                    <Button
                                                        startIcon={<DeleteIcon/>}
                                                        variant="contained"
                                                        onClick={() => handleDeleteTask(values?.id)}
                                                        className="btn-danger"
                                                        disabled={isSubmitting}
                                                    >
                                                        {t("general.button.delete")}
                                                    </Button>
                                                </Tooltip>

                                            )}

                                            <Collapse in={isChanged} className="">
                                                <Tooltip placement="top" title="Loại bỏ các thay đổi">
                                                    <Button
                                                        startIcon={<ReplayIcon/>}
                                                        variant="contained"
                                                        onClick={() => setValues(JSON.parse(JSON.stringify(dataTaskForm)))}
                                                        className="btn-info"
                                                        disabled={isSubmitting}
                                                        // type="reset"
                                                    >
                                                        Hoàn tác
                                                    </Button>
                                                </Tooltip>
                                            </Collapse>

                                            <Tooltip placement="top" title="Hủy thay đổi">
                                                <Button
                                                    startIcon={<BlockIcon/>}
                                                    variant="contained"
                                                    onClick={() => handleClose()}
                                                    disabled={isSubmitting}
                                                >
                                                    Đóng
                                                </Button>
                                            </Tooltip>
                                        </div>
                                    </Grid>
                                </Grid>
                            </DialogContent>
                        </Form>
                    )
                }
                }
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
        </>
    );
}

export default memo(observer(TaskInfo));
