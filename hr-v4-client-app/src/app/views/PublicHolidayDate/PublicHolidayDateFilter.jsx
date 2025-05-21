import { Button, ButtonGroup, Collapse, Grid } from '@material-ui/core';
import GlobitsPagingAutocompleteV2 from 'app/common/form/GlobitsPagingAutocompleteV2';
import { observer } from 'mobx-react';
import React, { memo } from 'react';
import { useTranslation } from 'react-i18next';
import { useStore } from 'app/stores';
import RotateLeftIcon from '@material-ui/icons/RotateLeft';
import HighlightOffIcon from '@material-ui/icons/HighlightOff';
import { pagingAllowanceTypes } from '../AllowanceType/AllowanceTypeService';
import GlobitsSelectInput from 'app/common/form/GlobitsSelectInput';
import LocalConstants from 'app/LocalConstants';
import { getFirstDateOfMonth, getLastDateOfMonth } from 'app/LocalFunction';
import SearchIcon from "@material-ui/icons/Search";

function PublicHolidayDateFilter (props) {
  const {publicHolidayDateStore} = useStore ();
  const {t} = useTranslation ();

  const {
    searchObject,
    intactSearchObject
  } = publicHolidayDateStore;

  const {
    isOpenFilter,
    handleFilter,
    handleCloseFilter
  } = props;

  function handleResetFilter () {
    const newSearchObject = {
      ... JSON.parse (JSON.stringify (intactSearchObject)),
      pageIndex:searchObject.pageIndex,
      pageSize:searchObject.pageSize,
      fromDate:getFirstDateOfMonth (),
      toDate:getLastDateOfMonth ()
    };
    handleFilter (newSearchObject);
  }

  return (
      <Collapse in={isOpenFilter} className="filterPopup">
        <div className="flex flex-column">
          <Grid container spacing={2}>
            <Grid item xs={12}>
              <div className="filterContent pt-8">
                <Grid container spacing={2}
                      className={"flex flex-end"}
                >
                  <Grid item xs={12} sm={4}>
                    <GlobitsSelectInput
                        label={"Loại ngày nghỉ"}
                        name='holidayType'
                        keyValue='value'
                        options={LocalConstants.HolidayLeaveType.getListData ()}
                    />
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

export default memo (observer (PublicHolidayDateFilter));