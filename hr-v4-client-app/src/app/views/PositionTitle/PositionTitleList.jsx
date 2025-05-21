import React, { memo } from "react";
import GlobitsTable from "../../common/GlobitsTable";
import { useStore } from "../../stores";
import { useTranslation } from "react-i18next";
import { IconButton, Icon } from "@material-ui/core";
import Config from "../../common/GlobitsConfigConst";
import { observer } from "mobx-react";
import GlobitsConfirmationDialog from "app/common/GlobitsConfirmationDialog";
import GlobitsBreadcrumb from "app/common/GlobitsBreadcrumb";
import { Grid, Button } from "@material-ui/core";
import { useTheme } from "@material-ui/core/styles";
import useMediaQuery from "@material-ui/core/useMediaQuery";
import AddIcon from "@material-ui/icons/Add";
import NoteIcon from "@material-ui/icons/Note";
import ImportExcelDialogPositionTitle from "./ImportExcelDialogPositionTitle";
import DeleteIcon from "@material-ui/icons/Delete";
import GlobitsSearchInput from "app/common/GlobitsSearchInput";
import PositionTitleForm from "./PositionTitleForm";

function MaterialButton(props) {
  const { item } = props;
  return (
    <div>
      <IconButton size="small" onClick={() => props.onSelect(item, 0)}>
        <Icon fontSize="small" color="primary">
          edit
        </Icon>
      </IconButton>
      <IconButton size="small" onClick={() => props.onSelect(item, 1)}>
        <Icon fontSize="small" color="secondary">
          delete
        </Icon>
      </IconButton>
    </div>
  );
}

function PositionTitleList() {
  const { positionTitleStore } = useStore();
  const { t } = useTranslation();

  const {
    positionTitleList,
    totalPages,
    totalElements,
    rowsPerPage,
    page,
    handleChangePage,
    setRowsPerPage,
    handleDelete,
    handleEditPosition,
    handleSelectListPositionTitle,
    updatePageData,
    selectedPositionTitleList,
    shouldOpenConfirmationDialog,
    handleClose,
    handleConfirmDelete,
    handleDeleteList,
    shouldOpenImportExcelDialog,
    importExcel,
  } = positionTitleStore;

  let columns = [
    {
      title: t("general.action"),
      minWidth: "100px",
      ...Config.tableCellConfig,
      render: (rowData) => (
        <MaterialButton
          item={rowData}
          onSelect={(rowData, method) => {
            if (method === 0) {
              handleEditPosition(rowData.id);
            } else if (method === 1) {
              handleDelete(rowData.id);
            } else {
              alert("Call Selected Here:" + rowData.id);
            }
          }}
        />
      ),
    },
    {
      title: t("position.code"),
      minWidth: "100px",
      field: "code",
      ...Config.tableCellConfig,
    },
    { title: t("position.name"), field: "name", minWidth: "200px", ...Config.tableCellConfig },
    {
      title: t("position.description"),
      minWidth: "150px",
      field: "description",
      ...Config.tableCellConfig,
    },
  ];

  const theme = useTheme();
  const isMobile = useMediaQuery(theme.breakpoints.down("sm"));
  const isExtraSmall = useMediaQuery(theme.breakpoints.down("xs"));

  return (
    <div className="content-index">
      <div className="index-breadcrumb">
        <GlobitsBreadcrumb routeSegments={[{ name: t("position.title") }]} />
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
                  onClick={handleEditPosition}
                >
                  {!isMobile && t("general.button.add")}
                </Button>
                <Button
                  className="mr-16 btn btn-danger d-inline-flex"
                  startIcon={<NoteIcon />}
                  variant="contained"
                  onClick={importExcel}
                >
                  {!isMobile && t("general.button.importExcel")}
                </Button>

                <ImportExcelDialogPositionTitle
                  t={t}
                  handleClose={handleClose}
                  open={shouldOpenImportExcelDialog}
                />

                {selectedPositionTitleList.length > 0 && (
                  <Button
                    className="mr-36 btn btn-warning d-inline-flex"
                    variant="contained"
                    startIcon={<DeleteIcon />}
                    onClick={handleDeleteList}
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
              <Grid item sm={4} xs={4}>
                <Button
                  className="btn btn-info d-inline-flex"
                  startIcon={<AddIcon />}
                  variant="contained"
                  onClick={() => {
                    handleEditPosition();
                  }}
                  fullWidth
                >
                  {!isMobile && t("general.button.add")}
                </Button>
              </Grid>
              <Grid item sm={4} xs={4}>
                <Button
                  className="btn btn-danger d-inline-flex"
                  startIcon={<NoteIcon />}
                  variant="contained"
                  onClick={importExcel}
                  fullWidth
                >
                  {!isMobile && t("general.button.importExcel")}
                </Button>
                <ImportExcelDialogPositionTitle
                  t={t}
                  handleClose={handleClose}
                  open={shouldOpenImportExcelDialog}
                />
              </Grid>
              <Grid item sm={4} xs={4}>
              </Grid>
              <Grid item sm={12} xs={12}>
                <GlobitsSearchInput search={updatePageData} />
              </Grid>
            </>
          )
        }
        <PositionTitleForm />
        <GlobitsConfirmationDialog
          open={shouldOpenConfirmationDialog}
          onConfirmDialogClose={handleClose}
          onYesClick={handleConfirmDelete}
          title={t("confirm_dialog.delete.title")}
          text={t("confirm_dialog.delete.text")}
          agree={t("confirm_dialog.delete.agree")}
          cancel={t("confirm_dialog.delete.cancel")}
        />

        <Grid item xs={12}>
          <GlobitsTable
            handleSelectList={handleSelectListPositionTitle}
            data={positionTitleList}
            columns={columns}
            totalPages={totalPages}
            handleChangePage={handleChangePage}
            setRowsPerPage={setRowsPerPage}
            pageSize={rowsPerPage}
            pageSizeOption={[10, 25, 50]}
            totalElements={totalElements}
            page={page}
          />
        </Grid>
      </Grid>
    </div>
  );
}

export default memo(observer(PositionTitleList));
