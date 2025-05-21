import {observer} from "mobx-react";
import React, {memo, useEffect} from "react";
import {useStore} from "app/stores";
import {Grid} from "@material-ui/core";
import GlobitsBreadcrumb from "app/common/GlobitsBreadcrumb";
import {useTranslation} from "react-i18next";
import GlobitsConfirmationDialog from "app/common/GlobitsConfirmationDialog";
import RecruitmentList from "./RecruitmentList";
import {useHistory} from "react-router-dom/cjs/react-router-dom";
import RecruitmentIndexToolbar from "./RecruitmentIndexToolbar";

function RecruitmentIndex() {
    const {recruitmentStore} = useStore();
    const {t} = useTranslation();

    const {
        handleDeleteList,
        pagingRecruitment,
        handleOpenCreateEdit,
        openConfirmDeleteListPopup,
        openConfirmDeletePopup,
        openCreateEditPopup,
        handleClose,
        handleConfirmDelete,
        handleConfirmDeleteList,
        searchObject,
        listOnDelete,
        resetStore,
        handleSetSearchObject
    } = recruitmentStore;

    useEffect(() => {
        pagingRecruitment();

        return resetStore;
    }, []);

    async function handleFilter(values) {
        const newSearchObject = {
            ...values,
            pageIndex: 1,
        };
        handleSetSearchObject(newSearchObject);
        await pagingRecruitment();
    }

    const history = useHistory();

    return (
        <div className="content-index">
            <div className="index-breadcrumb py-6">
                <GlobitsBreadcrumb
                    routeSegments={[
                        {name: t("navigation.recruitment.title")},
                        {name: t("navigation.recruitment.period.title")},
                    ]}
                />
            </div>
            <Grid className="index-card" container spacing={2}>
                <Grid item xs={12}>
                    <RecruitmentIndexToolbar/>
                </Grid>

                <Grid item xs={12}>
                    <RecruitmentList/>
                </Grid>
            </Grid>

            {openConfirmDeletePopup && (
                <GlobitsConfirmationDialog
                    open={openConfirmDeletePopup}
                    onConfirmDialogClose={handleClose}
                    onYesClick={handleConfirmDelete}
                    title={t("confirm_dialog.delete.title")}
                    text={t("confirm_dialog.delete.text")}
                    agree={t("confirm_dialog.delete.agree")}
                    cancel={t("confirm_dialog.delete.cancel")}
                />
            )}

            {openConfirmDeleteListPopup && (
                <GlobitsConfirmationDialog
                    open={openConfirmDeleteListPopup}
                    onConfirmDialogClose={handleClose}
                    onYesClick={handleConfirmDeleteList}
                    title={t("confirm_dialog.delete_list.title")}
                    text={t("confirm_dialog.delete_list.text")}
                    agree={t("confirm_dialog.delete_list.agree")}
                    cancel={t("confirm_dialog.delete_list.cancel")}
                />
            )}
        </div>
    );
}

export default memo(observer(RecruitmentIndex));
