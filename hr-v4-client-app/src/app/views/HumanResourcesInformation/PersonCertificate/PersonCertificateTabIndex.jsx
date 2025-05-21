import React, { memo, useEffect } from "react";
import { Button, ButtonGroup, makeStyles, } from "@material-ui/core";
import { FieldArray } from "formik";
import { useTranslation } from "react-i18next";
import AddIcon from "@material-ui/icons/Add";
import PersonCertificateForm from "./PersonCertificateForm";
import { useStore } from "app/stores";
import PersonCertificateList from "./PersonCertificateList";
import GlobitsConfirmationDialog from "app/common/GlobitsConfirmationDialog";
import DeleteOutlineIcon from "@material-ui/icons/DeleteOutline";
import { observer } from "mobx-react";
import { useParams } from "react-router-dom";

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

function PersonCertificateTabIndex() {
    const { t } = useTranslation();
    const { personCertificateStore } = useStore();
    const { id } = useParams();

    const classes = useStyles();

    const {
        getAllPersonCertificateByPerson,
        handleEdit,
        setPersonId,
        personCertificateList,
        shouldOpenEditorDialog,
        shouldOpenConfirmationDialog,
        handleClose,
        handleConfirmDelete,
        selectedPersonCertificateList,
        handleDeleteList,
        shouldOpenConfirmationDeleteListDialog,
        handleConfirmDeleteList,
    } = personCertificateStore;

    useEffect(() => {
        setPersonId(id)
        if (id) {
            getAllPersonCertificateByPerson();
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
                                startIcon={<AddIcon />}
                                type="button"
                                onClick={() => handleEdit()}
                            >
                                {t("general.Add.agreement")}
                            </Button>
                            <Button
                                disabled={selectedPersonCertificateList?.length <= 0}
                                startIcon={<DeleteOutlineIcon />}
                                onClick={() => handleDeleteList()}
                            >
                                {t("general.button.delete")}
                            </Button>
                        </ButtonGroup>
                        <div className={classes.tableContainer}>
                            {personCertificateList?.length > 0 ? (
                                <PersonCertificateList />
                            ) : (
                                <h5 className="text-primary n-w">
                                    Không có chứng chỉ/chứng nhận nào!
                                </h5>
                            )}
                        </div>
                    </div>
                )}
            />

            {shouldOpenEditorDialog && (<PersonCertificateForm />)}
            
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

export default memo(observer(PersonCertificateTabIndex));
