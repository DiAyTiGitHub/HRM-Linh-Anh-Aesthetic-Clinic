import { Button, DialogActions, DialogContent, Grid } from "@material-ui/core";
import BlockIcon from "@material-ui/icons/Block";
import SaveIcon from "@material-ui/icons/Save";
import { observer } from "mobx-react";
import { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import * as Yup from "yup";
import GlobitsTextField from "../../../common/form/GlobitsTextField";
import { useStore } from "../../../stores";
import { Form, Formik } from "formik";
import KPIItemSection from "./KPIItemSection";
import GlobitsPopupV2 from "../../../common/GlobitsPopupV2";

const defaultKPI = {
  id: "",
  name: "",
  code: "",
  // kpiItems:[
  //   {
  //     name: "",
  //     code: "",
  //     weight: "",
  //     usedForSalary: ""
  //   }
  // ]
};

export default observer(function KPIForm({ handleAfterSubmit, updateListOnClose, open }) {
  const { KPIStore } = useStore();
  const { t } = useTranslation();
  const { handleClose, saveOrUpdateKpi, selectedKpi,handleSelectKpi } = KPIStore;

  const [kpi, setKpi] = useState(defaultKPI);

  const validationSchema = Yup.object({
    name: Yup.string()
        .trim()
        .required(t("validation.required")),

    code: Yup.string()
        .trim()
        .required(t("validation.required")),

    kpiItems: Yup.array().of(
        Yup.object({
          name: Yup.string()
              .trim()
              .required(t("validation.required")),

          code: Yup.string()
              .trim()
              .required(t("validation.required")),

          weight: Yup.number()
              .required(t("validation.required")),

          usedForSalary: Yup.boolean()
              .required(t("validation.required")),
        })
    ),
  });


  useEffect(() => {
    if (selectedKpi) setKpi(selectedKpi);
    else setKpi(defaultKPI);
  }, [selectedKpi]);

  function handleFormSubmit(kpi) {
    saveOrUpdateKpi(kpi);
  }

  return (
    <GlobitsPopupV2
        open={open}
        size='md'
        noDialogContent
        title={(selectedKpi?.id ? t("general.button.edit") : t("general.button.add")) + " " + t("navigation.kpi")}
        onClosePopup={handleClose}>
      <Formik validationSchema={validationSchema} enableReinitialize initialValues={kpi} onSubmit={(values) => handleFormSubmit(values)}>
        {({ isSubmitting }) => (
          <Form autoComplete='off'>
            <div className='dialog-body'>
              <DialogContent className='o-hidden'>
                <Grid container spacing={2}>
                  <Grid item xs={6}>
                    <GlobitsTextField label={t("kpi.name")} name='name' required />
                  </Grid>

                  <Grid item xs={6}>
                    <GlobitsTextField label={t("kpi.code")} name='code' required />
                  </Grid>

                  <Grid item xs={12}>
                    <KPIItemSection/>
                  </Grid>
                </Grid>
              </DialogContent>
            </div>
            <div className='dialog-footer'>
              <DialogActions className='p-0'>
                <div className='flex flex-space-between flex-middle'>
                  <Button startIcon={<BlockIcon />} variant='contained' className='mr-12 btn btn-secondary d-inline-flex' color='secondary' onClick={() => handleClose()}>
                    {t("general.button.cancel")}
                  </Button>
                  <Button startIcon={<SaveIcon />} className='mr-0 btn btn-primary d-inline-flex' variant='contained' color='primary' type='submit' disabled={isSubmitting}>
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
