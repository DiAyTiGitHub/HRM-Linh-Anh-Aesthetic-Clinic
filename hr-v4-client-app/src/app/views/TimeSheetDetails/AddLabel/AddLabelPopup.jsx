import React, { useEffect, useState } from "react";
import {
  Dialog,
  IconButton,
  Typography,
  withStyles,
  makeStyles,
  Grid,
  DialogActions,
  Button,
  DialogContent,
} from "@material-ui/core";
import MuiDialogTitle from "@material-ui/core/DialogTitle";
import CloseIcon from "@material-ui/icons/Close";
import Draggable from "react-draggable";
import Paper from "@material-ui/core/Paper";
import { Formik, Form } from "formik";
import { useTranslation } from "react-i18next";
import { SketchPicker } from "react-color";
import BrushIcon from "@material-ui/icons/Brush";
import GlobitsTextField from "app/common/form/GlobitsTextField";
import SaveIcon from "@material-ui/icons/Save";
import BlockIcon from "@material-ui/icons/Block";
import * as Yup from "yup";

const useStyles = makeStyles(() => ({
  dialogTitle: {
    cursor: "move",
    textAlign: "center",
    backgroundColor: "#5e6c84",
  },
  dialogTitle_text: {
    textAlign: "center",
    color: "#ffffff",
  },
}));

function PaperComponent(props) {
  return (
    <div className="paper-container" style={{ maxWidth: "560px" }}>
      <Draggable
        handle="#draggable-dialog-label-form"
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
  const { children, classes, onClose, ...other } = props;
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

export default function SearchPopup(props) {
  const { t } = useTranslation();
  const { open, handleClose, item, handleSubmit } = props;
  const classes = useStyles();

  const initialItem = {
    name: "",
    color: "",
  };

  const validationSchema = Yup.object({
    name: Yup.string().required(t("validation.required")),
  });

  const [openColorPicker, setOpenColorPicker] = useState(false);
  const [color, setColor] = useState("#194d33");

  const handleChange = (color) => {
    //console.log("color:", color.hex);
    setColor(color.hex);
  };

  const [formValues, setFormValues] = useState(null);

  useEffect(() => {
    if (item) {
      setFormValues({ ...item });
    } else {
      setFormValues({ ...initialItem });
    }
  }, [item]);

  return (
    <Dialog
      className="dialog-container"
      open={open}
      PaperComponent={PaperComponent}
      fullWidth
      maxWidth="md"
    // onBackdropClick={handleClose}
    >
      <DialogTitle
        className={classes.dialogTitle}
        id="draggable-dialog-label-form"
      // onClose={handleClose}
      >
        <span className={`${classes.dialogTitle_text} mb-20`}>Nhãn mới</span>
      </DialogTitle>
      <Formik
        initialValues={formValues}
        validationSchema={validationSchema}
        onSubmit={(values) => handleSubmit(values)}
      >
        {({ isSubmitting, setFieldValue }) => (
          <Form autoComplete="off">
            <DialogContent className="dialog-body">
              <Grid container spacing={2}>
                <Grid item md={12} xs={12}>
                  <GlobitsTextField
                    label={
                      <span>
                        {t("label.name")}
                        <span className="text-danger"> * </span>
                      </span>
                    }
                    name="name"
                  />
                </Grid>
                <Grid item md={11} xs={11}>
                  {/* <GlobitsTextField
                    label="Màu"
                    name="color"
                    type="color"
                    disabled={!editable}
                    onClick={() => {
                      setOpenColorPicker1(!openColorPicker1);
                    }}
                  /> */}

                  <Button
                    style={{
                      backgroundColor: color,
                      width: "100%",
                      heigh: "200px",
                      display: "inline-block",
                    }}
                    disabled
                  //className={classes.colorButton}
                  >
                    <span style={{ color: "#fff" }}>{color}</span>
                  </Button>

                  {/* {openColorPicker && (
                    <div style={{ margin: "8px 0 0 8px" }}>
                      <SketchPicker
                        color={color}
                        onChange={(color) => {
                          handleChange(color);
                          setFieldValue("color", color.hex);
                        }}
                      />
                    </div>
                  )} */}
                </Grid>

                <Grid item md={1} xs={1}>
                  <BrushIcon
                    onClick={() => {
                      setOpenColorPicker(!openColorPicker);
                    }}
                  ></BrushIcon>
                </Grid>

                {/* Color buttons */}
                <Grid item md={2} xs={4}>
                  <Button
                    style={{
                      backgroundColor: "#61bd4f",
                      width: "100%",
                      heigh: "200px",
                      display: "inline-block",
                    }}
                    //className={classes.colorButton}
                    onClick={() => {
                      setFieldValue("color", "#61bd4f");
                      setColor("#61bd4f");
                    }}
                  >
                    {/* <span style={{ color: "#fff" }}>#61bd4f</span> */}
                  </Button>
                </Grid>
                <Grid item md={2} xs={4}>
                  <Button
                    style={{
                      backgroundColor: "#f2d600",
                      width: "100%",
                      heigh: "200px",
                      display: "inline-block",
                    }}
                    onClick={() => {
                      setFieldValue("color", "#f2d600");
                      setColor("#f2d600");
                    }}
                  >
                    {/* <span style={{ color: "#fff" }}>#f2d600</span> */}
                  </Button>
                </Grid>
                <Grid item md={2} xs={4}>
                  <Button
                    style={{
                      backgroundColor: "#ff9f1a",
                      width: "100%",
                      heigh: "200px",
                      display: "inline-block",
                    }}
                    onClick={() => {
                      setFieldValue("color", "#ff9f1a");
                      setColor("#ff9f1a");
                    }}
                  >
                    {/* <span style={{ color: "#fff" }}>#ff9f1a</span> */}
                  </Button>
                </Grid>

                <Grid item md={2} xs={4}>
                  <Button
                    style={{
                      backgroundColor: "#eb5a46",
                      width: "100%",
                      heigh: "200px",
                      display: "inline-block",
                    }}
                    onClick={() => {
                      setFieldValue("color", "#eb5a46");
                      setColor("#eb5a46");
                    }}
                  >
                    {/* <span style={{ color: "#fff" }}>#eb5a46</span> */}
                  </Button>
                </Grid>
                <Grid item md={2} xs={4}>
                  <Button
                    style={{
                      backgroundColor: "#c377e0",
                      width: "100%",
                      heigh: "200px",
                      display: "inline-block",
                    }}
                    onClick={() => {
                      setFieldValue("color", "#c377e0");
                      setColor("#c377e0");
                    }}
                  >
                    {/* <span style={{ color: "#fff" }}>#c377e0</span> */}
                  </Button>
                </Grid>
                <Grid item md={2} xs={4}>
                  <Button
                    style={{
                      backgroundColor: "#0079bf",
                      width: "100%",
                      heigh: "200px",
                      display: "inline-block",
                    }}
                    onClick={() => {
                      setFieldValue("color", "#0079bf");
                      setColor("#0079bf");
                    }}
                  >
                    {/* <span style={{ color: "#fff" }}>#0079bf</span> */}
                  </Button>
                </Grid>
                <Grid item md={2} xs={4}>
                  <Button
                    style={{
                      backgroundColor: "#00c2e0",
                      width: "100%",
                      heigh: "200px",
                      display: "inline-block",
                    }}
                    onClick={() => {
                      setFieldValue("color", "#00c2e0");
                      setColor("#00c2e0");
                    }}
                  >
                    {/* <span style={{ color: "#fff" }}>#00c2e0</span> */}
                  </Button>
                </Grid>
                <Grid item md={2} xs={4}>
                  <Button
                    style={{
                      backgroundColor: "#51e898",
                      width: "100%",
                      heigh: "200px",
                      display: "inline-block",
                    }}
                    onClick={() => {
                      setFieldValue("color", "#51e898");
                      setColor("#51e898");
                    }}
                  >
                    {/* <span style={{ color: "#fff" }}>#51e898</span> */}
                  </Button>
                </Grid>
                <Grid item md={2} xs={4}>
                  <Button
                    style={{
                      backgroundColor: "#ff78cb",
                      width: "100%",
                      heigh: "200px",
                      display: "inline-block",
                    }}
                    onClick={() => {
                      setFieldValue("color", "#ff78cb");
                      setColor("#ff78cb");
                    }}
                  >
                    {/* <span style={{ color: "#fff" }}>#ff78cb</span> */}
                  </Button>
                </Grid>
                <Grid item md={2} xs={4}>
                  <Button
                    style={{
                      backgroundColor: "#344563",
                      width: "100%",
                      heigh: "200px",
                      display: "inline-block",
                    }}
                    onClick={() => {
                      setFieldValue("color", "#344563");
                      setColor("#344563");
                    }}
                  >
                    {/* <span style={{ color: "#fff" }}>#344563</span> */}
                  </Button>
                </Grid>
                <Grid item md={2} xs={4}>
                  <Button
                    style={{
                      backgroundColor: "#b3bac5",
                      width: "100%",
                      heigh: "200px",
                      display: "inline-block",
                    }}
                    onClick={() => {
                      setFieldValue("color", "#b3bac5");
                      setColor("#b3bac5");
                    }}
                  >
                    {/* <span style={{ color: "#fff" }}>#b3bac5</span> */}
                  </Button>
                </Grid>
                {/* <Grid item md={2} xs={4}>
                  <BrushIcon
                    style={{
                      margin: "auto 0",
                      height: "100%",
                    }}
                    onClick={() => {
                      setOpenColorPicker(!openColorPicker);
                    }}
                  />
                </Grid> */}
                {openColorPicker && (
                  <div style={{ margin: "8px 0 0 8px" }}>
                    <SketchPicker
                      color={color}
                      onChange={(color) => {
                        handleChange(color);
                        setFieldValue("color", color.hex);
                      }}
                    />
                  </div>
                )}
                {/* End */}
              </Grid>
            </DialogContent>

            <DialogActions className="dialog-footer p-0">
              <div
                className="flex flex-space-between flex-middle"
                style={{ textAlign: "center" }}
              >
                <Button
                  startIcon={<BlockIcon />}
                  variant="contained"
                  className="mr-12 btn btn-secondary d-inline-flex"
                  color="secondary"
                  onClick={() => handleClose()}
                >
                  {t("general.button.cancel")}
                </Button>
                <Button
                  startIcon={<SaveIcon />}
                  className="mr-0 btn btn-primary d-inline-flex"
                  variant="contained"
                  color="primary"
                  type="submit"
                  disabled={isSubmitting}
                >
                  {t("general.button.save")}
                </Button>
              </div>
            </DialogActions>
          </Form>
        )}
      </Formik>
    </Dialog>
  );
}
