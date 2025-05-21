import React, { memo, useEffect, useState } from "react";
import { Form, Formik } from "formik";
import { Button, DialogActions, DialogContent, Grid } from "@material-ui/core";
import SaveIcon from "@material-ui/icons/Save";
import BlockIcon from "@material-ui/icons/Block";
import { useTranslation } from "react-i18next";
import { useStore } from "../../../stores";
import * as Yup from "yup";
import { observer } from "mobx-react";
import GlobitsTextField from "../../../common/form/GlobitsTextField";
import GlobitsPopupV2 from "app/common/GlobitsPopupV2";
import GlobitsPagingAutocompleteV2 from "app/common/form/GlobitsPagingAutocompleteV2";
import { pagingSalaryItem } from "../SalaryItemV2/SalaryItemV2Service";

function SalaryAutoMapForm(props) {
    const {
        salaryAutoMapStore,
        hrRoleUtilsStore

    } = useStore();

    const { t } = useTranslation();

    const {
        handleClose,
        saveOrUpdateSalaryAutoMap,
        selectedSalaryAutoMap,
    } = salaryAutoMapStore;

    const {
        isAdmin,
        isManager,
        isStaffView
    } = hrRoleUtilsStore;

    const [salaryAutoMap, setSalaryAutoMap] = useState({
        id: null,
        salaryItem: null,
        salaryAutoMapField: null,
        description: null
    });

    const validationSchema = Yup.object({
        salaryItems: Yup.array()
            .of(Yup.object().nullable()),
            // .min(1, t("validation.required"))
            // .required(t("validation.required")),
        salaryAutoMapField: Yup.string().required(t("validation.required")).nullable(),
    });

    useEffect(() => {
        if (selectedSalaryAutoMap) setSalaryAutoMap(selectedSalaryAutoMap);
    }, [selectedSalaryAutoMap]);


    return (
        <GlobitsPopupV2
            size="xs"
            scroll={"body"}
            open={props.open}
            noDialogContent
            title={'Cài đặt ' + t("salaryAutoMap.title")}
            onClosePopup={handleClose}
        >
            <Formik
                validationSchema={validationSchema}
                enableReinitialize
                initialValues={salaryAutoMap}
                onSubmit={(values) => saveOrUpdateSalaryAutoMap(values)}
            >
                {({ isSubmitting }) => (
                    <Form autoComplete="off">
                        <DialogContent className="o-hidden p-12">
                            <Grid container spacing={2}>

                                <Grid item xs={12} sm={12}>
                                    <GlobitsTextField
                                        label={t("salaryAutoMap.salaryAutoMapField")}
                                        name="salaryAutoMapField"
                                        disabled
                                        readOnly
                                    />
                                </Grid>

                                <Grid item xs={12} sm={12}>
                                    <GlobitsTextField
                                        label={t("salaryAutoMap.description")}
                                        name="description"
                                        multiline
                                        rows={3}
                                    />
                                </Grid>

                                <Grid item xs={12}>
                                    <GlobitsPagingAutocompleteV2
                                        label={t("salaryAutoMap.salaryItem")}
                                        name="salaryItems"
                                        multiple
                                        multiline
                                        rows={3}
                                        api={pagingSalaryItem}
                                        getOptionLabel={(option) =>
                                            option?.code && option?.name
                                                ? `${option.name} - ${option.code}`
                                                : option?.code || option?.name || ''
                                        }
                                    // readOnly={readOnly}
                                    />
                                </Grid>

                            </Grid>
                        </DialogContent>

                        <DialogActions className="dialog-footer px-12">
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

                                {/* {(isAdmin || isManager) && ( */}
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
                                {/* )} */}
                            </div>
                        </DialogActions>
                    </Form>
                )}
            </Formik>
        </GlobitsPopupV2>
    );
}

export default memo(observer(SalaryAutoMapForm));
