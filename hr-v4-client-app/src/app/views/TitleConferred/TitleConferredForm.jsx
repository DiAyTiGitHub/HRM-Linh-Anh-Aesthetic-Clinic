import React, {useState, useEffect} from "react";
import {observer} from "mobx-react";
import {Formik, Form} from "formik";
import {Grid, DialogActions, Button, DialogContent} from "@material-ui/core";
import SaveIcon from "@material-ui/icons/Save";
import BlockIcon from "@material-ui/icons/Block";
import {useTranslation} from "react-i18next";
import {useStore} from "../../stores";
import * as Yup from "yup";
import GlobitsTextField from "../../common/form/GlobitsTextField";
import GlobitsPopup from "app/common/GlobitsPopup";
import GlobitsPopupV2 from "../../common/GlobitsPopupV2";

export default observer(function TitleConferredForm() {
    const {titleConferredStore} = useStore();
    const {t} = useTranslation();
    const {
        intactTitleConferred,
        handleClose,
        createTitleConferred,
        editTitleConferred,
        selectedTitleConferred,
        shouldOpenEditorDialog
    } = titleConferredStore;

    const [titleConferred, setTitleConferred] = useState(intactTitleConferred);

    const validationSchema = Yup.object({
        code: Yup.string().required(t("validation.required")),
        name: Yup.string().required(t("validation.required")),
    });

    useEffect(() => {
        if (selectedTitleConferred) setTitleConferred(selectedTitleConferred);
        else setTitleConferred(intactTitleConferred)
    }, [selectedTitleConferred]);

    return (
        <GlobitsPopupV2
            size={"sm"}
            noDialogContent
            open={shouldOpenEditorDialog}
            onClosePopup={handleClose}
            title={(selectedTitleConferred?.id ? t("general.button.edit") : t("general.button.add")) + ' ' + t("titleConferred.title")}
        >
            <Formik
                validationSchema={validationSchema}
                enableReinitialize
                initialValues={titleConferred}
                onSubmit={(values) => values.id.length === 0 ? createTitleConferred(values) : editTitleConferred(values)}
            >
                {({isSubmitting}) => (
                    <Form autoComplete="off">
                        <div className="dialog-body">
                            <DialogContent className="o-hidden">
                                <Grid container spacing={2}>
                                    <Grid item md={12} sm={12} xs={12}>
                                        <GlobitsTextField
                                            validate
                                            label={t("titleConferred.code")}
                                            name="code"
                                        />
                                    </Grid>
                                    <Grid item md={12} sm={12} xs={12}>
                                        <GlobitsTextField
                                            validate
                                            label={t("titleConferred.name")}
                                            name="name"
                                        />
                                    </Grid>
                                    <Grid item md={12} sm={12} xs={12}>
                                        <GlobitsTextField
                                            label={t("titleConferred.description")}
                                            name="description"
                                            multiline
                                            rows={3}
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
