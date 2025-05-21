import React, { useState, useEffect } from "react";
import { Formik, Form } from "formik";
import { Grid, DialogActions, Button, DialogContent } from "@material-ui/core";
import SaveIcon from "@material-ui/icons/Save";
import BlockIcon from "@material-ui/icons/Block";
import { useTranslation } from "react-i18next";
import { useStore } from "../../stores";
import * as Yup from "yup";
import { observer } from "mobx-react";
import GlobitsTextField from "../../common/form/GlobitsTextField";
import GlobitsPopup from "app/common/GlobitsPopup";

export default observer(function LocationForm() {
  const { locationStore } = useStore();
  const { t } = useTranslation();
  const { handleClose, createLocation, editLocation, selectedLocation, shouldOpenEditorDialog } =
    locationStore;

  const [location, setLocation] = useState({
    id: "",
    code: "",
    name: "",
    longitude: null,
    latitude: null,
  });

  const validationSchema = Yup.object({
    code: Yup.string().required(t("validation.required")),
    name: Yup.string().required(t("validation.required")),
    longitude: Yup.number().required(t("validation.required")).nullable(),
    latitude: Yup.number().required(t("validation.required")).nullable(),
  });

  useEffect(() => {
    if (selectedLocation) setLocation(selectedLocation);
  }, [selectedLocation]);

  return (
    <GlobitsPopup
      open={shouldOpenEditorDialog}
      onClosePopup={handleClose}
      title={(selectedLocation?.id ? t("general.button.edit") : t("general.button.add")) + " " + t("location.title")}
      noDialogContent
    >
      <Formik
        validationSchema={validationSchema}
        enableReinitialize
        initialValues={location}
        onSubmit={(values) => values.id.length === 0 ? createLocation(values) : editLocation(values)}
      >
        {({ isSubmitting }) => (
          <Form autoComplete="off">
            <div className="dialog-body">
              <DialogContent className="o-hidden">
                <Grid container spacing={2}>
                  <Grid item md={12} sm={12} xs={12}>
                    <GlobitsTextField
                      validate
                      label={t("location.code")}
                      name="code" />
                  </Grid>
                  <Grid item md={12} sm={12} xs={12}>
                    <GlobitsTextField
                      validate
                      label={t("location.name")}
                      name="name" />
                  </Grid>
                  <Grid item md={12} sm={12} xs={12}>
                    <GlobitsTextField
                      validate
                      label={t("location.latitude")}
                      type="number"
                      name="latitude"
                    />
                  </Grid>
                  <Grid item md={12} sm={12} xs={12}>
                    <GlobitsTextField
                      validate
                      label={t("location.longitude")}
                      type="number"
                      name="longitude"
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
    </GlobitsPopup>
  );
});