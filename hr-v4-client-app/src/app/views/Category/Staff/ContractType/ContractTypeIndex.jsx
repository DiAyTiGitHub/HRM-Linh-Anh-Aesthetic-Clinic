import {observer} from "mobx-react";
import React, {useEffect} from "react";
import {useStore} from "../../../../stores";
import {Button, ButtonGroup, Grid, Popover} from "@material-ui/core";
import GlobitsBreadcrumb from "../../../../common/GlobitsBreadcrumb";
import AddIcon from "@material-ui/icons/Add";
import {useTranslation} from "react-i18next";
import ContractTypeList from "./ContractTypeList";
import GlobitsConfirmationDialog from "../../../../common/GlobitsConfirmationDialog";
import {useTheme} from "@material-ui/core/styles";
import useMediaQuery from "@material-ui/core/useMediaQuery";
import ContractTypeForm from "./ContractTypeForm";
import {Form, Formik} from "formik";
import DeleteOutlineIcon from "@material-ui/icons/DeleteOutline";
import DashboardIcon from '@material-ui/icons/Dashboard';
import PublishIcon from "@material-ui/icons/Publish";
import UpdateIcon from '@material-ui/icons/Update';
import GlobitsTextField from "../../../../common/form/GlobitsTextField";
import SearchIcon from "@material-ui/icons/Search";


export default observer(function ContractTypeIndex() {
    const {contractTypeStore} = useStore();
    const {t} = useTranslation();
    const [anchorEl, setAnchorEl] = React.useState(null);
    const {
        handleOpenForm,
        shouldOpenConfirmationDialog,
        handleClose,
        handleConfirmDelete,
        listSelected,
        pagingRecruitment,
        shouldOpenConfirmationMultiple,
        handleConfirmDeleteMultiple,
        handleOpenConfirm,
        updatePageData,
        shouldOpenEditorDialog
    } = contractTypeStore;

    useEffect(() => {
        pagingRecruitment();
    }, [updatePageData]);

    const theme = useTheme();
    const isMobile = useMediaQuery(theme.breakpoints.down("sm"));
    const isExtraSmall = useMediaQuery(theme.breakpoints.down("xs"));

    const open = Boolean(anchorEl);
    const id = open ? "simple-popper" : undefined;
    const handleClick = (event) => {
        setAnchorEl(anchorEl ? null : event.currentTarget);
    };
    const handleFilter = (values) => {
        updatePageData(values)
    }
    return (
        <div className="content-index">
            <div className="index-breadcrumb">
                <GlobitsBreadcrumb
                    routeSegments={[
                        {name: t("navigation.category.title")},
                        {name: t("navigation.category.staff.title")},
                        {name: t("contractType.title")},
                    ]}
                />
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
                                            <Button startIcon={<AddIcon/>} onClick={() => handleOpenForm()}>
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
                                            {/* <Button
                                                startIcon={<DashboardIcon/>}
                                                aria-describedby={id}
                                                type="button"
                                                onClick={handleClick}
                                            >
                                                Khác
                                            </Button> */}
                                            <Popover
                                                id={id}
                                                open={open}
                                                anchorEl={anchorEl}
                                                onClose={handleClick}
                                                anchorOrigin={{
                                                    vertical: "bottom",
                                                    horizontal: "right",
                                                }}
                                                transformOrigin={{
                                                    vertical: "top",
                                                    horizontal: "right",
                                                }}
                                            >
                                                <div className="menu-list-button">
                                                    <div
                                                        className="menu-item-button"
                                                        style={{borderBottom: "1px solid #e0e0e0"}}
                                                    >
                                                        <UpdateIcon style={{fontSize: 16}}/> Cập nhật trạng thái
                                                    </div>
                                                    <div className="menu-item-button">
                                                        <PublishIcon
                                                            style={{fontSize: 16, transform: "rotate(180deg)"}}
                                                        />{" "}
                                                        Kết xuất danh sách
                                                    </div>
                                                    <div className="menu-item-button">
                                                        <PublishIcon style={{fontSize: 16}}/> Import dữ liệu
                                                    </div>
                                                </div>
                                            </Popover>
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
                    <ContractTypeList/>
                </Grid>
            </Grid>
            {shouldOpenEditorDialog && <ContractTypeForm/>}
            <GlobitsConfirmationDialog
                open={shouldOpenConfirmationDialog}
                onConfirmDialogClose={handleClose}
                onYesClick={handleConfirmDelete}
                title={t("confirm_dialog.delete.title")}
                text={t("confirm_dialog.delete.text")}
                agree={t("confirm_dialog.delete.agree")}
                cancel={t("confirm_dialog.delete.cancel")}
            />

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
        </div>
    );
});
