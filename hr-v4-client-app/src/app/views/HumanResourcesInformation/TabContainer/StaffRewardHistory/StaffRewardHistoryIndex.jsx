import React, {useEffect} from "react";
import {Button, ButtonGroup, Grid, makeStyles, Tooltip,} from "@material-ui/core";
import {useTranslation} from "react-i18next";
import AddIcon from "@material-ui/icons/Add";
import StaffRewardHistoryForm from "./StaffRewardHistoryForm";
import {useStore} from "app/stores";
import StaffRewardHistoryList from "./StaffRewardHistoryList";
import GlobitsConfirmationDialog from "app/common/GlobitsConfirmationDialog";
import DeleteOutlineIcon from "@material-ui/icons/DeleteOutline";
import {observer} from "mobx-react";
import {useParams} from "react-router-dom";

const useStyles = makeStyles((theme) => ({
    root: {
        "& .MuiAccordion-rounded": {borderRadius: "5px"},
        "& .MuiPaper-root": {borderRadius: "5px"},
        "& .MuiAccordionSummary-root": {
            borderRadius: "5px",
            color: "#5899d1",
            fontWeight: "400",
            "& .MuiTypography-root": {fontSize: "1rem"},
        },
        "& .Mui-expanded": {
            "& .MuiAccordionSummary-root": {
                backgroundColor: "#EBF3F9",
                color: "#5899d1",
                fontWeight: "700",
                maxHeight: "50px !important",
                minHeight: "50px !important",
            },
            "& .MuiTypography-root": {fontWeight: 700},
        },
        "& .MuiButton-root": {borderRadius: "0.125rem !important"},
    },
    noAllowance: {
        textAlign: "center",
        marginTop: theme.spacing(4),
        fontStyle: "italic",
        color: "#999",
    },
    buttonGroupSpacing: {
        marginBottom: "10px",
    },
}));


export default observer(function StaffRewardHistoryIndex() {
        const {t} = useTranslation();
        const {staffRewardHistoryStore} = useStore();
        const {id} = useParams();

        const classes = useStyles();

        const {
            getAllStaffRewardHistoryByStaff,
            handleEdit,
            setStaffId,
            staffRewardHistoryList,
            shouldOpenEditorDialog,
            shouldOpenConfirmationDialog,
            handleClose,
            handleConfirmDelete,
            handleConfirmDeleteList,
            shouldOpenConfirmationDeleteListDialog,
            selectedStaffRewardHistoryList,
            handleDeleteList
        } = staffRewardHistoryStore;

        useEffect(() => {
            setStaffId(id)
            if (id) {
                getAllStaffRewardHistoryByStaff();
            }
        }, []);

        return (
            <>

                <Grid container spacing={2} className={classes.root}>
                    <Grid item xs={12} className="pb-0">
                        <ButtonGroup
                            color="container"
                            aria-label="outlined primary button group"
                            className={classes.buttonGroupSpacing}
                        >
                            <Tooltip title="Thêm mới quá trình khen thưởng cho nhân viên" placement="top" arrow>
                                <Button
                                    startIcon={<AddIcon/>}
                                    type="button"
                                    onClick={handleEdit}
                                >
                                    Thêm mới
                                </Button>
                            </Tooltip>

                            <Button
                                disabled={selectedStaffRewardHistoryList?.length <= 0}
                                startIcon={<DeleteOutlineIcon/>}
                                onClick={() => handleDeleteList()}
                            >
                                {t("general.button.delete")}
                            </Button>

                        </ButtonGroup>
                    </Grid>

                    <Grid item xs={12}>
                        {staffRewardHistoryList.length > 0 ? (
                            <StaffRewardHistoryList/>
                        ) : (
                            <p className="w-100 text-center">Chưa có quá trình khen thưởng nào</p>
                        )}
                    </Grid>
                </Grid>


                {shouldOpenEditorDialog && (
                    <StaffRewardHistoryForm/>
                )}

                {shouldOpenConfirmationDialog &&
                    <GlobitsConfirmationDialog
                        open={shouldOpenConfirmationDialog}
                        onConfirmDialogClose={handleClose}
                        onYesClick={handleConfirmDelete}
                        title={t("confirm_dialog.delete.title")}
                        text={t("confirm_dialog.delete.text")}
                        agree={t("confirm_dialog.delete.agree")}
                        cancel={t("confirm_dialog.delete.cancel")}
                    />}
                {shouldOpenConfirmationDeleteListDialog &&
                    <GlobitsConfirmationDialog
                        open={shouldOpenConfirmationDeleteListDialog}
                        onConfirmDialogClose={handleClose}
                        onYesClick={handleConfirmDeleteList}
                        title={t("confirm_dialog.delete.title")}
                        text={t("confirm_dialog.delete.text")}
                        agree={t("confirm_dialog.delete.agree")}
                        cancel={t("confirm_dialog.delete.cancel")}
                    />}
            </>
        );
    }
)
