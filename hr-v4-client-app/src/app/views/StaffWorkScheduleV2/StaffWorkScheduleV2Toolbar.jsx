import { Button, ButtonGroup, Grid, Tooltip } from "@material-ui/core";
import AddIcon from "@material-ui/icons/Add";
import CloudUploadIcon from "@material-ui/icons/CloudUpload";
import DeleteOutlineIcon from "@material-ui/icons/DeleteOutline";
import FilterListIcon from "@material-ui/icons/FilterList";
import GetAppIcon from "@material-ui/icons/GetApp";
import PieChartOutlinedIcon from "@material-ui/icons/PieChartOutlined";
import SearchIcon from "@material-ui/icons/Search";
import GlobitsDateTimePicker from "app/common/form/GlobitsDateTimePicker";
import { useStore } from "app/stores";
import { Form, Formik } from "formik";
import { observer } from "mobx-react";
import { memo, useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import { useHistory } from "react-router-dom";
import StaffWorkScheduleV2Filter from "./StaffWorkScheduleV2Filter";
import * as Yup from "yup";
import moment from "moment";
import EqualizerIcon from '@material-ui/icons/Equalizer';
import GlobitsPagingAutocompleteV2 from "../../common/form/GlobitsPagingAutocompleteV2";
import { pagingSalaryPeriod } from "../Salary/SalaryPeriod/SalaryPeriodService";

function StaffWorkScheduleV2Toolbar(props) {
  const history = useHistory();
  const { t } = useTranslation();

  const { staffWorkScheduleStore, hrRoleUtilsStore } = useStore();

  const {
    handleDeleteList,
    pagingStaffWorkSchedule,
    searchObject,
    listOnDelete,
    handleSetSearchObject,
    handleOpenAssignForm,
    handleOpenFormSWS,
    handleDownloadTemplate,
    uploadFileExcel,
    handleLockSchedules,
    handleOpenReStatisticSchedules
  } = staffWorkScheduleStore;

  async function handleFilter(values) {
    const newSearchObject = {
      ...values, pageIndex: 1,
    };
    handleSetSearchObject(newSearchObject);
    await pagingStaffWorkSchedule();
  }

  const [isOpenFilter, setIsOpenFilter] = useState(true);

  function handleCloseFilter() {
    if (isOpenFilter) {
      setIsOpenFilter(false);
    }
  }

  function handleOpenFilter() {
    if (!isOpenFilter) {
      setIsOpenFilter(true);
    }
  }

  function handleTogglePopupFilter() {
    if (isOpenFilter) handleCloseFilter(); else handleOpenFilter();
  }

  const validationSchema = Yup.object({
    fromDate: Yup.date()
      .transform((castValue, originalValue) => (originalValue ? new Date(originalValue) : castValue))
      // .required(t("validation.required"))
      .typeError("Từ ngày không đúng định dạng")
      .nullable(),

    toDate: Yup.date()
      .transform((castValue, originalValue) => (originalValue ? new Date(originalValue) : castValue))
      // .required(t("validation.required"))
      .typeError("Đến ngày không đúng định dạng")
      .nullable()
      .test("is-greater-or-equal", "Đến ngày phải lớn hơn hoặc bằng Từ ngày", function (value) {
        const { fromDate } = this.parent;
        if (fromDate && value) {
          return moment(value).isSameOrAfter(moment(fromDate), "date");
        }
        return true;
      }),
  });
  const {
    hasShiftAssignmentPermission,
    isCompensationBenifit,
    isAdmin,
    isManager,
    hasRoleManageHCNS
  } = hrRoleUtilsStore;
  let canApproveOrUnApprove = false;
  if (isAdmin || isManager || isCompensationBenifit || hasShiftAssignmentPermission || hasRoleManageHCNS) {
    canApproveOrUnApprove = true;
  }

  return (<Formik
    enableReinitialize
    initialValues={JSON.parse(JSON.stringify(searchObject))}
    validationSchema={validationSchema}
    onSubmit={handleFilter}
  >
    {({ resetForm, values, setFieldValue, setValues }) => {
      return (<Form autoComplete='off'>
        <Grid item xs={12}>
          <Grid container spacing={2}>
            <Grid item xs={12}>
              {(canApproveOrUnApprove) && (<>
                <ButtonGroup color='container' aria-label='outlined primary button group'>
                  <Tooltip placement='top' title='Phân ca làm việc nhiều nhân viên' arrow>
                    <Button
                      startIcon={<PieChartOutlinedIcon fontSize='small' />}
                      onClick={handleOpenAssignForm}>
                      Phân ca làm việc
                    </Button>
                  </Tooltip>

                  <Tooltip
                    placement='top'
                    title={"Phân ca làm việc cho 1 nhân viên"}
                    arrow>
                    <Button startIcon={<AddIcon />} onClick={handleOpenFormSWS}>
                      {"Thêm mới"}
                    </Button>
                  </Tooltip>

                  <Tooltip
                    placement='top'
                    title={"Tải xuống mẫu nhập ca làm việc"}
                    arrow>
                    <Button startIcon={<GetAppIcon />} onClick={handleDownloadTemplate}>
                      {t("Tải mẫu nhập")}
                    </Button>
                  </Tooltip>

                  <Tooltip placement='top' title={"Nhập phân ca làm việc"} arrow>
                    <Button
                      startIcon={<CloudUploadIcon />}
                      onClick={() => document.getElementById("fileExcel").click()}>
                      {t("general.button.importExcel")}
                    </Button>
                  </Tooltip>

                  <Tooltip
                    placement='top'
                    arrow
                    title='Thống kê lại kết quả của toàn bộ ca làm việc trong 1 khoảng thời gian'
                  >
                    <Button
                      // className="btn btn-success"
                      startIcon={<EqualizerIcon />}
                      onClick={() => handleOpenReStatisticSchedules()}>
                      Thống kê lại
                    </Button>
                  </Tooltip>

                  <Tooltip placement='top' arrow title={t("Chốt ca làm việc")}>
                    <Button
                      disabled={listOnDelete?.length <= 0}
                      startIcon={<DeleteOutlineIcon />}
                      onClick={handleLockSchedules}>
                      {t("Chốt ca làm việc")}
                    </Button>
                  </Tooltip>


                  <Tooltip placement='top' arrow title={t("general.button.delete")}>
                    <Button
                      disabled={listOnDelete?.length <= 0}
                      startIcon={<DeleteOutlineIcon />}
                      onClick={handleDeleteList}>
                      {t("general.button.delete")}
                    </Button>
                  </Tooltip>

                </ButtonGroup>

                <input
                  type='file'
                  id='fileExcel'
                  style={{ display: "none" }}
                  onChange={uploadFileExcel}
                />
              </>)}
            </Grid>
            <Grid item xs={12} className='flex items-center' style={{ width: "150px" }}>
              <div className='flex items-center h-100 flex-end pr-10'>
                <p className='no-wrap-text'>
                  <b>Kỳ lương:</b>
                </p>
              </div>
              <div style={{ width: "200px" }}>
                <GlobitsPagingAutocompleteV2
                  name='salaryPeriod'
                  // label='Kỳ lương'
                  api={pagingSalaryPeriod}
                  handleChange={(_, value) => {
                    setFieldValue("salaryPeriod", value);
                    // setFieldValue("salaryPeriodId", value?.id);
                    setFieldValue("fromDate", value?.fromDate);
                    setFieldValue("toDate", value?.toDate);
                  }}
                />
              </div>
            </Grid>

            <Grid item xs={12}>
              <Grid container spacing={1}>
                <Grid item xs={12}>
                  <div className='flex justify-end align-center'>
                    <div className='flex flex-center w-100'>
                      <Grid container spacing={2}>
                        <Grid item xs={12} sm={6} md={4}>
                          <div className='flex items-center h-100 flex-end'>
                            <p className='no-wrap-text'>
                              <b>Từ ngày:</b>
                            </p>
                          </div>
                        </Grid>

                        <Grid item xs={12} sm={6} md={8}>
                          <GlobitsDateTimePicker
                            // label="Từ ngày"
                            name='fromDate'
                            onChange={(value) => {
                              setFieldValue("fromDate", value);
                              setFieldValue("salaryPeriod", null);
                            }}
                          />
                        </Grid>
                      </Grid>
                    </div>

                    <div className='flex flex-center w-100'>
                      <Grid container spacing={2}>
                        <Grid item xs={12} sm={6} md={4}>
                          <div className='flex items-center h-100 flex-end'>
                            <p className='no-wrap-text'>
                              <b>Đến ngày:</b>
                            </p>
                          </div>
                        </Grid>

                        <Grid item xs={12} sm={6} md={8}>
                          <GlobitsDateTimePicker
                            // label="Đến ngày"
                            name='toDate'
                            onChange={(value) => {
                              setFieldValue("toDate", value);
                              setFieldValue("salaryPeriod", null);
                            }}
                          />
                        </Grid>
                      </Grid>
                    </div>

                    <ButtonGroup
                      className='filterButtonV4'
                      color='container'
                      aria-label='outlined primary button group'>
                      <Button
                        startIcon={<SearchIcon className={``} />}
                        className='ml-8 d-inline-flex py-2 px-8 btnHrStyle'
                        type='submit'>
                        Tìm kiếm
                      </Button>
                      <Button
                        startIcon={<FilterListIcon
                          className={` filterRotateIcon ${isOpenFilter && "onRotate"}`}
                        />}
                        className=' d-inline-flex py-2 px-8 btnHrStyle'
                        onClick={handleTogglePopupFilter}>
                        Bộ lọc
                      </Button>
                    </ButtonGroup>
                  </div>
                </Grid>
              </Grid>
            </Grid>
          </Grid>

          <StaffWorkScheduleV2Filter
            handleFilter={handleFilter}
            isOpenFilter={isOpenFilter}
            handleCloseFilter={handleCloseFilter}
          />
        </Grid>
      </Form>);
    }}
  </Formik>);
}

export default memo(observer(StaffWorkScheduleV2Toolbar));
