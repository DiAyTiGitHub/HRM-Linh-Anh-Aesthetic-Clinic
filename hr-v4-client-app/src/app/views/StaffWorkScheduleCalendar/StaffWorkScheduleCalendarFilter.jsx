import { Button, ButtonGroup, Grid } from '@material-ui/core';
import RotateLeftIcon from '@material-ui/icons/RotateLeft';
import SearchIcon from '@material-ui/icons/Search';
import GlobitsDateTimePicker from 'app/common/form/GlobitsDateTimePicker';
import { useStore } from 'app/stores';
import { useFormikContext } from 'formik';
import { observer } from 'mobx-react';
import { memo } from 'react';
import { useTranslation } from 'react-i18next';
import { pagingAllDepartments } from '../Department/DepartmentService';
import { pagingStaff } from '../HumanResourcesInformation/StaffService';
import { pagingAllOrg } from '../Organization/OrganizationService';
import { pagingPosition } from '../Position/PositionService';
import GlobitsPagingAutocompleteV2 from 'app/common/form/GlobitsPagingAutocompleteV2';
import { pagingPositionTitle } from "../PositionTitle/PositionTitleService";
import { SearchStaffWorkSchedule } from "app/common/Model/SearchObject/SearchStaffWorkSchedule";
import { pagingSalaryPeriod } from "../Salary/SalaryPeriod/SalaryPeriodService";

function StaffWorkScheduleCalendarFilter(props) {
  const { t } = useTranslation();

  const {
    staffWorkScheduleCalendarStore,
    hrRoleUtilsStore

  } = useStore();

  const {
    isAdmin,
    isManager,
    hasShiftAssignmentPermission
  } = hrRoleUtilsStore;

  const {
    searchObject,
    intactSearchObject
  } = staffWorkScheduleCalendarStore;


  const {
    handleFilter
  } = props;

  const { values, setFieldValue, setValues, handleReset, handleSubmit } = useFormikContext();

  function handleResetFilter() {
    const newSearchObject = {
      ...intactSearchObject,
      pageIndex: searchObject.pageIndex,
      pageSize: searchObject.pageSize,

    };
    handleFilter(newSearchObject);
  }

  return (
    // <Collapse in={isOpenFilter} className="filterPopup">
    <div className="flex flex-column">
      <Grid container spacing={2}>
        <Grid item xs={12}>
          <div className="filterContent pt-8">
            <Grid container spacing={2}>

              <Grid item xs={12}>
                <p className="m-0 p-0 borderThrough2">Khoảng thời gian thống kê</p>
              </Grid>

              <Grid item xs={12} sm={4} lg={3}>
                <GlobitsPagingAutocompleteV2
                  name='salaryPeriod'
                  label='Kỳ lương'
                  api={pagingSalaryPeriod}
                  handleChange={(_, value) => {
                    setFieldValue("salaryPeriod", value);
                    // setFieldValue("salaryPeriodId", value?.id);
                    setFieldValue("fromDate", value?.fromDate);
                    setFieldValue("toDate", value?.toDate);
                  }}
                />
              </Grid>

              <Grid item xs={12} sm={4} lg={3}>
                <GlobitsDateTimePicker
                  label="Từ ngày"
                  name="fromDate"
                  onChange={(newDate) => {
                    setFieldValue("fromDate", newDate);
                    setFieldValue("salaryPeriod", null);
                  }}
                />
              </Grid>

              <Grid item xs={12} sm={4} lg={3}>
                <GlobitsDateTimePicker
                  label="Đến ngày"
                  name="toDate"
                  onChange={(newDate) => {
                    setFieldValue("toDate", newDate);
                    setFieldValue("salaryPeriod", null);
                  }}
                />
              </Grid>

              <Grid item xs={12}>
                <p className="m-0 p-0 borderThrough2">Đối tượng thống kê</p>
              </Grid>

              <Grid item xs={12}>
                <Grid container spacing={2} className="flex">
                  <Grid item xs={12} sm={4} lg={3}>
                    <GlobitsPagingAutocompleteV2
                      name="organization"
                      label="Đơn vị"
                      api={pagingAllOrg}
                      readOnly={!hasShiftAssignmentPermission}
                    />
                  </Grid>

                  <Grid item xs={12} sm={4} lg={3}>
                    <GlobitsPagingAutocompleteV2
                      label={"Phòng ban"}
                      name="department"
                      api={pagingAllDepartments}
                      searchObject={{
                        pageIndex: 1, pageSize: 9999, keyword: "",
                        organizationId: values?.organization?.id,
                      }}
                      getOptionLabel={(option) =>
                        [option?.name, option?.code].filter(Boolean).join(' - ') || ''
                      }
                      readOnly={!hasShiftAssignmentPermission}

                    />
                  </Grid>

                  <Grid item xs={12} sm={4} lg={3}>
                    <GlobitsPagingAutocompleteV2
                      name="positionTitle"
                      label="Chức danh"
                      api={pagingPositionTitle}
                      searchObject={{
                        departmentId: values?.department?.id,
                      }}
                      readOnly={!hasShiftAssignmentPermission}
                    />
                  </Grid>

                  <Grid item xs={12} sm={4} lg={3}>
                    <GlobitsPagingAutocompleteV2
                      label="Nhân viên"
                      name="staff"
                      api={pagingStaff}
                      readOnly={!hasShiftAssignmentPermission}
                      getOptionLabel={(option) =>
                        [option?.displayName, option?.staffCode].filter(Boolean).join(' - ') || ''
                      }
                    />
                  </Grid>

                </Grid>
              </Grid>
            </Grid>

            <div className="pt-8 mt-12 border-top-fade">
              <div className="flex justify-end">
                <ButtonGroup
                  color="container"
                  aria-label="outlined primary button group"
                >
                  <Button
                    onClick={handleResetFilter}
                    startIcon={<RotateLeftIcon />}
                  >
                    Đặt lại
                  </Button>

                  <Button
                    startIcon={<SearchIcon className={``} />}
                    className="d-inline-flex py-2 px-8 btnHrStyle"
                    type="submit"
                  >
                    Tìm kiếm
                  </Button>
                </ButtonGroup>
              </div>
            </div>
          </div>
        </Grid>
      </Grid>
    </div>

    // </Collapse >
  );
}

export default memo(observer(StaffWorkScheduleCalendarFilter));