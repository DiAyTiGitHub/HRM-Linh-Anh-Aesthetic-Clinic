import {observer} from "mobx-react";
import React, {memo, useEffect} from "react";
import {useStore} from "app/stores";
import {Grid} from "@material-ui/core";
import GlobitsBreadcrumb from "app/common/GlobitsBreadcrumb";
import {useTranslation} from "react-i18next";
import GroupWorkIcon from '@material-ui/icons/GroupWork';
import DoneAllIcon from '@material-ui/icons/DoneAll';
import TabsComponent from "app/common/Tab/TabComponent";
import ExportCandidateList from "./ExportCandidateList";
import ExportCandidateIndexToolbar from "./ExportCandidateToolbar";
import CloseIcon from '@material-ui/icons/Close';
import PeopleIcon from '@material-ui/icons/People';
import GlobitsConfirmationDialog from "app/common/GlobitsConfirmationDialog";
import ExportCandidateConfirmExportExcelPopup from "./ExportCandidateConfirmExportExcelPopup";

function ExportCandidateIndex() {
    const {exportCandidateStore} = useStore();
    const {t} = useTranslation();

    const {
        pagingExportCandidate,
        searchObject,
        resetStore,
        handleChangeOnboardStatus,
        setPageIndex,
        handleSelectListChosen,
        handleClose,
        handleConfirmDeleteList,
        handleConfirmDelete,
        openConfirmDeletePopup,
        openConfirmDeleteListPopup,
        openConfirmExportExcel
    } = exportCandidateStore;

    useEffect(() => {
        pagingExportCandidate();

        return resetStore;
    }, []);


    async function handleChangeTabIndex(tabIndex) {
        handleChangeOnboardStatus(tabIndex);
        handleSelectListChosen([]);
        await setPageIndex(1);
    }

    const tabList = [
        {icon: <GroupWorkIcon fontSize="small"/>, label: "Tất cả"},
        {icon: <PeopleIcon fontSize="small"/>, label: "Chờ nhận việc"},
        {icon: <CloseIcon fontSize="small"/>, label: "Không đến nhận việc"},
        {icon: <DoneAllIcon fontSize="small"/>, label: "Đã nhận việc"},
    ];

    async function handleCloseConfirmDeleteList() {
        try {
            await pagingExportCandidate();
            handleClose();
        } catch (error) {
            console.error(error);
        }
    }

    return (
        <div className="content-index">
            <div className="index-breadcrumb py-6">
                <GlobitsBreadcrumb
                    routeSegments={[
                        {name: "Nhân viên"},
                        {name: "Tiếp nhận ứng viên"},
                        {name: "Xuất báo cáo tiếp nhận ứng viên"}
                    ]}/>
            </div>

            <Grid container spacing={2}>
                <Grid item xs={12} className="index-card">
                    <ExportCandidateIndexToolbar/>
                </Grid>

                <Grid item xs={12} className="index-card">
                    <TabsComponent
                        value={searchObject?.tabs}
                        handleChange={(_, tabIndex) => handleChangeTabIndex(tabIndex)}
                        tabList={tabList}
                    />

                    <ExportCandidateList/>
                </Grid>
            </Grid>


            {
                openConfirmDeletePopup && (
                    <GlobitsConfirmationDialog
                        open={openConfirmDeletePopup}
                        onConfirmDialogClose={handleClose}
                        onYesClick={handleConfirmDelete}
                        title={t("confirm_dialog.delete.title")}
                        text={t("confirm_dialog.delete.text")}
                        agree={t("confirm_dialog.delete.agree")}
                        cancel={t("confirm_dialog.delete.cancel")}
                    />
                )
            }

            {
                openConfirmDeleteListPopup && (
                    <GlobitsConfirmationDialog
                        open={openConfirmDeleteListPopup}
                        onConfirmDialogClose={handleCloseConfirmDeleteList}
                        onYesClick={handleConfirmDeleteList}
                        title={t("confirm_dialog.delete_list.title")}
                        text={t("confirm_dialog.delete_list.text")}
                        agree={t("confirm_dialog.delete_list.agree")}
                        cancel={t("confirm_dialog.delete_list.cancel")}
                    />
                )
            }

            {openConfirmExportExcel && (
                <ExportCandidateConfirmExportExcelPopup/>
            )}
        </div>
    );
}

export default memo(observer(ExportCandidateIndex));
