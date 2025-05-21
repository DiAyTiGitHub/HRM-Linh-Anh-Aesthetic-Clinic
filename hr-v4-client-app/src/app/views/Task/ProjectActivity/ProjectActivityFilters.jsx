import React from "react";
import { observer } from "mobx-react";
import { useStore } from "../../../stores";
import { useFormikContext } from "formik";
import GlobitsSearchInput from "app/common/GlobitsSearchInput";

export default observer(function DepartmentFilters() {
  const { timeSheetStore } = useStore();
  const { searchToListByPage } = timeSheetStore;

  const { values } = useFormikContext();
  return (
    <>
      <GlobitsSearchInput
        search={searchToListByPage}
        projectId={values?.project?.id}
      />
    </>
  );
});
