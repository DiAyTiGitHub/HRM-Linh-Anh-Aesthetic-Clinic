import { observer } from "mobx-react";
import React, { memo, useState } from "react";
import GlobitsTextField from "app/common/form/GlobitsTextField";
import AddIcon from "@material-ui/icons/Add";
import SearchIcon from '@material-ui/icons/Search';
import { Form, Formik } from "formik";
import { Button, ButtonGroup, Grid, Tooltip } from "@material-ui/core";
import { useStore } from "app/stores";
import { useTranslation } from "react-i18next";
import FilterListIcon from '@material-ui/icons/FilterList';
import HrIntroduceCostFilter from "./HrIntroduceCostFilter";
import DeleteIcon from "@material-ui/icons/Delete";
import GlobitsSelectInput from "../../common/form/GlobitsSelectInput";
import { LIST_MONTH, LIST_YEAR } from "../../LocalConstants";
import { getFullYear, getMonth } from "../../LocalFunction";
import { toast } from "react-toastify";
import CloudDownloadIcon from "@material-ui/icons/CloudDownload";

toast.configure ({
  autoClose:2000,
  draggable:false,
  limit:3,
});

function HrIntroduceCostToolbar () {
  const {hrIntroduceCostStore, hrRoleUtilsStore} = useStore ();
  const {t} = useTranslation ();
  const [currentMonth, setCurrentMonth] = useState (new Date ());

  const {
    handleDeleteList,
    pagingHrIntroduceCost,
    handleOpenCreateEdit,
    searchObject,
    listOnDelete,
    handleSetSearchObject,
    handleExcelIntroduceCostData
  } = hrIntroduceCostStore;

  async function handleFilter (values) {

    const newSearchObject = {
      ... values,
      pageIndex:1,
    };
    handleSetSearchObject (newSearchObject);
    await pagingHrIntroduceCost ();
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

  const {isManager, isAdmin} = hrRoleUtilsStore;

  return (
      <Formik
          enableReinitialize
          initialValues={searchObject}
          onSubmit={handleFilter}
      >
        {({resetForm, values, setFieldValue, setValues}) => {
          const handleChangeMonth = (date, isChangeToday) => {

            setCurrentMonth (date);
            setValues ({
              month:getMonth (date) + 1,
              year:getFullYear (date)
            });
          }
          return (
              <Form autoComplete="off">
                <Grid item xs={12}>
                  <Grid container spacing={2}>
                    <Grid item xs={6}>
                      {(isManager || isAdmin) && (
                          <ButtonGroup
                              color="container"
                              aria-label="outlined primary button group"
                          >
                            <Button
                                startIcon={<AddIcon/>}
                                onClick={() => handleOpenCreateEdit ()}
                            >
                              {t ("general.button.add")}
                            </Button>
                            <Tooltip
                                placement="top"
                                title={"Tải xuống danh sách thưởng giới thiệu nhân sự"}
                                arrow
                            >
                              <Button
                                  startIcon={<CloudDownloadIcon/>}
                                  onClick={() => handleExcelIntroduceCostData ()}
                              >
                                Xuất Excel
                              </Button>
                            </Tooltip>
                            <Button
                                fullWidth
                                startIcon={<DeleteIcon/>}
                                onClick={handleDeleteList}
                                disabled={listOnDelete?.length <= 0}
                            >
                              Xóa
                            </Button>
                          </ButtonGroup>
                      )}
                    </Grid>
                    <Grid xs={6} container spacing={1} className="flex flex-end align-center">
                      <Grid item xs={6} md={4} className="flex items-center">
                        <div className="flex items-center w-100">
                          <p className="whitespace-nowrap font-semibold pr-10">Tháng:</p>
                          <div className="w-100">
                            <GlobitsSelectInput
                                hideNullOption
                                name="month"
                                options={LIST_MONTH}
                                handleChange={({target}) => handleChangeMonth (new Date (getFullYear (currentMonth), target.value - 1))}
                            />
                          </div>
                        </div>
                      </Grid>

                      <Grid item xs={6} md={4} className="flex items-center">
                        <div className="flex items-center w-100">
                          <b className="whitespace-nowrap font-semibold pr-10">Năm:</b>
                          <div className="w-100">
                            <GlobitsSelectInput
                                hideNullOption
                                name='year'
                                options={LIST_YEAR}
                                handleChange={({target}) => handleChangeMonth (new Date (currentMonth).setFullYear (target.value))}
                            />
                          </div>
                        </div>
                      </Grid>
                    </Grid>
                    <Grid item xs={12}>
                      <div className="flex justify-between align-center">
                        <Tooltip placement="top" title="Tìm kiếm theo từ khóa...">
                          <GlobitsTextField
                              placeholder="Tìm kiếm theo từ khóa..."
                              name="keyword"
                              variant="outlined"
                              notDelay
                          />
                        </Tooltip>

                        <ButtonGroup
                            className="filterButtonV4"
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
                              startIcon={<FilterListIcon
                                  className={` filterRotateIcon ${isOpenFilter && 'onRotate'}`}/>}
                              className=" d-inline-flex py-2 px-8 btnHrStyle"
                              onClick={handleTogglePopupFilter}
                          >
                            Bộ lọc
                          </Button>
                        </ButtonGroup>
                      </div>
                    </Grid>
                  </Grid>

                  <HrIntroduceCostFilter
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

export default memo (observer (HrIntroduceCostToolbar));