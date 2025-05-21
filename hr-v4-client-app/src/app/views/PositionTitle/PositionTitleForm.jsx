import React from "react";
import { Formik, Form } from "formik";
import {
  Grid,
  makeStyles,
  DialogActions,
  Button,
  DialogContent,
} from "@material-ui/core";
import SaveIcon from "@material-ui/icons/Save";
import BlockIcon from "@material-ui/icons/Block";
import { useTranslation } from "react-i18next";
import { useStore } from "../../stores";
import { observer } from "mobx-react";
import * as Yup from "yup";

import GlobitsTextField from "../../common/form/GlobitsTextField";
import GlobitsPopup from "app/common/GlobitsPopup";

const useStyles = makeStyles((theme) => ({
  gridContainerForm: {
    maxHeight: "68vh",
    overflowY: "auto",
    marginBottom: 10,
  },
  textField: {
    width: "100%",
    margin: "10px 0px !important",
  },
}));

export default observer(function PositionTitleForm() {
  const classes = useStyles();
  const { positionTitleStore } = useStore();
  const { t } = useTranslation();
  const { handleClose, createPosition, editPosition, selectedPositionTitle, shouldOpenEditorDialog } = positionTitleStore;

  const validationSchema = Yup.object({
    code: Yup.string().required(t("validation.code")),
    name: Yup.string().required(t("validation.name")),
  });

  return (
    <GlobitsPopup
      open={shouldOpenEditorDialog}
      onClosePopup={handleClose}
      title={(selectedPositionTitle?.id ? t("general.button.edit") : t("general.button.add")) + " " + t("position.title")}
      noDialogContent
    >
      <Formik
        validationSchema={validationSchema}
        enableReinitialize
        initialValues={selectedPositionTitle}
        onSubmit={(values) =>
          values.id.length === 0 ? createPosition(values) : editPosition(values)
        }
      >
        {({ isSubmitting }) => (
          <Form autoComplete="off">
            <div className="dialog-body">
              <DialogContent className="o-hidden">
                <Grid container className={classes.gridContainerForm} spacing={2}>
                  <Grid item md={12} sm={12} xs={12}>
                    <GlobitsTextField
                      label={
                        <span>
                          {t("position.code")}
                          <span style={{ color: 'red' }}> * </span>
                        </span>
                      }
                      name="code" />
                  </Grid>
                  <Grid item md={12} sm={12} xs={12}>
                    <GlobitsTextField
                      label={
                        <span>
                          {t("position.name")}
                          <span style={{ color: 'red' }}> * </span>
                        </span>
                      }
                      name="name" />
                  </Grid>
                  <Grid item md={12} sm={12} xs={12}>
                    <GlobitsTextField
                      label={t("position.description")}
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
