import { Button, ButtonGroup, Grid } from "@material-ui/core";
import AddIcon from "@material-ui/icons/Add";
import SearchIcon from "@material-ui/icons/Search";
import GlobitsTextField from "app/common/form/GlobitsTextField";
import { useStore } from "app/stores";
import { Form, Formik } from "formik";
import { observer } from "mobx-react";
import { memo, useState } from "react";
import { useTranslation } from "react-i18next";
import { useHistory } from "react-router-dom";
import StaffPersonalSalaryItemValueFilter from "./StaffPersonalSalaryItemValueFilter";

function StaffPersonalSalaryItemValue() {
  const history = useHistory();
  const { staffSalaryItemValueStore } = useStore();
  const { t } = useTranslation();

  const {  handleOpenCreateEdit,  searchObject, updatePageData,} = staffSalaryItemValueStore;

  // async function handleFilter(values) {
  //     const newSearchObject = {
  //         ...values,
  //         pageIndex: 1,
  //     };
  //     handleSetSearchObject(newSearchObject);
  //     await pagingSalaryItem();
  // }

  const handleFilter = async (values) => {
    updatePageData(values?.keyword);
  };

  const [isOpenFilter, setIsOpenFilter] = useState(false);

  function handleCloseFilter() {
    if (isOpenFilter) {
      setIsOpenFilter(false);
    }
  }

  function handleOpenFilter() {
    if (!isOpenFilter) {
      setIsOpenFilter(true);
    }
  }
  function handleTogglePopupFilter() {
    if (isOpenFilter) handleCloseFilter();
    else handleOpenFilter();
  }

  return (
    <Formik enableReinitialize initialValues={JSON.parse(JSON.stringify(searchObject))} onSubmit={handleFilter}>
      {({ resetForm, values, setFieldValue, setValues }) => {
        return (
          <Form autoComplete='off'>
            <Grid item xs={12}>
              <Grid container spacing={2}>
                <Grid item xs={12} md={6}>
                  <ButtonGroup color='container' aria-label='outlined primary button group'>
                    <Button
                      startIcon={<AddIcon />}
                      type='button'
                      onClick={() => {
                        handleOpenCreateEdit();
                      }}>
                      Thêm mới
                    </Button>
                  </ButtonGroup>
                </Grid>

                <Grid item xs={12} md={6}>
                  <div className='flex justify-between align-center'>
                    <GlobitsTextField placeholder='Tìm kiếm theo mã, tên thành phần lương...' name='keyword' variant='outlined' notDelay />

                    <ButtonGroup className='filterButtonV4' color='container' aria-label='outlined primary button group'>
                      <Button startIcon={<SearchIcon className={``} />} className='ml-8 d-inline-flex py-2 px-8 btnHrStyle' type='submit'>
                        Tìm kiếm
                      </Button>
                      {/* <Button
                                                startIcon={<FilterListIcon className={` filterRotateIcon ${isOpenFilter && 'onRotate'}`} />}
                                                className=" d-inline-flex py-2 px-8 btnHrStyle"
                                                onClick={handleTogglePopupFilter}
                                            >
                                                Bộ lọc
                                            </Button> */}
                    </ButtonGroup>
                  </div>
                </Grid>
              </Grid>

              <StaffPersonalSalaryItemValueFilter handleFilter={handleFilter} handleCloseFilter={handleCloseFilter} isOpenFilter={isOpenFilter} />
            </Grid>
          </Form>
        );
      }}
    </Formik>
  );
}

export default memo(observer(StaffPersonalSalaryItemValue));
