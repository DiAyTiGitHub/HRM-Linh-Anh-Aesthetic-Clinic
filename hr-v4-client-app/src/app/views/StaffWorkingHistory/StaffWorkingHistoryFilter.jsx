import { Button, ButtonGroup, Collapse, Grid } from "@material-ui/core";
import GlobitsPagingAutocompleteV2 from "app/common/form/GlobitsPagingAutocompleteV2";
import { observer } from "mobx-react";
import React, { memo } from "react";
import { useTranslation } from "react-i18next";
import { useStore } from "app/stores";
import RotateLeftIcon from "@material-ui/icons/RotateLeft";
import HighlightOffIcon from "@material-ui/icons/HighlightOff";
import { pagingAllowance } from "../Allowance/AllowanceService";
import GlobitsDateTimePicker from "app/common/form/GlobitsDateTimePicker";
import { pagingAllOrg } from "../Organization/OrganizationService";
import { pagingAllDepartments } from "../Department/DepartmentService";
import { pagingPosition } from "../Position/PositionService";

function StaffWorkingHistoryFilter(props) {
  const { staffWorkingHistoryStore } = useStore();
  const { t } = useTranslation();

  const { searchObject, intactSearchObject } = staffWorkingHistoryStore;

  const { isOpenFilter, handleFilter, handleCloseFilter } = props;

  function handleResetFilter() {
    const newSearchObject = {
      ...intactSearchObject,
      pageIndex: searchObject.pageIndex,
      pageSize: searchObject.pageSize,
    };
    handleFilter(newSearchObject);
  }

  return (
    <Collapse in={isOpenFilter} className='filterPopup'>
      <div className='flex flex-column'>
        <Grid container spacing={2}>
          <Grid item xs={12}>
            <div className='filterContent pt-8'>
              <Grid container spacing={2}>
                <Grid item sm={4}>
                  <GlobitsDateTimePicker label={"Ngày bắt đầu"} name='startDate' />
                </Grid>

                <Grid item sm={4}>
                  <GlobitsDateTimePicker label={"Ngày kết thúc"} name='endDate' />
                </Grid>

                <Grid item sm={4}>
                  <GlobitsPagingAutocompleteV2 name='fromOrganization' label={t("Từ tổ chức")} api={pagingAllOrg} />
                </Grid>

                <Grid item sm={4}>
                  <GlobitsPagingAutocompleteV2 name='fromDepartment' label={t("Từ phòng ban")} api={pagingAllDepartments} />
                </Grid>
                <Grid item sm={4}>
                  <GlobitsPagingAutocompleteV2 name='fromPosition' label={t("Từ vị trí")} api={pagingPosition} />
                </Grid>

                <Grid item sm={4}>
                  <GlobitsPagingAutocompleteV2 name='toOrganization' label={t("Đến tổ chức")} api={pagingAllOrg} />
                </Grid>

                <Grid item sm={4}>
                  <GlobitsPagingAutocompleteV2 name='toDepartment' label={t("Đến phòng ban")} api={pagingAllDepartments} />
                </Grid>

                <Grid item sm={4}>
                  <GlobitsPagingAutocompleteV2 name='toPosition' label={t("Đến vị trí")} api={pagingPosition} />
                </Grid>
              </Grid>

              <div className='py-8 mt-12 border-bottom-fade border-top-fade'>
                <div className='flex justify-end'>
                  <ButtonGroup color='container' aria-label='outlined primary button group'>
                    <Button onClick={handleResetFilter} startIcon={<RotateLeftIcon />}>
                      Đặt lại
                    </Button>
                    <Button type='button' onClick={handleCloseFilter} startIcon={<HighlightOffIcon />}>
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

export default memo(observer(StaffWorkingHistoryFilter));
