import { observer } from "mobx-react";
import React, { memo, useState } from "react";
import { useHistory } from "react-router-dom";
import { Form, Formik } from "formik";
import {
  Grid,
  Button,
  ButtonGroup,
  Tooltip
} from "@material-ui/core";
import AddIcon from "@material-ui/icons/Add";
import DeleteOutlineIcon from "@material-ui/icons/DeleteOutline";
import SearchIcon from "@material-ui/icons/Search";
import CloudUploadIcon from "@material-ui/icons/CloudUpload";
import GetAppIcon from "@material-ui/icons/GetApp";

import GlobitsTextField from "app/common/form/GlobitsTextField";
import { useStore } from "app/stores";
import { useTranslation } from "react-i18next";
import SalaryItemFilter from "./SalaryItemFilter";

function SalaryItemIndexToolbar() {
  const history = useHistory();
  const { salaryItemStore, hrRoleUtilsStore } = useStore();
  const { t } = useTranslation();

  const {
    handleDeleteList,
    pagingSalaryItem,
    searchObject,
    listOnDelete,
    handleSetSearchObject,
    handleOpenCreateEdit,
    uploadFileExcel,
    handleDownloadSalaryItemTemplate
  } = salaryItemStore;

  const { isAdmin, isManager, isCompensationBenifit } = hrRoleUtilsStore;

  const [isOpenFilter, setIsOpenFilter] = useState(false);

  const handleFilter = async (values) => {
    handleSetSearchObject({ ...values, pageIndex: 1 });
    await pagingSalaryItem();
  };

  const handleTogglePopupFilter = () => {
    setIsOpenFilter((prev) => !prev);
  };

  return (
    <Formik
      enableReinitialize
      initialValues={JSON.parse(JSON.stringify(searchObject))}
      onSubmit={handleFilter}
    >
      {() => (
        <Form autoComplete="off">
          <Grid container spacing={2}>
            {/* Action buttons */}
            <Grid item xs={12} md={6}>
              {(isCompensationBenifit) && (
                <>
                  <ButtonGroup color="container" aria-label="toolbar actions">
                    <Tooltip
                      placement="top"
                      title="Thêm mới thành phần lương"
                      arrow
                    >
                      <Button
                        startIcon={<AddIcon />}
                        onClick={() => handleOpenCreateEdit()}
                      >
                        Thêm mới
                      </Button>
                    </Tooltip>

                    <Tooltip title="Import thành phần lương" arrow>
                      <Button
                        startIcon={<CloudUploadIcon />}
                        onClick={() => document.getElementById("fileExcel").click()}
                      >
                        {t("general.button.importExcel")}
                      </Button>
                    </Tooltip>

                    <Tooltip
                      placement="top"
                      title="Tải mẫu nhập dữ liệu thành phần lương"
                      arrow
                    >
                      <Button
                        startIcon={<GetAppIcon />}
                        onClick={handleDownloadSalaryItemTemplate}
                      >
                        Tải mẫu nhập
                      </Button>
                    </Tooltip>

                    <Tooltip
                      placement="top"
                      title="Xóa thành phần lương"
                      arrow
                    >
                      <Button
                        disabled={listOnDelete?.length <= 0}
                        startIcon={<DeleteOutlineIcon />}
                        onClick={handleDeleteList}
                      >
                        {t("general.button.delete")}
                      </Button>
                    </Tooltip>

                  </ButtonGroup>

                  <input
                    type="file"
                    id="fileExcel"
                    style={{ display: "none" }}
                    accept=".xls,.xlsx"
                    onChange={uploadFileExcel}
                  />
                </>
              )}
            </Grid>

            {/* Search */}
            <Grid item xs={12} md={6}>
              <div className="flex justify-between align-center">
                <GlobitsTextField
                  placeholder="Tìm kiếm theo mã, tên thành phần lương..."
                  name="keyword"
                  variant="outlined"
                  notDelay
                />

                <ButtonGroup className="filterButtonV4" color="container">
                  <Button
                    type="submit"
                    startIcon={<SearchIcon />}
                    className="ml-8 d-inline-flex py-2 px-8 btnHrStyle"
                  >
                    Tìm kiếm
                  </Button>

                  {/* Bộ lọc (ẩn tạm) */}
                  {/* <Button
                    startIcon={<FilterListIcon className={`filterRotateIcon ${isOpenFilter ? "onRotate" : ""}`} />}
                    onClick={handleTogglePopupFilter}
                  >
                    Bộ lọc
                  </Button> */}
                </ButtonGroup>
              </div>
            </Grid>
          </Grid>

          {/* Bộ lọc mở rộng */}
          <SalaryItemFilter
            handleFilter={handleFilter}
            handleCloseFilter={() => setIsOpenFilter(false)}
            isOpenFilter={isOpenFilter}
          />
        </Form>
      )}
    </Formik>
  );
}

export default memo(observer(SalaryItemIndexToolbar));
