import React, { useState, useEffect } from "react";
import { Formik, Form } from "formik";
import {
    Grid,
    DialogActions,
    Button,
    DialogContent,
    TextField,
} from "@material-ui/core";
import SaveIcon from "@material-ui/icons/Save";
import BlockIcon from "@material-ui/icons/Block";
import { useTranslation } from "react-i18next";
import { useStore } from "../../../../stores";
import * as Yup from "yup";
import { observer } from "mobx-react";
import GlobitsTextField from "../../../../common/form/GlobitsTextField";
import GlobitsPopupV2 from "app/common/GlobitsPopupV2";
import TabsComponent from "app/common/Tab/TabComponent";
import DoneAllIcon from "@material-ui/icons/DoneAll";
import PersonOutlineIcon from "@material-ui/icons/PersonOutline";
import GlobitsCheckBox from "app/common/form/GlobitsCheckBox";

export default observer(function ContractTypeForm() {
    const { contractTypeStore } = useStore();
    const { t } = useTranslation();
    const [tab, setTab] = useState(0);
    const {
        handleClose,
        selectedExamCategory,
        saveOrUpdate,
        shouldOpenEditorDialog,
    } = contractTypeStore;

    const tabList = [
        { icon: <PersonOutlineIcon fontSize="small" />, label: "Loại hợp đồng" },
    ];

    const validationSchema = Yup.object({
        code: Yup.string().required(t("validation.required")).nullable(),
        name: Yup.string().required(t("validation.required")).nullable(),
        //languageKey: Yup.string().required(t("validation.required")).nullable(),
        //duration: Yup.number().nullable().required(t("validation.required")),
        description: Yup.string().nullable(),
    });

    return (
        <GlobitsPopupV2
            open={shouldOpenEditorDialog}
            noDialogContent
            size="md"
            title={
                (selectedExamCategory?.id
                    ? t("general.button.edit")
                    : t("general.button.add")) +
                " " +
                t("contractType.title")
            }
            onClosePopup={handleClose}
        >
            <Formik
                validationSchema={validationSchema}
                enableReinitialize
                initialValues={selectedExamCategory}
                onSubmit={saveOrUpdate}
            >
                {({ isSubmitting, values, setFieldValue }) => (
                    <Form autoComplete="off">
                        <div className="dialog-body">
                            <DialogContent className="o-hidden">
                                <Grid container spacing={2}>
                                    <Grid item md={6} xs={12}>
                                        <GlobitsTextField
                                            label={
                                                <span>
                                                    Mã loại hợp đồng
                                                    <span style={{ color: "red" }}> * </span>
                                                </span>
                                            }
                                            name="code"
                                        />
                                    </Grid>
                                    <Grid item md={6} xs={12}>
                                        <GlobitsTextField
                                            label={
                                                <span>
                                                    Loại hợp đồng
                                                    <span style={{ color: "red" }}> * </span>
                                                </span>
                                            }
                                            name="name"
                                        />
                                    </Grid>
                                    <Grid item md={6} xs={12}>
                                        <GlobitsTextField
                                            label={
                                                <span>
                                                    Ngôn ngữ
                                                    {/* <span style={{ color: "red" }}> * </span> */}
                                                </span>
                                            }
                                            name="languageKey"
                                        />
                                    </Grid>
                                    <Grid item md={6} xs={12}>
                                        <GlobitsTextField
                                            label={
                                                <span>
                                                    Thời hạn (Tháng)
                                                    {/* <span style={{ color: "red" }}> * </span> */}
                                                </span>
                                            }
                                            name="duration"
                                            type="number"
                                            disabled={values?.indefinite}
                                        />
                                    </Grid>
                                    <Grid item md={12} xs={12}>
                                        <GlobitsTextField label={"Mô tả"} name="description" multiline rows={4} />
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
                                        onClick={() => handleClose()}
                                    >
                                        {t("general.button.cancel")}
                                    </Button>
                                    <Button
                                        startIcon={<SaveIcon />}
                                        className="mr-0 btn btn-primary d-inline-flex"
                                        variant="contained"
                                        color="primary"
                                        type="submit"
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
