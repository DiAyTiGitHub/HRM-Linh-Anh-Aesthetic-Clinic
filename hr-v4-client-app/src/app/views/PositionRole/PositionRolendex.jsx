import {observer} from "mobx-react";
import React, {memo, useEffect} from "react";
import {useStore} from "../../stores";
import {Grid, Button, IconButton, ButtonGroup} from "@material-ui/core";
import GlobitsBreadcrumb from "../../common/GlobitsBreadcrumb";
import DeleteIcon from "@material-ui/icons/Delete";
import AddIcon from "@material-ui/icons/Add";
import {useTranslation} from "react-i18next";
import GlobitsConfirmationDialog from "../../common/GlobitsConfirmationDialog";
import NoteIcon from "@material-ui/icons/Note";
import SearchIcon from '@material-ui/icons/Search';
import {Form, Formik} from "formik";
import GlobitsTextField from "app/common/form/GlobitsTextField";
import PositionRoleCUForm from "./PositionRoleCUForm";
import PositionRoleList from "./PositionRoleList";
import BudgetList from "../Salary/KPI/KPIList";
import DeleteOutlineIcon from "@material-ui/icons/DeleteOutline";

function PositionRolendex() {
    const {positionRoleStore} = useStore();
    const {t} = useTranslation();

    const {
        handleDeleteList,
        pagingPositionRole,
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
    } = positionRoleStore;

    useEffect(() => {
        pagingPositionRole();

        return resetStore;
    }, []);

    async function handleFilter(values) {
        const newSearchObject = {
            ...values,
            pageIndex: 1,
        };
        handleSetSearchObject(newSearchObject);
        await pagingPositionRole();
    }

    return (
        <div className="content-index">
            <div className="index-breadcrumb">
                <GlobitsBreadcrumb
                    routeSegments={[{name: "Cơ cấu tổ chức"}, {name: t("navigation.positionRole.title")}]}/>
            </div>
            <Grid className='index-card' container spacing={2}>
                <Grid item xs={12}>
                    <Formik
                        enableReinitialize
                        initialValues={searchObject}
                        onSubmit={handleFilter}
                    >
                        {({resetForm, values, setFieldValue, setValues}) => (
                            <Form autoComplete="off">
                                <Grid container spacing={2}>
                                    <Grid item xs={12} md={6}>
                                        <ButtonGroup
                                            color="container"
                                            aria-label="outlined primary button group"
                                        >
                                            <Button
                                                startIcon={<AddIcon/>}
                                                type="button"
                                                onClick={() => handleOpenCreateEdit()}
                                            >
                                                Thêm mới
                                            </Button>
                                            <Button
                                                disabled={listOnDelete?.length <= 0}
                                                startIcon={<DeleteOutlineIcon/>}
                                                onClick={handleDeleteList}
                                            >
                                                {t("general.button.delete")}
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
                        )}
                    </Formik>
                </Grid>
                <Grid item xs={12}>
                    <PositionRoleList/>
                </Grid>
            </Grid>

            {openCreateEditPopup && (
                <PositionRoleCUForm/>
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

export default memo(observer(PositionRolendex));
