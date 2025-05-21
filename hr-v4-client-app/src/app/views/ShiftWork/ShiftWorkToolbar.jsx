import { Button, ButtonGroup, Grid, Tooltip } from "@material-ui/core";
import AddIcon from "@material-ui/icons/Add";
import CloudDownloadIcon from "@material-ui/icons/CloudDownload";
import CloudUploadIcon from "@material-ui/icons/CloudUpload";
import DeleteOutlineIcon from "@material-ui/icons/DeleteOutline";
import FilterListIcon from "@material-ui/icons/FilterList";
import SearchIcon from '@material-ui/icons/Search';
import GlobitsTextField from "app/common/form/GlobitsTextField";
import { useStore } from "app/stores";
import { Form, Formik } from "formik";
import { observer } from "mobx-react";
import { memo, useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import localStorageService from "../../services/localStorageService";
import ShiftWorkFilter from "./ShiftWorkFilter";

function ShiftWorkToolbar() {
  const {shiftWorkStore, hrRoleUtilsStore} = useStore();
  const {t} = useTranslation();
  const {
    selectedShiftWorkList,
    updatePageData,
    handleEditShiftWork,
    handleDeleteList,
    uploadFileExcel,
    handleDownloadShiftWorkTemplate,
  } = shiftWorkStore;

  const {isManager, isAdmin, isCompensationBenifit} = hrRoleUtilsStore;

  const [initialValues, setInitialValues] = useState({
    keyword: "",
    department: null,
    organization: null,
  });

  const [isOpenFilter, setIsOpenFilter] = useState(false);

  useEffect(() => {
    const user = localStorageService.getLoginUser();
    const defaultValues = {
      keyword: "",
      department: user?.department || null,
      organization: user?.organization || null,
    };
    setInitialValues(defaultValues);
    updatePageData(defaultValues);
  }, []);

  async function handleFilter(values) {
    updatePageData(values);
  }

  function handleTogglePopupFilter() {
    setIsOpenFilter((prev) => !prev);
  }

  function handleCloseFilter() {
    setIsOpenFilter(false);
  }

  return (
    <Formik
      enableReinitialize
      initialValues={initialValues}
      onSubmit={handleFilter}
    >
      {({resetForm, values, setFieldValue, setValues}) => {

        return (
          <Form autoComplete="off">
            <Grid item xs={12}>
              <Grid container spacing={2}>
                <Grid item xs={12} lg={6}>
                  {(isManager || isAdmin || isCompensationBenifit) && (
                    <ButtonGroup
                      color="container"
                      aria-label="outlined primary button group"
                    >
                      <Button
                        startIcon={<AddIcon/>}
                        onClick={() => handleEditShiftWork()}
                      >
                        {t("general.button.add")}
                      </Button>
                      <Tooltip
                        placement="top"
                        title={"Import phòng ban áp dụng ca làm việc"}
                        arrow
                      >
                        <Button
                          startIcon={<CloudUploadIcon/>}
                          onClick={() => document.getElementById("fileExcel").click()}
                        >
                          {t("general.button.importExcel")}
                        </Button>
                      </Tooltip>

                      <Tooltip
                        placement="top"
                        title={"Tải mẫu import phòng ban áp dụng ca làm việc"}
                        arrow
                      >
                        <Button
                          startIcon={<CloudDownloadIcon/>}
                          onClick={() => handleDownloadShiftWorkTemplate()}
                        >
                          Tải Mẫu Nhập Excel
                        </Button>
                      </Tooltip>
                      <Button
                        disabled={selectedShiftWorkList?.length === 0}
                        startIcon={<DeleteOutlineIcon/>}
                        onClick={handleDeleteList}
                      >
                        {t("general.button.delete")}
                      </Button>
                    </ButtonGroup>
                  )}
                  {(isManager || isAdmin) && (
                    <input
                      type="file"
                      id="fileExcel"
                      style={{display: "none"}}
                      onChange={uploadFileExcel}
                    />)}
                </Grid>

                <Grid item xs={12} lg={6}>
                  <div className="flex justify-between align-center">
                    <Tooltip placement="top" title="Tìm kiếm theo từ khóa">
                      <GlobitsTextField
                        placeholder="Tìm kiếm theo từ khóa"
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
                      <Button
                        startIcon={<FilterListIcon
                          className={` filterRotateIcon ${isOpenFilter && 'onRotate'}`}/>}
                        className=" d-inline-flex py-2 px-8 btnHrStyle"
                        onClick={handleTogglePopupFilter}
                      >
                        Bộ lọc
                      </Button>
                    </ButtonGroup>
                  </div>
                </Grid>
              </Grid>

              <ShiftWorkFilter
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

export default memo(observer(ShiftWorkToolbar));