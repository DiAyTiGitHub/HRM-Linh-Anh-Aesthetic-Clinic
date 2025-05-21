import {observer} from "mobx-react";
import React, {useEffect} from "react";
import {useStore} from "../../stores";
import {Button, ButtonGroup, Grid} from "@material-ui/core";
import GlobitsBreadcrumb from "../../common/GlobitsBreadcrumb";
import DeleteIcon from "@material-ui/icons/Delete";
import AddIcon from "@material-ui/icons/Add";
import {useTranslation} from "react-i18next";
import ReligionList from "./ReligionList";
import GlobitsConfirmationDialog from "../../common/GlobitsConfirmationDialog";
import {useTheme} from "@material-ui/core/styles";
import useMediaQuery from "@material-ui/core/useMediaQuery";
import ReligionForm from "./ReligionForm";
import {Form, Formik} from "formik";
import GlobitsTextField from "../../common/form/GlobitsTextField";
import SearchIcon from "@material-ui/icons/Search";

export default observer(function EthnicsIndex() {
    const {religionStore} = useStore();
    const {t} = useTranslation();

    const {
        search,
        handleSetSearchObject,
        handleEditReligion,
        shouldOpenConfirmationDialog,
        handleClose,
        handleConfirmDelete,
        selectedReligionList,
        handleDeleteList,
        shouldOpenConfirmationDeleteListDialog,
        handleConfirmDeleteList,
        resetReligionStore
    } = religionStore;

    useEffect(() => {
        search();
        return resetReligionStore;
    }, []);

    const theme = useTheme();
    const isMobile = useMediaQuery(theme.breakpoints.down("sm"));

    async function handleFilter(values) {
        const newSearchObject = {
            ...values, pageIndex: 1,
        };
        handleSetSearchObject(newSearchObject);
        await search()
    }

    return (
        <div className="content-index">
            <div className="index-breadcrumb">
                <GlobitsBreadcrumb routeSegments={[{name: t("religion.title")}]}/>
            </div>
            <Grid className="index-card" container spacing={2}>
                <Grid item xs={12}>
                    <Formik
                        enableReinitialize
                        initialValues={{keyword: ""}}
                        onSubmit={handleFilter}
                    >
                        {({resetForm, values, setFieldValue, setValues}) => (<Form autoComplete="off">
                            <Grid container spacing={2}>
                                <Grid item xs={12} md={6}>
                                    <ButtonGroup
                                        color="container"
                                        aria-label="outlined primary button group"
                                    >
                                        <Button
                                            startIcon={<AddIcon/>}
                                            type="button"
                                            onClick={() => handleEditReligion()}
                                        >
                                            {!isMobile && t("general.button.add")}
                                        </Button>
                                        <Button
                                            disabled={selectedReligionList.length <= 0}
                                            startIcon={<DeleteIcon/>}
                                            type="button"
                                            onClick={() => handleDeleteList()}
                                        >
                                            {!isMobile && t("general.button.delete")}
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
                        </Form>)}
                    </Formik>
                </Grid>

                <ReligionForm/>

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

                <Grid item xs={12}>
                    <ReligionList/>
                </Grid>
            </Grid>
        </div>
    );
});
