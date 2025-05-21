import { Button, ButtonGroup, Grid, MenuItem, Popover, Tooltip } from "@material-ui/core";
import { useTheme } from "@material-ui/core/styles";
import useMediaQuery from "@material-ui/core/useMediaQuery";
import AddIcon from "@material-ui/icons/Add";
import AlarmOnIcon from "@material-ui/icons/AlarmOn";
import CloudUploadIcon from "@material-ui/icons/CloudUpload";
import DeleteOutlineIcon from "@material-ui/icons/DeleteOutline";
import DescriptionIcon from "@material-ui/icons/Description";
import FilterListIcon from "@material-ui/icons/FilterList";
import GetAppIcon from "@material-ui/icons/GetApp";
import SearchIcon from "@material-ui/icons/Search";
import GlobitsTextField from "app/common/form/GlobitsTextField";
import { useStore } from "app/stores";
import { Form, Formik } from "formik";
import { observer } from "mobx-react";
import moment from "moment";
import { memo, useState } from "react";
import { useTranslation } from "react-i18next";
import * as Yup from "yup";
import GlobitsDateTimePicker from "../../common/form/GlobitsDateTimePicker";
import TimeSheetDetailFilter from "./TimeSheetDetailFilter";
import GlobitsPagingAutocompleteV2 from "app/common/form/GlobitsPagingAutocompleteV2";
import { pagingSalaryPeriod } from "app/views/Salary/SalaryPeriod/SalaryPeriodService";
import SpaIcon from '@material-ui/icons/Spa';

function TimeSheetDetailIndexToolbar() {
  const { 
    timeSheetDetailStore, 
    staffWorkScheduleStore,
     hrRoleUtilsStore 
    } = useStore();

  const { t } = useTranslation();

  const {
    pagingTimeSheetDetail,
    searchObject,
    handleDownloadTimesheetDetailTemplate,
    handleSetSearchObject,
    uploadFileExcel,
    uploadFileDataWithSystemTemplate,
    handleOpenFormTimeSheetDetailCheck,
    listChosen,
    handleDeleteList,
    handleOpenCreateEdit,
    handleExportDataWithSystemTemplate,
    handleOpenExportLATimekeepingDataPopup
  } = timeSheetDetailStore;

  const { exportActualTimesheet } = staffWorkScheduleStore;

  async function handleFilter(values) {
    const newSearchObject = {
      ...values,
      pageIndex: 1,
    };
    handleSetSearchObject(newSearchObject);
    await pagingTimeSheetDetail();
  }

  //const { id } = useParams();

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
    if (isOpenFilter) handleCloseFilter();
    else handleOpenFilter();
  }

  async function handleExportExcelActualTimesheet(values) {
    const payload = {
      ...values
    };
    await exportActualTimesheet(payload);
  }


  const theme = useTheme();
  const isMobile = useMediaQuery(theme.breakpoints.down("sm"));

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

  const [anchorEl, setAnchorEl] = useState(null);

  const handleOpenPopover = (event) => {
    setAnchorEl(event.currentTarget);
  };

  const handleClosePopover = () => {
    setAnchorEl(null);
  };

  const [anchorElImportExcel, setAnchorElImportExcel] = useState(null);

  const handleOpenMenuImportExcel = (event) => {
    setAnchorElImportExcel(event.currentTarget);
  };

  const handleCloseMenuImportExcel = () => {
    setAnchorElImportExcel(null);
  };

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
  return (
    <Formik
      enableReinitialize
      initialValues={searchObject}
      onSubmit={handleFilter}
      validationSchema={validationSchema}
    >
      {({ resetForm, values, setFieldValue, setValues }) => {
        return (
          <Form autoComplete='off'>
            <Grid item xs={12}>
              <Grid container spacing={2}>
                <Grid item xs={12}>
                  <ButtonGroup color='container' aria-label='outlined primary button group'>
                    <Tooltip arrow placement='top' title='Chấm công theo thời gian thực tế'>
                      <Button
                        // className={`btn btn-info`}
                        // variant="contained"
                        startIcon={<AlarmOnIcon />}
                        onClick={() => {
                          handleOpenFormTimeSheetDetailCheck();
                        }}>
                        Chấm công
                      </Button>
                    </Tooltip>

                    {(canApproveOrUnApprove) && (
                      <Tooltip arrow placement='top' title='Tạo lần chấm công thủ công'>
                        <Button startIcon={<AddIcon />} onClick={() => handleOpenCreateEdit()}>
                          {t("general.button.add")}
                        </Button>
                      </Tooltip>
                    )}

                    {(canApproveOrUnApprove) && (
                      <Tooltip arrow placement='top' title='Tải mẫu nhập Excel dữ liệu chấm công'>
                        <Button
                          startIcon={<GetAppIcon />}
                          onClick={handleDownloadTimesheetDetailTemplate}>
                          Tải mẫu nhập
                        </Button>
                      </Tooltip>
                    )}

                    {(canApproveOrUnApprove) && (
                      <Tooltip placement='top' title='Nhập excel'>
                        <Button
                          startIcon={<CloudUploadIcon />}
                          onClick={(event) => handleOpenMenuImportExcel(event)} // Đảm bảo truyền đúng event
                        >
                          {t("general.button.importExcel")}
                        </Button>
                      </Tooltip>
                    )}

                    {(canApproveOrUnApprove) && (
                      <Tooltip arrow placement='top' title='Xuất Excel'>
                        <Button startIcon={<DescriptionIcon />} onClick={handleOpenPopover}>
                          {t("general.button.exportExcel")}
                        </Button>
                      </Tooltip>
                    )}

                    {(canApproveOrUnApprove) && (
                      <Tooltip
                        arrow
                        placement='top'
                        title='Xuất Excel dữ liệu chấm công từ máy chấm công hệ thống Linh Anh'
                      >
                        <Button
                          startIcon={<SpaIcon />}
                          onClick={handleOpenExportLATimekeepingDataPopup}
                        >
                          Excel data máy chấm công
                        </Button>
                      </Tooltip>
                    )}

                    {(canApproveOrUnApprove) && (
                      <Button
                        disabled={listChosen?.length === 0}
                        startIcon={<DeleteOutlineIcon />}
                        onClick={handleDeleteList}>
                        {t("general.button.delete")}
                      </Button>
                    )}


                  </ButtonGroup>
                  {(canApproveOrUnApprove) && (
                    <Popover
                      open={Boolean(anchorElImportExcel)}
                      anchorEl={anchorElImportExcel}
                      onClose={handleCloseMenuImportExcel}
                      anchorOrigin={{
                        vertical: "bottom",
                        horizontal: "left",
                      }}
                      transformOrigin={{
                        vertical: "top",
                        horizontal: "left",
                      }}>
                      <MenuItem onClick={() => document.getElementById("fileExcel").click()}>
                        Nhập dữ liệu Excel chấm công theo mẫu nhập
                      </MenuItem>
                      <MenuItem
                        onClick={() =>
                          document.getElementById("fileExcelExportDataWithSystemTemplate").click()
                        }>
                        Nhập dữ liệu Excel chấm công theo mẫu hệ thống xuất
                      </MenuItem>
                    </Popover>
                  )}

                  {(canApproveOrUnApprove) && (
                    <>
                      <input
                        type='file'
                        id='fileExcel'
                        style={{ display: "none" }}
                        onChange={uploadFileExcel}
                      />
                      <input
                        type='file'
                        id='fileExcelExportDataWithSystemTemplate'
                        style={{ display: "none" }}
                        onChange={uploadFileDataWithSystemTemplate}
                      />
                    </>
                  )}

                  {(canApproveOrUnApprove) && (
                    <Popover
                      open={Boolean(anchorEl)}
                      anchorEl={anchorEl}
                      onClose={handleClosePopover}
                      anchorOrigin={{
                        vertical: "bottom",
                        horizontal: "left",
                      }}
                      transformOrigin={{
                        vertical: "top",
                        horizontal: "left",
                      }}>
                      <MenuItem
                        onClick={() => {
                          handleExportExcelActualTimesheet(values);
                          handleClosePopover();
                        }}>
                        Xuất Excel dữ liệu chấm công theo mẫu thực tế
                      </MenuItem>

                      <MenuItem
                        onClick={() => {
                          handleExportDataWithSystemTemplate(values);
                          handleClosePopover();
                        }}>
                        Xuất Excel dữ liệu chấm công
                        {/* Xuất Excel dữ liệu chấm công theo mẫu hệ thống */}
                      </MenuItem>
                    </Popover>
                  )}
                </Grid>
              </Grid>

              <Grid container spacing={2}>
                <Grid item xs={12} className='mt-10'>
                  <p className='m-0 p-0 borderThrough2'>Lọc và tìm kiếm</p>
                </Grid>
                <Grid item xs={12} lg={6} className='flex items-center' style={{ width: "150px" }}>
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


                <Grid item xs={12} lg={6}>
                  <div className='flex justify-between align-center'>
                    <Tooltip placement='top' title='Tìm kiếm theo tên nhân viên'>
                      <GlobitsTextField
                        placeholder='Tìm kiếm theo tên nhân viên'
                        name='keyword'
                        variant='outlined'
                        notDelay
                      />
                    </Tooltip>

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
                        startIcon={
                          <FilterListIcon
                            className={` filterRotateIcon ${isOpenFilter && "onRotate"}`}
                          />
                        }
                        className=' d-inline-flex py-2 px-8 btnHrStyle'
                        onClick={handleTogglePopupFilter}>
                        Bộ lọc
                      </Button>
                    </ButtonGroup>
                  </div>
                </Grid>
              </Grid>

              <TimeSheetDetailFilter
                isOpenFilter={isOpenFilter}
                handleFilter={handleFilter}
                handleCloseFilter={handleCloseFilter}
                resetForm={resetForm}
              />
            </Grid>
          </Form>
        );
      }}
    </Formik>
  );
}

export default memo(observer(TimeSheetDetailIndexToolbar));
