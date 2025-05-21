import React, { useEffect, useState } from "react";
import {
  Dialog,
  IconButton,
  Typography,
  withStyles,
  makeStyles,
  DialogActions,
  Button,
  DialogContent,
  Grid,
} from "@material-ui/core";
import MuiDialogTitle from "@material-ui/core/DialogTitle";
import CloseIcon from "@material-ui/icons/Close";
import Paper from "@material-ui/core/Paper";
import BlockIcon from "@material-ui/icons/Block";
import { useStore } from "../../stores";
import { observer } from "mobx-react";
import Draggable from "react-draggable";
import { useTranslation } from "react-i18next";
import { useFormikContext } from "formik";
import ProjectActivityList from "./ProjectActivityList";
import GlobitsSearchInput from "./GlobitsSearchInput";

const useStyles = makeStyles((theme) => ({
  root: {
    background: "#E4f5fc",
    padding: "8px",
    borderRadius: "4px",
  },
  dialogTitle: {
    cursor: "move",
    textAlign: "center",
    backgroundColor: "#5e6c84",
  },
  dialogTitle_text: {
    textAlign: "center",
    color: "#ffffff",
  },
  table: {
    "& .MuiPaper-root": {
      maxHeight: "50vh",
      overflow: "auto",
    },

    "& .MuiTableCell-head:nth-child(2)": {
      width: "5% !important",
    },

    "& .MuiTableCell-head:nth-child(1)": {
      width: "5% !important",
    },
  },
}));

function PaperComponent(props) {
  return (
    <div
      className="paper-container"
      style={{ maxWidth: "1800px", minWidth: "1000px" }}
    >
      <Draggable
        handle="#draggable-dialog-project-activity"
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
          className={classes.closeButton}
          onClick={onClose}
        >
          <CloseIcon />
        </IconButton>
      ) : null}
    </MuiDialogTitle>
  );
});

export default observer(function ProjectActivity(props) {
  const { open, setIsOpenListActivity } = props;

  const { t } = useTranslation();
  const classes = useStyles();

  const { timeSheetStore } = useStore();

  const { searchToListByPage, handleChangeSearchToList } = timeSheetStore;

  const { values, setFieldValue } = useFormikContext();

  const [selectedActivity, setSelectedActivity] = useState(
    values?.projectActivity
  );

  useEffect(() => {
    searchToListByPage({
      projectId: values?.project?.id,
    });
  // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [values?.project?.id, searchToListByPage]);

  const handleSelectActivity = (_, activity) => {
    if (selectedActivity && selectedActivity.id === activity.id) {
      setSelectedActivity(null);
    } else {
      setSelectedActivity(activity);
    }
  };

  useEffect(() => {
    setFieldValue("projectActivity", selectedActivity);
  }, [setFieldValue, selectedActivity]);

  return (
    <Dialog
      className="dialog-container"
      open={open}
      PaperComponent={PaperComponent}
      fullWidth
      maxWidth="md"
    >
      <DialogTitle
        className="dialog-header"
        id="draggable-dialog-project-activity"
        onClose={() => {
          setIsOpenListActivity(false);
        }}
      >
        <span className={`mb-20`}>Danh sách hoạt động của dự án</span>
      </DialogTitle>

      <DialogContent className={`${classes.table} dialog-body`}>
        <Grid container className="mb-16">
          <Grid item lg={6} md={6} sm={4} xs={4}></Grid>
          <Grid item lg={6} md={6} sm={8} xs={8}>
            <GlobitsSearchInput
              search={({ keyword }) =>
                handleChangeSearchToList(
                  { projectId: values?.project?.id },
                  keyword
                )
              }
            />
          </Grid>
        </Grid>
        <ProjectActivityList
          selectedItem={selectedActivity}
          handleSelectItem={handleSelectActivity}
        />
      </DialogContent>
      <DialogActions className="dialog-footer p-0 mt-20">
        <div className="flex flex-space-between flex-middle">
          <Button
            startIcon={<BlockIcon />}
            variant="contained"
            className="mr-12 btn btn-secondary d-inline-flex"
            onClick={() => {
              setIsOpenListActivity(false);
            }}
          >
            {t("general.button.close")}
          </Button>
        </div>
      </DialogActions>
    </Dialog>
  );
});
