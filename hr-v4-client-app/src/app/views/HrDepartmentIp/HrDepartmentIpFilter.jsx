import { Button, ButtonGroup, Collapse, Grid } from '@material-ui/core';
import HighlightOffIcon from '@material-ui/icons/HighlightOff';
import RotateLeftIcon from '@material-ui/icons/RotateLeft';
import GlobitsPagingAutocompleteV2 from 'app/common/form/GlobitsPagingAutocompleteV2';
import { useStore } from 'app/stores';
import { observer } from 'mobx-react';
import React, { memo, useEffect } from 'react';
import { useTranslation } from 'react-i18next';
import { pagingAllDepartments } from "../Department/DepartmentService";
import { useFormikContext } from "formik";
import localStorageService from "../../services/localStorageService";
import { pagingAllOrg } from "../Organization/OrganizationService";
import SearchIcon from "@material-ui/icons/Search";

function HrDepartmentIpFilter (props) {
  const {hrDepartmentIpStore} = useStore ();
  const {t} = useTranslation ();
  const {values} = useFormikContext ()
  const {
    intactSearchObject,
    resetStore
  } = hrDepartmentIpStore;

  const {
    isOpenFilter,
    handleFilter,
    handleCloseFilter
  } = props;

  function handleResetFilter () {
    const newSearchObject = {
      ... JSON.parse (JSON.stringify (intactSearchObject)),
    };
    handleFilter (newSearchObject);
  }

  useEffect (() => {
    let user = localStorageService.getLoginUser ();
    const newValues = {
      ... values,
      organization:user?.organization || null,
      // department:user?.department || null ,
    }
    handleFilter (newValues)

    return resetStore
  }, []);
  return (
      <Collapse in={isOpenFilter} className="filterPopup">
        <div className="flex flex-column">
          <Grid container spacing={2}>
            <Grid item xs={12}>
              <div className="filterContent pt-8">
                <Grid container spacing={2} className={"flex flex-end"}>
                  <Grid item xs={12} sm={6} md={4} lg={3}>
                    <GlobitsPagingAutocompleteV2
                        name='organization'
                        label='Đơn vị'
                        api={pagingAllOrg}
                    />
                  </Grid>
                  <Grid item xs={12} sm={6} md={4} lg={3}>
                    <GlobitsPagingAutocompleteV2
                        name="department"
                        label={"Phòng ban"}
                        api={pagingAllDepartments}
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

export default memo (observer (HrDepartmentIpFilter));