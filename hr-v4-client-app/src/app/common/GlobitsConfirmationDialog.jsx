import React, { memo } from "react";
import DoneIcon from "@material-ui/icons/Done";
import BlockIcon from "@material-ui/icons/Block";
import { Dialog, DialogTitle, Icon, IconButton, Button, DialogContent, DialogActions } from "@material-ui/core";
import Draggable from "react-draggable";
import Paper from "@material-ui/core/Paper";
import "./SearchBox.scss";
import { useTranslation } from "react-i18next";

function GlobitsConfirmationDialog(props) {
    const { open, onConfirmDialogClose, text, title, agree, cancel, onYesClick, handleAfterConfirm } = props;

    const { t } = useTranslation();

    return (
        <Dialog
            maxWidth='xs'
            fullWidth={true}
            open={open}
            onClose={onConfirmDialogClose}
            className='confirmDeletePopup'
            PaperComponent={PaperComponent}>
            <DialogTitle
                className={`bgc-lighter-dark-blue confirmDeletePopupTitle uppercase capitalize text-white py-8 px-12`}
                style={{ cursor: "move" }}
                id='draggable-confirm-dialog-title'>
                {title}
            </DialogTitle>

            <IconButton
                className='p-12'
                style={{ position: "absolute", right: "0px", color: "white" }}
                onClick={() => onConfirmDialogClose()}>
                <Icon title={t("general.close")}>close</Icon>
            </IconButton>

            <DialogContent className={`px-12 py-0`}>
                <p className='py-12'>{text}</p>
            </DialogContent>

            <DialogActions className='confirmDeletePopupFooter'>
                <div className='flex flex-space-between flex-middle'>
                    <Button
                        startIcon={<BlockIcon className='mr-4' />}
                        variant='contained'
                        className='btn  bg-light-gray d-inline-flex mr-12'
                        onClick={onConfirmDialogClose}>
                        {cancel}
                    </Button>
                    <Button
                        className='btn btn-success d-inline-flex'
                        variant='contained'
                        startIcon={<DoneIcon className='mr-4' />}
                        onClick={async () => {
                            await onYesClick();
                            if (typeof handleAfterConfirm === "function") {
                                handleAfterConfirm();
                            }
                            onConfirmDialogClose();
                        }}>
                        {agree}
                    </Button>
                </div>
            </DialogActions>
        </Dialog>
    );
}

export default memo(GlobitsConfirmationDialog);

function PaperComponent(props) {
    return (
        <Draggable handle='#draggable-confirm-dialog-title' cancel={'[class*="MuiDialogContent-root"]'}>
            <Paper {...props} />
        </Draggable>
    );
}
