import React, {useEffect} from 'react';
import {useStore} from "../../stores";
import {observer} from 'mobx-react-lite';
import GlobitsBreadcrumb from 'app/common/GlobitsBreadcrumb';
import AddIcon from "@material-ui/icons/Add";
import {Button, ButtonGroup, Grid} from "@material-ui/core";
import GlobitsConfirmationDialog from 'app/common/GlobitsConfirmationDialog';
import DisciplineList from "./DisciplineList";
import {useTranslation} from 'react-i18next';
import {useTheme} from "@material-ui/core/styles";
import useMediaQuery from "@material-ui/core/useMediaQuery";
import DisciplineForm from './DisciplineForm';
import {Form, Formik} from "formik";
import DeleteOutlineIcon from "@material-ui/icons/DeleteOutline";
import GlobitsTextField from "../../common/form/GlobitsTextField";
import SearchIcon from "@material-ui/icons/Search";

export default observer(function DisciplineIndex() {
    const {disciplineStore} = useStore();
    const {t} = useTranslation();
    const
        {
            shouldOpenDeleteDialog,
            handleAddDiscipline,
            updatePageData,
            handleDeleteList,
            handleCloseDisciplineDialog,
            handleConfirmDelete,
            selectedDisciplineList,
            handleConfirmDeleteList,
            shouldOpenConfirmationDeleteListDialog,
            shouldOpenEditorDialog,
        } = disciplineStore;

    useEffect(() => {
        updatePageData();
    }, [updatePageData]);

    const theme = useTheme();
    const isMobile = useMediaQuery(theme.breakpoints.down("sm"));
    const isExtraSmall = useMediaQuery(theme.breakpoints.down("xs"));
    const handleFilter = (values) => {
        updatePageData(values)
    }
    return (
        <div className="content-index">
            <div className="index-breadcrumb">
                <GlobitsBreadcrumb routeSegments={[{name: t("discipline.title")}]}/>
            </div>
            <Grid className='index-card' container spacing={2}>
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
                                            <Button startIcon={<AddIcon/>}
                                                    onClick={() => handleAddDiscipline()}
                                            >
                                                {!isMobile && t("general.button.add")}
                                            </Button>
                                            <Button
                                                disabled={selectedDisciplineList?.length === 0}
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
                        )}
                    </Formik>
                </Grid>
                <Grid item xs={12}>
                    <DisciplineList/>
                </Grid>
            </Grid>

            {shouldOpenEditorDialog && <DisciplineForm open={shouldOpenEditorDialog}/>}

            <GlobitsConfirmationDialog
                open={shouldOpenDeleteDialog}
                onConfirmDialogClose={handleCloseDisciplineDialog}
                onYesClick={handleConfirmDelete}
                title={t("confirm_dialog.delete.title")}
                text={t("confirm_dialog.delete.text")}
                agree={t("confirm_dialog.delete.agree")}
                cancel={t("confirm_dialog.delete.cancel")}
            />
            <GlobitsConfirmationDialog
                open={shouldOpenConfirmationDeleteListDialog}
                onConfirmDialogClose={handleCloseDisciplineDialog}
                onYesClick={handleConfirmDeleteList}
                title={t("confirm_dialog.delete_list.title")}
                text={t("confirm_dialog.delete_list.text")}
                agree={t("confirm_dialog.delete_list.agree")}
                cancel={t("confirm_dialog.delete_list.cancel")}
            />
        </div>
    )
})
