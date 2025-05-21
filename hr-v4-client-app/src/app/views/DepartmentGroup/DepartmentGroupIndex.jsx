import { observer } from "mobx-react";
import React, { memo, useEffect } from "react";
import { useStore } from "../../stores";
import { Grid, Button, IconButton, ButtonGroup } from "@material-ui/core";
import GlobitsBreadcrumb from "../../common/GlobitsBreadcrumb";
import DeleteIcon from "@material-ui/icons/Delete";
import AddIcon from "@material-ui/icons/Add";
import { useTranslation } from "react-i18next";
import GlobitsConfirmationDialog from "../../common/GlobitsConfirmationDialog";
import NoteIcon from "@material-ui/icons/Note";
import SearchIcon from "@material-ui/icons/Search";
import { Form, Formik } from "formik";
import GlobitsTextField from "app/common/form/GlobitsTextField";
import RankTitleList from "./DepartmentGroupList";
import RankTitleCUForm from "./DepartmentGroupForm";

function DepartmentGroupIndex() {
  const { departmentGroupStore } = useStore();
  const { t } = useTranslation();

  const {
    handleDeleteList,
    pagingDepartmentGroup,
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
    handleSetSearchObject,
  } = departmentGroupStore;

  useEffect(() => {
    pagingDepartmentGroup();

    return resetStore;
  }, []);

  async function handleFilter(values) {
    const newSearchObject = {
      ...values,
      pageIndex: 1,
    };
    handleSetSearchObject(newSearchObject);
    await pagingDepartmentGroup();
  }

  return (
    <div className="content-index">
      <div className="index-breadcrumb">
        <GlobitsBreadcrumb
          routeSegments={[
            { name: t("navigation.category.title") },
            { name: t("navigation.category.staff.title") },
            { name: t("navigation.category.staff.departmentGroup") },
          ]}
        />
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
                    <Grid
                      container
                      spacing={2}
                      className="align-center mainBarFilter"
                    >
                      <Grid item xs={12} md={6}>
                        <ButtonGroup
                          color="container"
                          aria-label="outlined primary button group"
                        >
                          <Button
                            startIcon={<AddIcon />}
                            onClick={() => handleOpenCreateEdit()}
                          >
                            {t("general.button.add")}
                          </Button>

                          <Button
                            disabled={listOnDelete?.length <= 0}
                            startIcon={<DeleteIcon />}
                            onClick={handleDeleteList}
                          >
                            {t("general.button.delete")}
                          </Button>
                        </ButtonGroup>
                      </Grid>

                      <Grid item xs={12} md={6}>
                        <div className="flex justify-between align-center">
                          <GlobitsTextField
                            placeholder="Tìm kiếm theo tên nhóm phòng ban..."
                            name="keyword"
                            variant="outlined"
                            notDelay
                            InputProps={{
                              endAdornment: (
                                <IconButton
                                  className="py-0 px-4"
                                  aria-label="search"
                                  type="submit"
                                >
                                  <SearchIcon />
                                </IconButton>
                              ),
                            }}
                          />

                          {/* <Button
                            startIcon={<SearchIcon className={`mr-2`} />}
                            className="ml-8 d-inline-flex filterButtonV4 bgc-warning-d1 py-2 px-8 btn text-white"
                            // onClick={handleLoadViewingData}
                            type="submit"
                          >
                            Tìm kiếm
                          </Button> */}

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
          <RankTitleList />
        </Grid>
      </Grid>

      {openCreateEditPopup && <RankTitleCUForm />}

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
          onConfirmDialogClose={() => {
            pagingDepartmentGroup();
            handleClose();
          }}
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

export default memo(observer(DepartmentGroupIndex));
