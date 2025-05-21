import React, {useEffect, useState} from "react";
import {FieldArray, Form, Formik} from "formik";
import {
    Button,
    DialogActions,
    DialogContent,
    Grid, makeStyles,
} from "@material-ui/core";
import SaveIcon from "@material-ui/icons/Save";
import BlockIcon from "@material-ui/icons/Block";
import {useTranslation} from "react-i18next";
import {useStore} from "../../../../stores";
import * as Yup from "yup";
import {observer} from "mobx-react";
import GlobitsTextField from "../../../../common/form/GlobitsTextField";
import GlobitsPopupV2 from "../../../../common/GlobitsPopupV2";
import GlobitsEditor from "../../../../common/form/GlobitsEditor";
import GlobitsFileUpload from "../../../../common/form/FileUpload/GlobitsFileUpload";
const useStyles = makeStyles({
    table: {
        minWidth: 650,
        "& .MuiTableCell-root": {
            border: "2px solid rgba(224, 224, 224, 1)"
        }
    }
});
export default observer(function ContentTemplateForm(props) {
    const classes = useStyles();

    const {contentTemplateStore} = useStore();
    const {t} = useTranslation();
    const {
        handleClose,
        saveContentTemplate,
        selectedContentTemplate,
        intactcontentTemplate
    } = contentTemplateStore;

    const [contentTemplate, setContentTemplate] = useState(intactcontentTemplate);

    const validationSchema = Yup.object({
        code: Yup.string().nullable().required(t("validation.required")),
        name: Yup.string().nullable().required(t("validation.required")),
    });

    useEffect(() => {
        if (selectedContentTemplate) setContentTemplate(selectedContentTemplate);
        else setContentTemplate(intactcontentTemplate);
    }, [selectedContentTemplate]);

    return (
        <GlobitsPopupV2
            size={"md"}
            open={props.open}
            noDialogContent
            title={
                (selectedContentTemplate?.id ? t("general.button.edit") : t("general.button.add")) +
                " " +
                t("contentTemplate.title")
            }
            onClosePopup={handleClose}
        >
            <Formik
                validationSchema={validationSchema}
                enableReinitialize
                initialValues={{
                    ...contentTemplate,
                }}
                onSubmit={(values) => saveContentTemplate(values)
                    // values.id?.length === 0 || !values.id
                    //     ? createEvaluationItem(values)
                    //     : editEvaluationItem(values)
                }
            >
                {({isSubmitting, values, setFieldValue}) => (
                    <Form autoComplete="off">
                        <div className="dialog-body">
                            <DialogContent className="o-hidden">
                                <Grid container spacing={2}>
                                    <Grid item md={12} sm={12} xs={12}>
                                        <GlobitsTextField
                                            label={<span>{t("contentTemplate.name")}<span
                                                style={{color: "red"}}> * </span></span>}
                                            name="name"
                                        />
                                    </Grid>

                                    <Grid item md={12} sm={12} xs={12}>
                                        <GlobitsTextField
                                            label={<span>{t("contentTemplate.code")}<span
                                                style={{color: "red"}}> * </span></span>}
                                            name="code"
                                        />
                                    </Grid>
                                    <Grid item xs={12}>
                                        <GlobitsEditor
                                            label={<span>{t("contentTemplate.description")}<span
                                                style={{color: "red"}}> * </span></span>}
                                            name="content"
                                        />
                                    </Grid>
                                    {/*<Grid item xs={12}>*/}
                                    {/*    <GlobitsFileUpload*/}
                                    {/*        label={<span>File đính kèm<span*/}
                                    {/*            style={{color: "red"}}> * </span></span>}*/}
                                    {/*        name="content"*/}
                                    {/*    />*/}
                                    {/*</Grid>*/}
                                </Grid>
                            </DialogContent>
                        </div>
                        <div className="dialog-footer">
                            <DialogActions className="p-0">
                                <div className="flex flex-space-between flex-middle">
                                    <Button
                                        startIcon={<BlockIcon/>}
                                        variant="contained"
                                        className="mr-12 btn btn-secondary d-inline-flex"
                                        color="secondary"
                                        onClick={handleClose}
                                    >
                                        {t("general.button.cancel")}
                                    </Button>
                                    <Button
                                        startIcon={<SaveIcon/>}
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
        </GlobitsPopupV2>
    );
});
