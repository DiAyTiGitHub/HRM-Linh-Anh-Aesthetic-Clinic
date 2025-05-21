/* eslint-disable react-hooks/exhaustive-deps */
import {Button, ButtonGroup, Grid} from "@material-ui/core";
import AddIcon from "@material-ui/icons/Add";
import SearchIcon from "@material-ui/icons/Search";
import GlobitsBreadcrumb from "app/common/GlobitsBreadcrumb";
import GlobitsConfirmationDialog from "app/common/GlobitsConfirmationDialog";
import {useStore} from "app/stores";
import {Form, Formik} from "formik";
import {observer} from "mobx-react";
import React, {useEffect} from "react";
import {useTranslation} from "react-i18next";
import HrDocumentItemForm from "./HrDocumentItemForm";
import HrDocumentItemList from "./HrDocumentItemList";
import GlobitsTextField from "app/common/form/GlobitsTextField";
import DeleteOutlineIcon from "@material-ui/icons/DeleteOutline";

export default observer(function HrDocumentItemIndex() {
    const {hrDocumentItemStore} = useStore();
    const {t} = useTranslation();

    const {
        search,
        updatePageData,
        handleEditHrDocumentItem,
        shouldOpenEditorDialog,
        shouldOpenConfirmationDialog,
        handleClose,
        handleConfirmDelete,
        selectedListHrDocumentItem,
        handleConfirmDeleteList,
        handleDeleteList,
        shouldOpenConfirmationDeleteListDialog
    } = hrDocumentItemStore;

    useEffect(() => {
        search();
    }, []);

    async function handleFilter(values) {
        const newSearchObject = {
            ...values,
            pageIndex: 1,
        };
        await updatePageData(newSearchObject);
    }

    return (
        <div className='content-index'>
            <div className='index-breadcrumb'>
                <GlobitsBreadcrumb
                    routeSegments={[{name: t("navigation.HumanResourcesInformation")}, {name: t("navigation.hrDocumentItem")}]}/>
            </div>
            <Grid className='index-card' container spacing={3}>
                <Grid item xs={12}>
                    <Formik
                        enableReinitialize
                        initialValues={{keyword: ""}}
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
                                                onClick={() => handleEditHrDocumentItem()}
                                            >
                                                Thêm mới
                                            </Button>
                                            <Button
                                                disabled={selectedListHrDocumentItem?.length === 0}
                                                startIcon={<DeleteOutlineIcon/>}
                                                onClick={() => {
                                                    handleDeleteList();
                                                }}
                                            >
                                                {t("general.button.delete")}
                                            </Button>
                                        </ButtonGroup>
                                    </Grid>

                                    <Grid item xs={12} md={6}>
                                        <div className="flex justify-between align-center">
                                            <GlobitsTextField
                                                placeholder="Tìm kiếm theo từ khóa"
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
                    <HrDocumentItemList/>
                </Grid>
            </Grid>

            {shouldOpenEditorDialog && (
                <HrDocumentItemForm
                    open={shouldOpenEditorDialog}
                />
            )}

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
                title={t("confirm_dialog.delete.title")}
                text={t("confirm_dialog.delete.text")}
                agree={t("confirm_dialog.delete.agree")}
                cancel={t("confirm_dialog.delete.cancel")}
            />
        </div>
    );
});
