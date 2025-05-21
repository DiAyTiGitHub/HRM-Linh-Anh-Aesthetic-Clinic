import React from "react";
import { Dialog, DialogTitle, Icon, IconButton } from "@material-ui/core";
import Draggable from "react-draggable";
import Paper from "@material-ui/core/Paper";
import { useTranslation } from "react-i18next";
import { useStore } from "../../stores";
import { Form, Formik } from "formik";
import { Grid, DialogActions, Button, DialogContent } from "@material-ui/core";
import GlobitsTextField from "app/common/form/GlobitsTextField";
import SaveIcon from "@material-ui/icons/Save";
import BlockIcon from "@material-ui/icons/Block";
import { observer } from "mobx-react";

function PaperComponent(props) {
    return (
        <Draggable
            handle="#draggable-dialog-title"
            cancel={'[class*="MuiDialogContent-root"]'}
        >
            <Paper {...props} />
        </Draggable>
    );
}

export default observer(function GlobalPropertyForm(props) {
    const { globalPropertyStore } = useStore();
    const { t } = useTranslation();
    const { handleClosePopup, dataForm, handleSubmitFormOpenPopup, openPopupForm } = globalPropertyStore;

    return (
        <Dialog
            className="dialog-container"
            open={openPopupForm}
            PaperComponent={PaperComponent}
            fullWidth
            maxWidth="sm"
        >
            <DialogTitle
                className="dialog-header"
                style={{ cursor: "move" }}
                id="draggable-dialog-title"
            >
                <span className="mb-20 ">GlobalProperty</span>
            </DialogTitle>
            <IconButton
                style={{ position: "absolute", right: "10px", top: "10px" }}
                onClick={handleClosePopup}
            >
                <Icon color="disabled" title={t("general.close")}>
                    close
                </Icon>
            </IconButton>
            <Formik
                enableReinitialize
                initialValues={dataForm}
                onSubmit={(values) => handleSubmitFormOpenPopup(values, dataForm)}
            >
                {({ isSubmitting }) => (
                    <Form autoComplete="off">
                        <div className="dialog-body">
                            <DialogContent className="o-hidden">
                                <Grid container spacing={2}>
                                    <Grid item md={12} sm={12} xs={12}>
                                        <GlobitsTextField
                                            label={'Property'}
                                            name="property"
                                            disabled={dataForm.property}
                                        />
                                    </Grid>
                                    <Grid item md={12} sm={12} xs={12}>
                                        <GlobitsTextField
                                            label={"Tên Property"}
                                            name="propertyName"
                                        />
                                    </Grid>
                                    <Grid item md={12} sm={12} xs={12}>
                                        <GlobitsTextField
                                            label={'Value Property'}
                                            name="propertyValue"
                                        />
                                    </Grid>
                                    <Grid item md={12} sm={12} xs={12}>
                                        <GlobitsTextField
                                            label={'Tên Data Type'}
                                            name="dataTypeName"
                                        />
                                    </Grid>
                                    <Grid item md={12} sm={12} xs={12}>
                                        <GlobitsTextField
                                            label={'Mô tả'}
                                            name="description"
                                        />
                                    </Grid>
                                </Grid>
                            </DialogContent>
                        </div>
                        <div className="dialog-footer">
                            <DialogActions className="p-0">
                                <div className="flex flex-space-between flex-middle">
                                    <Button
                                        startIcon={<BlockIcon />}
                                        variant="contained"
                                        className="mr-12 btn btn-secondary d-inline-flex"
                                        color="secondary"
                                        onClick={handleClosePopup}
                                    >
                                        {t("general.button.cancel")}
                                    </Button>
                                    <Button
                                        startIcon={<SaveIcon />}
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
        </Dialog>
    );
})
