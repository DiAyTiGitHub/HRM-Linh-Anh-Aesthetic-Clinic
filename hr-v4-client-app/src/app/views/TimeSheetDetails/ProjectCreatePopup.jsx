import React from "react";
import { Dialog, DialogTitle, Icon, IconButton } from "@material-ui/core";
import Draggable from "react-draggable";
import Paper from "@material-ui/core/Paper";
import { useTranslation } from "react-i18next";
import { useStore } from "../../stores";
import ProjectForm from "./ProjectForm";
import { observer } from "mobx-react";

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

export default observer(function ProjectCreateEditPopup(props) {
  const { timeSheetStore } = useStore();
  const { t } = useTranslation();
  const { handleClose, selectedItem } = timeSheetStore;
  const { open } = props;

  return (
    <Dialog
      className="dialog-container"
      open={open}
      PaperComponent={PaperComponent}
      fullWidth
      maxWidth="sm"
    >
      <DialogTitle
        className="dialog-header "
        style={{ cursor: "move" }}
        id="draggable-dialog-title"
      >
        <span className="mb-20 ">
          {" "}
          {(selectedItem?.id
            ? t("general.button.edit")
            : t("general.button.add")) +
            " " +
            t("project.title")}{" "}
        </span>
        <IconButton
          style={{ position: "absolute", right: "10px", top: "10px" }}
          onClick={() => handleClose()}
        >
          <Icon color="disabled" title={t("general.close")}>
            close
          </Icon>
        </IconButton>
      </DialogTitle>
      <ProjectForm />
    </Dialog>
  );
});
