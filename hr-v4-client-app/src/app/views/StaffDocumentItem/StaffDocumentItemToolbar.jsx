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
import StaffDocumentItemFilter from "./StaffDocumentItemFilter";
import DeleteIcon from "@material-ui/icons/Delete";
import { getFullYear, getMonth } from "../../LocalFunction";
import { toast } from "react-toastify";
import * as Yup from "yup";
import moment from "moment";

toast.configure ({
  autoClose:2000,
  draggable:false,
  limit:3,
});

function StaffDocumentItemToolbar () {
  const {staffDocumentItemStore, hrRoleUtilsStore} = useStore ();
  const {t} = useTranslation ();
  const [currentMonth, setCurrentMonth] = useState (new Date ());

  const {
    handleDeleteList,
    pagingStaffDocumentItem,
    handleOpenCreateEdit,
    searchObject,
    listOnDelete,
    handleSetSearchObject
  } = staffDocumentItemStore;

  async function handleFilter (values) {

    const newSearchObject = {
      ... values,
      pageIndex:1,
    };
    handleSetSearchObject (newSearchObject);
    await pagingStaffDocumentItem ();
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
  const validationSchema = Yup.object ({
    fromDate:Yup.date ()
        .transform ((castValue, originalValue) => (originalValue? new Date (originalValue) : castValue))
        // .required(t("validation.required"))
        .typeError ("Từ ngày không đúng định dạng")
        .nullable (),

    toDate:Yup.date ()
        .transform ((castValue, originalValue) => (originalValue? new Date (originalValue) : castValue))
        // .required(t("validation.required"))
        .typeError ("Đến ngày không đúng định dạng")
        .nullable ()
        .test ("is-greater-or-equal", "Đến ngày phải lớn hơn hoặc bằng Từ ngày", function (value) {
          const {fromDate} = this.parent;
          if (fromDate && value) {
            return moment (value).isSameOrAfter (moment (fromDate), "date");
          }
          return true;
        }),
  });
  return (
      <Formik
          enableReinitialize
          initialValues={searchObject}
          onSubmit={handleFilter}
          validationSchema={validationSchema}
      >
        {({resetForm, values, setFieldValue, setValues}) => {
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
                    <Grid item xs={6}>
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

                  <StaffDocumentItemFilter
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

export default memo (observer (StaffDocumentItemToolbar));