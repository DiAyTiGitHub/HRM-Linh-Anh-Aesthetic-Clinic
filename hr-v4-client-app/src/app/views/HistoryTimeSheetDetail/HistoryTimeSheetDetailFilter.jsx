import { Button, ButtonGroup, Collapse, Grid } from "@material-ui/core";
import GlobitsPagingAutocompleteV2 from "app/common/form/GlobitsPagingAutocompleteV2";
import { observer } from "mobx-react";
import React, { memo } from "react";
import { useTranslation } from "react-i18next";
import { useStore } from "app/stores";
import RotateLeftIcon from "@material-ui/icons/RotateLeft";
import HighlightOffIcon from "@material-ui/icons/HighlightOff";
import GlobitsDateTimePicker from "app/common/form/GlobitsDateTimePicker";
import { pagingAllOrg } from "../Organization/OrganizationService";
import { useFormikContext } from "formik";
import { pagingHasPermissionDepartments, pagingStaff } from "../HumanResourcesInformation/StaffService";
import { pagingPositionTitle } from "../PositionTitle/PositionTitleService";
import { pagingShiftWork } from "../ShiftWork/ShiftWorkService";
import { pagingLeaveType } from "../LeaveType/LeaveTypeService";
import { useParams } from "react-router-dom";
import SearchIcon from "@material-ui/icons/Search";

function HistoryTimeSheetDetailFilter (props) {
  const {staffId} = useParams ();
  const {staffWorkScheduleStore, hrRoleUtilsStore} = useStore ();

  const {searchObject, intactSearchObject} = staffWorkScheduleStore;

  const {handleCloseFilter, isOpenFilter, handleFilter, oneStaff} = props;

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
      <Collapse in={isOpenFilter} className='filterPopup'>
        <div className='flex flex-column'>
          <Grid container spacing={2}>
            <Grid item xs={12}>
              <div className='filterContent pt-8'>
                <Grid container spacing={2}>
                  <Grid item xs={12}>
                    <p className='m-0 p-0 borderThrough2'>Đối tượng phân ca</p>
                  </Grid>

                  <Grid item xs={12}>
                    <Grid
                        container
                        spacing={2}
                        className='justify-end'
                    >
                      <Grid item xs={12} sm={6} md={4} lg={3}>
                        <GlobitsPagingAutocompleteV2
                            name='organization'
                            label='Đơn vị'
                            api={pagingAllOrg}
                            readOnly={!canChangeFilter}
                        />
                      </Grid>

                      <Grid item xs={12} sm={6} md={4} lg={3}>
                        <GlobitsPagingAutocompleteV2
                            label={"Phòng ban"}
                            name='department'
                            api={pagingHasPermissionDepartments}
                            searchObject={{
                              pageIndex:1,
                              pageSize:9999,
                              keyword:"",
                              organizationId:values?.organization?.id,
                            }}
                            getOptionLabel={(option) =>
                                [option?.name, option?.code].filter (Boolean).join (" - ") || ""
                            }
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
                      {!oneStaff && (
                          <Grid item xs={12} sm={6} md={4} lg={3}>
                            <GlobitsPagingAutocompleteV2
                                label={t ("Nhân viên")}
                                name='staff'
                                api={pagingStaff}
                                getOptionLabel={(option) => {
                                  return `${option?.displayName || ""} - ${option?.staffCode || ""}`;
                                }}
                                onChange={(_, value) => {
                                  setFieldValue ("staff", value);
                                  setFieldValue ("staffId", value?.id? value?.id : null);
                                }}
                                readOnly={!!staffId || !canChangeFilter}
                            />
                          </Grid>
                      )}
                    </Grid>
                  </Grid>

                  <Grid item xs={12} className='pb-0'>
                    <p className='m-0 p-0 borderThrough2'>Thông tin ca làm việc</p>
                  </Grid>

                  <Grid item xs={12}>
                    <Grid
                        container
                        spacing={2}
                        className='justify-end'
                    >
                      <Grid item xs={12} sm={6} md={4} lg={3}>
                        <GlobitsPagingAutocompleteV2
                            name='shiftWork'
                            label={t ("staffWorkSchedule.shiftWorks")}
                            api={pagingShiftWork}
                            onChange={(_, value) => {
                              setFieldValue ("shiftWork", value);
                              setFieldValue ("shiftWorkId", value?.id? value?.id : null);
                            }}
                            getOptionLabel={(option) =>
                                option?.name && option?.code
                                    ? `${option.name} - ${option.code}`
                                    : option?.name || option?.code || ""
                            }
                        />
                      </Grid>
                      <Grid item xs={12} sm={6} md={4} lg={3}>
                        <GlobitsPagingAutocompleteV2
                            name='leaveType'
                            label={t ("staffWorkSchedule.leaveType")}
                            api={pagingLeaveType}
                            onChange={(_, value) => {
                              setFieldValue ("leaveType", value);
                              setFieldValue ("leaveTypeId", value?.id? value?.id : null);
                            }}
                        />
                      </Grid>
                      <Grid item xs={12} sm={6} md={4} lg={3}>
                        <GlobitsDateTimePicker
                            label="Từ ngày"
                            name='fromDate'
                            onChange={(value) => {
                              setFieldValue ("fromDate", value);
                              setFieldValue ("salaryPeriod", null);
                            }}
                        />
                      </Grid>

                      <Grid item xs={12} sm={6} md={4} lg={3}>
                        <GlobitsDateTimePicker
                            label="Đến ngày"
                            name='toDate'
                            onChange={(value) => {
                              setFieldValue ("toDate", value);
                              setFieldValue ("salaryPeriod", null);
                            }}
                        />
                      </Grid>
                    </Grid>
                  </Grid>
                </Grid>

                <div className='py-8 mt-12 border-bottom-fade border-top-fade'>
                  <div className='flex justify-end'>
                    <ButtonGroup color='container' aria-label='outlined primary button group'>
                      <Button
                          startIcon={<SearchIcon className={``}/>}
                          className='ml-8 d-inline-flex py-2 px-8 btnHrStyle'
                          type='submit'>
                        Tìm kiếm
                      </Button>
                      <Button onClick={handleResetFilter} startIcon={<RotateLeftIcon/>}>
                        Đặt lại
                      </Button>
                      <Button
                          type='button'
                          onClick={handleCloseFilter}
                          startIcon={<HighlightOffIcon/>}>
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

export default memo (observer (HistoryTimeSheetDetailFilter));
