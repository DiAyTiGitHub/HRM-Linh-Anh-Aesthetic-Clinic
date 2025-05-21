import { observer } from "mobx-react";
import React, { memo, useEffect } from "react";
import { useStore } from "../../stores";
import { Button, ButtonGroup, Grid, IconButton } from "@material-ui/core";
import GlobitsBreadcrumb from "../../common/GlobitsBreadcrumb";
import DeleteIcon from "@material-ui/icons/Delete";
import AddIcon from "@material-ui/icons/Add";
import { useTranslation } from "react-i18next";
import GlobitsConfirmationDialog from "../../common/GlobitsConfirmationDialog";
import SearchIcon from '@material-ui/icons/Search';
import { Form, Formik } from "formik";
import GlobitsTextField from "app/common/form/GlobitsTextField";
import RankTitleList from "./RankTitleList";
import RankTitleCUForm from "./RankTitleCUForm";
import GetAppIcon from "@material-ui/icons/GetApp";
import CloudUploadIcon from "@material-ui/icons/CloudUpload";

function RankTitleIndex() {
    const { rankTitleStore, hrRoleUtilsStore } = useStore();
    const { t } = useTranslation();

    const {
        handleDeleteList,
        pagingRankTitle,
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
        handleSetSearchObject,
        handleDownloadRankTitleTemplate,
        uploadFileExcel,
        openViewPopup
    } = rankTitleStore;

    const {
        checkAllUserRoles,
        isAdmin,
    } = hrRoleUtilsStore

    useEffect(() => {
        checkAllUserRoles();
        pagingRankTitle();
        return resetStore;
    }, []);

    async function handleFilter(values) {
        const newSearchObject = {
            ...values,
            pageIndex: 1,
        };
        handleSetSearchObject(newSearchObject);
        await pagingRankTitle();
    }

    return (
        <div className="content-index">
            <div className="index-breadcrumb py-6">
                <GlobitsBreadcrumb
                    routeSegments={[
                        { name: t("navigation.organization.title") },
                        { name: t("navigation.organizationalDirectory.title") },
                        { name: t("navigation.rankTitle.title") }
                    ]}
                />
            </div>
            <Grid className="index-card" container spacing={2}>
                <Grid item xs={12}>
                    <Formik
                        enableReinitialize
                        initialValues={searchObject}
                        onSubmit={handleFilter}
                    >
                        {({ resetForm, values, setFieldValue, setValues }) => {
                            return (
                                <Form autoComplete="off">
                                    <div className="">
                                        <Grid container spacing={2} className="align-center mainBarFilter">
                                            <Grid item xs={12} md={6}>
                                                {(isAdmin) && (
                                                    <ButtonGroup
                                                        color="container"
                                                        aria-label="outlined primary button group"
                                                    >
                                                        <Button
                                                            startIcon={<AddIcon />}
                                                            onClick={() => handleOpenCreateEdit()}
                                                        >
                                                            {t("general.button.add")}
                                                        </Button>
                                                        <Button
                                                            startIcon={<GetAppIcon />}
                                                            onClick={handleDownloadRankTitleTemplate}
                                                        >
                                                            Tải mẫu nhập
                                                        </Button>
                                                        <Button
                                                            startIcon={<CloudUploadIcon />}
                                                            onClick={() => document.getElementById("fileExcel").click()}
                                                        >
                                                            {t("general.button.importExcel")}
                                                        </Button>
                                                        <Button
                                                            disabled={listOnDelete?.length <= 0}
                                                            startIcon={<DeleteIcon />}
                                                            onClick={handleDeleteList}
                                                        >
                                                            {t("general.button.delete")}
                                                        </Button>
                                                    </ButtonGroup>
                                                )}
                                                <input
                                                    type="file"
                                                    id="fileExcel"
                                                    style={{ display: "none" }}
                                                    onChange={uploadFileExcel}
                                                />
                                            </Grid>

                                            <Grid item xs={12} md={6}>
                                                <div className="flex justify-between align-center">
                                                    <GlobitsTextField
                                                        placeholder="Tìm kiếm theo tên cấp bậc..."
                                                        name="keyword"
                                                        variant="outlined"
                                                        notDelay
                                                        timeOut={0}
                                                        InputProps={{
                                                            endAdornment: (
                                                                <IconButton className="py-0 px-4" aria-label="search"
                                                                    type="submit">
                                                                    <SearchIcon />
                                                                </IconButton>
                                                            ),
                                                        }}
                                                    />
                                                </div>
                                            </Grid>
                                        </Grid>
                                    </div>
                                </Form>
                            );
                        }}
                    </Formik>
                </Grid>

                <Grid item xs={12}>
                    <RankTitleList />
                </Grid>
            </Grid>

            {openCreateEditPopup && (
                <RankTitleCUForm />
            )}
            {openViewPopup && (
                <RankTitleCUForm readOnly={true} />
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
                    onConfirmDialogClose={() => {
                        pagingRankTitle();
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

export default memo(observer(RankTitleIndex));
