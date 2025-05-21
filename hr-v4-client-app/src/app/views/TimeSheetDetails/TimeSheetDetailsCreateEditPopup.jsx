import React from "react";
import { Dialog, DialogTitle, Icon, IconButton } from "@material-ui/core";
import Draggable from "react-draggable";
import Paper from "@material-ui/core/Paper";
import { useTranslation } from "react-i18next";
import { useStore } from "../../stores";
import TimeSheetDetailsForm from "./TimeSheetDetailsForm";
import { observer } from "mobx-react";
import { makeStyles } from "@material-ui/core";

const useStyles = makeStyles((theme) => ({
  root: {
    "& .MuiDialog-paper": {
      overflowY: "hidden !important",
    },
  },
}));

function PaperComponent(props) {
  return (
    <Draggable
      handle="#draggable-dialog-title"
      cancel={'[class*="MuiDialogContent-root"]'}
    >
      <Paper {...props} />
    </Draggable>
  );
}

export default observer(function TimeSheetDetailsCreateEditPopup(props) {
  const { timeSheetDetailsStore } = useStore();
  const classes = useStyles();
  const { t } = useTranslation();
  const { handleClose, selectedTimeSheetDetail } = timeSheetDetailsStore;
  const { open } = props;

  return (
    <Dialog
      className={`dialog-container ${classes.root}`}
      open={open}
      PaperComponent={PaperComponent}
      fullWidth
      maxWidth="lg"
    >
      <DialogTitle className="dialog-header" id="draggable-dialog-title">
        <span className="mb-20 ">
          {(selectedTimeSheetDetail?.id
            ? t("general.button.edit")
            : t("general.button.add")) +
            " " +
            t("timeSheet.title")}
        </span>
      </DialogTitle>
      <IconButton
        style={{ position: "absolute", right: "10px", top: "10px" }}
        onClick={() => handleClose()}
      >
        <Icon color="disabled" title={t("general.close")}>
          close
        </Icon>
      </IconButton>
      <TimeSheetDetailsForm />
    </Dialog>
  );
});
