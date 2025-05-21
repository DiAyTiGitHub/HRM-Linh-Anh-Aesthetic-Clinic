import React from "react";
import {
  Dialog,
  IconButton,
  Typography,
  withStyles,
} from "@material-ui/core";
import MuiDialogTitle from "@material-ui/core/DialogTitle";
import CloseIcon from "@material-ui/icons/Close";
import { useTranslation } from "react-i18next";
import SearchForm from "./SearchForm";
import { useStore } from "../../stores";
import { Form, Formik } from "formik";
import LocalConstants from "app/LocalConstants";
import { useHistory } from "react-router-dom";
import ConstantList from "../../appConfig";


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

export default function SearchPopup(props) {
  const { t } = useTranslation();
  const { open, handleClose } = props;
  const { administrativeUnitStore } = useStore();
  const { updatePageData, searchObject, handleSetSearchObject } =
    administrativeUnitStore;
  const history = useHistory();

  function hanledFormSubmit(searchObj) {
    const newSearchObject = {
      ...searchObj,
      ...LocalConstants.DEFAULT_PAGINATIONS,
    };
    handleSetSearchObject(newSearchObject);
    updatePageData();
    if (newSearchObject.org) {
      history.push(
        ConstantList.ROOT_PATH + "administrative-unit" + newSearchObject.org.id
      );
    } else {
      history.push(ConstantList.ROOT_PATH + "administrative-unit");
    }

    handleClose();
  }

  return (
    <Dialog
      className="dialog-container"
      open={open}
      onClose={handleClose}
      fullWidth
      maxWidth="sm"
    >
      <DialogTitle
        style={{ cursor: "move", backgroundColor: "rgba(0, 103, 120, 0.08)" }}
        id="draggable-dialog-title"
        onClose={handleClose}
      >
        <span className="mb-20" style={{ color: "#006778" }}>
          {" "}
          {t("general.button.advanceSearch")}{" "}
        </span>
      </DialogTitle>
      <Formik
        initialValues={searchObject}
        onSubmit={(values) => hanledFormSubmit(values)}
      >
        {() => (
          <Form autoComplete="off">
            <SearchForm />
          </Form>
        )}
      </Formik>
    </Dialog>
  );
}
