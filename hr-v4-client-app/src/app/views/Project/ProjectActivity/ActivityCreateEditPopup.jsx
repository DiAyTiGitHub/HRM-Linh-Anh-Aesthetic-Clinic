import React from "react";
import {useTranslation} from "react-i18next";
import {useStore} from "../../../stores";
import {Formik, Form} from "formik";
import {Grid, DialogActions, Button, DialogContent} from "@material-ui/core";
import * as Yup from "yup";
import GlobitsTextField from "../../../common/form/GlobitsTextField";
import SelectActivityPopup from "./SelectActivityPopup";
import {observer} from "mobx-react";
import GlobitsDateTimePicker from "app/common/form/GlobitsDateTimePicker";
import GlobitsPopup from "app/common/GlobitsPopup";
import moment from "moment";
import GlobitsPopupV2 from "../../../common/GlobitsPopupV2";

export default observer(function ActivityCreateEditPopup(props) {
    const {t} = useTranslation();
    const {dataEditProject} = props;
    const {projectActivityStore} = useStore();

    const [isOpenPopup, setIsOpenPopup] = React.useState(false)

    const {
        handleClosePopup,
        saveActivity,
        openCreateEditPopup,
        selectedActivity,
    } = projectActivityStore;

    const validationSchema = Yup.object({
        name: Yup.string().required(t("validation.name")).nullable(),
        code: Yup.string().required(t("Trường này là bắt buộc")).nullable(),
    });

    function calculateEstimatedTime(startTime, endTime) {
        if (moment(startTime).isValid() && moment(endTime).isValid()) {
            return moment(endTime).diff(new Date(startTime), "days");
        }
        return null;
    }

    console.log({...selectedActivity})
    return (
        <GlobitsPopupV2
            size={"md"}
            popupId="activityCEPA"
            title={
                (selectedActivity?.id
                    ? t("general.button.edit")
                    : t("general.button.add")) +
                " " +
                t("timeSheet.activity")
            }
            open={openCreateEditPopup}
            onClosePopup={handleClosePopup}
            noDialogContent
        >
            <Formik
                enableReinitialize
                initialValues={selectedActivity}
                onSubmit={(values, actions) => {
                    values.project = dataEditProject;
                    saveActivity(values);
                    actions.setSubmitting(false);
                }}
                validationSchema={validationSchema}
            >
                {({isSubmitting, values, setFieldValue}) => {
                    return (
                        <Form autoComplete="off">
                            <DialogContent
                                className="dialog-body"
                                style={{
                                    minWidth: "300px",
                                    overflow: "auto",
                                    maxHeight: "90vh",
                                }}
                            >
                                <Grid container spacing={2} style={{marginBottom: "8px"}}>
                                    <Grid item xs={12} md={12} spacing={1} container>
                                        <Grid item xs={12}>
                                            <div className="input-popup-container">
                                                <GlobitsTextField
                                                    label={t("project.activity.parent")}
                                                    name="parent.name"
                                                    disabled
                                                    value={values?.parent ? values?.parent?.name : ""}
                                                />
                                                <Button
                                                    variant="contained"
                                                    style={{
                                                        marginTop: "25px",
                                                    }}
                                                    className="btn-primary"
                                                    onClick={() => setIsOpenPopup(true)}
                                                >
                                                    {t("general.button.select")}
                                                </Button>
                                                <SelectActivityPopup
                                                    isOpenPopup={isOpenPopup}
                                                    setIsOpenPopup={setIsOpenPopup}
                                                />
                                            </div>
                                        </Grid>
                                        <Grid item xs={12} md={6}>
                                            <GlobitsTextField
                                                label={t("project.activity.code")}
                                                name="code"
                                                validate={true}
                                            />
                                        </Grid>

                                        <Grid item xs={12} md={6}>
                                            <GlobitsTextField
                                                label={t("project.activity.name")}
                                                name="name"
                                                validate={true}
                                            />
                                        </Grid>

                                        <Grid item xs={12} sm={6}>
                                            <GlobitsDateTimePicker
                                                label={"Thời gian bắt đầu"}
                                                name="startTime"
                                                maxDate={values.endTime}
                                                onChange={(value) => {
                                                    setFieldValue("startTime", value);
                                                }}
                                            />
                                        </Grid>

                                        <Grid item xs={12} sm={6}>
                                            <GlobitsDateTimePicker
                                                label={"Thời gian kết thúc"}
                                                name="endTime"
                                                minDate={values?.startTime}
                                                onChange={(value) => {
                                                    setFieldValue("endTime", value);
                                                }}
                                            />
                                        </Grid>

                                        {/* <Grid item xs={12} sm={6} md={4}>
                                            <GlobitsTextField type={'number'} label={'Thời gian ước tính'} name="estimateDuration" />
                                        </Grid> */}

                                        <Grid item xs={12}>
                                            <GlobitsTextField
                                                label={t("project.activity.description")}
                                                name="description"
                                                multiline
                                                rows={3}
                                            />
                                        </Grid>
                                    </Grid>
                                </Grid>
                            </DialogContent>

                            <DialogActions className="dialog-footer p-0">
                                <div className="flex flex-space-between flex-middle">
                                    <Button
                                        variant="contained"
                                        className="mr-12 btn btn-secondary d-inline-flex"
                                        color="secondary"
                                        onClick={() => handleClosePopup()}
                                    >
                                        {t("general.button.close")}
                                    </Button>
                                    <Button
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
                        </Form>
                    );
                }}
            </Formik>
        </GlobitsPopupV2>
    );
});
