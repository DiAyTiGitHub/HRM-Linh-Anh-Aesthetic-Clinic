import { Button, ButtonGroup, Collapse, Grid } from '@material-ui/core';
import GlobitsPagingAutocompleteV2 from 'app/common/form/GlobitsPagingAutocompleteV2';
import { observer } from 'mobx-react';
import React, { memo, useEffect } from 'react';
import { useFormikContext } from 'formik';
import { useTranslation } from 'react-i18next';
import { useStore } from 'app/stores';
import RotateLeftIcon from '@material-ui/icons/RotateLeft';
import HighlightOffIcon from '@material-ui/icons/HighlightOff';
import { pagingHasPermissionDepartments, pagingStaff } from '../HumanResourcesInformation/StaffService';
import { pagingAllOrg } from "app/views/Organization/OrganizationService";
import { pagingPositionTitle } from "app/views/PositionTitle/PositionTitleService";
import localStorageService from "../../services/localStorageService";
import SearchIcon from "@material-ui/icons/Search";

const searchObject = {
  keyword:"",
  department:null,
  organization:null
}

function ShiftWorkFilter (props) {
  const {shiftWorkStore} = useStore ();
  const {resetForm} = useFormikContext ()

  const {updatePageData} = shiftWorkStore;

  const {
    handleCloseFilter,
    isOpenFilter,
    handleFilter
  } = props;

  const {t} = useTranslation ();
  const {values, setFieldValue} = useFormikContext ();

  function handleResetFilter () {
    resetForm ()
    handleFilter (searchObject);
  }

  return (
      <Collapse in={isOpenFilter} className="filterPopup">
        <div className="flex flex-column">
          <Grid container spacing={2}>
            <Grid item xs={12}>
              <div className="filterContent pt-8">
                <Grid container spacing={2}>
                  <Grid item xs={12}>
                    <Grid container spacing={2}
                          className={"flex flex-end"}
                    >
                      <Grid item xs={12} sm={6} md={4} lg={3}>
                        <GlobitsPagingAutocompleteV2
                            name="organization"
                            label="Đơn vị"
                            api={pagingAllOrg}
                            handleChange={(_, value) => {
                              setFieldValue ("organization", value)
                              setFieldValue ("department", null)
                            }}
                        />
                      </Grid>

                      <Grid item xs={12} sm={6} md={4} lg={3}>
                        <GlobitsPagingAutocompleteV2
                            label={"Phòng ban"}
                            name="department"
                            api={pagingHasPermissionDepartments}
                            searchObject={{
                              pageIndex:1, pageSize:9999, keyword:"",
                              organizationId:values?.organization?.id,
                            }}
                            getOptionLabel={(option) =>
                                [option?.name, option?.code].filter (Boolean).join (' - ') || ''
                            }
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
                          startIcon={<SearchIcon className={``}/>}
                          className="ml-8 d-inline-flex py-2 px-8 btnHrStyle"
                          type="submit"
                      >
                        Tìm kiếm
                      </Button>
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

export default memo (observer (ShiftWorkFilter));