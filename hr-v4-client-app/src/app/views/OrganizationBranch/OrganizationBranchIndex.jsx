import { observer } from "mobx-react";
import React, { memo, useEffect } from "react";
import { useStore } from "../../stores";
import { Grid, Button, IconButton } from "@material-ui/core";
import GlobitsBreadcrumb from "../../common/GlobitsBreadcrumb";
import DeleteIcon from "@material-ui/icons/Delete";
import AddIcon from "@material-ui/icons/Add";
import { useTranslation } from "react-i18next";
import GlobitsConfirmationDialog from "../../common/GlobitsConfirmationDialog";
import NoteIcon from "@material-ui/icons/Note";
import SearchIcon from '@material-ui/icons/Search';
import { Form, Formik } from "formik";
import GlobitsTextField from "app/common/form/GlobitsTextField";
import OrganizationBranchCUForm from "./OrganizationBranchCUForm";
import OrganizationBranchList from "./OrganizationBranchList";

function OrganizationBranchIndex() {
  const { organizationBranchStore } = useStore();
  const { t } = useTranslation();

  const {
    handleDeleteList,
    pagingOrganizationBranches,
    handleOpenCreateEdit,
    openConfirmDeleteListPopup,
    openConfirmDeletePopup,
    openCreateEditPopup,
    handleClose,
    handleConfirmDelete,
    handleConfirmDeleteList,
    searchObject,
    listOnDelete,
    resetStore,
    handleSetSearchObject
  } = organizationBranchStore;

  useEffect(() => {
    pagingOrganizationBranches();

    return resetStore;
  }, []);

  async function handleFilter(values) {
    const newSearchObject = {
      ...values,
      pageIndex: 1,
    };
    handleSetSearchObject(newSearchObject);
    await pagingOrganizationBranches();
  }

  return (
    <div className="content-index">
      <div className="index-breadcrumb">
        <GlobitsBreadcrumb routeSegments={[{ name: "Cơ cấu tổ chức" }, { name: t("organizationBranch.title") }]} />
      </div>
      <Grid className="index-card" container spacing={2}>
        <Grid item xs={12}>
          <Formik
            enableReinitialize
            initialValues={searchObject}
            onSubmit={handleFilter}
          >
            {({ resetForm, values, setFieldValue, setValues }) => {
              return (
                <Form autoComplete="off">
                  <div className="">
                    <Grid container spacing={2} className="align-center mainBarFilter">
                      <Grid item xs={12} md={3}>
                        <Grid container spacing={2} className="">
                          <Grid item xs={12} sm={6}>
                            <Button
                              className="btn text-white d-inline-flex bgc-lighter-dark-green"
                              startIcon={<AddIcon />}
                              variant="contained"
                              fullWidth
                              onClick={() => handleOpenCreateEdit()}
                            >
                              Thêm mới
                            </Button>
                          </Grid>

                          <Grid item xs={12} sm={6}>
                            {listOnDelete?.length > 0 && (
                              <Button
                                className="btn btn-danger d-inline-flex"
                                variant="contained"
                                fullWidth
                                startIcon={<DeleteIcon />}
                                onClick={handleDeleteList}
                              >
                                {/* {!isMobile && t("general.button.delete")} */}
                                Xóa
                              </Button>
                            )}
                          </Grid>
                        </Grid>
                      </Grid>

                      <Grid item xs={12} md={9}>
                        <div className="flex justify-between align-center">
                          <GlobitsTextField
                            placeholder="Tìm kiếm theo mã, tên đơn vị làm việc..."
                            name="keyword"
                            variant="outlined"
                            notDelay
                            InputProps={{
                              endAdornment: (
                                <IconButton className="py-0 px-4" aria-label="search" type="submit">
                                  <SearchIcon />
                                </IconButton>
                              ),
                            }}
                          />

                          <Button
                            startIcon={<SearchIcon className={`mr-2`} />}
                            className="ml-8 d-inline-flex filterButtonV4 bgc-warning-d1 py-2 px-8 btn text-white"
                            // onClick={handleLoadViewingData}
                            type="submit"
                          >
                            Tìm kiếm
                          </Button>

                          {/* <Button
                          startIcon={<FilterListIcon className={`mr-4 filterRotateIcon ${isOpenFilter && 'onRotate'}`} />}
                          className="ml-8 d-inline-flex filterButtonV4 bgc-lighter-dark-green py-2 px-8 btn text-white"
                          onClick={handleTogglePopupFilter}
                        >
                          Bộ lọc

                          <Tooltip title="Có trường lọc được thay đổi gây ảnh hưởng đến kết quả tìm kiếm" placement="top-end" >
                            <span className={`${!showAlertIcon ? 'display-none' : "flex"} changedFieldDot`}>
                              <ErrorIcon />
                            </span>
                          </Tooltip>
                        </Button> */}
                        </div>
                      </Grid>
                    </Grid>


                    {/* <Collapse in={isOpenFilter} className="filterPopup">
                    <div className="flex flex-column">
                      <Grid container spacing={2}>
                        
                      </Grid>

                      <div className="pt-8 mt-12" style={{ borderTop: "1px solid #b3b3b3" }}>
                        <div className="flex justify-end" >
                          <Button
                            className="btn px-8 py-2 bg-light-gray d-inline-flex mr-12"
                            type="button"
                            onClick={handleResetFilter}
                          >
                            <RotateLeftIcon className="mr-6" />
                            Đặt lại
                          </Button>

                          <Button
                            className="btn px-8 py-2 btn-danger d-inline-flex"
                            // fullWidth
                            type="submit"
                          >
                            <BackupIcon className="mr-6" />
                            Lưu bộ lọc và tìm kiếm
                          </Button>
                        </div>
                      </div>
                    </div>

                  </Collapse> */}
                  </div>
                </Form>
              );
            }}
          </Formik>
        </Grid>

        <Grid item xs={12}>
          <OrganizationBranchList />
        </Grid>
      </Grid>

      {openCreateEditPopup && (
        <OrganizationBranchCUForm />
      )}


      {openConfirmDeletePopup && (
        <GlobitsConfirmationDialog
          open={openConfirmDeletePopup}
          onConfirmDialogClose={handleClose}
          onYesClick={handleConfirmDelete}
          title={t("confirm_dialog.delete.title")}
          text={t("confirm_dialog.delete.text")}
          agree={t("confirm_dialog.delete.agree")}
          cancel={t("confirm_dialog.delete.cancel")}
        />
      )}


      {openConfirmDeleteListPopup && (
        <GlobitsConfirmationDialog
          open={openConfirmDeleteListPopup}
          onConfirmDialogClose={handleClose}
          onYesClick={handleConfirmDeleteList}
          title={t("confirm_dialog.delete_list.title")}
          text={t("confirm_dialog.delete_list.text")}
          agree={t("confirm_dialog.delete_list.agree")}
          cancel={t("confirm_dialog.delete_list.cancel")}
        />
      )}
    </div>
  );
}

export default memo(observer(OrganizationBranchIndex));
