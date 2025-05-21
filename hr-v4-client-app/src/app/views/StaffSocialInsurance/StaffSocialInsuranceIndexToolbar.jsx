import { observer } from "mobx-react";
import React, { memo, useEffect, useState } from "react";
import DeleteOutlineIcon from "@material-ui/icons/DeleteOutline";
import { useHistory } from "react-router-dom";
import GlobitsTextField from "app/common/form/GlobitsTextField";
import AddIcon from "@material-ui/icons/Add";
import SearchIcon from "@material-ui/icons/Search";
import { Form, Formik } from "formik";
import { Grid, Button, IconButton, Tabs, Tab, AppBar, makeStyles, ButtonGroup, Tooltip } from "@material-ui/core";
import { useStore } from "app/stores";
import { useTranslation } from "react-i18next";
import MonetizationOnIcon from "@material-ui/icons/MonetizationOn";
import StaffSocialInsuranceCUForm from "./StaffSocialInsuranceCUForm";
import StaffSocialInsuranceFilter from "./StaffSocialInsuranceFilter";
import FilterListIcon from "@material-ui/icons/FilterList";
import LocalConstants, { OrganizationType } from "app/LocalConstants";
import { use } from "react";
import CloudDownloadIcon from "@material-ui/icons/CloudDownload";
import PeopleIcon from '@material-ui/icons/People';
import PersonIcon from '@material-ui/icons/Person';
import ConfirmationNumberIcon from '@material-ui/icons/ConfirmationNumber';
import MoneyOffIcon from '@material-ui/icons/MoneyOff';
import GlobitsPagingAutocompleteV2 from "../../common/form/GlobitsPagingAutocompleteV2";
import { pagingAllOrg } from "../Organization/OrganizationService";


function StaffSocialInsuranceIndexToolbar () {
  const history = useHistory ();
  const {staffSocialInsuranceStore, hrRoleUtilsStore} = useStore ();
  const {t} = useTranslation ();

  const {
    pagingStaffSocialInsurance,
    searchObject,
    listOnDelete,
    handleSetSearchObject,
    handleOpenCreateEdit,
    handleDeleteList,
    openCreateEditPopup,
    handleOpenConfirmChangeStatus,
    handleExportBHXH,
    handleOpenAutoCreateInsuranceTicketPopup,
    handleOpenCreateSingleInsuranceTicketPopup
  } = staffSocialInsuranceStore;

  const {
    isCompensationBenifit,
    checkAllUserRoles,
    isManager,
    isAdmin
  } = hrRoleUtilsStore;

  async function handleFilter (values) {
    const newSearchObject = {
      ... values,
      pageIndex:1,
    };
    handleSetSearchObject (newSearchObject);
    await pagingStaffSocialInsurance ();
  }

  const [isOpenFilter, setIsOpenFilter] = useState (true);

  function handleCloseFilter () {
    if (isOpenFilter) {
      setIsOpenFilter (false);
    }
  }

  function handleOpenFilter () {
    if (!isOpenFilter) {
      setIsOpenFilter (true);
    }
  }

  function handleTogglePopupFilter () {
    if (isOpenFilter) {
      handleCloseFilter ();
    } else {
      handleOpenFilter ();
    }
  }

  useEffect (() => {
    checkAllUserRoles ();
  }, [isOpenFilter]);

  let canMannipulateData = false;
  if (isAdmin || isManager || isCompensationBenifit) {
    canMannipulateData = true;
  }

  return (
      <Formik enableReinitialize initialValues={searchObject} onSubmit={handleFilter}>
        {({resetForm, values, setFieldValue, setValues}) => {
          return (
              <Form autoComplete='off'>
                <Grid item xs={12}>
                  <Grid container spacing={2}>
                    <Grid item xs={12}>
                      {(canMannipulateData) && (
                          <ButtonGroup color='container' aria-label='outlined primary button group'>
                            {/* <Button
                                                startIcon={<AddIcon />}
                                                type='button'
                                                onClick={() => {
                                                    handleOpenCreateEdit();
                                                }}>
                                                Thêm mới
                                            </Button> */}


                            <Tooltip
                                arrow
                                placement="top"
                                title="Tạo phiếu BHXH cho toàn bộ nhân viên được đóng BHXH"
                            >
                              <Button
                                  startIcon={<ConfirmationNumberIcon/>}
                                  onClick={handleOpenAutoCreateInsuranceTicketPopup}
                              >
                                Tạo danh sách
                              </Button>
                            </Tooltip>

                            <Tooltip
                                arrow
                                placement="top"
                                title="Tạo phiếu BHXH cho toàn bộ nhân viên được đóng BHXH"
                            >
                              <Button
                                  startIcon={<AddIcon/>}
                                  onClick={handleOpenCreateSingleInsuranceTicketPopup}
                              >
                                Thêm mới
                              </Button>
                            </Tooltip>


                            <Tooltip
                                arrow
                                placement="top"
                                title="Xóa phiếu bảo hiểm"
                            >
                              <Button
                                  disabled={listOnDelete?.length <= 0}
                                  startIcon={<DeleteOutlineIcon/>}
                                  onClick={handleDeleteList}>
                                {t ("general.button.delete")}
                              </Button>
                            </Tooltip>


                            <Tooltip
                                arrow
                                placement="top"
                                title="xuất danh sách theo bộ lọc"
                            >
                              <Button
                                  startIcon={<CloudDownloadIcon/>}
                                  onClick={() => handleExportBHXH ()}>
                                Xuất Excel
                              </Button>
                            </Tooltip>

                            <Tooltip
                                arrow
                                placement="top"
                                title="Đánh dấu là chưa chi trả"
                            >
                              <Button
                                  disabled={listOnDelete?.length <= 0}
                                  startIcon={<MoneyOffIcon/>}
                                  onClick={() =>
                                      handleOpenConfirmChangeStatus (
                                          LocalConstants.StaffSocialInsurancePaidStatus.UNPAID.value
                                      )
                                  }>
                                Chưa chi trả
                              </Button>
                            </Tooltip>

                            <Tooltip
                                arrow
                                placement="top"
                                title="Đánh dấu là đã chi trả"
                            >
                              <Button
                                  disabled={listOnDelete?.length <= 0}
                                  startIcon={<MonetizationOnIcon/>}
                                  onClick={() =>
                                      handleOpenConfirmChangeStatus (
                                          LocalConstants.StaffSocialInsurancePaidStatus.PAID.value
                                      )
                                  }>
                                Chi trả
                              </Button>
                            </Tooltip>


                          </ButtonGroup>
                      )}
                    </Grid>

                    <Grid item xs={12}>
                      <Grid container spacing={1} className={"flex flex-space-between"}>
                        <Grid item xs={12} md={4} className='flex items-center'
                              style={{width:"150px"}}>
                          <div className='flex items-center h-100 flex-end pr-10'>
                            <p className='no-wrap-text'>
                              <b>Pháp nhân:</b>
                            </p>
                          </div>
                          <div style={{width:"250px"}}>
                            <GlobitsPagingAutocompleteV2
                                name='contractOrganization'
                                // label='Pháp nhân'
                                api={pagingAllOrg}
                                searchObject={{
                                  organizationType:OrganizationType.LEGAL_ENTITY.value
                                }}
                            />
                          </div>
                        </Grid>

                        <Grid item xs={12} md={8}>
                          <div className='flex justify-between align-center'>
                            <Tooltip placement='top' title='Tìm kiếm theo mã, tên nhân viên...'>
                              <GlobitsTextField
                                  placeholder='Tìm kiếm theo mã, tên nhân viên......'
                                  name='keyword'
                                  variant='outlined'
                                  notDelay
                              />
                            </Tooltip>

                            <ButtonGroup
                                className='filterButtonV4'
                                color='container'
                                aria-label='outlined primary button group'>
                              <Button
                                  startIcon={<SearchIcon className={``}/>}
                                  className='ml-8 d-inline-flex py-2 px-8 btnHrStyle'
                                  type='submit'>
                                Tìm kiếm
                              </Button>
                              <Button
                                  startIcon={
                                    <FilterListIcon
                                        className={` filterRotateIcon ${isOpenFilter && "onRotate"
                                        }`}
                                    />
                                  }
                                  className=' d-inline-flex py-2 px-8 btnHrStyle'
                                  onClick={handleTogglePopupFilter}>
                                Bộ lọc
                              </Button>
                            </ButtonGroup>
                          </div>
                        </Grid>
                      </Grid>
                    </Grid>
                  </Grid>

                  <StaffSocialInsuranceFilter
                      handleFilter={handleFilter}
                      isOpenFilter={isOpenFilter}
                      handleCloseFilter={handleCloseFilter}
                  />

                  {openCreateEditPopup && <StaffSocialInsuranceCUForm/>}
                </Grid>
              </Form>
          );
        }}
      </Formik>
  );
}

export default memo (observer (StaffSocialInsuranceIndexToolbar));
