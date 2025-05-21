import React, {useState, useEffect} from "react";
import {Formik, Form} from "formik";
import {Grid, DialogActions, Button, DialogContent} from "@material-ui/core";
import SaveIcon from "@material-ui/icons/Save";
import BlockIcon from "@material-ui/icons/Block";
import {useTranslation} from "react-i18next";
import {useStore} from "../../stores";
import * as Yup from "yup";
import {observer} from "mobx-react";
import GlobitsTextField from "../../common/form/GlobitsTextField";
import GlobitsPopup from "app/common/GlobitsPopup";
import GlobitsPopupV2 from "app/common/GlobitsPopupV2";
import {CodePrefixes} from "../../LocalConstants";

export default observer(function SpecialityForm() {
    const {specialityStore} = useStore();
    const {t} = useTranslation();
    const {
        handleClose,
        createSpeciality,
        editSpeciality,
        selectedSpeciality,
        shouldOpenEditorDialog,
        intactSpeciality,
        autoGenCode
    } = specialityStore;

    const [speciality, setSpeciality] = useState(intactSpeciality);
    const autoGenCodeFunc = async () => {
        const code = await autoGenCode(CodePrefixes.CHUYEN_NGANH);
        if (code) {
            // Tạo object mới để tránh thay đổi trực tiếp state
            const updated = {...intactSpeciality, code};
            setSpeciality(updated);
        }
    };

    useEffect(() => {
        if (selectedSpeciality) {
            setSpeciality(selectedSpeciality)
        } else {
            autoGenCodeFunc()
        }
    }, [selectedSpeciality]);
    const validationSchema = Yup.object({
        code: Yup.string().required(t("validation.required")),
        name: Yup.string().required(t("validation.required")),
        nameEng: Yup.string().required(t("validation.required")),
    });


    return (
        <GlobitsPopupV2
            size="sm"
            noDialogContent
            open={shouldOpenEditorDialog}
            onClosePopup={handleClose}
            title={(selectedSpeciality?.id ? t("general.button.edit") : t("general.button.add")) + ' ' + t("speciality.title")}
        >
            <Formik
                validationSchema={validationSchema}
                enableReinitialize
                initialValues={speciality}
                onSubmit={(values) =>
                    values.id.length === 0
                        ? createSpeciality(values)
                        : editSpeciality(values)
                }
            >
                {({isSubmitting}) => (
                    <Form autoComplete="off">
                        <div className="dialog-body">
                            <DialogContent className="o-hidden">
                                <Grid container spacing={2}>
                                    <Grid item xs={12}>
                                        <GlobitsTextField
                                            label={t("speciality.code")}
                                            validate
                                            name="code"
                                        />
                                    </Grid>

                                    <Grid item xs={12}>
                                        <GlobitsTextField
                                            validate
                                            label={t("speciality.name")}
                                            name="name"
                                        />
                                    </Grid>

                                    <Grid item xs={12}>
                                        <GlobitsTextField
                                            validate
                                            label={t("speciality.nameEng")}
                                            name="nameEng"
                                        />
                                    </Grid>

                                    {/* <Grid item xs={12}>
                    <GlobitsTextField
                      label={t("speciality.description")}
                      name="description"
                      multiline
                      rows={3}
                    />
                  </Grid> */}
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
