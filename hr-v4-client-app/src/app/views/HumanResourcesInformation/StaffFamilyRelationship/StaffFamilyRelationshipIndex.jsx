import React, {useEffect} from "react";
import {Button, ButtonGroup, makeStyles,} from "@material-ui/core";
import {FieldArray} from "formik";
import {useTranslation} from "react-i18next";
import AddIcon from "@material-ui/icons/Add";
import StaffFamilyRelationshipForm from "./StaffFamilyRelationshipForm";
import {useStore} from "app/stores";
import StaffFamilyRelationshipList from "./StaffFamilyRelationshipList";
import GlobitsConfirmationDialog from "app/common/GlobitsConfirmationDialog";
import DeleteOutlineIcon from "@material-ui/icons/DeleteOutline";
import {observer} from "mobx-react";
import {useParams} from "react-router-dom";

const useStyles = makeStyles((theme) => ({
    root: {
        background: "#E4f5fc",
        padding: "10px 15px",
        borderRadius: "5px",
    },
    groupContainer: {
        width: "100%",
        "& .MuiOutlinedInput-root": {
            borderRadius: "0!important",
        },
    },
    tableContainer: {
        marginTop: "8px",
    },
}));

export default observer(function StaffFamilyRelationshipIndex() {
    const {t} = useTranslation();
    const {staffFamilyRelationshipStore} = useStore();
    const {id} = useParams();

    const classes = useStyles();

    const {
        getAllStaffFamilyRelationshipByStaffId,
        handleStaffFamilyRelationshipEdit,
        staffFamilyRelationshipList,
        setStaffId,
        shouldOpenEditorDialog,
        shouldOpenConfirmationDialog,
        handleClose,
        handleConfirmDelete,
        selectedStaffFamilyRelationshipList,
        handleDeleteList,
        shouldOpenConfirmationDeleteListDialog,
        handleConfirmDeleteList,
    } = staffFamilyRelationshipStore;

    useEffect(() => {
        setStaffId(id)
        if (id) {
            getAllStaffFamilyRelationshipByStaffId();
        }
    }, []);

    return (
        <>
            <FieldArray
                name="personCertificate"
                render={(arrayHelpers) => (
                    <div className={classes.groupContainer}>
                        <ButtonGroup
                            color="container"
                            aria-label="outlined primary button group"
                        >
                            <Button
                                startIcon={<AddIcon/>}
                                type="button"
                                onClick={() => handleStaffFamilyRelationshipEdit()}
                            >
                                {t("general.Add.salaryHistory")}
                            </Button>
                            <Button
                                disabled={selectedStaffFamilyRelationshipList?.length <= 0}
                                startIcon={<DeleteOutlineIcon/>}
                                onClick={() => handleDeleteList()}
                            >
                                {t("general.button.delete")}
                            </Button>
                        </ButtonGroup>
                        <div className={classes.tableContainer}>
                            {staffFamilyRelationshipList?.length > 0 ? (
                                <StaffFamilyRelationshipList/>
                            ) : (
                                <h5 className="text-primary n-w">
                                    Không có quan hệ thân nhân !
                                </h5>
                            )}
                        </div>
                    </div>
                )}
            />
            {shouldOpenEditorDialog && (<StaffFamilyRelationshipForm/>)}
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
})
