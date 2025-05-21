import React, {memo, useState} from "react";
import DoneIcon from "@material-ui/icons/Done";
import BlockIcon from "@material-ui/icons/Block";
import {
    Dialog,
    DialogTitle,
    Icon,
    IconButton,
    Button,
    DialogContent,
    DialogActions,
    Typography
} from "@material-ui/core";
import Draggable from "react-draggable";
import Paper from "@material-ui/core/Paper";
import {useTranslation} from "react-i18next";
import "./SearchBox.scss";
import QRCode from "react-qr-code";

function GlobitsQrLink(props) {
    const {
        open,
        onConfirmDialogClose,
        text,
        title,
        agree,
        cancel,
        onYesClick,
        handleAfterConfirm,
        maxWidth = 'xs'
    } = props;

    const {t} = useTranslation();
    const [copySuccess, setCopySuccess] = useState('');

    // Hàm sao chép link vào clipboard
    const handleCopyClick = () => {
        navigator.clipboard.writeText(text)
            .then(() => {
                setCopySuccess('Copied!');
                setTimeout(() => setCopySuccess(''), 2000); // Hiển thị thông báo "Copied!" trong 2 giây
            })
            .catch(err => {
                setCopySuccess('Failed to copy!');
            });
    };

    return (
        <Dialog
            maxWidth={maxWidth}
            fullWidth={true}
            open={open}
            onClose={onConfirmDialogClose}
            className="confirmDeletePopup"
            PaperComponent={PaperComponent}>
            <DialogTitle
                className="confirmDeletePopupTitle"
                style={{cursor: "move"}}
                id="draggable-confirm-dialog-title">
                {title}
            </DialogTitle>

            <IconButton
                className="confirmCloseBtn"
                onClick={onConfirmDialogClose}
                style={{position: 'absolute', right: '8px', top: '8px'}}>
                <Icon title={t("general.close")}>close</Icon>
            </IconButton>

            <DialogContent className="confirmDeletePopupContent">
                <div style={{textAlign: 'center', padding: '16px', display: 'flex', justifyContent: 'center'}}>
                    <QRCode
                        value={text}
                    />
                </div>
                <div style={{textAlign: 'center', paddingTop: '16px'}}>
                    <Typography variant="body2" style={{wordBreak: 'break-all'}}>
                        {text}
                    </Typography>
                    <Button
                        variant="contained"
                        color="primary"
                        onClick={handleCopyClick}
                        style={{marginTop: '8px'}}>
                        Copy Link
                    </Button>
                    {copySuccess && (
                        <Typography variant="body2" color="primary" style={{marginTop: '8px'}}>
                            {copySuccess}
                        </Typography>
                    )}
                </div>
            </DialogContent>
        </Dialog>
    );
}

export default memo(GlobitsQrLink);

function PaperComponent(props) {
    return (
        <Draggable handle="#draggable-confirm-dialog-title" cancel={'[class*="MuiDialogContent-root"]'}>
            <Paper {...props} />
        </Draggable>
    );
}
