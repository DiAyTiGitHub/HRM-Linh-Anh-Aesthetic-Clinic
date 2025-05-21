import { observer } from "mobx-react";
import React, { useEffect } from "react";
import { useStore } from "../../stores";
import { Grid, Button } from "@material-ui/core";
import GlobitsBreadcrumb from "../../common/GlobitsBreadcrumb";
import DeleteIcon from "@material-ui/icons/Delete";
import AddIcon from "@material-ui/icons/Add";
import { useTranslation } from "react-i18next";
import ContractTypeList from "./ContractTypeList";
import GlobitsConfirmationDialog from "../../common/GlobitsConfirmationDialog";
import { useTheme } from "@material-ui/core/styles";
import useMediaQuery from "@material-ui/core/useMediaQuery";
import ContractTypeForm from "./ContractTypeForm";
import GlobitsSearchInput from "app/common/GlobitsSearchInput";

export default observer(function ContractTypeIndex() {
  const { contractTypeStore } = useStore();
  const { t } = useTranslation();

  const {
    selectedContractTypeList,
    updatePageData,
    handleEditContractType,
    shouldOpenEditorDialog,
    shouldOpenConfirmationDialog,
    shouldOpenConfirmationDeleteListDialog,
    handleClose,
    handleConfirmDelete,
    handleDeleteList,
    handleConfirmDeleteList,
  } = contractTypeStore;

  useEffect(() => {
    updatePageData();
  }, [updatePageData]);

  const theme = useTheme();
  const isMobile = useMediaQuery(theme.breakpoints.down("sm"));
  const isExtraSmall = useMediaQuery(theme.breakpoints.down("xs"));

  return (
    <div className="content-index">
      <div className="index-breadcrumb">
        <GlobitsBreadcrumb routeSegments={[{ name: t("navigation.salary") }, { name: t("contractType.title") }]} />

      </div>

      <Grid className="index-card" container spacing={2}>
        {
          !isExtraSmall && (
            <>
              <Grid item lg={6} md={6} sm={4} xs={4}>
                <Button
                  className="mr-16 btn btn-info d-inline-flex"
                  startIcon={<AddIcon />}
                  variant="contained"
                  onClick={() => {
                    handleEditContractType();
                  }}
                >
                  {!isMobile && t("general.button.add")}
                </Button>
                {selectedContractTypeList.length > 0 && (
                  <Button
                    className="mr-36 btn btn-warning d-inline-flex"
                    variant="contained"
                    startIcon={<DeleteIcon />}
                    onClick={() => {
                      handleDeleteList();
                    }}
                  >
                    {!isMobile && t("general.button.delete")}
                  </Button>
                )}
              </Grid>
              <Grid item lg={6} md={6} sm={8} xs={8}>
                <GlobitsSearchInput search={updatePageData} />
              </Grid>
            </>
          )
        }

        {
          isExtraSmall && (
            <>
              <Grid item sm={6} xs={6}>
                <Button
                  className="btn btn-info d-inline-flex"
                  startIcon={<AddIcon />}
                  variant="contained"
                  onClick={() => {
                    handleEditContractType();
                  }}
                  fullWidth
                >
                  {t("general.button.add")}
                </Button>
              </Grid>
              <Grid item sm={6} xs={6}>
                {selectedContractTypeList.length > 0 && (
                  <Button
                    className="btn btn-warning d-inline-flex"
                    variant="contained"
                    startIcon={<DeleteIcon />}
                    onClick={() => {
                      handleDeleteList();
                    }}
                    fullWidth
                  >
                    {t("general.button.delete")}
                  </Button>
                )}
              </Grid>
              <Grid item sm={12} xs={12}>
                <GlobitsSearchInput search={updatePageData} />
              </Grid>
            </>
          )
        }

        <ContractTypeForm open={shouldOpenEditorDialog} />

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
          <ContractTypeList />
        </Grid>
      </Grid>
    </div>
  );
});
