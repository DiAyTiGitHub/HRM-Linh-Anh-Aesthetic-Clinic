import { Button, ButtonGroup, Collapse, Grid } from "@material-ui/core";
import HighlightOffIcon from "@material-ui/icons/HighlightOff";
import RotateLeftIcon from "@material-ui/icons/RotateLeft";
import GlobitsPagingAutocompleteV2 from "app/common/form/GlobitsPagingAutocompleteV2";
import { useStore } from "app/stores";
import { pagingAllDepartments } from "app/views/Department/DepartmentService";
import { pagingAllOrg } from "app/views/Organization/OrganizationService";
import { pagingPosition } from "app/views/Position/PositionService";
import { useFormikContext } from "formik";
import { observer } from "mobx-react";
import { memo } from "react";
import { useTranslation } from "react-i18next";
import GlobitsNumberInput from "app/common/form/GlobitsNumberInput";
import GlobitsCheckBox from "app/common/form/GlobitsCheckBox";
import { pagingPositionTitle } from "app/views/PositionTitle/PositionTitleService";

function StaffFilter(props) {

  const { userStore, hrRoleUtilsStore } = useStore();

  const {
    values,
    setFieldValue

  } = useFormikContext();

  const { t } = useTranslation();

  const { intactSearchObject } = userStore;

  const { isAdmin, isManager } = hrRoleUtilsStore;

  const {
    isOpenFilter,
    handleFilter,
    handleCloseFilter,
    isDisableFilter
  } = props;


  function handleResetFilter() {
    const newSearchObject = {
      ...intactSearchObject,
      levelNumber: 1,
      pageIndex: 1,
      pageSize: 10,
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

                {/* <Grid item xs={12} className="pb-0 flex flex-end">
                  <p className="m-0 p-0 borderThrough2">
                    Tiêu chí
                  </p>
                </Grid> */}

                <Grid item xs={12} sm={6} md={4} lg={3}>
                  <GlobitsPagingAutocompleteV2
                    label={"Đơn vị"}
                    name='organization'
                    handleChange={(_, value) => {
                      setFieldValue("organization", value);
                      setFieldValue("department", null);
                      setFieldValue("positionTitle", null);
                    }}
                    api={pagingAllOrg}
                    disabled={isDisableFilter}
                  />
                </Grid>

                <Grid item xs={12} sm={6} md={4} lg={3}>
                  <GlobitsPagingAutocompleteV2
                    label={"Phòng ban"}
                    name='department'
                    api={pagingAllDepartments}
                    searchObject={{
                      organizationId: values?.organization?.id,
                    }}
                    handleChange={(_, value) => {
                      setFieldValue("department", value);
                      setFieldValue("positionTitle", null);
                    }}
                    disabled={isDisableFilter}
                  />
                </Grid>

                <Grid item xs={12} sm={6} md={4} lg={3}>
                  <GlobitsPagingAutocompleteV2
                    label={"Chức danh"}
                    name='positionTitle'
                    api={pagingPositionTitle}
                    searchObject={{
                      departmentId: values?.department?.id,
                    }}
                    disabled={isDisableFilter}
                  />
                </Grid>

                {!isAdmin && !isManager && (
                  <Grid item xs={12} sm={6} md={4} lg={3}>
                    <GlobitsNumberInput
                      label={"Số cấp dưới cần lấy dữ liệu"}
                      name='levelNumber'
                      disabled={isDisableFilter}
                    />
                  </Grid>
                )}

                {!isAdmin && !isManager && (
                  <Grid item xs={12} sm={6} md={4} lg={3} className={"flex align-end"}>
                    <GlobitsCheckBox
                      label={"Chỉ lấy nhân viên cấp dưới"}
                      name='collectInEachLevel'
                      disabled={isDisableFilter}
                    />
                  </Grid>
                )}
              </Grid>

              <div className='py-8 mt-12 border-bottom-fade border-top-fade'>
                <div className='flex justify-end'>
                  <ButtonGroup
                    color='container'
                    aria-label='outlined primary button group'
                  >
                    <Button
                      onClick={handleResetFilter}
                      startIcon={<RotateLeftIcon />}
                    >
                      Đặt lại
                    </Button>

                    <Button
                      type='button'
                      onClick={handleCloseFilter}
                      startIcon={<HighlightOffIcon />}
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

export default memo(observer(StaffFilter));
