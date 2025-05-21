import {Button, ButtonGroup, Grid, useMediaQuery, useTheme} from "@material-ui/core";
import GlobitsBreadcrumb from "app/common/GlobitsBreadcrumb";
import React, {useEffect} from "react";
import {useTranslation} from "react-i18next";
import AddIcon from "@material-ui/icons/Add";
import DeleteOutlineIcon from "@material-ui/icons/DeleteOutline";
import ExamCategoryList from "./RecruitmentExamTypeList";
import {useStore} from "app/stores";
import ExamCategoryForm from "./RecruitmentExamTypeForm";
import GlobitsConfirmationDialog from "app/common/GlobitsConfirmationDialog";
import {observer} from "mobx-react";
import {Form, Formik} from "formik";
import GlobitsSearchInput from "app/common/GlobitsSearchInput";
import GlobitsTextField from "../../../common/form/GlobitsTextField";
import SearchIcon from "@material-ui/icons/Search";

const RecruitmentExamTypeIndex = () => {

    const {recruitmentExamTypeStore} = useStore();
    const {t} = useTranslation();
    const theme = useTheme();
    const isMobile = useMediaQuery(theme.breakpoints.down("sm"));
    const [anchorEl, setAnchorEl] = React.useState(null);

    const {
        handleOpenForm,
        shouldOpenConfirmationMultiple,
        shouldOpenConfirmationDialog,
        handleClose,
        handleConfirmDelete,
        listSelected,
        pagingRecruitmentExamType,
        handleConfirmDeleteMultiple,
        handleOpenConfirm,
        updatePageData,
        keyword
    } = recruitmentExamTypeStore;

    const open = Boolean(anchorEl);
    const id = open ? "simple-popper" : undefined;
    const handleClick = (event) => {
        setAnchorEl(anchorEl ? null : event.currentTarget);
    };

    useEffect(() => {
        pagingRecruitmentExamType();
    }, []);

    async function handleFilter(values) {
        const newSearchObject = {
            ...values,
            pageIndex: 1,
        };
        await updatePageData(newSearchObject);
    }

    return (
        <div className="content-index">
            <div className="index-breadcrumb">
                <GlobitsBreadcrumb
                    routeSegments={[
                        {name: t("navigation.category.title")},
                        {name: t("navigation.recruitment.category")},
                        {name: "Loại kiểm tra"},
                    ]}
                />
            </div>
            <Grid className="index-card" container spacing={2}>
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
                                                onClick={() => handleOpenForm()}
                                            >
                                                {!isMobile && t("general.button.add")}
                                            </Button>
                                            <Button
                                                disabled={listSelected?.length === 0}
                                                startIcon={<DeleteOutlineIcon/>}
                                                onClick={() => {
                                                    handleOpenConfirm()
                                                }}
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
                            </Form>
                        )}
                    </Formik>
                </Grid>

                <ExamCategoryForm/>
                {shouldOpenConfirmationDialog && (
                    <GlobitsConfirmationDialog
                        open={shouldOpenConfirmationDialog}
                        onConfirmDialogClose={handleClose}
                        onYesClick={handleConfirmDelete}
                        title={t("confirm_dialog.delete.title")}
                        text={t("confirm_dialog.delete.text")}
                        agree={t("confirm_dialog.delete.agree")}
                        cancel={t("confirm_dialog.delete.cancel")}
                    />
                )}
                {shouldOpenConfirmationMultiple && (
                    <GlobitsConfirmationDialog
                        open={shouldOpenConfirmationMultiple}
                        onConfirmDialogClose={handleClose}
                        onYesClick={handleConfirmDeleteMultiple}
                        title={t("confirm_dialog.delete.title")}
                        text={t("confirm_dialog.delete.text")}
                        agree={t("confirm_dialog.delete.agree")}
                        cancel={t("confirm_dialog.delete.cancel")}
                    />
                )}
                <Grid item xs={12}>
                    <ExamCategoryList/>
                </Grid>
            </Grid>
        </div>
    );
};

export default observer(RecruitmentExamTypeIndex);
