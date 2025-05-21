import {Button, ButtonGroup, Grid} from "@material-ui/core";
import {useTheme} from "@material-ui/core/styles";
import AddIcon from "@material-ui/icons/Add";
import {useStore} from "app/stores";
import {observer} from "mobx-react";
import React, {useEffect} from "react";
import {useTranslation} from "react-i18next";
import BudgetList from "./KPIResultList";
import GlobitsConfirmationDialog from "app/common/GlobitsConfirmationDialog";
import GlobitsBreadcrumb from "app/common/GlobitsBreadcrumb";
import KPIResultForm from "./KPIResultForm";
import GlobitsTextField from "../../../common/form/GlobitsTextField";
import SearchIcon from "@material-ui/icons/Search";
import {Form, Formik} from "formik";

export default observer(function KPIIndex() {
    const {KPIResultStore} = useStore();
    const {t} = useTranslation();

    const {
        shouldOpenConfirmationDialog,
        shouldOpenEditorDialog,
        handleConfirmDelete,
        handleClose,
        handleEditKPIResult,
        search,
        updatePageData
    } = KPIResultStore;

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
                    routeSegments={[
                        {name: t("navigation.salary")},
                        {name: t("navigation.kpi-result")},
                    ]}
                /></div>
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
                                                onClick={() => handleEditKPIResult()}
                                            >
                                                Thêm mới
                                            </Button>
                                            {/*
                                                <Button
                                                    disabled={listOnDelete?.length <= 0}
                                                    startIcon={<DeleteOutlineIcon />}
                                                    onClick={handleDeleteList}
                                                >
                                                    {t("general.button.delete")}
                                                </Button>
                                                */}
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
                    <BudgetList/>
                </Grid>
            </Grid>
            {shouldOpenEditorDialog && <KPIResultForm open={shouldOpenEditorDialog}/>}

            <GlobitsConfirmationDialog
                open={shouldOpenConfirmationDialog}
                onConfirmDialogClose={handleClose}
                onYesClick={handleConfirmDelete}
                title={t("confirm_dialog.delete.title")}
                text={t("confirm_dialog.delete.text")}
                agree={t("confirm_dialog.delete.agree")}
                cancel={t("confirm_dialog.delete.cancel")}
            />
        </div>
    );
});
