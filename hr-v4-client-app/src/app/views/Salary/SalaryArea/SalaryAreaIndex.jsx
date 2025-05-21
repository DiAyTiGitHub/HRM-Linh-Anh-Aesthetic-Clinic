import { observer } from "mobx-react";
import React , { memo , useEffect } from "react";
import { useStore } from "app/stores";
import { Button , ButtonGroup , Grid } from "@material-ui/core";
import GlobitsBreadcrumb from "app/common/GlobitsBreadcrumb";
import AddIcon from "@material-ui/icons/Add";
import { useTranslation } from "react-i18next";
import GlobitsConfirmationDialog from "app/common/GlobitsConfirmationDialog";
import SearchIcon from '@material-ui/icons/Search';
import { Form , Formik } from "formik";
import GlobitsTextField from "app/common/form/GlobitsTextField";
import SalaryAreaList from "./SalaryAreaList";
import SalaryAreaCUForm from "./SalaryAreaCUForm";
import { useTheme } from "@material-ui/core/styles";
import useMediaQuery from "@material-ui/core/useMediaQuery";
import DeleteOutlineIcon from "@material-ui/icons/DeleteOutline";

function SalaryAreaIndex() {
    const {salaryAreaStore , hrRoleUtilsStore} = useStore();
    const {t} = useTranslation();

    const {
        handleDeleteList ,
        pagingSalaryArea ,
        handleOpenCreateEdit ,
        openConfirmDeleteListPopup ,
        openConfirmDeletePopup ,
        openCreateEditPopup ,
        handleClose ,
        handleConfirmDelete ,
        handleConfirmDeleteList ,
        searchObject ,
        listOnDelete ,
        resetStore ,
        handleSetSearchObject ,
        openViewPopup
    } = salaryAreaStore;

    useEffect(() => {
        pagingSalaryArea();

        return resetStore;
    } , []);
    const theme = useTheme();
    const isMobile = useMediaQuery(theme.breakpoints.down("sm"));

    const {
        isAdmin ,
        isManager ,
    } = hrRoleUtilsStore;

    async function handleFilter(values) {
        const newSearchObject = {
            ... values ,
            pageIndex:1 ,
        };
        handleSetSearchObject(newSearchObject);
        await pagingSalaryArea();
    }

    return (
        <div className="content-index">
            <div className="index-breadcrumb">
                <GlobitsBreadcrumb
                    routeSegments={[{name:t("navigation.salary")} , {name:t("navigation.salaryArea.title")}]}/>
            </div>
            <Grid className="index-card" container spacing={2}>
                <Grid item xs={12}>
                    <Formik
                        enableReinitialize
                        initialValues={searchObject}
                        onSubmit={handleFilter}
                    >
                        {({resetForm , values , setFieldValue , setValues}) => {
                            return (
                                <Form autoComplete="off">
                                    <Grid container spacing={2}>
                                        <Grid item xs={12} md={6}>
                                            {(isManager || isAdmin) && (
                                                <ButtonGroup
                                                    color="container"
                                                    aria-label="outlined primary button group"
                                                >
                                                    <Button
                                                        startIcon={<AddIcon/>}
                                                        onClick={() => handleOpenCreateEdit()}
                                                    >
                                                        {!isMobile && t("general.button.add")}
                                                    </Button>
                                                    <Button
                                                        disabled={listOnDelete
                                                            ?.length === 0}
                                                        startIcon={<DeleteOutlineIcon/>}
                                                        onClick={() => {
                                                            handleDeleteList();
                                                        }}
                                                    >
                                                        {!isMobile && t("general.button.delete")}
                                                    </Button>
                                                </ButtonGroup>
                                            )}
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
                    <SalaryAreaList/>
                </Grid>
            </Grid>

            {openCreateEditPopup && (
                <SalaryAreaCUForm/>
            )}
            {openViewPopup && (
                <SalaryAreaCUForm readOnly={true}/>
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

export default memo(observer(SalaryAreaIndex));
