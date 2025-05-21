import React from "react";
import {
  Dialog,
  DialogTitle,
  Icon,
  IconButton,
  DialogContent,
  Grid,
} from "@material-ui/core";
import Draggable from "react-draggable";
import Paper from "@material-ui/core/Paper";
import { useTranslation } from "react-i18next";
import { observer } from "mobx-react";
import { makeStyles } from "@material-ui/core";
import { useStore } from "app/stores";
import StaffPopupNotTimeKeepingListIn from "./StaffPopupNotTimeKeepingListIn";

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

export default observer(function StaffPopupNotTimeKeepingIn(props) {
  const { timeSheetStore } = useStore();
  const classes = useStyles();
  const { t } = useTranslation();
  const { handleClose_StaffNotTimeKeepingIn } = timeSheetStore;

  const { open, timeKeeping } = props;

  return (
    <Dialog
      className={`dialog-container ${classes.root}`}
      open={open}
      PaperComponent={PaperComponent}
      fullWidth
      maxWidth="lg"
    >
      <DialogTitle
        className="dialog-header bgc-primary"
        style={{ cursor: "move" }}
        id="draggable-dialog-title"
      >
        <span className="mb-20 text-white">
          Danh sách nhân viên chưa điểm danh trong ngày
        </span>
      </DialogTitle>
      <IconButton
        style={{ position: "absolute", right: "10px", top: "10px" }}
        onClick={() => handleClose_StaffNotTimeKeepingIn()}
      >
        <Icon color="disabled" title={t("general.close")}>
          close
        </Icon>
      </IconButton>
      <DialogContent>
        <Grid item xs={12}>
          <StaffPopupNotTimeKeepingListIn timeKeeping={timeKeeping} />
        </Grid>
      </DialogContent>
    </Dialog>
  );
});
