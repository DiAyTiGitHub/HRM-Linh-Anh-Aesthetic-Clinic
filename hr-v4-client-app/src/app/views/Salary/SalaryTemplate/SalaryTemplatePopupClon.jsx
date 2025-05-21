import React, { memo } from "react";
import { useStore } from "app/stores";
import { DialogActions, DialogContent, Grid, Button } from "@material-ui/core";
import GlobitsPopupV2 from "app/common/GlobitsPopupV2";
import { Formik, Form } from "formik";
import GlobitsTextField from "app/common/form/GlobitsTextField";
import ConstantList from "app/appConfig";
//import { useTranslation } from "react-i18next";
import { observer } from "mobx-react";
import { useHistory } from "react-router-dom";
import SaveIcon from "@material-ui/icons/Save";
import BlockIcon from "@material-ui/icons/Block";
import { useTranslation } from "react-i18next";
import FormikFocusError from "app/common/FormikFocusError";

function SalaryTemplatePopupClon() {
    const { t } = useTranslation();

    const { salaryTemplateStore } = useStore();

    const {
        openPopupClon,
        setClosePopupClon,
        selectedSalaryTemplateClon,
        handleClonSalaryTemplate
    } = salaryTemplateStore;

    const history = useHistory();

    async function handleSaveSalaryTemplateClon(selectedSalaryTemplateClon) {
        try {
            const data = await handleClonSalaryTemplate(selectedSalaryTemplateClon);
            if (!data?.id) throw Error("Error when saving salary template");
            const redirectUrl = ConstantList.ROOT_PATH + `salary-template/` + data?.id;
            history.push(redirectUrl);
        } catch (error) {
            console.error("An error occurred while saving the cloned template:", error);
        }
        setClosePopupClon();
    }

    return (
        <GlobitsPopupV2
            scroll="body"
            noDialogContent
            size="sm"
            open={openPopupClon}
            title="Nhân bản mẫu bảng lương"
            onClosePopup={setClosePopupClon}
            onClose={setClosePopupClon}
        >
            <Formik
                initialValues={selectedSalaryTemplateClon}
                onSubmit={handleSaveSalaryTemplateClon}
                enableReinitialize

            >
                {({ values, handleChange, handleBlur, isSubmitting }) => {

                    return (
                        <Form autoComplete="off">
                            <div className="dialog-body">
                                <DialogContent className="dialog-body p-12">
                                    <FormikFocusError />

                                    <Grid container spacing={2}>
                                        {/* Mã mẫu */}
                                        <Grid item xs={12} sm={6}>
                                            <GlobitsTextField
                                                name="code"
                                                label="Mã mẫu"
                                                variant="outlined"
                                                fullWidth
                                                value={values?.code}
                                                onChange={handleChange}
                                                onBlur={handleBlur}
                                                margin="normal"
                                            />
                                        </Grid>

                                        {/* Tên mẫu bảng lương */}
                                        <Grid item xs={12} sm={6}>
                                            <GlobitsTextField
                                                name="name"
                                                label="Tên mẫu bảng lương"
                                                variant="outlined"
                                                fullWidth
                                                value={values?.name}
                                                onChange={handleChange}
                                                onBlur={handleBlur}
                                                margin="normal"
                                            />
                                        </Grid>

                                        {/* Mô tả */}
                                        <Grid item xs={12}>
                                            <GlobitsTextField
                                                name="description"
                                                label="Mô tả"
                                                variant="outlined"
                                                fullWidth
                                                value={values?.description}
                                                onChange={handleChange}
                                                onBlur={handleBlur}
                                                margin="normal"
                                            />
                                        </Grid>
                                    </Grid>
                                </DialogContent>
                            </div>

                            <div className="dialog-footer dialog-footer-v2 py-8">
                                <DialogActions className="p-0">
                                    <div className="flex flex-space-between flex-middle">
                                        <Button
                                            startIcon={<BlockIcon />}
                                            variant="contained"
                                            className="mr-12 btn btn-secondary d-inline-flex"
                                            color="secondary"
                                            onClick={handleClonSalaryTemplate}
                                            disabled={isSubmitting}
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
                    );
                }}
            </Formik>
        </GlobitsPopupV2>
    );
}

export default memo(observer(SalaryTemplatePopupClon));
