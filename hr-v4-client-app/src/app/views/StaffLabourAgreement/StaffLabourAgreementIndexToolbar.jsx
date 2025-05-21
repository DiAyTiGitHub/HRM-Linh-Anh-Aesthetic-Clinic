import { observer } from "mobx-react";
import React, { memo, useEffect, useState } from "react";
import DeleteOutlineIcon from "@material-ui/icons/DeleteOutline";
import GlobitsTextField from "app/common/form/GlobitsTextField";
import AddIcon from "@material-ui/icons/Add";
import SearchIcon from '@material-ui/icons/Search';
import { Form, Formik } from "formik";
import { Grid, Button, ButtonGroup, Tooltip } from "@material-ui/core";
import { useStore } from "app/stores";
import { useTranslation } from "react-i18next";
import FilterListIcon from '@material-ui/icons/FilterList';
import StaffLabourAgreementFilter from "./StaffLabourAgreementFilter";
import ErrorOutlineOutlinedIcon from '@material-ui/icons/ErrorOutlineOutlined';
import GetAppIcon from "@material-ui/icons/GetApp";
import CloudUploadIcon from "@material-ui/icons/CloudUpload";
import CloudDownloadIcon from "@material-ui/icons/CloudDownload";

function StaffLabourAgreementIndexToolbar() {
  const {
    staffLabourAgreementStore,
    hrRoleUtilsStore
  } = useStore();
  const { t } = useTranslation();

  const {
    handleDeleteList,
    pagingStaffLabourAgreement,
    handleOpenCreateEdit,
    searchObject,
    listOnDelete,
    handleSetSearchObject,
    hasOverdueContract,
    pagingOverdueContract,
    uploadFileExcelStaffLabourAgreement,
    handleDownloadStaffLabourAgreementTemplate,
    handleExportExcelStaffLabourAgreementData,
  } = staffLabourAgreementStore;

  async function handleFilter(values) {
    const newSearchObject = {
      ...values,
      pageIndex: 1,
    };

    //handle for choosing staff
    // if (newSearchObject?.staff?.id) {
    //     newSearchObject.staffId = newSearchObject?.staff?.id;
    // }
    // else {
    //     newSearchObject.staffId = null;
    // }

    handleSetSearchObject(newSearchObject);
    await pagingStaffLabourAgreement();
  }

  const [isOpenFilter, setIsOpenFilter] = useState(false);

  function handleCloseFilter() {
    if (isOpenFilter) {
      setIsOpenFilter(false);
    }
  }

  function handleOpenFilter() {
    if (!isOpenFilter) {
      // setInitialFilters(getKanbanFilter());
      setIsOpenFilter(true);
    }
  }

  function handleTogglePopupFilter() {
    if (isOpenFilter) handleCloseFilter();
    else handleOpenFilter();
  }

  const { isAdmin, isManager, checkAllUserRoles } = hrRoleUtilsStore;

  return (
    <Formik
      enableReinitialize
      initialValues={searchObject}
      onSubmit={handleFilter}
    >
      {({ resetForm, values, setFieldValue, setValues }) => {

        return (
          <Form autoComplete="off">
            <Grid item xs={12}>
              <Grid container spacing={2}>
                <Grid item xs={12} >
                  {(isAdmin || isManager) && (
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
                      <Tooltip
                        placement="top"
                        title={"Tải mẫu nhập danh hợp đồng"}
                        arrow
                      >
                        <Button
                          startIcon={<GetAppIcon />}
                          onClick={handleDownloadStaffLabourAgreementTemplate}
                        >
                          Tải mẫu nhập
                        </Button>
                      </Tooltip>

                      <Tooltip
                        placement="top"
                        title={"Nhập danh sách hợp đồng"}
                        arrow
                      >
                        <Button
                          startIcon={<CloudUploadIcon />}
                          onClick={() => document.getElementById("fileExcel").click()}
                        >
                          {t("general.button.importExcel")}
                        </Button>
                      </Tooltip>

                      <Tooltip
                        placement="top"
                        title={"Tải xuống Excel danh sách hợp đồng"}
                        arrow
                      >
                        <Button
                          startIcon={<CloudDownloadIcon />}
                          onClick={() => handleExportExcelStaffLabourAgreementData()}
                        >
                          Xuất Excel
                        </Button>
                      </Tooltip>
                      <Button
                        disabled={listOnDelete?.length === 0}
                        startIcon={<DeleteOutlineIcon />}
                        onClick={handleDeleteList}
                      >
                        {t("general.button.delete")}
                      </Button>

                      {/* {(hasOverdueContract === true) &&
                        <Button
                          startIcon={<ErrorOutlineOutlinedIcon />}
                          onClick={() => {
                            pagingOverdueContract()
                          }}
                        >
                          {t("Cảnh báo hợp đồng sắp hết hạn")}
                        </Button>
                      } */}
                    </ButtonGroup>
                  )}
                  {(isAdmin || isManager) && (

                    <input
                      type="file"
                      id="fileExcel"
                      style={{ display: "none" }}
                      onChange={uploadFileExcelStaffLabourAgreement}
                    />)}
                </Grid>

                <Grid item xs={12}>
                  <div className="flex justify-between align-center">
                    <Tooltip placement="top" title="Tìm kiếm theo mã, tên đợt tuyển dụng">
                      <GlobitsTextField
                        placeholder="Tìm kiếm theo tên nhân viên, hợp đồng số ... "
                        name="keyword"
                        variant="outlined"
                        notDelay
                      />
                    </Tooltip>

                    <ButtonGroup
                      className="filterButtonV4"
                      color="container"
                      aria-label="outlined primary button group"
                    >
                      <Button
                        startIcon={<SearchIcon className={``} />}
                        className="ml-8 d-inline-flex py-2 px-8 btnHrStyle"
                        type="submit"
                      >
                        Tìm kiếm
                      </Button>
                      <Button
                        startIcon={<FilterListIcon
                          className={` filterRotateIcon ${isOpenFilter && 'onRotate'}`} />}
                        className=" d-inline-flex py-2 px-8 btnHrStyle"
                        onClick={handleTogglePopupFilter}
                      >
                        Bộ lọc
                      </Button>
                    </ButtonGroup>
                  </div>
                </Grid>
              </Grid>

              <StaffLabourAgreementFilter
                isOpenFilter={isOpenFilter}
                handleFilter={handleFilter}
                handleCloseFilter={handleCloseFilter}
              />

            </Grid>
          </Form>
        );
      }}
    </Formik>
  );
}

export default memo(observer(StaffLabourAgreementIndexToolbar));