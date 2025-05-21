import React, { useEffect, useRef } from "react";
import { useTranslation } from "react-i18next";
import { DialogContent } from "@material-ui/core";
import { makeStyles } from "@material-ui/core/styles";
import { Dialog, DialogTitle, Icon, IconButton, DialogActions } from "@material-ui/core";
import Draggable from "react-draggable";
import Paper from "@material-ui/core/Paper";
import { memo } from "react";
import PropTypes from "prop-types";
import { AddCircleOutline } from "@material-ui/icons";

const useStyles = makeStyles({
    root: {
        "& .MuiDialogContent-root": {
            overflow: "auto !important",
        },
    },
});

function GlobitsPopupV2({
    open,
    onClosePopup = () => {},
    title,
    size,
    children,
    styleTitle,
    noHeader,
    styleContent,
    action,
    noDialogContent,
    popupId,
    scroll,
}) {
    const { t } = useTranslation();
    const classes = useStyles();
    const dialogRef = useRef(null); // Tạo ref để tham chiếu đến dialog

    useEffect(() => {
        const handleClickOutside = (event) => {
            if (dialogRef.current && !dialogRef.current.contains(event.target)) {
                onClosePopup();
            }
        };

        if (open) {
            document.addEventListener("mousedown", handleClickOutside);
        } else {
            document.removeEventListener("mousedown", handleClickOutside);
        }

        return () => {
            document.removeEventListener("mousedown", handleClickOutside);
        };
    }, [open]);

    function PaperComponent(props) {
        return (
            <Draggable
                handle={popupId ? "#" + popupId : "#globits-draggable-dialog-title"}
                cancel={'[class*="MuiDialogContent-root"]'}>
                <Paper {...props} />
            </Draggable>
        );
    }

    return (
        <Dialog
            className={`dialog-container ${classes.root}`}
            open={open}
            PaperComponent={PaperComponent}
            fullWidth
            scroll={scroll || "paper"}
            maxWidth={size}>
            <div>
                {!noHeader ? (
                    <>
                        <DialogTitle
                            className='dialog-header dialog-header-v2'
                            style={{ cursor: "move", ...styleTitle }}
                            id={popupId || "globits-draggable-dialog-title"}>
                            <span className=' flex flex-middle gap-1'>
                                <AddCircleOutline />
                                {title}
                            </span>
                        </DialogTitle>

                        <IconButton
                            className='p-12 '
                            style={{ position: "absolute", right: "0px", top: "0px" }}
                            onClick={() => onClosePopup()}>
                            <Icon title={t("general.close")}>close</Icon>
                        </IconButton>
                    </>
                ) : (
                    <></>
                )}
                {!noDialogContent ? (
                    <DialogContent style={{ overflowY: "auto", maxHeight: "75vh", ...styleContent }}>
                        {children}
                    </DialogContent>
                ) : (
                    <>{children}</>
                )}
                {action ? (
                    <DialogActions className='dialog-footer dialog-footer-v2 p-0 mt-20'>{action}</DialogActions>
                ) : (
                    <></>
                )}
            </div>
        </Dialog>
    );
}

GlobitsPopupV2.propTypes = {
    size: PropTypes.oneOf([false, "xs", "sm", "md", "lg", "xl"]),
    open: PropTypes.bool.isRequired,
    onClosePopup: PropTypes.func.isRequired,
    title: PropTypes.string,
    children: PropTypes.node,
    noHeader: PropTypes.bool,
    noDialogContent: PropTypes.bool,
    action: PropTypes.node,
    styleTitle: PropTypes.object,
};

GlobitsPopupV2.defaultProps = {
    size: "lg",
};

export default memo(GlobitsPopupV2);
