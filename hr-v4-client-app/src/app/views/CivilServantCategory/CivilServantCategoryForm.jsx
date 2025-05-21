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
import GlobitsPopupV2 from "../../common/GlobitsPopupV2";

export default observer(function CivilServantCategoryForm(props) {
    const {civilServantCategoryStore} = useStore();
    const {t} = useTranslation();
    const {handleClose, createCategory, editCategory, selectedItem} = civilServantCategoryStore;

    const [category, setCategory] = useState({
        id: "", code: "", name: "",
    });

    const validationSchema = Yup.object({
        code: Yup.string().required(t("validation.required")), name: Yup.string().required(t("validation.required")),
    });

    useEffect(() => {
        if (selectedItem) setCategory(selectedItem);
    }, [selectedItem]);

    useEffect(() => {
        console.log(category);
    }, [category]);


    return (<GlobitsPopupV2
            size={"sm"}
            open={props.open}
            noDialogContent
            title={(selectedItem?.id ? t("general.button.edit") : t("general.button.add")) + ' ' + t("civilServantCategory.title")}
            onClosePopup={handleClose}
        >
            <Formik
                validationSchema={validationSchema}
                enableReinitialize
                initialValues={category}
                onSubmit={(values) => values.id.length === 0 ? createCategory(values) : editCategory(values)}
            >
                {({isSubmitting}) => (<Form autoComplete="off">
                        <div className="dialog-body">
                            <DialogContent className="o-hidden">
                                <Grid container spacing={2}>
                                    <Grid item md={12} sm={12} xs={12}>
                                        <GlobitsTextField
                                            label={<span>
                          
                          {t("civilServantCategory.code")}
                                                <span style={{color: 'red'}}> * </span>
                        </span>}
                                            name="code"
                                        />
                                    </Grid>
                                    <Grid item md={12} sm={12} xs={12}>
                                        <GlobitsTextField
                                            label={<span>
                          
                          {t("civilServantCategory.name")}
                                                <span style={{color: 'red'}}> * </span>
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
