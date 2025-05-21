import {
  DialogContent,
  Grid,
} from "@material-ui/core";
import GlobitsPopupV2 from "app/common/GlobitsPopupV2";
import GlobitsSearchInput from "app/common/GlobitsSearchInput";
import { useStore } from "app/stores";
import { observer } from "mobx-react";
import { memo } from "react";
import { useTranslation } from "react-i18next";
import SelectDepartmentList from "./SelectDepartmentList";

function SelectDepartmentPopup({
  open,
  handleSelect,
  handleClose
}) {
  const { t } = useTranslation();
  const { departmentStore } = useStore();

  const { updatePageData } = departmentStore;

  return (
    <GlobitsPopupV2 
      popupId="select-deparment-popup"
      scroll={"body"}
      size="lg"
      open={open}
      noDialogContent
      title={t("department.select")}
      onClosePopup={handleClose}
    >
      <DialogContent
        className="p-12 o-hidden"
        style={{ overflowY: "auto", maxHeight: "88vh" }}
      >
        <Grid container spacing={2}>
          <Grid item lg={6} md={6} sm={4} xs={4}></Grid>

          <Grid item lg={6} md={6} sm={8} xs={8}>
            <GlobitsSearchInput search={updatePageData} />
          </Grid>

          <Grid item xs={12}>
            <SelectDepartmentList
              handleSelect={handleSelect}
            />
          </Grid>
        </Grid>

      </DialogContent>
    </GlobitsPopupV2>
  );
}

export default memo(observer(SelectDepartmentPopup));