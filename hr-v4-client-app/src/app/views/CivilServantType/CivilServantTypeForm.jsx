import React, {useEffect, useState} from "react";
import {Form, Formik} from "formik";
import {Button, DialogActions, DialogContent, Grid} from "@material-ui/core";
import SaveIcon from "@material-ui/icons/Save";
import BlockIcon from "@material-ui/icons/Block";
import {useTranslation} from "react-i18next";
import {useStore} from "../../stores";
import * as Yup from "yup";
import {observer} from "mobx-react";
import GlobitsTextField from "../../common/form/GlobitsTextField";
import GlobitsSelectInput from "../../common/form/GlobitsSelectInput";
import LocalConstants from "../../LocalConstants";
import GlobitsPopupV2 from "../../common/GlobitsPopupV2";

export default observer(function CivilServantTypeForm(props) {
    const {civilServantTypeStore} = useStore();
    const {t} = useTranslation();
    const {
        handleClose, createCivilServantType, editCivilServantType, selectedCivilServantType,
    } = civilServantTypeStore;

    const [civilServantType, setCivilServantType] = useState({
        id: "", code: "", name: "", languageKey: "Tiếng Việt",
    });

    const validationSchema = Yup.object({
        code: Yup.string().required(t("validation.required")),
        name: Yup.string().trim().required(t("validation.required")),
        languageKey: Yup.string().required(t("validation.required")),
    });

    useEffect(() => {
        if (selectedCivilServantType) setCivilServantType(selectedCivilServantType);
    }, [selectedCivilServantType]);

    useEffect(() => {
        console.log(civilServantType);
    }, [civilServantType]);


    return (
        <GlobitsPopupV2
            size={"sm"}
            open={props.open}
            noDialogContent
            title={(selectedCivilServantType?.id ? t("general.button.edit") : t("general.button.add")) + ' ' + t("civilServantType.title")}
            onClosePopup={handleClose}
        >
            <Formik
                validationSchema={validationSchema}
                enableReinitialize
                initialValues={civilServantType}
                onSubmit={(values) => values.id.length === 0 ? createCivilServantType(values) : editCivilServantType(values)}
            >
                {({isSubmitting}) => (<Form autoComplete="off">
                    <div className="dialog-body">
                        <DialogContent className="o-hidden">
                            <Grid container spacing={2}>
                                <Grid item md={12} sm={12} xs={12}>
                                    <GlobitsTextField
                                        label={<span>
                          
                          {t("civilServantType.code")}
                                            <span style={{color: 'red'}}> * </span>
                        </span>}
                                        name="code"
                                    />
                                </Grid>
                                <Grid item md={12} sm={12} xs={12}>
                                    <GlobitsTextField
                                        label={<span>
                          
                          {t("civilServantType.name")}
                                            <span style={{color: 'red'}}> * </span>
                        </span>}
                                        name="name"
                                    />
                                </Grid>
                                <Grid item md={12} sm={12} xs={12}>
                                    <GlobitsSelectInput
                                        label={<span>
                            {t("civilServantType.languageKey")}
                                            <span style={{color: 'red'}}> * </span>
                          </span>}
                                        name="languageKey"
                                        keyValue="name"
                                        options={LocalConstants.ListLanguage}
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
