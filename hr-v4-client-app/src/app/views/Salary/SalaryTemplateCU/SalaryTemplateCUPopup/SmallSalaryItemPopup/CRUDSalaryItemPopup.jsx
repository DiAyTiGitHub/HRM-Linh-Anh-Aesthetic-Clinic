import React, { memo, useEffect, useState } from "react";
import { observer } from "mobx-react";
import { useTranslation } from "react-i18next";
import GlobitsPopupV2 from "app/common/GlobitsPopupV2";
import QuickFieldCRUDSalaryItem from "./QuickFieldCRUDSalaryItem";

function CRUDSalaryItemPopup(props) {
    const {
        open,
        handleCloseChooseItemPopup
    } = props;

    const { t } = useTranslation();

    return (
        <GlobitsPopupV2
            noDialogContent
            popupId={"chooseCRUDSalaryItemPopup"}
            open={open}
            title='Chọn thành phần lương'
            size="xl"
            scroll={"body"}
            onClosePopup={handleCloseChooseItemPopup}
        >
            <QuickFieldCRUDSalaryItem
                handleCloseChooseItemPopup={handleCloseChooseItemPopup}
            />
        </GlobitsPopupV2>
    );
}

export default memo(observer(CRUDSalaryItemPopup));