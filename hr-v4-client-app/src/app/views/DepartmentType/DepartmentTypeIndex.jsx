import { Grid } from "@material-ui/core";
import { useTheme } from "@material-ui/core/styles";
import useMediaQuery from "@material-ui/core/useMediaQuery";
import { observer } from "mobx-react";
import React, { memo, useEffect } from "react";
import { useTranslation } from "react-i18next";
import GlobitsBreadcrumb from "../../common/GlobitsBreadcrumb";
import GlobitsConfirmationDialog from "../../common/GlobitsConfirmationDialog";
import { useStore } from "../../stores";
import DepartmentTypeCUForm from "./DepartmentTypeCUForm";
import DepartmentTypeList from "./DepartmentTypeList";
import DepartmentTypeToolbar from "./DepartmentTypeToolbar";

function DepartmentTypeIndex() {
    const { departmentTypeStore } = useStore();
    const { t } = useTranslation();
    const [anchorEl, setAnchorEl] = React.useState(null);

    const open = Boolean(anchorEl);
    const id = open ? "simple-popper" : undefined;

    const {
        pagingDepartmentType,
        openConfirmDeleteListPopup,
        openConfirmDeletePopup,
        openCreateEditPopup,
        handleClose,
        handleConfirmDelete,
        handleConfirmDeleteList,
        searchObject,
        resetStore,
        handleSetSearchObject,
        openViewPopup
    } = departmentTypeStore;

    useEffect(() => {
        pagingDepartmentType();
        return resetStore;
    }, []);

    async function handleFilter(values) {
        const newSearchObject = {
            ...values,
            pageIndex: 1,
            pageSize: searchObject.pageSize
        };
        handleSetSearchObject(newSearchObject);
        await pagingDepartmentType();
    }

    const handleClick = (event) => {
        setAnchorEl(anchorEl ? null : event.currentTarget);
    };

    const theme = useTheme();
    const isMobile = useMediaQuery(theme.breakpoints.down("sm"));
    const isExtraSmall = useMediaQuery(theme.breakpoints.down("xs"));

    return (
        <div className="content-index">
            <div className="index-breadcrumb py-6">
                <GlobitsBreadcrumb
                    routeSegments={[
                        { name: t("navigation.organization.title") },
                        { name: t("navigation.organizationalDirectory.title") },
                        { name: t("navigation.departmentType.title") }
                    ]}
                />
            </div>
            <Grid className="index-card" container spacing={2}>
                <Grid item xs={12}>
                    <DepartmentTypeToolbar />
                </Grid>
                <Grid item xs={12}>
                    <DepartmentTypeList />
                </Grid>
            </Grid>

            {openCreateEditPopup && (<DepartmentTypeCUForm />)}
            {openViewPopup && (<DepartmentTypeCUForm readOnly={true} />)}
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
                    onConfirmDialogClose={() => {
                        pagingDepartmentType();
                        handleClose();
                    }}
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

export default memo(observer(DepartmentTypeIndex));
