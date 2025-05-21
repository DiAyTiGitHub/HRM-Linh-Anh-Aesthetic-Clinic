/* eslint-disable react-hooks/exhaustive-deps */
import {observer} from "mobx-react";
import React, {useEffect} from "react";
import {useStore} from "../../stores";
import {Button, ButtonGroup, Grid} from "@material-ui/core";
import GlobitsBreadcrumb from "../../common/GlobitsBreadcrumb";
import AddIcon from "@material-ui/icons/Add";
import {useTranslation} from "react-i18next";
import WorkingStatusList from "./WorkingStatusList";
import GlobitsConfirmationDialog from "../../common/GlobitsConfirmationDialog";
import useMediaQuery from "@material-ui/core/useMediaQuery";
import WorkingStatusForm from "./WorkingStatusForm";
import {useLocation} from "react-router-dom";
import {useTheme} from "@material-ui/core/styles";
import {Form, Formik} from "formik";
import DeleteOutlineIcon from "@material-ui/icons/DeleteOutline";
import GlobitsTextField from "../../common/form/GlobitsTextField";
import SearchIcon from "@material-ui/icons/Search";

function WorkingStatusIndex() {
    const {t} = useTranslation();
    const location = useLocation();

    const {
        selectedWorkingStatusDelete,
        selectedWorkingStatusList,
        shouldOpenConfirmationDeleteListDialog,
        handleConfirmDeleteList,
        onPagingWorkingStatus,
        onOpenFormWorkingStatusEdit,
        onClosePopup,
        onDeleteWorkingStatus,
        onChangeFormSearch,
        handleDeleteList,
        resetWorkingStatusStore
    } = useStore().workingStatusStore;

    useEffect(() => {
        onPagingWorkingStatus();
        return resetWorkingStatusStore;
    }, [location]);

    const theme = useTheme();
    const isMobile = useMediaQuery(theme.breakpoints.down("sm"));

    async function handleFilter(values) {
        await onChangeFormSearch(values)
    }

    return (
        <div className="content-index">
            <div className="index-breadcrumb py-6">
                <GlobitsBreadcrumb routeSegments={[{name: t("workingStatus.title")}]}/>
            </div>
            <Grid className="index-card" container spacing={2}>
                <Grid item xs={12}>
                    <Formik
                        enableReinitialize
                        initialValues={{keyword: ""}}
                        onSubmit={handleFilter}
                    >
                        {({resetForm, values, setFieldValue, setValues}) => {
                            return (
                                <Form autoComplete="off">
                                    <Grid container spacing={2}>
                                        <Grid item xs={12} md={6}>
                                            <ButtonGroup
                                                color="container"
                                                aria-label="outlined primary button group"
                                            >
                                                <Button
                                                    startIcon={<AddIcon/>}
                                                    onClick={() => onOpenFormWorkingStatusEdit()}
                                                >
                                                    {!isMobile && t("general.button.add")}
                                                </Button>
                                                <Button
                                                    disabled={selectedWorkingStatusList
                                                        ?.length === 0}
                                                    startIcon={<DeleteOutlineIcon/>}
                                                    onClick={() => {
                                                        handleDeleteList();
                                                    }}
                                                >
                                                    {!isMobile && t("general.button.delete")}
                                                </Button>
                                            </ButtonGroup>
                                        </Grid>
                                        <Grid item xs={12} md={6}>
                                            <div className="flex justify-between align-center">
                                                <GlobitsTextField
                                                    placeholder="Tìm kiếm theo từ khóa..."
                                                    name="keyword"
                                                    variant="outlined"
                                                    notDelay
                                                />
                                                <ButtonGroup
                                                    className="filterButtonV4"
                                                    color="container"
                                                    aria-label="outlined primary button group"
                                                >
                                                    <Button
                                                        startIcon={<SearchIcon/>}
                                                        className="ml-8 d-inline-flex py-2 px-8 btnHrStyle"
                                                        type="submit"
                                                    >
                                                        Tìm kiếm
                                                    </Button>
                                                </ButtonGroup>
                                            </div>
                                        </Grid>
                                    </Grid>
                                </Form>
                            );
                        }}
                    </Formik>
                </Grid>

                <Grid item xs={12}>
                    <WorkingStatusList/>
                </Grid>
            </Grid>

            <WorkingStatusForm/>

            <GlobitsConfirmationDialog
                open={Boolean(selectedWorkingStatusDelete)}
                onConfirmDialogClose={onClosePopup}
                onYesClick={onDeleteWorkingStatus}
                title={t("confirm_dialog.delete.title")}
                text={t("confirm_dialog.delete.text")}
                agree={t("confirm_dialog.delete.agree")}
                cancel={t("confirm_dialog.delete.cancel")}
            />

            <GlobitsConfirmationDialog
                open={shouldOpenConfirmationDeleteListDialog}
                onConfirmDialogClose={onClosePopup}
                onYesClick={handleConfirmDeleteList}
                title={t("confirm_dialog.delete_list.title")}
                text={t("confirm_dialog.delete_list.text")}
                agree={t("confirm_dialog.delete_list.agree")}
                cancel={t("confirm_dialog.delete_list.cancel")}
            />
        </div>
    );
};

export default observer(WorkingStatusIndex)