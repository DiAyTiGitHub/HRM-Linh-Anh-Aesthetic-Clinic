import { Grid } from "@material-ui/core";
import { observer } from "mobx-react";
import { useEffect } from "react";
import { useTranslation } from "react-i18next";
import GlobitsBreadcrumb from "../../common/GlobitsBreadcrumb";
import GlobitsConfirmationDialog from "../../common/GlobitsConfirmationDialog";
import { useStore } from "../../stores";
import InterviewScheduleForm from "./InterviewScheduleForm";
import InterviewScheduleList from "./InterviewScheduleList";
import InterviewScheduleToolbar from "./InterviewScheduleToolbar";

export default observer(function InterviewScheduleIndex() {
    const {interviewScheduleStore} = useStore();
    const {t} = useTranslation();

    const {
        search ,
        handleSetSearchObject ,
        resetInterviewScheduleStore ,
        shouldOpenEditorDialog ,
        shouldOpenConfirmationDialog ,
        shouldOpenConfirmationDeleteListDialog ,
        handleClose ,
        handleConfirmDelete ,
        handleDeleteList ,
        handleConfirmDeleteList ,
    } = interviewScheduleStore;

    useEffect(() => {
        search();
        return resetInterviewScheduleStore;
    } , []);

    return (
        <div className="content-index">
            <div className="index-breadcrumb">
                <GlobitsBreadcrumb
                    routeSegments={[{name:t("Lịch phỏng vấn")}]}
                />
            </div>

            <Grid className="index-card" container spacing={2}>
                <Grid item xs={12}>
                    <InterviewScheduleToolbar/>
                </Grid>

                <Grid item xs={12}>
                    <InterviewScheduleList/>
                </Grid>
                <InterviewScheduleForm open={shouldOpenEditorDialog}/>

                <GlobitsConfirmationDialog
                    open={shouldOpenConfirmationDialog}
                    onConfirmDialogClose={handleClose}
                    onYesClick={handleConfirmDelete}
                    title={t("confirm_dialog.delete.title")}
                    text={t("confirm_dialog.delete.text")}
                    agree={t("confirm_dialog.delete.agree")}
                    cancel={t("confirm_dialog.delete.cancel")}
                />

                <GlobitsConfirmationDialog
                    open={shouldOpenConfirmationDeleteListDialog}
                    onConfirmDialogClose={handleClose}
                    onYesClick={handleConfirmDeleteList}
                    title={t("confirm_dialog.delete_list.title")}
                    text={t("confirm_dialog.delete_list.text")}
                    agree={t("confirm_dialog.delete_list.agree")}
                    cancel={t("confirm_dialog.delete_list.cancel")}
                />
            </Grid>
        </div>
    );
});
