import React, { memo } from "react";
import DoneIcon from '@material-ui/icons/Done';
import BlockIcon from '@material-ui/icons/Block';
import { Dialog, DialogTitle, Icon, IconButton, Button, DialogContent, DialogActions } from "@material-ui/core";
import "./SearchBox.scss";
import { useTranslation } from "react-i18next";
import { observer } from "mobx-react";
import PaperComponent from "./DraggablePopup/PaperComponent";

function GlobitsColorfulThemePopup(props) {
  const {
    open,
    handleClose,
    children,
    title = "Xác nhận",
    agreeText = "Xác nhận",
    cancelText = "Hủy bỏ",
    onConfirm,
    size = "md",
    hideFooter,
    hideScrollBody,
    popupId = "draggable-colorful-theme-dialog-title"
  } = props;

  const { t } = useTranslation();

  return (
    <Dialog
      maxWidth={size}
      fullWidth={true}
      open={open}
      onClose={handleClose}
      className="confirmDeletePopup"
      PaperComponent={(props) => <PaperComponent {...props} popupId={popupId} />} // Pass popupId here
    >
      <DialogTitle
        className={`bgc-lighter-dark-blue confirmDeletePopupTitle uppercase capitalize text-white py-8 px-12`}
        style={{ cursor: "move" }}
        id={popupId}
      >
        {title}
      </DialogTitle>

      <IconButton
        className="p-12"
        style={{ position: "absolute", right: "0px", color: "white" }}
        onClick={() => handleClose()}
      >
        <Icon title={t("general.close")}>
          close
        </Icon>
      </IconButton>

      <DialogContent
        className={`p-12`}
        style={hideScrollBody ? { overflowY: "auto !important", overflowX: "hidden !important" } : {}}
      >
        {children}
      </DialogContent>


      {!hideFooter && (
        <DialogActions className="confirmDeletePopupFooter">
          <div className="flex flex-space-between flex-middle">
            <Button
              startIcon={<BlockIcon className="mr-4" />}
              variant="contained"
              className="btn  bg-light-gray d-inline-flex mr-12"
              onClick={handleClose}
            >
              {cancelText}
            </Button>
            <Button
              className="btn btn-success d-inline-flex"
              variant="contained"
              startIcon={<DoneIcon className="mr-4" />}
              onClick={onConfirm}
            >
              {agreeText}
            </Button>
          </div>
        </DialogActions>
      )}

    </Dialog>
  );
}


export default memo(observer(GlobitsColorfulThemePopup));