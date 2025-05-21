import { Button, ButtonGroup, Collapse, Grid } from '@material-ui/core';
import GlobitsPagingAutocompleteV2 from 'app/common/form/GlobitsPagingAutocompleteV2';
import { observer } from 'mobx-react';
import React, { memo } from 'react';
import { useFormikContext } from 'formik';
import { useTranslation } from 'react-i18next';
import { useStore } from 'app/stores';
import RotateLeftIcon from '@material-ui/icons/RotateLeft';
import HighlightOffIcon from '@material-ui/icons/HighlightOff';
import { pagingStaff } from 'app/views/HumanResourcesInformation/StaffService';
import { pagingAllOrg } from "app/views/Organization/OrganizationService";
import { pagingAllDepartments } from "app/views/Department/DepartmentService";
import { pagingPositionTitle } from "app/views/PositionTitle/PositionTitleService";
import GlobitsDateTimePicker from 'app/common/form/GlobitsDateTimePicker';
import SearchIcon from "@material-ui/icons/Search";


function ConfirmStaffWorkScheduleFilter (props) {
  const {confirmStaffWorkScheduleStore, hrRoleUtilsStore} = useStore ();
  const {
    searchObject,
    intactSearchObject
  } = confirmStaffWorkScheduleStore;

  const {
    handleCloseFilter,
    isOpenFilter,
    handleFilter
  } = props;

  const {t} = useTranslation ();
  const {values, setFieldValue} = useFormikContext ();

  function handleResetFilter () {
    const newSearchObject = {
      ... JSON.parse (JSON.stringify (intactSearchObject)),
      pageIndex:searchObject.pageIndex,
      pageSize:searchObject.pageSize,
    };
    handleFilter (newSearchObject);
  }

  const {
    hasShiftAssignmentPermission,
    isCompensationBenifit,
    isAdmin,
    isManager,
    hasRoleManageHCNS
  } = hrRoleUtilsStore;
  let canChangeFilter = false;
  if (isAdmin || isManager || isCompensationBenifit || hasShiftAssignmentPermission || hasRoleManageHCNS) {
    canChangeFilter = true;
  }
  return (
      <Collapse in={isOpenFilter} className="filterPopup">
        <div className="flex flex-column">
          <Grid container spacing={2}>
            <Grid item xs={12}>
              <div className="filterContent pt-8">
                <Grid container spacing={2}>

                  <Grid item xs={12}>
                    <Grid container spacing={2} className='justify-end'>
                      <Grid item xs={12} sm={6} md={4} lg={3}>
                        <GlobitsPagingAutocompleteV2
                            name='organization'
                            label='Đơn vị'
                            api={pagingAllOrg}
                            handleChange={(_, value) => {
                              setFieldValue ('organization', value);
                              setFieldValue ('department', null);
                              setFieldValue ('positionTitle', null);
                            }}
                            readOnly={!canChangeFilter}
                        />
                      </Grid>
                      <Grid item xs={12} sm={6} md={4} lg={3}>
                        <GlobitsPagingAutocompleteV2
                            name="department"
                            label={"Phòng ban"}
                            api={pagingAllDepartments}
                            searchObject={{
                              organizationId:values?.organization?.id,
                            }}
                            handleChange={(_, value) => {
                              setFieldValue ('department', value);
                              setFieldValue ('positionTitle', null);
                            }}
                            readOnly={!canChangeFilter}
                        />
                      </Grid>
                      <Grid item xs={12} sm={6} md={4} lg={3}>
                        <GlobitsPagingAutocompleteV2
                            name='positionTitle'
                            label='Chức danh'
                            api={pagingPositionTitle}
                            searchObject={{
                              departmentId:values?.department?.id,
                            }}
                            readOnly={!canChangeFilter}
                        />
                      </Grid>
                      <Grid item xs={12} sm={6} md={4} lg={3}>
                        <GlobitsPagingAutocompleteV2
                            name="staff"
                            label="Nhân viên"
                            api={pagingStaff}
                            getOptionLabel={(option) => {
                              return `${option?.displayName || ""} - ${option?.staffCode || ""}`;
                            }}
                            readOnly={!canChangeFilter}
                        />
                      </Grid>
                      <Grid item xs={12} sm={6} md={4} lg={3}>
                        <GlobitsDateTimePicker
                            label="Từ ngày"
                            name="fromDate"
                        />
                      </Grid>
                      <Grid item xs={12} sm={6} md={4} lg={3}>
                        <GlobitsDateTimePicker
                            label="Đến ngày"
                            name="toDate"
                        />
                      </Grid>
                    </Grid>
                  </Grid>

                </Grid>

                <div className="py-8 mt-12 border-bottom-fade border-top-fade">
                  <div className="flex justify-end">
                    <ButtonGroup
                        color="container"
                        aria-label="outlined primary button group"
                    >
                      <Button startIcon={<SearchIcon className={``}/>}
                              className="ml-8 d-inline-flex py-2 px-8 btnHrStyle" type="submit"> Tìm kiếm </Button>
                      <Button
                          onClick={handleResetFilter}
                          startIcon={<RotateLeftIcon/>}
                      >
                        Đặt lại
                      </Button>
                      <Button
                          type="button"
                          onClick={handleCloseFilter}
                          startIcon={<HighlightOffIcon/>}
                      >
                        Đóng bộ lọc
                      </Button>
                    </ButtonGroup>
                  </div>
                </div>
              </div>
            </Grid>
          </Grid>
        </div>

      </Collapse>
  );
}

export default memo (observer (ConfirmStaffWorkScheduleFilter));