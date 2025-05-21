import {Button, ButtonGroup, Grid, useMediaQuery, useTheme,} from "@material-ui/core";
import GlobitsBreadcrumb from "app/common/GlobitsBreadcrumb";
import React, {useEffect} from "react";
import {useTranslation} from "react-i18next";
import AddIcon from "@material-ui/icons/Add";
import DeleteOutlineIcon from "@material-ui/icons/DeleteOutline";
import {useStore} from "app/stores";
import GlobitsConfirmationDialog from "app/common/GlobitsConfirmationDialog";
import {observer} from "mobx-react";
import {Form, Formik} from "formik";
import LeavingJobReasonForm from "./LeavingJobReasonForm";
import LeavingJobReasonList from "./LeavingJobReasonList";
import GlobitsTextField from "../../../../common/form/GlobitsTextField";
import SearchIcon from "@material-ui/icons/Search";

const LeavingJobReasonIndex = () => {
    const {leavingJobReasonStore} = useStore();
    const {t} = useTranslation();
    const theme = useTheme();
    const isMobile = useMediaQuery(theme.breakpoints.down("sm"));
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
        shouldOpenEditorDialog,
        updatePageData
    } = leavingJobReasonStore;

    const open = Boolean(anchorEl);
    const id = open ? "simple-popper" : undefined;
    const handleClick = (event) => {
        setAnchorEl(anchorEl ? null : event.currentTarget);
    };

    useEffect(() => {
        pagingRecruitment();
    }, []);
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
                        {name: t("navigation.category.staff.leaveReasons")},
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
                                                    console.log("listSelected:", listSelected);
                                                    handleOpenConfirm();
                                                }}
                                            >
                                                {!isMobile && t("general.button.delete")}
                                            </Button>
                                            {/*<Button*/}
                                            {/*    startIcon={<DashboardIcon/>}*/}
                                            {/*    aria-describedby={id}*/}
                                            {/*    type="button"*/}
                                            {/*    onClick={handleClick}*/}
                                            {/*>*/}
                                            {/*    Khác*/}
                                            {/*</Button>*/}
                                            {/*<Popover*/}
                                            {/*    id={id}*/}
                                            {/*    open={open}*/}
                                            {/*    anchorEl={anchorEl}*/}
                                            {/*    onClose={handleClick}*/}
                                            {/*    anchorOrigin={{*/}
                                            {/*        vertical: "bottom",*/}
                                            {/*        horizontal: "right",*/}
                                            {/*    }}*/}
                                            {/*    transformOrigin={{*/}
                                            {/*        vertical: "top",*/}
                                            {/*        horizontal: "right",*/}
                                            {/*    }}*/}
                                            {/*>*/}
                                            {/*    <div className="menu-list-button">*/}
                                            {/*        <div*/}
                                            {/*            className="menu-item-button"*/}
                                            {/*            style={{borderBottom: "1px solid #e0e0e0"}}*/}
                                            {/*        >*/}
                                            {/*            <UpdateIcon style={{fontSize: 16}}/> Cập nhật trạng thái*/}
                                            {/*        </div>*/}
                                            {/*        <div className="menu-item-button">*/}
                                            {/*            <PublishIcon*/}
                                            {/*                style={{fontSize: 16, transform: "rotate(180deg)"}}*/}
                                            {/*            />{" "}*/}
                                            {/*            Kết xuất danh sách*/}
                                            {/*        </div>*/}
                                            {/*        <div className="menu-item-button">*/}
                                            {/*            <PublishIcon style={{fontSize: 16}}/> Import dữ liệu*/}
                                            {/*        </div>*/}
                                            {/*    </div>*/}
                                            {/*</Popover>*/}
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
                    <LeavingJobReasonList/>
                </Grid>
            </Grid>
            {shouldOpenEditorDialog && <LeavingJobReasonForm/>}
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
        </div>
    );
};

export default observer(LeavingJobReasonIndex);
