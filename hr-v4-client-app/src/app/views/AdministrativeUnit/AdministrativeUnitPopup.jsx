import React from "react";
import { useTranslation } from "react-i18next";
import { useStore } from "../../stores";
import EthnicsForm from "./AdministrativeUnitForm";
import { observer } from "mobx-react";

import Popup from "app/common/DraggablePopup/Popup";

export default observer(function EthnicsCreateEditPopup(props) {
  const { administrativeUnitStore } = useStore();
  const { t } = useTranslation();
  const { handleClose, selectedAdministrativeUnit } = administrativeUnitStore;
  const { open } = props;

  return (
    <Popup
      title={t("administrativeUnit.title")}
      longTitle
      open={open}
      handleClose={handleClose}
      selectedItem={selectedAdministrativeUnit}
      FormComponent={EthnicsForm}
    />
  );
});
