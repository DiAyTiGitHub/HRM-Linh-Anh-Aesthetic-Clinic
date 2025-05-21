import { observer } from "mobx-react";
import React, { memo, useEffect, useState } from "react";
import DeleteOutlineIcon from "@material-ui/icons/DeleteOutline";
import GlobitsTextField from "app/common/form/GlobitsTextField";
import AddIcon from "@material-ui/icons/Add";
import SearchIcon from "@material-ui/icons/Search";
import { Form, Formik } from "formik";
import { Grid, Button, ButtonGroup, Tooltip } from "@material-ui/core";
import { useStore } from "app/stores";
import { useTranslation } from "react-i18next";
import FilterListIcon from "@material-ui/icons/FilterList";
import HrResourcePlanFilter from "./HrResourcePlanFilter";
import DoneIcon from '@material-ui/icons/Done';
import ClearIcon from '@material-ui/icons/Clear';
import LocalConstants from "app/LocalConstants";

function HrResourcePlanIndexToolbar () {
  const {hrResourcePlanStore, hrRoleUtilsStore} = useStore ();
  const {t} = useTranslation ();

  const {
    tabCU,
    handleDeleteList,
    pagingHrResourcePlan,
    pagingAggregateHrResourcePlan,
    handleOpenCreateEdit,
    searchObject,
    listOnDelete,
    handleSetSearchObject,
    handleOpenConfirmStatusPopup,
  } = hrResourcePlanStore;

  const {
    isAdmin,
    isPositionManager,
    isGeneralDirector,
    isDeputyGeneralDirector
  } = hrRoleUtilsStore;

  async function handleFilter (values) {
    const newSearchObject = {
      ... values,
      pageIndex:1,
    };
    handleSetSearchObject (newSearchObject);
    if (tabCU === 0) {
      await pagingHrResourcePlan ();
    } else if (tabCU === 1) {
      await pagingAggregateHrResourcePlan ();
    }
  }

  const [isOpenFilter, setIsOpenFilter] = useState (false);

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
    if (isOpenFilter) handleCloseFilter ();
    else handleOpenFilter ();
  }

  return (
      <Formik
          enableReinitialize
          initialValues={searchObject}
          onSubmit={handleFilter}
      >
        {({resetForm, values, setFieldValue, setValues}) => {

          return (
              <Form autoComplete='off'>
                <Grid item xs={12}>
                  <Grid container spacing={2}>
                    <Grid item xs={12} xl={6}>
                      <ButtonGroup color='container' aria-label='outlined primary button group'>
                        {(isAdmin || isPositionManager) &&
                            <Tooltip
                                arrow
                                placement="top"
                                title={("Thêm mới yêu cầu định biên")}
                            >
                              <Button startIcon={<AddIcon/>}
                                      onClick={() => handleOpenCreateEdit (false, false)}>
                                Thêm mới
                              </Button>
                            </Tooltip>
                        }
                        {(isAdmin || isPositionManager) &&
                            <Tooltip
                                placement="top"
                                title="Xoá yêu cầu"
                                arrow
                            >
                              <Button
                                  disabled={listOnDelete?.length === 0}
                                  startIcon={<DeleteOutlineIcon/>}
                                  onClick={handleDeleteList}>
                                {t ("general.button.delete")}
                              </Button>
                            </Tooltip>
                        }
                        {(isAdmin || isDeputyGeneralDirector || isGeneralDirector) &&
                            <Tooltip
                                placement="top"
                                title="Phê duyệt yêu cầu"
                                arrow
                            >
                              <Button
                                  disabled={listOnDelete?.length === 0}
                                  startIcon={<DoneIcon/>}
                                  onClick={() => handleOpenConfirmStatusPopup (LocalConstants.HrResourcePlanApprovalStatus.APPROVED.value)}
                              >
                                Phê duyệt
                              </Button>
                            </Tooltip>
                        }
                        {(isAdmin || isDeputyGeneralDirector || isGeneralDirector) &&
                            <Tooltip
                                placement="top"
                                title="Không phê duyệt yêu cầu"
                                arrow
                            >
                              <Button
                                  disabled={listOnDelete?.length === 0}
                                  startIcon={<ClearIcon/>}
                                  onClick={() => handleOpenConfirmStatusPopup (LocalConstants.HrResourcePlanApprovalStatus.NOT_APPROVED.value)}
                              >
                                Không duyệt
                              </Button>
                            </Tooltip>
                        }

                        {/*{(isAdmin || isDeputyGeneralDirector) &&*/}
                        {/*    <Tooltip*/}
                        {/*        placement="top"*/}
                        {/*        title="Phó tổng giám đốc phê duyệt yêu cầu"*/}
                        {/*        arrow*/}
                        {/*    >*/}
                        {/*      <Button*/}
                        {/*          disabled={listOnDelete?.length === 0}*/}
                        {/*          startIcon={<DoneIcon/>}*/}
                        {/*          onClick={() => handleOpenViceDirectorConfirmPopup (LocalConstants.HrResourcePlanApprovalStatus.APPROVED.value)}*/}
                        {/*      >*/}
                        {/*        PTGĐ phê duyệt*/}
                        {/*      </Button>*/}
                        {/*    </Tooltip>*/}
                        {/*}*/}
                        {/*{(isAdmin || isDeputyGeneralDirector) &&*/}
                        {/*    <Tooltip*/}
                        {/*        placement="top"*/}
                        {/*        title="Phó tổng giám đốc không phê duyệt yêu cầu"*/}
                        {/*        arrow*/}
                        {/*    >*/}
                        {/*      <Button*/}
                        {/*          disabled={listOnDelete?.length === 0}*/}
                        {/*          startIcon={<ClearIcon/>}*/}
                        {/*          onClick={() => handleOpenViceDirectorConfirmPopup (LocalConstants.HrResourcePlanApprovalStatus.NOT_APPROVED.value)}*/}
                        {/*      >*/}
                        {/*        PTGĐ không phê duyệt*/}
                        {/*      </Button>*/}
                        {/*    </Tooltip>*/}
                        {/*}*/}
                        {/*{(isAdmin || isGeneralDirector) &&*/}
                        {/*    <Tooltip*/}
                        {/*        placement="top"*/}
                        {/*        title="Tổng giám đốc phê duyệt yêu cầu"*/}
                        {/*        arrow*/}
                        {/*    >*/}
                        {/*      <Button*/}
                        {/*          disabled={listOnDelete?.length === 0}*/}
                        {/*          startIcon={<DoneIcon/>}*/}
                        {/*          onClick={() => handleOpenDirectorConfirmPopup (LocalConstants.HrResourcePlanApprovalStatus.APPROVED.value)}*/}
                        {/*      >*/}
                        {/*        TGĐ phê duyệt*/}
                        {/*      </Button>*/}
                        {/*    </Tooltip>*/}
                        {/*}*/}
                        {/*{(isAdmin || isGeneralDirector) &&*/}
                        {/*    <Tooltip*/}
                        {/*        placement="top"*/}
                        {/*        title="Tổng giám đốc không phê duyệt yêu cầu"*/}
                        {/*        arrow*/}
                        {/*    >*/}
                        {/*      <Button*/}
                        {/*          disabled={listOnDelete?.length === 0}*/}
                        {/*          startIcon={<ClearIcon/>}*/}
                        {/*          onClick={() => handleOpenDirectorConfirmPopup (LocalConstants.HrResourcePlanApprovalStatus.NOT_APPROVED.value)}*/}
                        {/*      >*/}
                        {/*        TGĐ không phê duyệt*/}
                        {/*      </Button>*/}
                        {/*    </Tooltip>*/}
                        {/*}*/}
                      </ButtonGroup>
                    </Grid>

                    <Grid item xs={12} xl={6}>
                      <div className='flex justify-between align-center'>
                        <Tooltip placement='top' title='Tìm kiếm theo định biên'>
                          <GlobitsTextField
                              placeholder='Tìm kiếm theo định biên'
                              name='keyword'
                              variant='outlined'
                              notDelay
                          />
                        </Tooltip>
                        <div className='flex justify-end'>
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
                      </div>
                    </Grid>
                  </Grid>

                  <HrResourcePlanFilter
                      isOpenFilter={isOpenFilter}
                      handleFilter={handleFilter}
                      handleCloseFilter={handleCloseFilter}
                  />
                </Grid>
              </Form>
          );
        }}
      </Formik>
  );
}

export default memo (observer (HrResourcePlanIndexToolbar));
