/* eslint-disable react-hooks/exhaustive-deps */
import {Button, ButtonGroup, Grid} from "@material-ui/core";
import {useTheme} from "@material-ui/core/styles";
import useMediaQuery from "@material-ui/core/useMediaQuery";
import AddIcon from "@material-ui/icons/Add";
import GlobitsSearchInput from "app/common/GlobitsSearchInput";
import {useStore} from "app/stores";
import {observer} from "mobx-react";
import React, {useEffect} from "react";
import {useTranslation} from "react-i18next";
import BudgetList from "./KPIList";
import GlobitsConfirmationDialog from "app/common/GlobitsConfirmationDialog";
import GlobitsBreadcrumb from "app/common/GlobitsBreadcrumb";
import KPIForm from "./KPIForm";
import {Form, Formik} from "formik";
import GlobitsTextField from "../../../common/form/GlobitsTextField";
import SearchIcon from "@material-ui/icons/Search";
import DeleteOutlineIcon from "@material-ui/icons/DeleteOutline";

export default observer(function KPIIndex() {
    const {KPIStore} = useStore();
    const {t} = useTranslation();

    const {
        search,
        updatePageData,
        handleEditKPI,
        shouldOpenEditorDialog,
        shouldOpenConfirmationDialog,
        handleClose,
        handleConfirmDelete,
        resetKpiStore
    } = KPIStore;

    useEffect(() => {
        search();
        return resetKpiStore;
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
                <GlobitsBreadcrumb routeSegments={[{name: t("navigation.salary")}, {name: t("navigation.kpi")}]}/>
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
                                                onClick={() => handleEditKPI()}
                                            >
                                                Thêm mới
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
                    <BudgetList/>
                </Grid>
            </Grid>

            {shouldOpenEditorDialog && <KPIForm open={shouldOpenEditorDialog}/>}

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
