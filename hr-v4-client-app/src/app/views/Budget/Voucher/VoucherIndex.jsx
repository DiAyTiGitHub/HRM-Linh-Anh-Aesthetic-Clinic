/* eslint-disable react-hooks/exhaustive-deps */
import {observer} from "mobx-react";
import React, {useEffect} from "react";
import {useStore} from "../../../stores";
import {Box, Grid, Typography} from "@material-ui/core";
import {useTranslation} from "react-i18next";
import VoucherList from "./VoucherList";
import GlobitsConfirmationDialog from "../../../common/GlobitsConfirmationDialog";
import {useTheme} from "@material-ui/core/styles";
import useMediaQuery from "@material-ui/core/useMediaQuery";
import GlobitsBreadcrumb from "app/common/GlobitsBreadcrumb";
import VourcherIndexToolbar from "./VourcherIndexToolbar";
import VoucherSummaryBalance from "./VoucherSummaryBalance";

export default observer(function VoucherIndex() {
    const {voucherStore, budgetStore} = useStore();
    const {t} = useTranslation();

    const {
        updatePageData,
        shouldOpenConfirmationDialog,
        handleClose,
        handleConfirmDelete,
        shouldOpenConfirmationDeleteListDialog,
        handleConfirmDeleteList,
        resetVoucherStore,
    } = voucherStore;

    const {selectBudgetSummaryBalance} = budgetStore;
    useEffect(() => {
        resetVoucherStore();
        updatePageData();
    }, []);
    const theme = useTheme();
    const isMobile = useMediaQuery(theme.breakpoints.down("sm"));
    return (<div className='content-index'>
        <div className='index-breadcrumb'>
            <GlobitsBreadcrumb routeSegments={[{name: t("voucher.title")}]}/>
        </div>

        <Grid className='index-card' container spacing={2}>
            <Grid item xs={12} className="index-card">
                <VourcherIndexToolbar/>
            </Grid>
            {selectBudgetSummaryBalance && (
                <Grid item xs={12} className="index-card">
                    <VoucherSummaryBalance data={selectBudgetSummaryBalance}/>
                </Grid>

            )}
            <Grid item xs={12} className="index-card">
                <VoucherList/>
            </Grid>
        </Grid>

        <GlobitsConfirmationDialog
            open={shouldOpenConfirmationDialog}
            onConfirmDialogClose={handleClose}
            onYesClick={handleConfirmDelete}
            title={t("confirm_dialog.delete.title")}
            text={t("confirm_dialog.delete.text")}
            agree={t("confirm_dialog.delete.agree")}
            cancel={t("confirm_dialog.delete.cancel")}
        />

        <GlobitsConfirmationDialog
            open={shouldOpenConfirmationDeleteListDialog}
            onConfirmDialogClose={handleClose}
            onYesClick={handleConfirmDeleteList}
            title={t("confirm_dialog.delete_list.title")}
            text={t("confirm_dialog.delete_list.text")}
            agree={t("confirm_dialog.delete_list.agree")}
            cancel={t("confirm_dialog.delete_list.cancel")}
        />
    </div>)
        ;
});
