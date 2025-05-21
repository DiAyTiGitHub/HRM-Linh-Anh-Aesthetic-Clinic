import React, {useEffect, useState} from "react";
import {Form, Formik} from "formik";
import {Button, DialogActions, DialogContent, Grid} from "@material-ui/core";
import SaveIcon from "@material-ui/icons/Save";
import BlockIcon from "@material-ui/icons/Block";
import {useTranslation} from "react-i18next";
import {useStore} from "../../../../../stores";
import * as Yup from "yup";
import {observer} from "mobx-react";
import GlobitsTextField from "../../../../../common/form/GlobitsTextField";
import GlobitsPopupV2 from "../../../../../common/GlobitsPopupV2";
import GlobitsAutocomplete from "../../../../../common/form/GlobitsAutocomplete";
import {CodePrefixes, EvaluationItemType} from "../../../../../LocalConstants";
import GlobitsSelectInput from "../../../../../common/form/GlobitsSelectInput";

export default observer(function EvaluationItemForm(props) {
    const {evaluationItemStore} = useStore();
    const {t} = useTranslation();
    const {
        handleClose,
        createEvaluationItem,
        editEvaluationItem,
        selectedEvaluationItem,
        intactEvaluationItem,
        autoGenCode
    } = evaluationItemStore;

    const [evaluationItem, setEvaluationItem] = useState(intactEvaluationItem);
    const autoGenCodeFunc = async () => {
        const code = await autoGenCode(CodePrefixes.TIEU_CHI_DANH_GIA);
        if (code) {
            // Tạo object mới để tránh thay đổi trực tiếp state
            const updated = {...intactEvaluationItem, ...{code: code}};
            setEvaluationItem(updated);
        }
    };
    const validationSchema = Yup.object({
        code: Yup.string().required(t("validation.required")),
        name: Yup.string().required(t("validation.required")),
    });

    useEffect(() => {
        if (selectedEvaluationItem) {
            setEvaluationItem(selectedEvaluationItem);
        } else {
            autoGenCodeFunc()
            setEvaluationItem(intactEvaluationItem)
        }
    }, [selectedEvaluationItem]);

    return (
        <GlobitsPopupV2
            size={"sm"}
            open={props.open}
            noDialogContent
            title={
                (selectedEvaluationItem?.id ? t("general.button.edit") : t("general.button.add")) +
                " " +
                t("evaluationItem.title")
            }
            onClosePopup={handleClose}
        >
            <Formik
                validationSchema={validationSchema}
                enableReinitialize
                initialValues={evaluationItem}
                onSubmit={(values) =>
                    values.id?.length === 0 || !values.id
                        ? createEvaluationItem(values)
                        : editEvaluationItem(values)
                }
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
                                                    {t("evaluationItem.name")}
                                                    <span style={{color: "red"}}> * </span>
                                                </span>
                                            }
                                            name="name"
                                        />
                                    </Grid>

                                    <Grid item md={12} sm={12} xs={12}>
                                        <GlobitsTextField
                                            label={
                                                <span>
                                                    {t("evaluationItem.code")}
                                                    <span style={{color: "red"}}> * </span>
                                                </span>
                                            }
                                            name="code"
                                        />
                                    </Grid>
                                    <Grid item md={12} sm={12} xs={12}>
                                        <GlobitsTextField
                                            label={t("evaluationItem.description")}
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
                                        onClick={handleClose}
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
