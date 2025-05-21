

import React, { memo, useEffect, useMemo, useState } from "react";
import { observer } from "mobx-react";
import { useTranslation } from "react-i18next";
import GlobitsPopupV2 from "app/common/GlobitsPopupV2";
import QuickFieldCRUDSalaryItem from "app/views/Salary/SalaryTemplateCU/SalaryTemplateCUPopup/SmallSalaryItemPopup/QuickFieldCRUDSalaryItem";
import ResultItemsViewCRUDItem from "./TemplateItemsViewCRUDItem";
import { useFormikContext } from "formik";
import { Button, DialogActions } from "@material-ui/core";
import DoneAllIcon from '@material-ui/icons/DoneAll';
import BlockIcon from "@material-ui/icons/Block";
import { useStore } from "app/stores";

function TemplateItemChooseMultiple(props) {
    const {
        isOpen,
        handleClosePopup
    } = props;

    const { t } = useTranslation();
    const { values, setFieldValue } = useFormikContext();

    const { salaryTemplateStore } = useStore();

    const {
        handleCompleteChooseItems,
        setChosenItemIds,
        chosenItemIds
    } = salaryTemplateStore;


    function getCurrentTemplateItemsAndChosenItemIds() {
        const handleResult = {
            currentTemplateItems: null,
            chosenItemIds: null,
        };

        if (!isOpen) return {};

        const currentItems = [...JSON.parse(JSON.stringify(values?.templateItems))];

        const chosenIds = [];
        Array.from(currentItems)?.forEach(function (templateItem, index) {
            if (templateItem?.salaryItem?.id) {
                chosenIds.push(templateItem?.salaryItem?.id);
            }
        });

        setChosenItemIds(chosenIds);

        // handle set to result set
        handleResult.currentTemplateItems = currentItems;
        handleResult.chosenItemIds = chosenItemIds;

        return handleResult;
    }

    async function handleCompleteChoose() {
        try {
            const result = getCurrentTemplateItemsAndChosenItemIds();

            const payload = {
                currentTemplateItems: result?.currentTemplateItems,
                chosenItemIds: result?.chosenItemIds,
                salaryTemplateId: values?.id,
            };

            const response = await handleCompleteChooseItems(payload);
            setFieldValue("templateItems", response);

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
        if (!isOpen) return;

        getCurrentTemplateItemsAndChosenItemIds();

        pagingSalaryItem();

        return resetStore;
    }, [isOpen]);

    return (
        <GlobitsPopupV2
            noDialogContent
            popupId={"TemplateItemChooseMultiple"}
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

export default memo(observer(TemplateItemChooseMultiple));