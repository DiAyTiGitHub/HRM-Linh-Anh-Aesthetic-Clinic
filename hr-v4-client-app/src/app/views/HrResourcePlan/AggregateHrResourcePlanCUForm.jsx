import { Button, DialogActions, DialogContent, Grid } from "@material-ui/core";
import GlobitsPopupV2 from "app/common/GlobitsPopupV2";
import GlobitsTextField from "app/common/form/GlobitsTextField";
import { Form, Formik } from "formik";
import { observer } from "mobx-react";
import { memo, useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import * as Yup from "yup";
import { useStore } from "../../stores";
import SelectHrResourceComponent from "./components/SelectHrResourceComponent";
import GlobitsDateTimePicker from "app/common/form/GlobitsDateTimePicker";

function AggregateHrResourcePlanCUForm() {
    const { hrResourcePlanStore } = useStore();
    const { t } = useTranslation();

    const {
        handleClose,
        saveHrResourcePlan,
        pagingAggregateHrResourcePlan,
        selectedHrResourcePlan,
        openAggregateCreateEditPopup,
    } = hrResourcePlanStore;

    const validationSchema = Yup.object({
        code: Yup.string().required(t("validation.code")).nullable(),
        name: Yup.string().required(t("validation.name")).nullable(),
    });

    async function handleSaveForm(values) {
        await saveHrResourcePlan(values);
        await pagingAggregateHrResourcePlan();
    }

    const [initialValues, setInitialValues] = useState(selectedHrResourcePlan);

    useEffect(
        function () {
            setInitialValues({
                ...selectedHrResourcePlan,
            });
        },
        [selectedHrResourcePlan, selectedHrResourcePlan?.id]
    );

    return (
        <GlobitsPopupV2
            size='lg'
            scroll={"body"}
            open={openAggregateCreateEditPopup}
            // open={true}
            noDialogContent
            title={
                (selectedHrResourcePlan?.id ? t("general.button.edit") : t("general.button.add")) +
                " " +
                t("navigation.hrResourcePlan.title")
            }
            onClosePopup={handleClose}>
            <Formik
                validationSchema={validationSchema}
                enableReinitialize
                initialValues={initialValues}
                onSubmit={handleSaveForm}>
                {({ isSubmitting, values, setFieldValue, initialValues }) => {
                    return (
                        <Form autoComplete='off'>
                            <div className='dialog-body'>
                                <DialogContent className='o-hidden p-12'>
                                    <Grid container spacing={2}>
                                        <Grid item xs={4}>
                                            <GlobitsTextField validate label='Tên định biên' name='name' />
                                        </Grid>

                                        <Grid item xs={4}>
                                            <GlobitsTextField validate label='Mã định biên' name='code' />
                                        </Grid>

                                        <Grid item md={4}>
                                            <GlobitsDateTimePicker name='planDate' label='Ngày tổng hợp định biên' />
                                        </Grid>
                                        <Grid item xs={12}>
                                            <GlobitsTextField
                                                isTextArea={true}
                                                multiline
                                                minRows={2}
                                                label={t("Mô tả")}
                                                name='description'
                                            />
                                        </Grid>
                                        <Grid item xs={12}>
                                            <SelectHrResourceComponent />
                                        </Grid>
                                    </Grid>
                                </DialogContent>

                                <DialogActions className='dialog-footer px-12'>
                                    <div className='flex flex-space-between flex-middle'>
                                        <Button
                                            variant='contained'
                                            className='mr-12 btn btn-secondary d-inline-flex'
                                            color='secondary'
                                            // disabled={isSubmitting}
                                            onClick={handleClose}>
                                            {t("general.button.close")}
                                        </Button>
                                        <Button
                                            className='mr-0 btn btn-primary d-inline-flex'
                                            variant='contained'
                                            color='primary'
                                            type='submit'
                                            // disabled={isSubmitting}\
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

export default memo(observer(AggregateHrResourcePlanCUForm));
