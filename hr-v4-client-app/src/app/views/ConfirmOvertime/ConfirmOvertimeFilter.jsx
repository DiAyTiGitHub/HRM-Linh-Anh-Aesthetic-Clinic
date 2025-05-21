import { Button, ButtonGroup, Collapse, Grid } from '@material-ui/core';
import { observer } from 'mobx-react';
import React, { memo, useEffect } from 'react';
import { useFormikContext } from 'formik';
import { useTranslation } from 'react-i18next';
import { useStore } from 'app/stores';
import RotateLeftIcon from '@material-ui/icons/RotateLeft';
import HighlightOffIcon from '@material-ui/icons/HighlightOff';
import { pagingStaff } from '../HumanResourcesInformation/StaffService';
import { pagingShiftWork } from '../ShiftWork/ShiftWorkService';
import GlobitsPagingAutocomplete from 'app/common/form/GlobitsPagingAutocomplete';
import GlobitsPagingAutocompleteV2 from "../../common/form/GlobitsPagingAutocompleteV2";
import { pagingAllOrg } from "../Organization/OrganizationService";
import { pagingAllDepartments } from "../Department/DepartmentService";
import { pagingPositionTitle } from "../PositionTitle/PositionTitleService";
import localStorageService from "../../services/localStorageService";

function ConfirmOvertimeFilter (props) {
  const {
    confirmOvertimeStore,
    userStore,
    hrRoleUtilsStore
  } = useStore ();

  const {
    searchObject,
    intactSearchObject
  } = confirmOvertimeStore;

  const {
    isAdmin,
    isManager,
    isCompensationBenifit,
    hasShiftAssignmentPermission,
    hasRoleManageHCNS
  } = hrRoleUtilsStore;

  const {
    handleCloseFilter,
    isOpenFilter,
    handleFilter
  } = props;

  const {t} = useTranslation ();
  const {
    values,
    setFieldValue,
  } = useFormikContext ();

  function handleResetFilter () {
    const newSearchObject = {
      ... JSON.parse (JSON.stringify (intactSearchObject)),
      pageIndex:searchObject.pageIndex,
      pageSize:searchObject.pageSize,
    };
    handleFilter (newSearchObject);
  }

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
                  <Grid item xs={12} className="pb-0">
                    <p className="m-0 p-0 borderThrough2">
                      Đối tượng
                    </p>
                  </Grid>

                  <Grid item xs={12}>
                    <Grid container spacing={2}
                        // className='justify-end'
                    >
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
                            handleChange={(_, value) => {
                              setFieldValue ('positionTitle', value);
                              setFieldValue ('positionTitleId', value?.id);
                            }}
                            readOnly={!canChangeFilter}
                        />
                      </Grid>
                      <Grid item xs={12} sm={6} md={4} lg={3}>
                        <GlobitsPagingAutocomplete
                            label={t ("Nhân viên")}
                            name="staff"
                            api={pagingStaff}
                            getOptionLabel={(option) => {
                              return `${option?.displayName || ""} - ${option?.staffCode || ""}`;
                            }}
                            onChange={(_, value) => {
                              setFieldValue ("staff", value);
                              setFieldValue ("staffId", value?.id? value?.id : null);
                            }}
                            readOnly={!canChangeFilter}
                        />
                      </Grid>

                      <Grid item xs={12} className="pb-0">
                        <p className="m-0 p-0 borderThrough2">
                          Tiêu chí khác
                        </p>
                      </Grid>

                      <Grid item xs={12} sm={6} md={4} lg={3}>
                        <GlobitsPagingAutocomplete
                            name="shiftWork"
                            label={t ("staffWorkSchedule.shiftWorks")}
                            api={pagingShiftWork}
                            onChange={(_, value) => {
                              setFieldValue ("shiftWork", value);
                              setFieldValue ("shiftWorkId", value?.id? value?.id : null);
                            }}
                        />
                      </Grid>

                      <Grid item xs={12} sm={6} md={4} lg={3}>
                        <GlobitsPagingAutocomplete
                            label={t ("Người xác nhận")}
                            name="coordinator"
                            api={pagingStaff}
                            getOptionLabel={(option) => {
                              return `${option?.displayName || ""} - ${option?.staffCode || ""}`;
                            }}
                            onChange={(_, value) => {
                              setFieldValue ("coordinator", value);
                              setFieldValue ("coordinatorId", value?.id? value?.id : null);
                            }}
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

export default memo (observer (ConfirmOvertimeFilter));