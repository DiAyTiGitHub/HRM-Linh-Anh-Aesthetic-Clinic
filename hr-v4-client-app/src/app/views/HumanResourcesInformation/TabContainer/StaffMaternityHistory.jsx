import React, { useEffect, useState } from "react";
import {
    Grid,
    Button,
    IconButton,
    Icon,
    makeStyles,
    Table,
    TableBody,
    TableCell,
    TableHead,
    TableRow,
} from "@material-ui/core";
import AddIcon from "@material-ui/icons/Add";
import InfoIcon from "@material-ui/icons/Info";
import Dialog from "@material-ui/core/Dialog";
import MuiDialogTitle from "@material-ui/core/DialogTitle";
import CloseIcon from "@material-ui/icons/Close";
import Typography from "@material-ui/core/Typography";
import MuiDialogContent from "@material-ui/core/DialogContent";
import moment from "moment";
import { useFormikContext, FieldArray } from "formik";
import { useTranslation } from "react-i18next";
import { useStore } from "../../../stores";
import { useParams } from "react-router";
import { withStyles } from "@material-ui/core/styles";
import StaffMaternityHistoryPopupAdd from "./Popup/StaffMaternityHistoryPopupAdd";

const useStyles = makeStyles((theme) => ({
    root: {
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
        marginTop: "16px",
        overflowX: "auto",
        overflowY: "auto",
        maxHeight: "60vh",
        "& .MuiTableCell-body": {
            border: "1px solid #e9ecef",
            textAlign: "center",
        },
        "& .MuiTableCell-head": {
            padding: "10px",
            minWidth: "150px",
            border: "1px solid #e9ecef",
            textAlign: "center",
        },
    },
    tableHeader: {
        width: "100%",
        borderBottom: "1px solid #E3F2FD",
        marginBottom: "8px",
        "& th": {
            width: "calc(100vw / 4)",
        },
    },
}));

const styles = (theme) => ({
    root: {
        margin: 0,
        padding: theme.spacing(2),
        backgroundColor: "#FDCE09",

        "& .MuiTypography-h6": {
            color: "#ffffff !important",
        },
        "& .MuiIconButton-root": {
            color: "#ffffff !important",
        },
    },
    closeButton: {
        position: "absolute",
        right: theme.spacing(1),
        top: theme.spacing(1),
        color: theme.palette.grey[500],
    },
});

const DialogTitle = withStyles(styles)((props) => {
    const { children, classes, onClose, ...other } = props;
    return (
        <MuiDialogTitle disableTypography className={classes.root} {...other}>
            <Typography variant='h6'>{children}</Typography>
            {onClose ? (
                <IconButton aria-label='close' className={classes.closeButton} onClick={onClose}>
                    <CloseIcon />
                </IconButton>
            ) : null}
        </MuiDialogTitle>
    );
});

const DialogContent = withStyles((theme) => ({
    root: {
        padding: theme.spacing(2),
    },
}))(MuiDialogContent);

export default function StaffMaternityHistory() {
    const { t } = useTranslation();
    const classes = useStyles();
    const [openPopup, setOpenPopup] = useState(false);
    const [editable, setEditable] = useState(true);
    const [editableIndex, setEditableIndex] = useState(null);
    const [open, setOpen] = useState(false);
    const [item, setItem] = useState(null);

    const { values, setFieldValue } = useFormikContext();
    const list = values?.maternityHistory;

    const handleSubmitItem = (values) => {
        let newList = list ? list : [];
        if (!item) {
            newList.push(values);
        } else {
            newList = newList?.map((el, index) => {
                if (values?.index === index) {
                    return values;
                }
                return el;
            });
        }

        setFieldValue("maternityHistory", newList);
        setOpenPopup(false);
        setEditable(true);
    };

    const [staff, setStaff] = useState({});

    const { staffStore } = useStore();
    const { getStaff } = staffStore;

    const { id } = useParams();

    useEffect(() => {
        if (id) getStaff(id).then((data) => setStaff(data));
    }, [id, getStaff]);

    return (
        <Grid container spacing={2}>
            <Grid item xs={12}>
                <FieldArray
                    name='maternityHistory'
                    render={(arrayHelpers) => (
                        <div className={classes.groupContainer}>
                            <Button
                                className='btn btn-primary d-inline-flex'
                                startIcon={<AddIcon />}
                                variant='contained'
                                onClick={() => {
                                    if (staff.gender === "F") {
                                        setOpenPopup(true);
                                        setItem(null);
                                    } else {
                                        setOpen(true);
                                        setFieldValue("maternityHistory", null);
                                    }
                                }}>
                                {t("general.button.add")}
                            </Button>
                            <div className={classes.tableContainer}>
                                {list?.length > 0 ? (
                                    <Table style={{ tableLayout: "auto" }}>
                                        <TableHead>
                                            <TableRow>
                                                <TableCell style={{ minWidth: "60px" }}>
                                                    {t("general.action")}
                                                </TableCell>

                                                <TableCell colSpan={1}>{t("maternityHistory.startDate")}</TableCell>
                                                <TableCell colSpan={1}>{t("maternityHistory.endDate")}</TableCell>
                                                <TableCell colSpan={1}>{t("maternityHistory.birthNumber")}</TableCell>
                                                <TableCell colSpan={1}>{t("maternityHistory.note")}</TableCell>
                                            </TableRow>
                                        </TableHead>
                                        <TableBody>
                                            {list?.map((item, index) => (
                                                <TableRow key={index}>
                                                    <TableCell>
                                                        {editable === false && editableIndex === index ? (
                                                            <IconButton size='small'>
                                                                <Icon fontSize='small' color='primary'>
                                                                    visibility
                                                                </Icon>
                                                            </IconButton>
                                                        ) : (
                                                            <>
                                                                <IconButton
                                                                    size='small'
                                                                    onClick={() => {
                                                                        setItem({ ...item, index });
                                                                        setOpenPopup(true);
                                                                        setEditable(false);
                                                                        setEditableIndex(index);
                                                                    }}>
                                                                    <Icon fontSize='small' color='primary'>
                                                                        edit
                                                                    </Icon>
                                                                </IconButton>
                                                                <IconButton
                                                                    size='small'
                                                                    onClick={() => arrayHelpers.remove(index)}>
                                                                    <Icon fontSize='small' color='secondary'>
                                                                        delete
                                                                    </Icon>
                                                                </IconButton>
                                                            </>
                                                        )}
                                                    </TableCell>

                                                    <TableCell colSpan={1}>
                                                        {item?.startDate
                                                            ? moment(item?.startDate).format("DD/MM/YYYY")
                                                            : null}
                                                    </TableCell>
                                                    <TableCell colSpan={1}>
                                                        {item?.endDate
                                                            ? moment(item?.endDate).format("DD/MM/YYYY")
                                                            : null}
                                                    </TableCell>
                                                    <TableCell colSpan={1}>
                                                        {item?.birthNumber ? item?.birthNumber : null}
                                                    </TableCell>
                                                    <TableCell colSpan={1}>{item?.note ? item?.note : ""}</TableCell>
                                                </TableRow>
                                            ))}
                                        </TableBody>
                                    </Table>
                                ) : (
                                    <h5 className='text-primary n-w'>Không có quá trình thai sản nào</h5>
                                )}
                            </div>
                        </div>
                    )}
                />
                {openPopup && (
                    <StaffMaternityHistoryPopupAdd
                        item={item}
                        handleSubmit={handleSubmitItem}
                        open={openPopup}
                        handleClose={() => {
                            setOpenPopup(false);
                            setEditable(true);
                        }}
                        editable={editable}
                    />
                )}

                <Dialog aria-labelledby='customized-dialog-title' open={open}>
                    <DialogTitle id='customized-dialog-title' onClose={() => setOpen(false)}>
                        <InfoIcon /> Thông báo
                    </DialogTitle>
                    <DialogContent style={{ fontSize: "20px", fontWeight: "700" }}>
                        Không có quá trình thai sản dành cho nam !
                    </DialogContent>
                </Dialog>
            </Grid>
        </Grid>
    );
}
