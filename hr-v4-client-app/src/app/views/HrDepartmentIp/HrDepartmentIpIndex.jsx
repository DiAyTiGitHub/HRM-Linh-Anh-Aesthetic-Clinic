import { Grid } from "@material-ui/core";
import { observer } from "mobx-react";
import { memo } from "react";
import { useTranslation } from "react-i18next";
import GlobitsBreadcrumb from "../../common/GlobitsBreadcrumb";
import GlobitsConfirmationDialog from "../../common/GlobitsConfirmationDialog";
import { useStore } from "../../stores";
import HrDepartmentIpCUForm from "./HrDepartmentIpCUForm";
import HrDepartmentIpList from "./HrDepartmentIpList";
import HrDepartmentIpToolbar from "./HrDepartmentIpToolbar";

function HrDepartmentIpIndex() {
    const {hrDepartmentIpStore} = useStore();
    const {t} = useTranslation();

    const {
        openConfirmDeleteListPopup ,
        openConfirmDeletePopup ,
        openCreateEditPopup ,
        handleClose ,
        handleConfirmDelete ,
        handleConfirmDeleteList ,
        openViewPopup
    } = hrDepartmentIpStore;

    return (
        <div className="content-index">
            <div className="index-breadcrumb py-6">
                <GlobitsBreadcrumb routeSegments={[
                    {name:"Chấm công"} ,
                    {name:"Danh mục chấm công"} ,
                    {name:t("navigation.hrDepartmentIp.title")}
                ]}/>
            </div>
            <Grid className="index-card" container spacing={2}>
                <Grid item xs={12}>
                    <HrDepartmentIpToolbar/>
                </Grid>

                <Grid item xs={12}>
                    <HrDepartmentIpList/>
                </Grid>
            </Grid>

            {openCreateEditPopup && (
                <HrDepartmentIpCUForm/>
            )}
            {openViewPopup && (
                <HrDepartmentIpCUForm readOnly={true}/>
            )}


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

export default memo(observer(HrDepartmentIpIndex));
