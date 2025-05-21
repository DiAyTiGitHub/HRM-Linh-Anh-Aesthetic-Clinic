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

export default observer(function EducationalManagementLevelForm(props) {
    const {educationalManagementLevelStore} = useStore();
    const {t} = useTranslation();
    const {
        handleClose,
        createEducationalManagementLevel,
        editEducationalManagementLevel,
        selectedEducationalManagementLevel,
        intactEducationalManagementLevel,
    } = educationalManagementLevelStore;

    const [educationalManagementLevel, setEducationalManagementLevel] = useState(intactEducationalManagementLevel);

    const validationSchema = Yup.object({
        code: Yup.string().required(t("validation.required")), name: Yup.string().required(t("validation.required")),
    });

    useEffect(() => {
        if (selectedEducationalManagementLevel) setEducationalManagementLevel(selectedEducationalManagementLevel);
        else setEducationalManagementLevel(intactEducationalManagementLevel)
    }, [selectedEducationalManagementLevel]);


    return (
        <GlobitsPopupV2
            size={"sm"}
            open={props.open}
            noDialogContent
            title={(selectedEducationalManagementLevel?.id ? t("general.button.edit") : t("general.button.add")) + ' ' + t("educationalManagementLevel.title")}
            onClosePopup={handleClose}
        >
            <Formik
                validationSchema={validationSchema}
                enableReinitialize
                initialValues={educationalManagementLevel}
                onSubmit={(values) => values.id.length === 0 ? createEducationalManagementLevel(values) : editEducationalManagementLevel(values)}
            >
                {({isSubmitting}) => (<Form autoComplete="off">
                    <div className="dialog-body">
                        <DialogContent className="o-hidden">
                            <Grid container spacing={2}>
                                <Grid item md={12} sm={12} xs={12}>
                                    <GlobitsTextField
                                        label={<span>
                          {t("educationalManagementLevel.code")}
                                            <span style={{color: "red"}}> * </span>
                        </span>}
                                        name="code"
                                    />
                                </Grid>
                                <Grid item md={12} sm={12} xs={12}>
                                    <GlobitsTextField
                                        label={<span>
                          {t("educationalManagementLevel.name")}
                                            <span style={{color: "red"}}> * </span>
                        </span>}
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
                </Form>)}
            </Formik>
        </GlobitsPopupV2>);
});
