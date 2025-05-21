import React, { memo, useEffect, useState } from "react";
import { observer } from "mobx-react";
import { useFormikContext } from "formik";
import Draggable from "react-draggable";
import {
    Dialog,
    DialogTitle,
    Icon,
    IconButton,
    DialogContent,
    Grid,
    DialogActions,
    Button,
    Tooltip,
} from "@material-ui/core";
import Paper from "@material-ui/core/Paper";
import { useTranslation } from "react-i18next";
import DepartmentFilters from "app/views/Department/DepartmentFilters";
import TouchAppIcon from '@material-ui/icons/TouchApp';
import ChooseCandidateDepartmentList from "./ChooseCandidateDepartmentList";

function PaperComponent(props) {
    return (
        <Draggable
            handle="#draggable-dialog-title"
            cancel={'[class*="MuiDialogContent-root"]'}
        >
            <Paper {...props} />
        </Draggable>
    );
}

function ChooseCandidateDepartmentPopup() {
    const { t } = useTranslation();

    const [openPopup, setOpenPopup] = useState(false);

    const handleConfirmSelectDepartment = () => {
        setOpenPopup(false);
    };

    function handleClosePopup() {
        setOpenPopup(false);
    }

    return (
        <>
            <Tooltip placement="top" title={t("general.button.select") + " đơn vị"}>
                <Button
                    variant="contained"
                    className="btn-info mt-25 px-0"
                    onClick={() => setOpenPopup(true)}
                >
                    <TouchAppIcon className="text-white" />
                </Button>
            </Tooltip>

            <Dialog
                className="dialog-container"
                open={openPopup}
                PaperComponent={PaperComponent}
                fullWidth
                maxWidth="md"
            >
                <DialogTitle className="dialog-header" id="draggable-dialog-title">
                    <span className="mb-20 text-white">{t("Lựa chọn đơn vị")}</span>
                </DialogTitle>
                <IconButton
                    style={{ position: "absolute", right: "0", top: "0" }}
                    onClick={handleClosePopup}
                >
                    <Icon
                        className="text-white"
                        title={t("general.close")}
                    >
                        close
                    </Icon>
                </IconButton>
                <DialogContent
                    className="dialog-body"
                    style={{ maxHeight: "80vh" }}
                >
                    <Grid container className="mb-16">
                        <Grid item lg={6} md={6} sm={4} xs={4}></Grid>
                        <Grid item lg={6} md={6} sm={8} xs={8}>
                            <DepartmentFilters />
                        </Grid>
                    </Grid>
                    <Grid item xs={12}>
                        <ChooseCandidateDepartmentList
                            handleClose={handleClosePopup}
                        />
                    </Grid>
                </DialogContent>
                {/* <DialogActions className="dialog-footer p-0">
                    <div className="flex flex-space-between flex-middle">
                        <Button
                            variant="contained"
                            className="mr-12 btn btn-secondary d-inline-flex"
                            color="secondary"
                            onClick={handleClosePopup}
                        >
                            {t("general.button.cancel")}
                        </Button>
                        <Button
                            className="mr-0 btn btn-primary d-inline-flex"
                            variant="contained"
                            color="primary"
                            onClick={() => {
                                handleConfirmSelectDepartment();
                            }}
                        >
                            {t("general.button.select")}
                        </Button>
                    </div>
                </DialogActions> */}
            </Dialog >
        </>
    );
}

export default memo(observer(ChooseCandidateDepartmentPopup));
