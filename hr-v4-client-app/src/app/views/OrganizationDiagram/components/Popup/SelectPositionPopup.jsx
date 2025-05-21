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
import SelectPositionList from "./SelectPositionList";

function SelectPositionPopup({
  open,
  handleSelect,
  handleClose
}) {
  const { t } = useTranslation();
  const { positionStore } = useStore();

  const { updatePageData } = positionStore;

  return (
    <GlobitsPopupV2
      popupId="select-deparment-popup"
      scroll={"body"}
      size="lg"
      open={open}
      noDialogContent
      title={t("Lựa chọn vị trí")}
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
            <SelectPositionList
              handleSelect={handleSelect}
            />
          </Grid>
        </Grid>

      </DialogContent>
    </GlobitsPopupV2>
  );
}

export default memo(observer(SelectPositionPopup));