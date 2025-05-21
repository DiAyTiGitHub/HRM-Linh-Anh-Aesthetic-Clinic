import React, { useState, useEffect } from "react";
import { Formik, Form } from "formik";
import { observer } from "mobx-react";
import { Grid, DialogActions, Button, DialogContent } from "@material-ui/core";
import SaveIcon from "@material-ui/icons/Save";
import BlockIcon from "@material-ui/icons/Block";
import { useTranslation } from "react-i18next";
import { useStore } from "../../stores";
import * as Yup from "yup";

import GlobitsTextField from "../../common/form/GlobitsTextField";
import GlobitsPopup from "app/common/GlobitsPopup";

export default observer(function ProvinceForm() {
  const { provinceStore } = useStore();
  const { t } = useTranslation();
  const { handleClose, createProvince, editProvince, selectedProvince, shouldOpenEditorDialog } = provinceStore;

  const [province, setProvince] = useState({
    id: "",
    code: "",
    name: "",
  });

  const validationSchema = Yup.object({
    code: Yup.string().required(t("validation.code")),
    name: Yup.string().required(t("validation.name")),
  });

  useEffect(() => {
    if (selectedProvince) setProvince(selectedProvince);
  }, [selectedProvince]);

  return (
    <GlobitsPopup
      noDialogContent
      open={shouldOpenEditorDialog}
      onClosePopup={handleClose}
      title={(selectedProvince?.id ? t("general.button.edit") : t("general.button.add")) + " " + t("province.title")}
    >
      <Formik
        validationSchema={validationSchema}
        enableReinitialize
        initialValues={province}
        onSubmit={(values) =>
          values.id.length === 0 ? createProvince(values) : editProvince(values)
        }
      >
        {({ isSubmitting }) => (
          <Form autoComplete="off">
            <div className="dialog-body">
              <DialogContent className="o-hidden">
                <Grid container spacing={2}>
                  <Grid item md={12} sm={12} xs={12}>
                    <GlobitsTextField
                      validate
                      label={t("academic.code")}
                      name="code"
                    />
                  </Grid>
                  <Grid item md={12} sm={12} xs={12}>
                    <GlobitsTextField
                      validate
                      label={t("academic.name")}
                      name="name"
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
