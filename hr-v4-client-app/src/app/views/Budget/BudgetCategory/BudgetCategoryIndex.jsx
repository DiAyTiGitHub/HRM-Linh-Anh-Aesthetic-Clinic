/* eslint-disable react-hooks/exhaustive-deps */
import {observer} from "mobx-react";
import React, {useEffect} from "react";
import {useStore} from "../../../stores";
import {Button, ButtonGroup, Grid} from "@material-ui/core";
import DeleteIcon from "@material-ui/icons/Delete";
import AddIcon from "@material-ui/icons/Add";
import {useTranslation} from "react-i18next";
import BudgetCategoryList from "./BudgetCategoryList";
import GlobitsConfirmationDialog from "../../../common/GlobitsConfirmationDialog";
import {useTheme} from "@material-ui/core/styles";
import useMediaQuery from "@material-ui/core/useMediaQuery";
import GlobitsSearchInput from "app/common/GlobitsSearchInput";
import BudgetCategoryForm from "./BudgetCategoryForm";
import GlobitsBreadcrumb from "app/common/GlobitsBreadcrumb";
import {Form, Formik} from "formik";
import GlobitsTextField from "../../../common/form/GlobitsTextField";
import SearchIcon from "@material-ui/icons/Search";

export default observer(function BudgetCategoryIndex() {
    const {budgetCategoryStore} = useStore();
    const {t} = useTranslation();

    const {
        updatePageData,
        handleEditBudgetCategory,
        shouldOpenEditorDialog,
        shouldOpenConfirmationDialog,
        handleClose,
        handleConfirmDelete,
        selectedBudgetCategoryList,
        handleDeleteList,
        shouldOpenConfirmationDeleteListDialog,
        handleConfirmDeleteList,
        resetBudgetCategoryStore,
        search
    } = budgetCategoryStore;

    useEffect(() => {
        search();
        return resetBudgetCategoryStore;
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
            <div className='index-breadcrumb py-6'>
                <GlobitsBreadcrumb routeSegments={[{name: t("budgetCategory.title")}]}/>
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
                                                onClick={() => handleEditBudgetCategory()}
                                            >
                                                Thêm mới
                                            </Button>
                                            <Button
                                                startIcon={<DeleteIcon/>}
                                                onClick={() => handleDeleteList()}
                                                disabled={selectedBudgetCategoryList.length <= 0}
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
                    <BudgetCategoryList/>
                </Grid>
            </Grid>
            {shouldOpenEditorDialog && <BudgetCategoryForm open={shouldOpenEditorDialog}/>}

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
        </div>
    );
});
