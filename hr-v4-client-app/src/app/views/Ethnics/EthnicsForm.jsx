import React, {useEffect, useState} from "react";
import {Form, Formik} from "formik";
import {Button, DialogActions, DialogContent, Grid} from "@material-ui/core";
import SaveIcon from "@material-ui/icons/Save";
import BlockIcon from "@material-ui/icons/Block";
import {useTranslation} from "react-i18next";
import {useStore} from "../../stores";
import {observer} from "mobx-react";
import * as Yup from "yup";

import GlobitsTextField from "../../common/form/GlobitsTextField";
import GlobitsPopupV2 from "../../common/GlobitsPopupV2";

const defaultEthnics = {
    id: "",
    code: "",
    name: "",
    description: "",
}
export default observer(function EthnicsForm(props) {
    const {ethnicsStore} = useStore();
    const {t} = useTranslation();
    const {handleClose, createEthnics, editEthnics, selectedEthnics} =
        ethnicsStore;

    const [ethnics, setEthnics] = useState(defaultEthnics);

    const validationSchema = Yup.object({
        code: Yup.string().required(t("validation.required")),
        name: Yup.string().required(t("validation.required")),
    });

    useEffect(() => {
        if (selectedEthnics) setEthnics(selectedEthnics);
        else setEthnics(defaultEthnics)
    }, [selectedEthnics]);

    return (
        <GlobitsPopupV2
            size={"sm"}
            open={props.open}
            noDialogContent
            title={(selectedEthnics?.id ? t("general.button.edit") : t("general.button.add")) + ' ' + t("ethnics.title")}
            onClosePopup={handleClose}
        >
            <Formik
                validationSchema={validationSchema}
                enableReinitialize
                initialValues={ethnics}
                onSubmit={(values) => values.id.length === 0 ? createEthnics(values) : editEthnics(values)}
            >
                {({isSubmitting}) => (
                    <Form autoComplete="off">
                        <div className="dialog-body">
                            <DialogContent className="o-hidden">
                                <Grid container spacing={2}>
                                    <Grid item md={12} sm={12} xs={12}>
                                        <GlobitsTextField
                                            label={
                                                <span>
                          
                          {t("ethnics.code")}
                                                    <span style={{color: 'red'}}> * </span>
                        </span>
                                            }
                                            name="code"/>
                                    </Grid>
                                    <Grid item md={12} sm={12} xs={12}>
                                        <GlobitsTextField
                                            label={
                                                <span>
                          
                          {t("ethnics.name")}
                                                    <span style={{color: 'red'}}> * </span>
                        </span>
                                            }
                                            name="name"/>
                                    </Grid>
                                    <Grid item md={12} sm={12} xs={12}>
                                        <GlobitsTextField
                                            label={t("ethnics.description")}
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
