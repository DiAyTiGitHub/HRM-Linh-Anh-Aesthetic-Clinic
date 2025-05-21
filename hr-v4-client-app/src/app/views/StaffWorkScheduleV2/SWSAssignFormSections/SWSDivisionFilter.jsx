import { Grid } from "@material-ui/core";
import { observer } from "mobx-react";
import { memo, useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import GlobitsPagingAutocompleteV2 from "app/common/form/GlobitsPagingAutocompleteV2";
import { useFormikContext } from "formik";
import { pagingAllOrg } from "app/views/Organization/OrganizationService";
import { pagingPositionTitle } from "app/views/PositionTitle/PositionTitleService";
import { pagingHasPermissionDepartments, pagingStaff } from "app/views/HumanResourcesInformation/StaffService";
import { toast } from "react-toastify";
import { useStore } from "../../../stores";
import { OrganizationType } from "../../../LocalConstants";

function SWSDivisionFilter(props) {

  const { t } = useTranslation();

  const { userStore } = useStore();

  const {
    setSharedFilter
  } = userStore;

  const {
    values,
    setFieldValue,
  } = useFormikContext();

  async function autoSetShiftAssignmentStaffs() {
    try {
      const payload = {
        organizationId: values?.organization?.id,
        departmentId: values?.department?.id,
        positionTitleId: values?.positionTitle?.id,
        pageIndex: 1,
        pageSize: 9999999,
      };

      const { data } = await pagingStaff(payload);

      setFieldValue("staffs", data?.content || []);
    } catch (error) {
      console.error(error);
      toast.error("Lấy dữ liệu nhân viên có lỗi, vui lòng thử lại sau");
    }
  }

  const [isFirstRender, setIsFirstRender] = useState(true);

  useEffect(function () {
    if (isFirstRender) {
      setIsFirstRender(false);
      return;
    }

    if (values?.department?.id) {
      autoSetShiftAssignmentStaffs();
    }

  }, [values?.department?.id, values?.organization?.id, values?.positionTitle?.id]);

  return (
    <Grid container spacing={2}>
      <Grid item xs={12} sm={6} md={4}>
        <GlobitsPagingAutocompleteV2
          name="organization"
          label="Đơn vị"
          handleChange={(_, value) => {
            setFieldValue("organization", value);
            setFieldValue("department", null);
            setFieldValue("positionTitle", null);

            setSharedFilter("organization", value ? { ...value } : null);
            setSharedFilter("department", null);
            setSharedFilter("positionTitle", null);
          }}
          api={pagingAllOrg}
          searchObject={{ organizationType: OrganizationType.OPERATION.value }}
        />
      </Grid>

      <Grid item xs={12} sm={6} md={4}>
        <GlobitsPagingAutocompleteV2
          label={"Phòng ban"}
          name="department"
          disabled={values?.organization?.id ? false : true}
          api={pagingHasPermissionDepartments}
          searchObject={{
            pageIndex: 1,
            pageSize: 9999,
            keyword: "",
            organizationId: values?.organization?.id,
          }}
          handleChange={(_, value) => {
            setFieldValue("department", value);
            setFieldValue("positionTitle", null);
            setSharedFilter("department", value ? { ...value } : null);
            setSharedFilter("positionTitle", null);
          }}
          getOptionLabel={(option) => {
            return option?.code ? `${option?.name} - ${option?.code}` : option?.name;
          }}

        />
      </Grid>

      <Grid item xs={12} sm={6} md={4}>
        <GlobitsPagingAutocompleteV2
          label={"Chức danh"}
          name='positionTitle'
          api={pagingPositionTitle}
          searchObject={{
            departmentId: values?.department?.id,
          }}
          handleChange={(_, value) => {
            setFieldValue("positionTitle", value);
            setSharedFilter("positionTitle", value ? { ...value } : null);
          }}
          disabled={values?.department?.id ? false : true}
        />
      </Grid>
    </Grid>
  );
}

export default memo(observer(SWSDivisionFilter));
