

import React, { memo, useEffect, useMemo, useState } from "react";
import { observer } from "mobx-react";
import { useTranslation } from "react-i18next";
import GlobitsPopupV2 from "app/common/GlobitsPopupV2";
import QuickFieldCRUDSalaryItem from "app/views/Salary/SalaryTemplateCU/SalaryTemplateCUPopup/SmallSalaryItemPopup/QuickFieldCRUDSalaryItem";
import ResultItemsViewCRUDItem from "./ResultItemsViewCRUDItem";
import { useFormikContext } from "formik";
import { Button, DialogActions } from "@material-ui/core";
import DoneAllIcon from '@material-ui/icons/DoneAll';
import BlockIcon from "@material-ui/icons/Block";
import { useStore } from "app/stores";

function ResultItemChooseMultiple(props) {
    const {
        isOpen,
        handleClosePopup
    } = props;

    const { t } = useTranslation();
    const { values, setFieldValue } = useFormikContext();

    const { salaryResultDetailStore } = useStore();
    const {
        handleCompleteChooseItems,
        setChosenItemIds,
        chosenItemIds
    } = salaryResultDetailStore;


    const currentChosenItems = useMemo(function () {
        if (!isOpen) return;

        const currentItems = [...JSON.parse(JSON.stringify(values?.resultItems))];

        const chosenIds = [];
        Array.from(currentItems)?.forEach(function (resultItem, index) {
            if (resultItem?.salaryItem?.id) {
                chosenIds.push(resultItem?.salaryItem?.id);
            }
        });

        setChosenItemIds(chosenIds);

        return currentItems;
    }, [isOpen, values?.resultItems]);

    async function handleCompleteChoose() {
        try {
            const payload = {
                currentResultItems: currentChosenItems,
                chosenItemIds: chosenItemIds,
                salaryResultId: values?.id,
            };

            const response = await handleCompleteChooseItems(payload);
            setFieldValue("resultItems", response);

            handleClosePopup();
        }
        catch (err) {
            console.error(err);
        }
    }

    
    const { salaryItemStore } = useStore();

    const {
        pagingSalaryItem,
        resetStore,
    } = salaryItemStore;

    useEffect(() => {
        if (isOpen)
            pagingSalaryItem();

        return resetStore;
    }, [isOpen]);

    return (
        <GlobitsPopupV2
            noDialogContent
            popupId={"ResultItemChooseMultiple"}
            open={isOpen}
            title='Chọn thành phần lương'
            size="xl"
            scroll={"body"}
            onClosePopup={handleClosePopup}
        >
            <ResultItemsViewCRUDItem />

            <div className="dialog-footer dialog-footer-v2 py-8 px-12">
                <DialogActions className="p-0">
                    <div className="flex flex-space-between flex-middle">
                        <Button
                            startIcon={<BlockIcon />}
                            variant="contained"
                            className="mr-12 btn btn-secondary d-inline-flex"
                            color="secondary"
                            onClick={handleClosePopup}
                        >
                            {t("general.button.cancel")}
                        </Button>

                        <Button
                            startIcon={<DoneAllIcon />}
                            className="mr-0 btn btn-success d-inline-flex"
                            variant="contained"
                            color="primary"
                            type="button"
                            onClick={handleCompleteChoose}
                        >
                            Áp dụng
                        </Button>

                    </div>
                </DialogActions>
            </div>
        </GlobitsPopupV2>
    );
}

export default memo(observer(ResultItemChooseMultiple));