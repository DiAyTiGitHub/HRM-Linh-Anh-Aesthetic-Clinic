import React, {memo} from "react";
import {Form, Formik} from "formik";
import {Button, DialogActions, DialogContent} from "@material-ui/core";
import SaveIcon from "@material-ui/icons/Save";
import BlockIcon from "@material-ui/icons/Block";
import {useTranslation} from "react-i18next";
import {useStore} from "../../stores";
import {observer} from "mobx-react";
import * as Yup from "yup";
import GlobitsTextField from "app/common/form/GlobitsTextField";
import GlobitsPopupV2 from "../../common/GlobitsPopupV2";

function WorkingStatusForm() {
    const {workingStatusStore} = useStore();
    const {t} = useTranslation();
    const {selectedWorkingStatusEdit, onSaveWorkingStatus, onClosePopup} = workingStatusStore;

    const validationSchema = Yup.object({
        code: Yup.string().required(t("validation.required")).nullable(),
        name: Yup.string().required(t("validation.required")).nullable(),
    });

    return (
        <GlobitsPopupV2
            open={Boolean(selectedWorkingStatusEdit)}
            noDialogContent
            title={(selectedWorkingStatusEdit?.id ? t("general.button.edit") : t("general.button.add")) + ' ' + t("workingStatus.title")}
            onClosePopup={onClosePopup}
            size="xs"
        >
            <Formik
                validationSchema={validationSchema}
                enableReinitialize
                initialValues={selectedWorkingStatusEdit}
                onSubmit={onSaveWorkingStatus}
            >
                {({isSubmitting}) => (
                    <Form autoComplete="off">
                        <DialogContent className="o-hidden dialog-body">
                            <div>
                                <GlobitsTextField validate label={t("workingStatus.code")} name="code" notDelay/>
                            </div>

                            <div className="mt-10">
                                <GlobitsTextField validate label={t("workingStatus.name")} name="name" notDelay/>
                            </div>

                            <div className="mt-10">
                                <GlobitsTextField label={t("workingStatus.statusValue")} name="statusValue"
                                                  type="number" notDelay/>
                            </div>
                        </DialogContent>

                        <DialogActions className="dialog-footer p-0">
                            <div className="flex flex-space-between flex-middle">
                                <Button
                                    startIcon={<BlockIcon/>}
                                    className="mr-12 btn btn-secondary d-inline-flex"
                                    onClick={() => onClosePopup()}
                                >
                                    {t("general.button.cancel")}
                                </Button>
                                <Button
                                    startIcon={<SaveIcon/>}
                                    className="mr-0 btn btn-primary d-inline-flex"
                                    type="submit"
                                    disabled={isSubmitting}
                                >
                                    {t("general.button.save")}
                                </Button>
                            </div>
                        </DialogActions>
                    </Form>
                )}
            </Formik>
        </GlobitsPopupV2>
    );
};

export default memo(observer(WorkingStatusForm))