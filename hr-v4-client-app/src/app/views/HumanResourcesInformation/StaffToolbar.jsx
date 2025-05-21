import { Button, ButtonGroup, Grid, Menu, MenuItem, Tooltip } from "@material-ui/core";
import FilterListIcon from "@material-ui/icons/FilterList";
import SearchIcon from "@material-ui/icons/Search";
import GlobitsTextField from "app/common/form/GlobitsTextField";
import { useStore } from "app/stores";
import { Form, Formik } from "formik";
import { observer } from "mobx-react";
import { memo, useMemo, useState } from "react";
import PersonAddIcon from "@material-ui/icons/PersonAdd";
import { useTranslation } from "react-i18next";
import StaffFilter from "./StaffFilter";
import AddIcon from "@material-ui/icons/Add";
import CloudDownloadIcon from "@material-ui/icons/CloudDownload";
import localStorageService from "app/services/localStorageService";
import GetAppIcon from "@material-ui/icons/GetApp";
import CloudUploadIcon from "@material-ui/icons/CloudUpload";
import GlobitsColorfulThemePopup from "app/common/GlobitsColorfulThemePopup";
import LocalConstants from "app/LocalConstants";
import DateRangeIcon from "@material-ui/icons/DateRange";

function StaffToolbar() {
  const { t } = useTranslation();

  const { staffStore } = useStore();

  const {
    handleSetUsingStaffSO,
    handleClose,
    onPagingStaff,
    searchStaff,
    setShouldOpenFormCreate,
    selectedStaffList,
    handleOpentCreateUserForlStaff,
    handleDownloadExcelListStaff,
    uploadFileExcelListStaff,
    uploadFileExcelListNewStaff,
    openConfirmDownload,
    openPopupConfirmDownloadExcelListStaff,
    handleDownloadTemplateImportStaff,
    handleOpenFixShiftDateRangePopup,
  } = staffStore;

  const [isOpenFilter, setIsOpenFilter] = useState(false);
  const [countFilterActive, setCountFilterActive] = useState(0);

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

  async function handleFilter(values) {
    const newSearchObject = {
      ...values,
      pageIndex: 1,
    };
    handleSetUsingStaffSO(newSearchObject);
    await onPagingStaff();
  }

  function handleTogglePopupFilter() {
    if (isOpenFilter) handleCloseFilter();
    else handleOpenFilter();
  }

  const isAdmin = useMemo(() => {
    let roles = localStorageService.getLoginUser()?.user?.roles?.map((item) => item.authority) || [];
    let auth = ["HR_MANAGER", "ROLE_ADMIN", "ROLE_SUPER_ADMIN"];
    return roles.some((role) => auth.indexOf(role) !== -1);
  }, []);
  const [anchorEl, setAnchorEl] = useState(null);

  const handleOpenMenu = (event) => {
    setAnchorEl(event.currentTarget);
  };

  const handleCloseMenu = () => {
    setAnchorEl(null);
  };
  return (
    <Formik enableReinitialize initialValues={searchStaff} onSubmit={handleFilter}>
      {({ resetForm, values, setFieldValue, setValues }) => {
        return (
          <Form autoComplete='off'>
            <Grid item xs={12}>
              <Grid container spacing={2}>
                <Grid item xs={12}
                // xl={4}
                >
                  <ButtonGroup color='container' aria-label='outlined primary button group'>
                    <Tooltip arrow placement='top' title='Thêm mới nhân viên'>
                      <Button
                        startIcon={<AddIcon fontSize='small' />}
                        onClick={() => setShouldOpenFormCreate(true)}>
                        {t("general.button.add")}
                      </Button>
                    </Tooltip>

                    <Tooltip placement='top' arrow title='Xuất excel'>
                      <Button
                        startIcon={<CloudDownloadIcon fontSize='small' />}
                        onClick={handleOpenMenu}>
                        Xuất Excel
                      </Button>
                    </Tooltip>

                    {isAdmin && (
                      <Tooltip placement='top' title='Nhập danh sách nhân viên' arrow>
                        <Button
                          startIcon={<CloudUploadIcon />}
                          onClick={() => document.getElementById("fileExcel").click()}>
                          {t("general.button.importExcel") + " nhân viên cũ"}
                        </Button>
                      </Tooltip>
                    )}

                    {isAdmin && (
                      <Tooltip placement='top' title='Nhập danh sách nhân viên mới' arrow>
                        <Button
                          startIcon={<CloudUploadIcon />}
                          onClick={() => document.getElementById("fileNewExcel").click()}>
                          {t("general.button.importExcel") + " nhân viên mới"}
                        </Button>
                      </Tooltip>
                    )}
                    {isAdmin && (
                      <Tooltip
                        placement='top'
                        title='Tải mẫu nhập excel danh sách nhân viên'
                        arrow>
                        <Button
                          startIcon={<GetAppIcon fontSize='small' />}
                          onClick={() => handleDownloadTemplateImportStaff()}>
                          Tải mẫu nhập
                        </Button>
                      </Tooltip>
                    )}
                    <Menu
                      anchorEl={anchorEl}
                      open={Boolean(anchorEl)}
                      onClose={handleCloseMenu}
                      anchorOrigin={{
                        vertical: "bottom", // Menu sẽ bắt đầu từ phía dưới của nút
                        horizontal: "left", // Căn trái với nút
                      }}
                      transformOrigin={{
                        vertical: "top", // Điểm gốc của menu là phía trên
                        horizontal: "left", // Căn trái
                      }}>
                      {isAdmin && (
                        <MenuItem
                          className='flex items-center justify-center'
                          onClick={openConfirmDownload}>
                          Xuất danh sách nhân viên
                        </MenuItem>
                      )}
                      {/* <MenuItem
                                                className='flex items-center justify-center'
                                                onClick={() => {
                                                    exportStaffLaborReportExcel();
                                                    handleCloseMenu();
                                                }}>
                                                Xuất báo cáo sử dụng lao động
                                            </MenuItem>
                                            <MenuItem
                                                className='flex items-center justify-center'
                                                onClick={() => {
                                                    exportExcelListHrIntroduceCost();
                                                    handleCloseMenu();
                                                }}>
                                                Xuất báo cáo chi phí giới thiệu nhân viên
                                            </MenuItem> */}
                      {/* <MenuItem
                                                className='flex items-center justify-center'
                                                onClick={() => {
                                                    exportLaborManagementBook();
                                                    handleCloseMenu();
                                                }}>
                                                Xuất Sổ quản lý lao động
                                            </MenuItem> */}
                      {/* <MenuItem
                                                className='flex items-center justify-center'
                                                onClick={() => {
                                                    handleExportExcelStaff();
                                                    handleCloseMenu();
                                                }}>
                                                Xuất danh sách nhân viên
                                            </MenuItem> */}
                    </Menu>

                    <Tooltip placement='top' title='Tạo tài khoản cho nhân viên'>
                      <Button
                        // className="btn btn-success"
                        disabled={selectedStaffList.length === 0}
                        startIcon={<PersonAddIcon />}
                        onClick={() => handleOpentCreateUserForlStaff()}>
                        {t("general.button.createUser")}
                      </Button>
                    </Tooltip>

                    {values?.staffWorkShiftType == LocalConstants.StaffWorkShiftType.FIXED.value &&
                      values?.staffLeaveShiftType ==
                      LocalConstants.StaffLeaveShiftType.FIXED.value && (
                        <Tooltip placement='top' title='Phân ca làm việc cố định cho các nhân '>
                          <Button
                            // className="btn btn-success"
                            disabled={selectedStaffList.length === 0}
                            startIcon={<DateRangeIcon />}
                            onClick={() => handleOpenFixShiftDateRangePopup()}>
                            Tạo lịch làm việc
                          </Button>
                        </Tooltip>
                      )}

                    <GlobitsColorfulThemePopup
                      open={openPopupConfirmDownloadExcelListStaff}
                      handleClose={handleClose}
                      size={"sm"}
                      onConfirm={() => {
                        handleClose();
                        handleDownloadExcelListStaff();
                      }}>
                      <ExportExcelConfirmWarningContent />
                    </GlobitsColorfulThemePopup>

                    {/* <Button
                      // className="btn btn-danger"
                      startIcon={<NoteIcon />}
                      onClick={importExcel}
                    >
                      {t("general.button.importExcel")}
                    </Button> */}
                  </ButtonGroup>
                </Grid>

                <input
                  type='file'
                  id='fileNewExcel'
                  style={{ display: "none" }}
                  onChange={uploadFileExcelListNewStaff}
                />

                <input
                  type='file'
                  id='fileExcel'
                  style={{ display: "none" }}
                  onChange={uploadFileExcelListStaff}
                />

                <Grid item xs={12}
                // xl={8}
                >
                  <div className='flex justify-between align-center'>
                    <Tooltip
                      placement='top'
                      title='Tìm kiếm theo tên, mã nhân viên, email, số điện thoại...'>
                      <GlobitsTextField
                        placeholder='Tìm kiếm theo tên, mã nhân viên, email, số điện thoại...'
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
                        Bộ lọc {countFilterActive > 0 && "(" + countFilterActive + ")"}
                      </Button>
                    </ButtonGroup>
                  </div>
                </Grid>
              </Grid>

              <Grid item xs={12}>
                <StaffFilter
                  isOpenFilter={isOpenFilter}
                  handleFilter={handleFilter}
                  handleCloseFilter={handleCloseFilter}
                  setCountFilterActive={setCountFilterActive}
                />
              </Grid>
            </Grid>
          </Form>
        );
      }}
    </Formik>
  );
}

export default memo(observer(StaffToolbar));

function ExportExcelConfirmWarningContent() {
  return (
    <div className='dialogScrollContent'>
      <h6 className='text-red'>
        <strong>{`Lưu ý: `}</strong>
        Bạn đang thực hiện thao tác xuất danh sách nhân viên. Hệ thống sẽ truy xuất và xuất toàn bộ dữ liệu nhân
        viên hiện có,
        <strong>{` có thể cần đến vài phút`}</strong>
        <br />
        <strong className='flex pt-6'>BẠN CÓ CHẮC MUỐN THỰC HIỆN HÀNH ĐỘNG NÀY?</strong>
      </h6>
    </div>
  );
}
