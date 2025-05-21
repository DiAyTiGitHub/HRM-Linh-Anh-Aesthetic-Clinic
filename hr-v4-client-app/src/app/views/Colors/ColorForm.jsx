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

export default observer(function ColorForm(props) {
  const { colorsStore } = useStore();
  const { t } = useTranslation();
  const { handleClose, createColor, editColor, selectedColor } = colorsStore;

  const [color, setColor] = useState({
    id: "",
    code: "",
    value: "",
  });

  const validationSchema = Yup.object({
    code: Yup.string().required(t("validation.code")),
    value: Yup.string().required(t("validation.value")),
  });

  useEffect(() => {
    if (selectedColor) setColor(selectedColor);
  }, [selectedColor]);

  useEffect(() => {
    console.log(color);
  }, [color]);

  function hanledFormSubmit(color) {
    if (color.id.length === 0) {
      createColor(color);
    } else {
      editColor(color);
    }
  }

  return (
    <GlobitsPopup
      open={props.open}
      noDialogContent
      title={(selectedColor?.id ? t("general.button.edit") : t("general.button.add")) + ' ' + t("color.title")}
      onClosePopup={handleClose}
    >
      <Formik
        validationSchema={validationSchema}
        enableReinitialize
        initialValues={color}
        onSubmit={(values) => hanledFormSubmit(values)}
      >
        {({ isSubmitting }) => (
          <Form autoComplete="off">
            <div className="dialog-body">
              <DialogContent className="o-hidden">
                <Grid container spacing={2}>
                  <Grid item md={12} sm={12} xs={12}>
                    <GlobitsTextField
                      label={
                        <span>
                          <span style={{ color: 'red' }}> * </span>
                          {t("color.code")}
                        </span>
                      }
                      name="code" />
                  </Grid>
                  <Grid item md={12} sm={12} xs={12}>
                    <GlobitsTextField
                      label={
                        <span>
                          <span style={{ color: 'red' }}> * </span>
                          {t("color.value")}
                        </span>
                      }
                      name="value" />
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
