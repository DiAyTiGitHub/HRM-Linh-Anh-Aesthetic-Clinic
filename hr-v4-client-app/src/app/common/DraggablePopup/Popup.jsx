import React from "react";
import { Dialog, DialogTitle, Icon, IconButton } from "@material-ui/core";
import { useTranslation } from "react-i18next";
import PaperComponent from "./PaperComponent";
import { observer } from "mobx-react";

function Popup({
  title,
  longTitle,
  directedTitle,
  open,
  handleClose,
  selectedItem,
  FormComponent,
}) {
  const formProps = {
    handleClose,
    selectedItem,
  };
  const { t } = useTranslation();

  return (
    <Dialog open={open} PaperComponent={PaperComponent} fullWidth maxWidth="sm">
      <DialogTitle
        className="dialog-header bgc-primary-d1"
        style={
          !longTitle
            ? { cursor: "move" }
            : { cursor: "move", paddingRight: "54px" }
        }
        id="draggable-dialog-title"
      >
        <span
          className="mb-20 text-white"
          // style={longTitle && { paddingRight: '20px' }}
        >
          {" "}
          {!directedTitle
            ? (selectedItem?.id
                ? t("general.button.edit")
                : t("general.button.add")) +
              " " +
              title
            : directedTitle}{" "}
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
      <FormComponent {...formProps} />
    </Dialog>
  );
}

export default observer(Popup);
