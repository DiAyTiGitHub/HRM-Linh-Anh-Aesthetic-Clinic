import { Button, DialogActions, DialogContent, Grid } from "@material-ui/core";
import BlockIcon from "@material-ui/icons/Block";
import SaveIcon from "@material-ui/icons/Save";
import { Form, Formik } from "formik";
import { observer } from "mobx-react";
import { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import GlobitsPagingAutocompleteV2 from "../../../common/form/GlobitsPagingAutocompleteV2";
import GlobitsPopupV2 from "../../../common/GlobitsPopupV2";
import { useStore } from "../../../stores";
import { pagingStaff } from "../../HumanResourcesInformation/StaffService";
import { pagingKPI } from "../KPI/KPIService";
import KPIResultItemSection from "./KPIResultItemSection";

const defaultKPIResult = {
    id: "",
    staff: null, //object
    kpi: null, //object
    kpiResultItems: [
        {
            value: null,
            kpiItem: null, //object
        }
    ]
};

export default observer(function KPIResultForm({handleAfterSubmit, updateListOnClose, open}) {
    const {KPIResultStore} = useStore();
    const {t} = useTranslation();

    const {
        saveOrUpdateKPIResult,
        handleClose,
        selectedKpiResult
    } = KPIResultStore;

    const [KPIResult, setKPIResult] = useState(defaultKPIResult);

    // const validationSchema = Yup.object({
    //     staff: Yup.object().nullable().required(t("validation.value")),
    //     kpi: Yup.object().nullable().required(t("validation.value")),
    //     kpiResultItems: Yup.array().of(
    //         Yup.object({
    //             value: Yup.number()
    //                 .nullable()
    //         })
    //     ),
    // });

    useEffect(() => {
        if (selectedKpiResult) setKPIResult(selectedKpiResult);
        else setKPIResult(defaultKPIResult);
    }, [selectedKpiResult]);

    function handleFormSubmit(values) {
        saveOrUpdateKPIResult(values);
    }

    return (
        <GlobitsPopupV2 open={open} size='md' noDialogContent
                      title={(selectedKpiResult?.id ? t("general.button.edit") : t("general.button.add")) + " " + t("navigation.kpi-result")}
                      onClosePopup={handleClose}>
            <Formik
                // validationSchema={validationSchema}
                enableReinitialize
                initialValues={KPIResult}
                onSubmit={(values) => handleFormSubmit(values)}>
                {({isSubmitting}) => (
                    <Form autoComplete='off'>
                        <div className='dialog-body'>
                            <DialogContent className='o-hidden'>
                                <Grid container spacing={2}>
                                    <Grid item xs={6}>
                                        <GlobitsPagingAutocompleteV2
                                            name="staff"
                                            label={"Nhân viên"}
                                            api={pagingStaff}
                                            getOptionLabel={(option) => `${option?.displayName}`}
                                            required
                                        />
                                    </Grid>

                                    <Grid item xs={6}>
                                        <GlobitsPagingAutocompleteV2
                                            name="kpi"
                                            label={"KPI"}
                                            api={pagingKPI}
                                            getOptionLabel={(option) => `${option?.name}`}
                                            required
                                        />
                                    </Grid>

                                    <Grid item xs={12}>
                                        <KPIResultItemSection/>
                                    </Grid>
                                </Grid>
                            </DialogContent>
                        </div>
                        <div className='dialog-footer'>
                            <DialogActions className='p-0'>
                                <div className='flex flex-space-between flex-middle'>
                                    <Button startIcon={<BlockIcon/>} variant='contained'
                                            className='mr-12 btn btn-secondary d-inline-flex' color='secondary'
                                            onClick={() => handleClose()}>
                                        {t("general.button.cancel")}
                                    </Button>
                                    <Button startIcon={<SaveIcon/>} className='mr-0 btn btn-primary d-inline-flex'
                                            variant='contained' color='primary' type='submit' disabled={isSubmitting}>
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
})
;
