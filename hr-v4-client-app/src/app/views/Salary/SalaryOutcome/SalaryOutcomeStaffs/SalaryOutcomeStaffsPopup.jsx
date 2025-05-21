import React, { useState, useEffect, memo } from "react";
import { Formik, Form } from "formik";
import { Grid, DialogActions, Button, DialogContent } from "@material-ui/core";
import { useTranslation } from "react-i18next";
import { useStore } from "app/stores";
import * as Yup from "yup";
import { observer } from "mobx-react";
import GlobitsPopupV2 from "app/common/GlobitsPopupV2";
import LocalConstants from "app/LocalConstants";
import SalaryStaffsToolbar from "./SalaryStaffsToolbar";
import SalaryStaffsList from "./SalaryStaffsList";
import MonetizationOnIcon from '@material-ui/icons/MonetizationOn';

function SalaryOutcomeStaffsPopup() {
    const { salaryOutcomeStore } = useStore();
    const { t } = useTranslation();

    const {
        handleClose,
        pagingAvailableCalculateStaffs,
        handleSetSearchObject,
        handleOpenChooseStaffsPopup,
        searchObject,
        openChooseStaffsPopup,
        handleCalculateSalaryByStaffs
    } = salaryOutcomeStore;

    useEffect(function () {
        if (openChooseStaffsPopup) {
            pagingAvailableCalculateStaffs();
        }

        return handleClose;
    }, [openChooseStaffsPopup]);

    async function handleConfirmCalculateSalaryByStaffs() {
        try {
            await handleCalculateSalaryByStaffs();
            handleClose();
        }
        catch (error) {
            console.error(error);
        }
    }

    return (
        <GlobitsPopupV2
            size="lg"
            scroll={"body"}
            open={openChooseStaffsPopup}
            noDialogContent
            title={"Danh sách nhân viên tính lương"}
            onClosePopup={handleClose}
        >

            <DialogContent className="o-hidden p-12">
                <SalaryStaffsToolbar />
            </DialogContent>

            <DialogActions className="dialog-footer px-12">
                <div className="flex flex-space-between flex-middle">
                    <Button
                        className="mr-0 btn btn-green d-inline-flex"
                        variant="contained"
                        onClick={handleConfirmCalculateSalaryByStaffs}
                        startIcon={<MonetizationOnIcon />}
                    >
                        Xác nhận tính lương
                    </Button>
                </div>
            </DialogActions>

        </GlobitsPopupV2>
    );
}

export default memo(observer(SalaryOutcomeStaffsPopup));
