import { Button, ButtonGroup, Grid, Tooltip } from "@material-ui/core";
import { DoneAll } from "@material-ui/icons";
import AddIcon from "@material-ui/icons/Add";
import DeleteOutlineIcon from "@material-ui/icons/DeleteOutline";
import SearchIcon from '@material-ui/icons/Search';
import GlobitsTextField from "app/common/form/GlobitsTextField";
import LocalConstants from "app/LocalConstants";
import { useStore } from "app/stores";
import { Form, Formik } from "formik";
import { observer } from "mobx-react";
import { memo, useState } from "react";
import { useTranslation } from "react-i18next";
import { useHistory } from "react-router-dom";
import OvertimeRequestFilter from "./OvertimeRequestFilter";
import CloseIcon from '@material-ui/icons/Close';
import FilterListIcon from '@material-ui/icons/FilterList';
import * as Yup from "yup";
import moment from "moment";
import GlobitsPagingAutocompleteV2 from "../../common/form/GlobitsPagingAutocompleteV2";
import { pagingSalaryPeriod } from "../Salary/SalaryPeriod/SalaryPeriodService";
import GetAppIcon from "@material-ui/icons/GetApp";
import CloudUploadIcon from "@material-ui/icons/CloudUpload";

function OvertimeRequestIndexToolbar() {
  const history = useHistory();
  const { overtimeRequestStore, hrRoleUtilsStore } = useStore();
  const { t } = useTranslation();

  const {
    pagingOvertimeRequest,
    searchObject,
    listChosen,
    handleSetSearchObject,
    handleDeleteList,
    handleOpenCreateEdit,
    handleOpenConfirmUpdateStatusPopup,
    handleDownloadOTImportTemplate,
    handleImportExcelOvertimeRequest
  } = overtimeRequestStore;

  function handlePreSubmit(values) {
    const newValues = {
      ...values,
      departmentId: values?.department?.id,
      organizationId: values?.organization?.id,
      positionTitleId: values?.positionTitle?.id,
    };
    return newValues;
  }

  async function handleFilter(values) {
    const newSearchObject = {
      ...handlePreSubmit(values), pageIndex: 1,
    };
    handleSetSearchObject(newSearchObject);
    await pagingOvertimeRequest();
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

  return (
    <Formik
      enableReinitialize
      initialValues={searchObject}
      onSubmit={handleFilter}
      validationSchema={validationSchema}
    >
      {({ resetForm, values, setFieldValue, setValues }) => {

        return (<Form autoComplete="off">
          <Grid item xs={12}>
            <Grid container spacing={2}>
              <Grid item xs={12}>
                <ButtonGroup
                  color="container"
                  aria-label="outlined primary button group"
                >
                  <Tooltip
                    arrow
                    placement="top"
                    title="Tạo mới yêu cầu"
                  >
                    <Button
                      startIcon={<AddIcon />}
                      onClick={() => handleOpenCreateEdit()}
                    >
                      {t("general.button.add")}
                    </Button>
                  </Tooltip>

                  {canApproveOrUnApprove && (
                    <Tooltip
                      arrow
                      placement="top"
                      title="Tải mẫu nhập excel yêu cầu xác nhận làm thêm giờ"
                    >
                      <Button
                        startIcon={<GetAppIcon />}
                        onClick={() => handleDownloadOTImportTemplate()}
                      >
                        Tải mẫu nhập
                      </Button>
                    </Tooltip>
                  )}

                  {(canApproveOrUnApprove) && (
                    <Tooltip placement='top' title='Nhập excel yêu cầu xác nhận làm thêm giờ'>
                      <Button
                        startIcon={<CloudUploadIcon />}
                        onClick={() => document.getElementById("fileExcel").click()}
                      >
                        {t("general.button.importExcel")}
                      </Button>
                    </Tooltip>
                  )}


                  {(canApproveOrUnApprove) && (
                    <Tooltip
                      arrow
                      placement="top"
                      title="Đánh dấu là đã phê duyệt yêu cầu"
                    >
                      <Button
                        disabled={listChosen?.length === 0}
                        startIcon={<DoneAll />}
                        onClick={() => handleOpenConfirmUpdateStatusPopup(LocalConstants.OvertimeRequestApprovalStatus.APPROVED.value)}
                      >
                        Duyệt
                      </Button>
                    </Tooltip>
                  )}

                  {(canApproveOrUnApprove) && (
                    <Tooltip
                      arrow
                      placement="top"
                      title="Đánh dấu là không phê duyệt yêu cầu"
                    >
                      <Button
                        disabled={listChosen?.length === 0}
                        startIcon={<CloseIcon />}
                        onClick={() => handleOpenConfirmUpdateStatusPopup(LocalConstants.OvertimeRequestApprovalStatus.NOT_APPROVED.value)}
                      >
                        Không duyệt
                      </Button>
                    </Tooltip>
                  )}

                  <Tooltip
                    arrow
                    placement="top"
                    title="Xóa yêu cầu"
                  >
                    <Button
                      disabled={listChosen?.length === 0}
                      startIcon={<DeleteOutlineIcon />}
                      onClick={handleDeleteList}
                    >
                      {t("general.button.delete")}
                    </Button>
                  </Tooltip>


                </ButtonGroup>
              </Grid>

              {(canApproveOrUnApprove) && (
                <>
                  <input
                    type='file'
                    id='fileExcel'
                    style={{ display: "none" }}
                    onChange={handleImportExcelOvertimeRequest}
                  />

                </>
              )}

              <Grid item xs={12} md={6} className='flex items-center' style={{ width: "150px" }}>
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

              <Grid item xs={12} md={6}>
                <div className="flex justify-between align-center">
                  <Tooltip placement="top" title="Tìm kiếm theo từ khóa...">
                    <GlobitsTextField
                      placeholder="Tìm kiếm theo từ khóa..."
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

            <OvertimeRequestFilter
              isOpenFilter={isOpenFilter}
              handleFilter={handleFilter}
              handleCloseFilter={handleCloseFilter}
            />
          </Grid>
        </Form>);
      }}
    </Formik>);
}

export default memo(observer(OvertimeRequestIndexToolbar));