import { observer } from "mobx-react";
import React, { memo, useState } from "react";
import DeleteOutlineIcon from "@material-ui/icons/DeleteOutline";
import GlobitsTextField from "app/common/form/GlobitsTextField";
import AddIcon from "@material-ui/icons/Add";
import SearchIcon from '@material-ui/icons/Search';
import { Form, Formik } from "formik";
import { Grid, Button, ButtonGroup, Tooltip } from "@material-ui/core";
import { useStore } from "app/stores";
import { useTranslation } from "react-i18next";
import FilterListIcon from '@material-ui/icons/FilterList';
import AllowanceFilter from "./PositionTitleV2Filter";
import StaffPositionFilter from "./PositionTitleV2Filter";
import DeleteIcon from "@material-ui/icons/Delete";
import GetAppIcon from "@material-ui/icons/GetApp";
import CloudUploadIcon from "@material-ui/icons/CloudUpload";

function GroupPositionTitleV2Toolbar () {
  const {t} = useTranslation ();
  const {positionTitleV2Store} = useStore ();

  const {
    handleDeleteList,
    pagingParentPositionTitle,
    handleOpenCreateEdit,
    searchObject,
    listOnDelete,
    handleSetParentSearchObject,
    uploadFileExcelGroupPositionTitle,
    handleDownloadGroupPositionTemplate
  } = positionTitleV2Store;

  async function handleFilter (values) {
    const newSearchObject = {
      ... values,
      pageIndex:1,
      pageSize:searchObject.pageSize,
    }
    handleSetParentSearchObject (newSearchObject);
    await pagingParentPositionTitle ();
  }

  const [isOpenFilter, setIsOpenFilter] = useState (false);

  function handleCloseFilter () {
    if (isOpenFilter) {
      setIsOpenFilter (false);
    }
  }

  function handleOpenFilter () {
    if (!isOpenFilter) {
      setIsOpenFilter (true);
    }
  }

  function handleTogglePopupFilter () {
    if (isOpenFilter) handleCloseFilter ();
    else handleOpenFilter ();
  }

  const {isAdmin} = useStore ().hrRoleUtilsStore

  return (
      <Formik
          enableReinitialize
          initialValues={searchObject}
          onSubmit={handleFilter}
      >
        {({resetForm, values, setFieldValue, setValues}) => {

          return (
              <Form autoComplete="off">
                <Grid item xs={12}>
                  <Grid container spacing={2}>
                    <Grid item xs={12} md={6}>
                      {(isAdmin) && (
                          <ButtonGroup
                              color="container"
                              aria-label="outlined primary button group"
                          >
                            <Button
                                startIcon={<AddIcon/>}
                                onClick={() => handleOpenCreateEdit ()}
                            >
                              {t ("general.button.add")}
                            </Button>
                            <Button
                                startIcon={<GetAppIcon/>}
                                onClick={handleDownloadGroupPositionTemplate}
                            >
                              Tải mẫu nhập
                            </Button>
                            <Button
                                startIcon={<CloudUploadIcon/>}
                                onClick={() => document.getElementById ("fileExcel").click ()}
                            >
                              {t ("general.button.importExcel")}
                            </Button>

                            <Button
                                startIcon={<DeleteIcon/>}
                                onClick={handleDeleteList}
                                disabled={!listOnDelete?.length > 0}
                            >
                              Xóa
                            </Button>
                          </ButtonGroup>
                      )}
                      <input
                          type="file"
                          id="fileExcel"
                          style={{display:"none"}}
                          onChange={uploadFileExcelGroupPositionTitle}
                      />
                    </Grid>
                    <Grid item xs={12} md={6}>
                      <div className="flex justify-between align-center">
                        <Tooltip placement="top" title="Tìm kiếm theo mã, tên nhóm ngạch">
                          <GlobitsTextField
                              placeholder="Tìm kiếm theo mã, tên nhóm ngạch"
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
                              startIcon={<SearchIcon className={``}/>}
                              className="ml-8 d-inline-flex py-2 px-8 btnHrStyle"
                              type="submit"
                          >
                            Tìm kiếm
                          </Button>
                          {/* <Button
                                                startIcon={<FilterListIcon
                                                    className={` filterRotateIcon ${isOpenFilter && 'onRotate'}`}/>}
                                                className=" d-inline-flex py-2 px-8 btnHrStyle"
                                                onClick={handleTogglePopupFilter}
                                            >
                                                Bộ lọc
                                            </Button> */}
                        </ButtonGroup>
                      </div>
                    </Grid>
                  </Grid>

                  {/* <StaffPositionFilter
                                isOpenFilter={isOpenFilter}
                                handleFilter={handleFilter}
                                handleCloseFilter={handleCloseFilter}
                            /> */}
                </Grid>
              </Form>
          );
        }}
      </Formik>
  );
}

export default memo (observer (GroupPositionTitleV2Toolbar));