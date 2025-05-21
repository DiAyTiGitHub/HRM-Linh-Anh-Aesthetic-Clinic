import React, { useState } from "react";
import {
  Dialog,
  Icon,
  IconButton,
  Typography,
  withStyles,
  makeStyles,
  Button,
  DialogContent,
} from "@material-ui/core";
import { useStore } from "../../stores";
import MuiDialogTitle from "@material-ui/core/DialogTitle";
import CloseIcon from "@material-ui/icons/Close";
import { observer } from "mobx-react";
import Draggable from "react-draggable";
import Paper from "@material-ui/core/Paper";
import { useTranslation } from "react-i18next";
import { useFormikContext, FieldArray } from "formik";
import CheckIcon from "@material-ui/icons/Check";

const useStyles = makeStyles(() => ({
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
    width: "100%",
    //background: "#fafafa",
    // padding: "8px",
    marginTop: "16px",
    borderRadius: "12px",
  },
  tableContainer__Content: {
    marginBottom: " 24px",
    paddingTop: "24px",
    borderTop: "2px solid rgba(0, 0, 0, 0.08)",
  },
  tableHeader: {
    "& >table": {
      width: "100%",
      borderBottom: "1px solid #E3F2FD",
      marginBottom: "8px",
      "padding-right": "27px",
      "& th": {
        width: "calc(100vw / 4)",
      },
    },
  },
  tableHeaderCell: {
    padding: "10px",
  },
  tableHeaderSTT: {
    width: "5%",
    padding: "10px",
    textAlign: "center",
  },
  tableHeaderCheckbox: {
    width: "310px",
    padding: "10px",
  },
  tableColAction: {
    width: "10%",
    padding: "10px",
    textAlign: "center",
  },
}));

function PaperComponent(props) {
  return (
    <div
      className="paper-container"
      style={{ maxWidth: "600px", minWidth: "400px" }}
    >
      <Draggable
        handle="#draggable-dialog"
        cancel={'[class*="MuiDialogContent-root"]'}
      >
        <Paper {...props} />
      </Draggable>
    </div>
  );
}

const styles = (theme) => ({
  root: {
    margin: 0,
    padding: theme.spacing(2),
  },
  closeButton: {
    position: "absolute",
    right: theme.spacing(1),
    top: theme.spacing(1),
    color: theme.palette.grey[500],
  },
});

const DialogTitle = withStyles(styles)((props) => {
  const { children, classes, handleClosePopup, onClose, ...other } = props;
  return (
    <MuiDialogTitle disableTypography className={classes.root} {...other}>
      <Typography variant="h6">{children}</Typography>
      {onClose ? (
        <IconButton
          aria-label="close"
          className={classes.closeButton}
          onClick={onClose}
        >
          <CloseIcon />
        </IconButton>
      ) : null}
    </MuiDialogTitle>
  );
});

export default observer(function ImportFile(props) {
  const { t } = useTranslation();
  const { timeSheetStore } = useStore();
  const {
          listFile
        } = timeSheetStore;
  const { open, handleClose, handleClosePopup } = props;
  const classes = useStyles();


  const [item, setItem] = useState(null);
  const { values } = useFormikContext();



  const [openPopup, setOpenPopup] = useState(false);

  return (
    <Dialog
      className="dialog-container"
      open={open}
      PaperComponent={PaperComponent}
      fullWidth
      maxWidth="md"
      onBackdropClick={handleClose}
    >
      <DialogTitle
        style={{
          cursor: "move",
          textAlign: "center",
          backgroundColor: "#5e6c84",
        }}
        id="draggable-dialog"
        onClose={handleClose}
      >
        <span className="mb-20" style={{ color: "white" }}>
          File
        </span>
      </DialogTitle>
      <DialogContent>
        <FieldArray
          name="label"
          render={() => (
            <div className={classes.groupContainer}>
              <div className={classes.tableContainer}>
                <div className={classes.tableContainer__Content}>
                  {listFile?.length > 0 && (
                    <div>
                      {listFile?.map((item, index) => (
                        <div
                          key={index}
                          style={{
                            width: "100%",
                            marginBottom: "8px",
                            display: "flex",
                          }}
                        >
                          <button
                            key={index}
                            style={{
                              display: "flex",
                              justifyContent: "center",
                              width: "100%",
                              height: "35px",
                              borderRadius: "3px",
                              backgroundColor: item.color,
                              marginRight: "8px",
                              border: "1px solid rgba(0, 0, 0, 0.08)",
                            }}
                            onClick={() => {
                              handleClosePopup(values, listFile);
                            }}
                          >
                            <span
                              style={{
                                color: "white",
                                display: "block",
                                margin: "auto",
                                fontSize: "18px",
                                textAlign: "center",
                                // paddingLeft:"20px"
                              }}
                            >
                              {item?.name}
                            </span>
                            {listFile.findIndex(
                              (labelTask) => labelTask.id === item.id
                            ) > -1 && (
                              <CheckIcon
                                key={index}
                                style={{
                                  color: "white",
                                  position: "absolute",
                                  right: "90px",
                                }}
                              />
                            )}
                          </button>
                          <>
                            <IconButton
                              size="small"
                              onClick={() => {
                                // handleDeleteLabel({ ...item });
                                // handleChangeLabel({ ...item });
                              }}
                            >
                              <Icon fontSize="small" color="error">
                                delete
                              </Icon>
                            </IconButton>
                          </>
                        </div>
                      ))}
                    </div>
                  )}
                  {!(listFile?.length > 0) && (
                    <h5 className="text-primary n-w">Không có File nào</h5>
                  )}
                </div>
                <Button
                  variant="contained"
                  className=" btn btn-secondary w-100 mb-20 mt-10"
                  onClick={() => {
                    setItem(null);
                    setOpenPopup(true);
                    // toast.success("Lưu thành công")
                  }}
                >
                  <span style={{ fontSize: "18px", color: "white" }}>
                    Thêm File
                  </span>
                </Button>
              </div>
            </div>
          )}
        />
      </DialogContent>
    </Dialog>
  );
});
