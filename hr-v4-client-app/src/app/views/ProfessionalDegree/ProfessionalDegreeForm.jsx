import React, {useEffect, useState} from "react";
import {observer} from "mobx-react";
import {Form, Formik} from "formik";
import {Button, DialogActions, DialogContent, Grid} from "@material-ui/core";
import SaveIcon from "@material-ui/icons/Save";
import BlockIcon from "@material-ui/icons/Block";
import {useTranslation} from "react-i18next";
import {useStore} from "../../stores";
import * as Yup from "yup";
import GlobitsTextField from "../../common/form/GlobitsTextField";
import GlobitsPopupV2 from "../../common/GlobitsPopupV2";

export default observer(function ProfessionalDegreeForm() {
    const {professionalDegreeStore} = useStore();
    const {t} = useTranslation();
    const {
        handleClose,
        createProfessionalDegree,
        editProfessionalDegree,
        selectedProfessionalDegree,
        shouldOpenEditorDialog,
        intactProfessional
    } = professionalDegreeStore;

    const [professionalDegree, setProfessionalDegree] = useState(intactProfessional);

    const validationSchema = Yup.object({
        code: Yup.string().required(t("validation.required")),
        name: Yup.string().required(t("validation.required")),
    });

    useEffect(() => {
        if (selectedProfessionalDegree) setProfessionalDegree(selectedProfessionalDegree);
        else setProfessionalDegree(intactProfessional)
    }, [selectedProfessionalDegree]);

    return (
        <GlobitsPopupV2
            size={"sm"}
            open={shouldOpenEditorDialog}
            onClosePopup={handleClose}
            title={(selectedProfessionalDegree?.id ? t("general.button.edit") : t("general.button.add")) + " " + t("professionalDegree.title")}
            noDialogContent
        >
            <Formik
                validationSchema={validationSchema}
                enableReinitialize
                initialValues={professionalDegree}
                onSubmit={(values) => values.id.length === 0 ? createProfessionalDegree(values) : editProfessionalDegree(values)}
            >
                {({isSubmitting}) => (
                    <Form autoComplete="off">
                        <div className="dialog-body">
                            <DialogContent className="o-hidden">
                                <Grid container spacing={2}>
                                    <Grid item md={12} sm={12} xs={12}>
                                        <GlobitsTextField
                                            validate
                                            label={t("professionalDegree.code")}
                                            name="code"/>
                                    </Grid>
                                    <Grid item md={12} sm={12} xs={12}>
                                        <GlobitsTextField
                                            validate
                                            label={t("professionalDegree.name")}
                                            name="name"/>
                                    </Grid>
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
                                        onClick={() => handleClose()}
                                    >
                                        {t("general.button.cancel")}
                                    </Button>
                                    <Button
                                        startIcon={<SaveIcon/>}
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
                )}
            </Formik>
        </GlobitsPopupV2>
    );
})
;
