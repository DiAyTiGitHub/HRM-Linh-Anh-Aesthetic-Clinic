import React, { useEffect, useState } from "react";
import {
  Dialog,
  IconButton,
  Typography,
  withStyles,
  Grid,
  DialogActions,
  Button,
  Icon,
  DialogContent,
} from "@material-ui/core";
import MuiDialogTitle from "@material-ui/core/DialogTitle";
import CloseIcon from "@material-ui/icons/Close";
import Draggable from "react-draggable";
import Paper from "@material-ui/core/Paper";
import { Formik, Form } from "formik";
import { useTranslation } from "react-i18next";
import GlobitsDateTimePicker from "app/common/form/GlobitsDateTimePicker";
import GlobitsTextField from "app/common/form/GlobitsTextField";
import GlobitsPagingAutocomplete from "app/common/form/GlobitsPagingAutocomplete";
import * as Yup from "yup";
import { pagingCertificates } from "app/views/Certificate/CertificateService";
import CancelIcon from '@material-ui/icons/Cancel';
import SaveIcon from '@material-ui/icons/Save';

function PaperComponent(props) {
  return (
    <div className="paper-container">
      <Draggable
        handle="#draggable-dialog-title"
        cancel={'[class*="MuiDialogContent-root"]'}
      >
        <Paper {...props} />
      </Draggable>
    </div>
  );
}

const styles = (theme) => ({
  root: {
    margin: 0,
    padding: theme.spacing(2),
  },
  closeButton: {
    position: "absolute",
    right: theme.spacing(1),
    top: theme.spacing(1),
    color: theme.palette.grey[500],
  },
});

const DialogTitle = withStyles(styles)((props) => {
  const { children, classes, onClose, ...other } = props;
  return (
    <MuiDialogTitle disableTypography className={classes.root} {...other}>
      <Typography variant="h6">{children}</Typography>
      {onClose ? (
        <IconButton
          aria-label="close"
          className={classes.closeButton}
          onClick={onClose}
        >
          <CloseIcon />
        </IconButton>
      ) : null}
    </MuiDialogTitle>
  );
});

export default function PersonCertificatePopup(props) {
  const { t } = useTranslation();
  const { open, handleClose, item, handleSubmit, editable } = props;

  const initialItem = {
    certificate: null,
    name: "",
    level: "",
    issueDate: null,
  };

  const validationSchema = Yup.object({
    certificate: Yup.object().required(t("validation.required")).nullable(),
    name: Yup.string().required(t("validation.required")).nullable(),
    level: Yup.string().required(t("validation.required")).nullable(),
    issueDate: Yup.date()
      .transform(function transformDate(castValue, originalValue) {
        return originalValue ? new Date(originalValue) : castValue;
      })
      .typeError("Ngày không đúng định dạng")
      .required(t("validation.required"))
      .nullable(),
  });

  const [formValues, setFormValues] = useState(null);

  useEffect(() => {
    if (item) {
      setFormValues({ ...item });
    } else {
      setFormValues({ ...initialItem });
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [item]);

  return (
    <Dialog
      className="dialog-container"
      open={open}
      PaperComponent={PaperComponent}
      fullWidth
      maxWidth="sm"
    >
      <DialogTitle className="dialog-header " id="draggable-dialog-title">
        <span className="mb-20 text-white">
          {editable ? t("general.button.add") : t("general.button.edit")}{" "}
          {t("trình độ học vấn")}
        </span>
      </DialogTitle>
      <IconButton
        style={{ position: "absolute", right: "0", top: "0" }}
        onClick={() => handleClose()}
      >
        <Icon className="text-white" title={t("general.close")}>
          close
        </Icon>
      </IconButton>
      <Formik
        initialValues={formValues}
        onSubmit={(values) => handleSubmit(values)}
        validationSchema={validationSchema}
      >
        {({ isSubmitting, values }) => (
          <Form autoComplete="off">
            <DialogContent
              className="dialog-body"
              style={{ maxHeight: "80vh", minWidth: "300px" }}
            >
              <Grid container spacing={2}>
                <Grid item xs={12}>
                  <GlobitsPagingAutocomplete
                    label={t("certificate.title")}
                    requiredLabel
                    name="certificate"
                    api={pagingCertificates}
                  />
                </Grid>

                <Grid item xs={12}>
                  <GlobitsTextField
                    name="name"
                    label={
                      <span>
                        {t("certificate.name")}
                        <span className="text-danger"> * </span>
                      </span>
                    }
                  />
                </Grid>
                <Grid item xs={12}>
                  <GlobitsTextField
                    name="level"
                    label={
                      <span>
                        {t("certificate.level")}
                        <span className="text-danger"> * </span>
                      </span>
                    }
                  />
                </Grid>
                <Grid item xs={12}>
                  <GlobitsDateTimePicker
                    name="issueDate"
                    label={
                      <span>
                        {t("certificate.issueDate")}
                        <span className="text-danger"> * </span>
                      </span>
                    }
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
                  startIcon={<CancelIcon className="mr-4" />}
                >
                  {t("general.button.close")}
                </Button>
                <Button
                  className="mr-0 btn btn-primary d-inline-flex"
                  variant="contained"
                  color="primary"
                  type="submit"
                  startIcon={<SaveIcon className="mr-4" />}
                  disabled={isSubmitting}
                >
                  {t("general.button.save")}
                </Button>
              </div>
            </DialogActions>
          </Form>
        )}
      </Formik>
    </Dialog>
  );
}
