/* eslint-disable react-hooks/exhaustive-deps */
import {observer} from "mobx-react";
import React, {memo, useEffect} from "react";
import {useStore} from "../../stores";
import {
    Grid,
    Button,
    useTheme,
    ButtonGroup,
    Popover,
} from "@material-ui/core";
import GlobitsBreadcrumb from "../../common/GlobitsBreadcrumb";
import DeleteIcon from "@material-ui/icons/Delete";
import AddIcon from "@material-ui/icons/Add";
import {useTranslation} from "react-i18next";
import RewardList from "./RewardList";
import GlobitsConfirmationDialog from "../../common/GlobitsConfirmationDialog";
import useMediaQuery from "@material-ui/core/useMediaQuery";
import GlobitsSearchInput from "app/common/GlobitsSearchInput";
import RewardForm from "./RewardForm";
import {useLocation} from "react-router-dom";
import DashboardIcon from "@material-ui/icons/Dashboard";
import PublishIcon from "@material-ui/icons/Publish";
import UpdateIcon from "@material-ui/icons/Update";
import DeleteOutlineIcon from "@material-ui/icons/DeleteOutline";
import history from "history.js";
import ConstantList from "../../appConfig";
import {Form, Formik} from "formik";
import GlobitsTextField from "../../common/form/GlobitsTextField";
import SearchIcon from "@material-ui/icons/Search";
import PositionRoleList from "../PositionRole/PositionRoleList";

function RewardIndex() {
    const {t} = useTranslation();
    const location = useLocation();
    const theme = useTheme();
    const [anchorEl, setAnchorEl] = React.useState(null);

    const open = Boolean(anchorEl);
    const id = open ? "simple-popper" : undefined;
    const handleClick = (event) => {
        setAnchorEl(anchorEl ? null : event.currentTarget);
    };

    const {
        handleConfirmDelete,
        handleOpenConfirm,
        handleClose,
        shouldOpenConfirmationMultiple,
        shouldOpenConfirmationDialog,
        resetStore,
        listSelected,
        pagingRecruitment,
        handleConfirmDeleteMultiple,
        updatePageData
    } = useStore().rewardStore;

    useEffect(() => {
        pagingRecruitment();
        return resetStore;
    }, [location]);

    const isExtraSmall = useMediaQuery((theme) => theme.breakpoints.down("xs"));
    const isMobile = useMediaQuery(theme.breakpoints.down("sm"));
    const handleFilter = (values) => {
        updatePageData(values)
    }
    return (
        <div className="content-index">
            <GlobitsBreadcrumb
                routeSegments={[
                    {name: t("navigation.category.title")},
                    {name: t("navigation.category.staff.title")},
                    {name: t("navigation.category.staff.awardType")},
                ]}
            />
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
                                            <Button
                                                startIcon={<AddIcon/>}
                                                onClick={() => {
                                                    history.push(ConstantList.ROOT_PATH + "category/staff/reward-form")
                                                }}
                                            >
                                                {!isMobile && t("general.button.add")}
                                            </Button>

                                            <Button
                                                disabled={listSelected?.length === 0}
                                                startIcon={<DeleteOutlineIcon/>}
                                                onClick={() => {
                                                    handleOpenConfirm();
                                                }}
                                            >
                                                {!isMobile && t("general.button.delete")}
                                            </Button>

                                            {/* <Button
                startIcon={<DashboardIcon />}
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
                                                placeholder="Tìm kiếm theo tên nhóm quyền..."
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
                    <RewardList/>
                </Grid>
            </Grid>
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
}

export default memo(observer(RewardIndex));
