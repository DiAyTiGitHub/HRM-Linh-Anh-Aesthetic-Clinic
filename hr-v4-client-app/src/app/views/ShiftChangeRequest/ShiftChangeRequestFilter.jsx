import { Button, ButtonGroup, Collapse, Grid } from "@material-ui/core";
import HighlightOffIcon from "@material-ui/icons/HighlightOff";
import RotateLeftIcon from "@material-ui/icons/RotateLeft";
import GlobitsPagingAutocompleteV2 from "app/common/form/GlobitsPagingAutocompleteV2";
import localStorageService from "app/services/localStorageService";
import { useStore } from "app/stores";
import { useFormikContext } from "formik";
import { observer } from "mobx-react";
import React, { memo, useEffect } from "react";
import { useTranslation } from "react-i18next";
import { pagingHasPermissionDepartments, pagingStaff } from "../HumanResourcesInformation/StaffService";
import { pagingAllOrg } from "../Organization/OrganizationService";
import { pagingPositionTitle } from "../PositionTitle/PositionTitleService";
import { getCurrentStaff } from "../profile/ProfileService";
import GlobitsDateTimePicker from "../../common/form/GlobitsDateTimePicker";
import SearchIcon from "@material-ui/icons/Search";

function ShiftChangeRequestFilter (props) {
  const {shiftChangeRequestStore} = useStore ();
  const {searchObject, intactSearchObject, handleSetSearchObject, pagingShiftChangeRequest} =
      shiftChangeRequestStore;

  const {handleCloseFilter, isOpenFilter, handleFilter} = props;

  const {t} = useTranslation ();
  const {values, setFieldValue, setValues, handleReset, handleSubmit} = useFormikContext ();

  function handleResetFilter () {
    const newSearchObject = {
      ... JSON.parse (JSON.stringify (intactSearchObject)),
      pageIndex:searchObject.pageIndex,
      pageSize:searchObject.pageSize,
    };
    handleFilter (newSearchObject);
  }

  const {hrRoleUtilsStore} = useStore ();

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
                <Grid container spacing={2}
                      className='justify-end'
                >
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
                              pageSize:10,
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
                    </Grid>
                  </Grid>
                  <Grid item xs={12} className='pb-0'>
                    <p className='m-0 p-0 borderThrough2'>Thông tin nhân viên yêu cầu đổi ca</p>
                  </Grid>
                  <Grid item xs={12} sm={6} md={4} lg={3}>
                    <GlobitsPagingAutocompleteV2
                        label={t ("Nhân viên")}
                        name='staff'
                        api={pagingStaff}
                        getOptionLabel={(option) => {
                          return `${option?.displayName} - ${option?.staffCode}`;
                        }}
                        readOnly={!canChangeFilter}
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

                <div className='py-8 mt-12 border-bottom-fade border-top-fade'>
                  <div className='flex justify-end'>
                    <ButtonGroup color='container' aria-label='outlined primary button group'>
                      <Button startIcon={<SearchIcon className={``}/>}
                              className="ml-8 d-inline-flex py-2 px-8 btnHrStyle" type="submit"> Tìm kiếm </Button>
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

export default memo (observer (ShiftChangeRequestFilter));
