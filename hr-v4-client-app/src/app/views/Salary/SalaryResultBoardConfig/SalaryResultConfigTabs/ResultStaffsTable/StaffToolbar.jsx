import { Button, ButtonGroup, Grid, Tooltip } from "@material-ui/core";
import FilterListIcon from "@material-ui/icons/FilterList";
import SearchIcon from "@material-ui/icons/Search";
import GlobitsTextField from "app/common/form/GlobitsTextField";
import { useStore } from "app/stores";
import { Form, Formik } from "formik";
import { observer } from "mobx-react";
import { memo, useState } from "react";
import { useTranslation } from "react-i18next";
import StaffFilter from "./StaffFilter";

/*
*   Select multiple staffs
*/
function StaffToolbar(props) {
  const {
    isDisableFilter
  } = props;

  const { t } = useTranslation();
  const { userStore } = useStore();

  const {
    usingStaffSO,
    handleSetUsingStaffSO,
    pagingLowerStaff

  } = userStore;

  async function handleFilter(values) {
    const newSearchObject = {
      ...values,
      pageIndex: 1,
    };
    handleSetUsingStaffSO(newSearchObject);
    await pagingLowerStaff();
  }

  const [isOpenFilter, setIsOpenFilter] = useState(true);

  function handleTogglePopupFilter() {
    setIsOpenFilter((prev) => !prev);
  }

  return (
    <Formik enableReinitialize initialValues={usingStaffSO} onSubmit={handleFilter}>
      {({ resetForm, values, setFieldValue, setValues }) => {
        return (
          <Form autoComplete='off'>
            <Grid item xs={12}>
              <Grid container spacing={2}>
                <Grid item xs={12} md={12}>
                  <div className='flex justify-between align-center'>
                    <Tooltip placement='top' title='Tìm kiếm theo tên nhân viên'>
                      <GlobitsTextField placeholder='Tìm kiếm theo tên nhân viên' name='keyword' variant='outlined' notDelay />
                    </Tooltip>

                    <ButtonGroup className='filterButtonV4' color='container' aria-label='outlined primary button group'>
                      <Button startIcon={<SearchIcon className={``} />} className='ml-8 d-inline-flex py-2 px-8 btnHrStyle' type='submit'>
                        Tìm kiếm
                      </Button>
                      <Button
                        startIcon={<FilterListIcon className={`filterRotateIcon ${isOpenFilter && "onRotate"}`} />}
                        className='d-inline-flex py-2 px-8 btnHrStyle'
                        onClick={handleTogglePopupFilter}
                      >
                        Bộ lọc
                      </Button>
                    </ButtonGroup>
                  </div>
                </Grid>
              </Grid>

              <StaffFilter
                isDisableFilter={isDisableFilter}
                setValues={setValues}
                isOpenFilter={isOpenFilter}
                handleFilter={handleFilter}
                handleCloseFilter={handleTogglePopupFilter}
              />
            </Grid>
          </Form>
        );
      }}
    </Formik>
  );
}

export default memo(observer(StaffToolbar));
