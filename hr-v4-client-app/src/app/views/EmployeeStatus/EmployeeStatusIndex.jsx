/* eslint-disable react-hooks/exhaustive-deps */
import {observer} from "mobx-react";
import React, {useEffect} from "react";
import {useStore} from "../../stores";
import {Button, ButtonGroup, Grid} from "@material-ui/core";
import GlobitsBreadcrumb from "../../common/GlobitsBreadcrumb";
import AddIcon from "@material-ui/icons/Add";
import {useTranslation} from "react-i18next";
import EmployeeStatusList from "./EmployeeStatusList";
import GlobitsConfirmationDialog from "../../common/GlobitsConfirmationDialog";
import useMediaQuery from "@material-ui/core/useMediaQuery";
import EmployeeStatusForm from "./EmployeeStatusForm";
import {useLocation} from "react-router-dom/cjs/react-router-dom";
import {withRouter} from "react-router-dom/cjs/react-router-dom.min";
import {useTheme} from "@material-ui/core/styles";
import {Form, Formik} from "formik";
import DeleteOutlineIcon from "@material-ui/icons/DeleteOutline";
import GlobitsTextField from "../../common/form/GlobitsTextField";
import SearchIcon from "@material-ui/icons/Search";

function EmployeeStatusIndex() {
    const {t} = useTranslation();
    const location = useLocation();

    const {
        shouldOpenConfirmationDeleteListDialog,
        selectedEmployeeStatusList,
        selectedEmployeeStatusDelete,
        handleDeleteList,
        handleConfirmDeleteList,
        onPagingEmployeeStatus,
        onChangeFormSearch,
        onOpenFormEmployeeStatusEdit,
        onDeleteEmployeeStatus,
        onClosePopup,
        selectedEmployeeStatusEdit
    } = useStore().employeeStatusStore;

    useEffect(() => {
        onPagingEmployeeStatus();
    }, [location]);

    const theme = useTheme();
    const isMobile = useMediaQuery(theme.breakpoints.down("sm"));

    async function handleFilter(values) {
        await onChangeFormSearch(values)
    }

    return (
        <section className="content-index">
            <GlobitsBreadcrumb routeSegments={[{name: t("employeeStatus.title")}]}/>

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
                                                    onClick={() => onOpenFormEmployeeStatusEdit()}
                                                >
                                                    {!isMobile && t("general.button.add")}
                                                </Button>
                                                <Button
                                                    disabled={selectedEmployeeStatusList
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
                    <EmployeeStatusList/>
                </Grid>
            </Grid>

            {Boolean(selectedEmployeeStatusEdit) && (
                <EmployeeStatusForm/>
            )}

            <GlobitsConfirmationDialog
                open={Boolean(selectedEmployeeStatusDelete)}
                onConfirmDialogClose={onClosePopup}
                onYesClick={onDeleteEmployeeStatus}
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
        </section>
    );
};

export default withRouter(observer(EmployeeStatusIndex))