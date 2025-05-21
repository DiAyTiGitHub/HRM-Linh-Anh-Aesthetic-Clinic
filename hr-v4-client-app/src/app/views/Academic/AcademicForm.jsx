import React, {useState, useEffect} from "react";
import {Formik, Form} from "formik";
import {observer} from "mobx-react";
import {Grid, DialogActions, Button, DialogContent} from "@material-ui/core";
import SaveIcon from "@material-ui/icons/Save";
import BlockIcon from "@material-ui/icons/Block";
import {useTranslation} from "react-i18next";
import {useStore} from "app/stores";
import * as Yup from "yup";
import GlobitsTextField from "app/common/form/GlobitsTextField";
import GlobitsPopup from "app/common/GlobitsPopup";
import GlobitsPopupV2 from "../../common/GlobitsPopupV2";

export default observer(function AcademicForm(props) {
    const {academicStore} = useStore();
    const {t} = useTranslation();
    const {
        handleClose,
        createAcademic,
        editAcademic,
        selectedAcademic,
        intactAcademic
    } = academicStore;

    const [academic, setAcademic] = useState(intactAcademic);

    const validationSchema = Yup.object({
        code: Yup.string().required(t("validation.code")),
        name: Yup.string().required(t("validation.name")),
    });

    useEffect(() => {
        if (selectedAcademic) setAcademic(selectedAcademic);
        else setAcademic(intactAcademic)
    }, [selectedAcademic]);

    return (
        <GlobitsPopupV2
            size={"sm"}
            open={props.open}
            noDialogContent
            title={(selectedAcademic?.id ? t("general.button.edit") : t("general.button.add")) + ' ' + t("academic.titleLower")}
            onClosePopup={handleClose}
        >
            <Formik
                validationSchema={validationSchema}
                enableReinitialize
                initialValues={academic}
                onSubmit={(values) => values.id.length === 0 ? createAcademic(values) : editAcademic(values)}
            >
                {({isSubmitting}) => (
                    <Form autoComplete="off">
                        <div className="dialog-body">
                            <DialogContent className="o-hidden">
                                <Grid container spacing={2}>
                                    <Grid item md={12} sm={12} xs={12}>
                                        <GlobitsTextField
                                            validate
                                            label={t("academic.code")}
                                            name="code"
                                        />
                                    </Grid>
                                    <Grid item md={12} sm={12} xs={12}>
                                        <GlobitsTextField
                                            validate
                                            label={t("academic.name")}
                                            name="name"
                                        />
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
});
