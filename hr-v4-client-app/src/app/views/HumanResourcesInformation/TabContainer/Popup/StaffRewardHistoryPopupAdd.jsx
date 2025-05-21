import React, {useEffect, useState} from "react";
import {Button, DialogActions, DialogContent, Grid,} from "@material-ui/core";
import {Form, Formik} from "formik";
import {useTranslation} from "react-i18next";
import GlobitsDateTimePicker from "app/common/form/GlobitsDateTimePicker";
import GlobitsTextField from "app/common/form/GlobitsTextField";
import GlobitsPagingAutocomplete from "app/common/form/GlobitsPagingAutocomplete";
import {pagingRewards} from "app/views/Reward/RewardService";
import * as Yup from "yup";
import GlobitsPopupV2 from "../../../../common/GlobitsPopupV2";

export default function StaffRewardHistoryPopupAdd(props) {
    const {t} = useTranslation();
    const {open, handleClose, item, handleSubmit, editable} = props;

    const initialItem = {
        organizationName: "",
        rewardDate: null,
        rewardType: null,
    };

    const validationSchema = Yup.object({
        organizationName: Yup.string()
            .required(t("validation.required"))
            .nullable(),

        rewardDate: Yup.date()
            .transform(function transformDate(castValue, originalValue) {
                return originalValue ? new Date(originalValue) : castValue;
            })
            .required(t("validation.required"))
            .typeError("Ngày khen thưởng không đúng định dạng")
            .nullable(),

        rewardType: Yup.object().required(t("validation.required")).nullable(),
    });

    const [formValues, setFormValues] = useState(null);

    useEffect(() => {
        if (item) {
            setFormValues({...item});
        } else {
            setFormValues({...initialItem});
        }
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [item]);

    return (
        <GlobitsPopupV2
            size={"md"}
            open={open}
            onClosePopup={handleClose}
            noDialogContent
            title={(editable ? t("general.button.add") : t("general.button.edit")) + " " + t("rewardHistory.title")}
        >
            <Formik
                initialValues={formValues}
                onSubmit={(values) => handleSubmit(values)}
                validationSchema={validationSchema}
            >
                {({isSubmitting}) => (
                    <Form autoComplete="off">
                        <DialogContent
                            className="dialog-body"
                            style={{maxHeight: "80vh", minWidth: "300px"}}
                        >
                            <Grid container spacing={2}>
                                <Grid item xs={12} md={4}>
                                    <GlobitsTextField
                                        label={
                                            <span>
                        {t("rewardHistory.organizationName")}
                                                <span className="text-danger"> * </span>
                      </span>
                                        }
                                        name="organizationName"
                                    />
                                </Grid>
                                <Grid item xs={12} md={4}>
                                    <GlobitsDateTimePicker
                                        label={
                                            <span>
                        {t("rewardHistory.rewardDate")}
                                                <span className="text-danger"> * </span>
                      </span>
                                        }
                                        name="rewardDate"
                                        disableFuture={true}
                                    />
                                </Grid>
                                <Grid item xs={12} md={4}>
                                    <GlobitsPagingAutocomplete
                                        name="rewardType"
                                        label={t("rewardHistory.rewardType")}
                                        requiredLabel
                                        api={pagingRewards}
                                    />
                                </Grid>
                            </Grid>
                        </DialogContent>

                        <DialogActions className="dialog-footer p-0">
                            <div className="flex flex-space-between flex-middle">
                                <Button
                                    variant="contained"
                                    className="mr-12 btn btn-secondary d-inline-flex"
                                    color="secondary"
                                    onClick={() => {
                                        handleClose();
                                    }}
                                >
                                    {t("general.button.close")}
                                </Button>
                                <Button
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
                    </Form>
                )}
            </Formik>
        </GlobitsPopupV2>
    );
}
