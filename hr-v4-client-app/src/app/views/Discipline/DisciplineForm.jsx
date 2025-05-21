import React, { useEffect, useState } from 'react';
import { useStore } from "../../stores";
import { observer } from 'mobx-react-lite';
import GlobitsTextField from 'app/common/form/GlobitsTextField';
import { useTranslation } from 'react-i18next';
import { Form, Formik } from "formik";
import * as Yup from "yup";
import SaveIcon from "@material-ui/icons/Save";
import BlockIcon from "@material-ui/icons/Block";
import { Button, DialogActions, DialogContent, Grid } from "@material-ui/core";
import GlobitsPopupV2 from "../../common/GlobitsPopupV2";
import { HrDiscipline } from 'app/common/Model/HumanResource/HrDiscipline';
import {CodePrefixes} from "../../LocalConstants";


export default observer(function DisciplineForm(props) {
    const { t } = useTranslation();

    const validationSchema = Yup.object({
        code: Yup.string().required(t("validation.code")),
        level: Yup.number().min(0, "Giá trị thấp nhất là 0").required(t("validation.required")).nullable(),
        name: Yup.string().required(t("validation.name")),
        // languageKey: Yup.string().required(t("validation.required")),
        // disciplineType: Yup.number().min(0, "Giá trị thấp nhất là 0").required(t("validation.required")).nullable(),
        // formal: Yup.number().min(0, "Giá trị thấp nhất là 0").required(t("validation.required")).nullable(),
        // evaluateYear: Yup.number().min(2019, ("Từ năm 2019 trở đi")).max(new Date().getFullYear(), `Từ năm ${new Date().getFullYear()} trở lại`).required(t("validation.required")).nullable(),
    });

    const { disciplineStore } = useStore();
    const
        {
            addDiscipline,
            updateDiscipline,
            handleCloseDisciplineDialog,
            selectedDiscipline,
            autoGenCode
        } = disciplineStore;
    const [discipline, setDiscipline] = useState(selectedDiscipline);
    const autoGenCodeFunc = async () => {
        const code = await autoGenCode(CodePrefixes.LOAI_KY_LUAT);
        if (code) {
            // Tạo object mới để tránh thay đổi trực tiếp state
            const updated = {...selectedDiscipline, ...{code:code}};
            setDiscipline(updated);
        }
    };
    useEffect(() => {
        if(!selectedDiscipline?.id){
            console.log("hello")
            autoGenCodeFunc();
        }
    }, []);
    useEffect(() => {
        if (selectedDiscipline) setDiscipline(selectedDiscipline);
    }, [selectedDiscipline]);

    function handleFormSubmit(discipline) {
        if (!discipline?.id) {
            addDiscipline(discipline);
        } else {
            updateDiscipline(discipline);
        }
    }

    console.log(discipline)
    return (
        <GlobitsPopupV2
            size={"xs"}
            open={props.open}
            noDialogContent
            title={(selectedDiscipline?.id ? t("general.button.edit") : t("general.button.add")) + ' ' + t("discipline.title")}
            onClosePopup={handleCloseDisciplineDialog}
        >
            <Formik
                enableReinitialize
                validationSchema={validationSchema}
                initialValues={discipline}
                onSubmit={handleFormSubmit}
            >
                {({ isSubmitting }) => (
                    <Form autoComplete="off">
                        <div className="dialog-body">
                            <DialogContent className="o-hidden p-12">
                                <Grid container spacing={2}>
                                    <Grid item xs={12}>
                                        <GlobitsTextField
                                            name="code"
                                            label={t("discipline.code")}
                                            required
                                        />
                                    </Grid>

                                    <Grid item xs={12}>
                                        <GlobitsTextField
                                            name="name"
                                            label={t("discipline.name")}
                                            required
                                        />
                                    </Grid>

                                    <Grid item xs={12}>
                                        <GlobitsTextField
                                            label={"Mức độ kỷ luật"}
                                            name="level"
                                            type="number"
                                        />
                                    </Grid>

                                    {/* <Grid item xs={12}>
                                        <GlobitsTextField
                                            label={t("discipline.languageKey")}
                                            required
                                            name="languageKey" />
                                    </Grid>

                                    <Grid item xs={12}>
                                        <GlobitsTextField
                                            label={t("discipline.formal")}
                                            required
                                            name="formal"
                                            type="number"
                                        />
                                    </Grid>

                                    <Grid item xs={12}>
                                        <GlobitsTextField
                                            label={t("discipline.evaluateYear")}
                                            required
                                            name="evaluateYear"
                                            type="number"
                                        />
                                    </Grid>
                                    <Grid item xs={12}>
                                        <GlobitsTextField
                                            label={t("discipline.disciplineType")}
                                            required
                                            name="disciplineType"
                                            type="number"
                                        />
                                    </Grid> */}

                                    <Grid item md={12} xs={12}>
                                        <GlobitsTextField
                                            name="description"
                                            label={t("discipline.description")}
                                            multiline
                                            rows={3} />
                                    </Grid>
                                </Grid>
                            </DialogContent>
                        </div>

                        <div className="dialog-footer">
                            <DialogActions className="p-0">
                                <div className="flex flex-space-between flex-middle">
                                    <Button
                                        variant="contained"
                                        className="mr-12 btn btn-secondary d-inline-flex"
                                        color="secondary"
                                        startIcon={<BlockIcon />}
                                        onClick={() => handleCloseDisciplineDialog()}>
                                        {t("general.button.cancel")}
                                    </Button>
                                    <Button
                                        className="mr-0 btn btn-primary d-inline-flex"
                                        variant="contained"
                                        color="primary"
                                        type="submit"
                                        startIcon={<SaveIcon />}
                                    // disabled={isSubmitting}
                                    >
                                        {t("general.button.save")}
                                    </Button>
                                </div>
                            </DialogActions>
                        </div>
                    </Form>
                )}
            </Formik>
        </GlobitsPopupV2>
    );
})
