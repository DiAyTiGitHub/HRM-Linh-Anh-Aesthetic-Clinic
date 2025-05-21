import { observer } from "mobx-react";
import React, { useEffect } from "react";
import { useStore } from "../../stores";
import { Grid, Button } from "@material-ui/core";
import GlobitsBreadcrumb from "../../common/GlobitsBreadcrumb";
import DeleteIcon from "@material-ui/icons/Delete";
import AddIcon from "@material-ui/icons/Add";
import { useTranslation } from "react-i18next";
import ColorList from "./ColorList";
import GlobitsConfirmationDialog from "../../common/GlobitsConfirmationDialog";
import GlobitsSearchInput from "app/common/GlobitsSearchInput";
import ColorForm from "./ColorForm";

export default observer(function ColorIndex() {
  const { colorsStore } = useStore();
  const { t } = useTranslation();

  const {
    colorList,
    updatePageData,
    handleEditColor,
    shouldOpenEditorDialog,
    shouldOpenConfirmationDialog,
    shouldOpenConfirmationDeleteListDialog,
    handleClose,
    handleConfirmDelete,
    handleDeleteList,
    handleConfirmDeleteList,
    selectedColorList,
  } = colorsStore;

  useEffect(() => {
    updatePageData();
  }, [updatePageData]);

  return (
    <div className="content-index">
      <div className="index-breadcrumb">
        <GlobitsBreadcrumb routeSegments={[{ name: t("color.title") }]} />
      </div>

      <Grid className="index-card" container spacing={3}>
        <Grid item md={8} sm={12}>
          <Button
            className="mr-16 btn btn-info d-inline-flex"
            startIcon={<AddIcon />}
            variant="contained"
            onClick={() => {
              handleEditColor();
            }}
          >
            {t("general.button.add")}
          </Button>
          {selectedColorList.length > 0 && (
            <Button
              className="mr-36 btn btn-warning d-inline-flex"
              variant="contained"
              startIcon={<DeleteIcon />}
              onClick={() => {
                handleDeleteList();
              }}
            >
              {t("general.button.delete")}
            </Button>
          )}
        </Grid>
        <Grid item lg={4} md={4} sm={12} xs={12}>
          <GlobitsSearchInput search={updatePageData} />
        </Grid>

        <ColorForm open={shouldOpenEditorDialog} />

        <GlobitsConfirmationDialog
          open={shouldOpenConfirmationDialog}
          onConfirmDialogClose={handleClose}
          onYesClick={handleConfirmDelete}
          title={t("confirm_dialog.delete.title")}
          text={t("confirm_dialog.delete.text")}
          agree={t("confirm_dialog.delete.agree")}
          cancel={t("confirm_dialog.delete.cancel")}
        />

        <GlobitsConfirmationDialog
          open={shouldOpenConfirmationDeleteListDialog}
          onConfirmDialogClose={handleClose}
          onYesClick={handleConfirmDeleteList}
          title={t("confirm_dialog.delete_list.title")}
          text={t("confirm_dialog.delete_list.text")}
          agree={t("confirm_dialog.delete_list.agree")}
          cancel={t("confirm_dialog.delete_list.cancel")}
        />

        <Grid item xs={12}>
          <ColorList />
        </Grid>
      </Grid>
    </div>
  );
});
