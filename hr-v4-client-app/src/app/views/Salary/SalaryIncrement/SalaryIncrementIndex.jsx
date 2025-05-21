import { observer } from "mobx-react";
import React , { useEffect } from "react";
import { useStore } from "app/stores";
import { Button , ButtonGroup , Grid } from "@material-ui/core";
import GlobitsBreadcrumb from "app/common/GlobitsBreadcrumb";
import AddIcon from "@material-ui/icons/Add";
import { useTranslation } from "react-i18next";
import GlobitsConfirmationDialog from "app/common/GlobitsConfirmationDialog";
import { useTheme } from "@material-ui/core/styles";
import useMediaQuery from "@material-ui/core/useMediaQuery";
import SalaryIncrementList from "./SalaryIncrementList";
import SalaryIncrementForm from "./SalaryIncrementForm";
import { Form , Formik } from "formik";
import DeleteOutlineIcon from "@material-ui/icons/DeleteOutline";
import GlobitsTextField from "../../../common/form/GlobitsTextField";
import SearchIcon from "@material-ui/icons/Search";

export default observer(function SalaryIncrementIndex() {
    const {salaryIncrementStore , hrRoleUtilsStore} = useStore();
    const {t} = useTranslation();
    const {
        selectedSalaryIncrementList ,
        updatePageData ,
        handleEditSalaryIncrement ,
        handleDeleteList ,
        handleClose ,
        handleConfirmDelete ,
        handleConfirmDeleteList ,
        shouldOpenConfirmationDialog ,
        shouldOpenConfirmationDeleteListDialog ,
        shouldOpenEditorDialog ,
        openViewPopup
    } = salaryIncrementStore;

    useEffect(() => {
        updatePageData();
    } , []);

    const theme = useTheme();
    const isMobile = useMediaQuery(theme.breakpoints.down("sm"));
    const handleFilter = (values) => {
        updatePageData(values)
    }

    const {
        isAdmin ,
        isManager ,
    } = hrRoleUtilsStore;
    return (
        <div className="content-index">
            <div className="index-breadcrumb">
                {/* <GlobitsBreadcrumb routeSegments={[{ name: t("salaryIncrement.title") }]} /> */}
                <GlobitsBreadcrumb routeSegments={[{name:t("navigation.salary")} , {name:"Loại tăng lương"}]}/>
            </div>
            <Grid className='index-card' container spacing={2}>
                <Grid item xs={12}>
                    <Formik
                        enableReinitialize
                        initialValues={{keyword:""}}
                        onSubmit={handleFilter}
                    >
                        {({resetForm , values , setFieldValue , setValues}) => (
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
                                                    onClick={() => handleEditSalaryIncrement()}
                                                >
                                                    {!isMobile && t("general.button.add")}
                                                </Button>
                                                <Button
                                                    disabled={selectedSalaryIncrementList
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
                        )}
                    </Formik>
                </Grid>
                <Grid item xs={12}>
                    <SalaryIncrementList/>
                </Grid>
            </Grid>
            {shouldOpenEditorDialog && <SalaryIncrementForm/>}
            {openViewPopup && <SalaryIncrementForm readOnly={true}/>}

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